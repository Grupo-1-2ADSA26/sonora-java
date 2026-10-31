package project.sonora.poi.apache;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicaRepository {

    public int salvarTodas(List<Musica> musicas) throws SQLException {
        String sqlArtista =
                "INSERT INTO artista (nome, url_artista) VALUES (?, ?)";

        String sqlBuscarArtista =
                "SELECT id_artista FROM artista WHERE nome = ?";

        String sqlMusica =
                "INSERT INTO musica (titulo, album, id_artista, uri_musica) " +
                        "VALUES (?, ?, ?, ?)";

        String sqlMetrica =
                "INSERT INTO metrica_streaming " +
                        "(views, likes, comentarios, streams, danceability, " +
                        "energy, loudness, speechiness, acousticness, valence, " +
                        "tempo, duracao_ms, id_musica, id_plataforma) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int inseridas = 0;

        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement buscarArtista =
                            conn.prepareStatement(sqlBuscarArtista);
                    PreparedStatement inserirArtista =
                            conn.prepareStatement(
                                    sqlArtista, Statement.RETURN_GENERATED_KEYS
                            );
                    PreparedStatement inserirMusica =
                            conn.prepareStatement(
                                    sqlMusica, Statement.RETURN_GENERATED_KEYS
                            );
                    PreparedStatement inserirMetrica =
                            conn.prepareStatement(sqlMetrica)
            ) {
                Map<String, Integer> artistas = new HashMap<>();

                for (Musica musica : musicas) {
                    String artista = CorrigirTextoUtil.corrigir(musica.getNomeArtista());
                    String titulo = CorrigirTextoUtil.corrigir(musica.getTrackMusica());

                    // A tabela musica exige título e artista.
                    if (artista == null || artista.isBlank()
                            || titulo == null || titulo.isBlank()) {
                        System.out.println(
                                "Registro ignorado: artista ou título ausente."
                        );
                        continue;
                    }

                    int idArtista;

                    if (artistas.containsKey(artista)) {
                        idArtista = artistas.get(artista);
                    } else {
                        buscarArtista.setString(1, artista);

                        try (ResultSet rs = buscarArtista.executeQuery()) {
                            if (rs.next()) {
                                idArtista = rs.getInt("id_artista");
                            } else {
                                inserirArtista.setString(1, artista);
                                inserirArtista.setString(2, musica.getUrlSpotify());
                                inserirArtista.executeUpdate();

                                try (ResultSet chaves =
                                             inserirArtista.getGeneratedKeys()) {
                                    if (!chaves.next()) {
                                        throw new SQLException(
                                                "Não foi possível obter o ID do artista."
                                        );
                                    }
                                    idArtista = chaves.getInt(1);
                                }
                            }
                        }

                        artistas.put(artista, idArtista);
                    }

                    // Insere a música e recupera seu ID.
                    inserirMusica.setString(1, titulo);
                    inserirMusica.setString(2, CorrigirTextoUtil.corrigir(musica.getAlbum()));
                    inserirMusica.setInt(3, idArtista);
                    inserirMusica.setString(4, musica.getUriMusica());
                    inserirMusica.executeUpdate();

                    int idMusica;

                    try (ResultSet chaves =
                                 inserirMusica.getGeneratedKeys()) {
                        if (!chaves.next()) {
                            throw new SQLException(
                                    "Não foi possível obter o ID da música."
                            );
                        }
                        idMusica = chaves.getInt(1);
                    }

                    // Insere as métricas da música.
                    inserirMetrica.setObject(1, musica.getViews());
                    inserirMetrica.setObject(2, musica.getLikes());
                    inserirMetrica.setObject(3, musica.getComentarios());
                    inserirMetrica.setObject(4, musica.getStreams());
                    inserirMetrica.setObject(5, musica.getDanceability());
                    inserirMetrica.setObject(6, musica.getEnergy());
                    inserirMetrica.setObject(7, musica.getLoudness());
                    inserirMetrica.setObject(8, musica.getSpeechiness());
                    inserirMetrica.setObject(9, musica.getAcousticness());
                    inserirMetrica.setObject(10, musica.getValence());
                    inserirMetrica.setObject(11, musica.getTempo());

                    if (musica.getDurationMs() == null) {
                        inserirMetrica.setNull(12, Types.INTEGER);
                    } else {
                        inserirMetrica.setInt(
                                12, musica.getDurationMs().intValue()
                        );
                    }

                    inserirMetrica.setInt(13, idMusica);
                    inserirMetrica.setInt(14, 1); // ID 1 = Spotify (da tabela plataforma)
                    inserirMetrica.executeUpdate();

                    inseridas++;
                }

                conn.commit();

                System.out.println(
                        "Carga finalizada: " + inseridas + " músicas inseridas."
                );

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        return inseridas;
    }


    public int classificarGenerosArtistas(int limite) throws SQLException {

        String sqlBuscar =
                "SELECT id_artista, nome, url_artista " +
                        "FROM artista " +
                        "WHERE genero_status = 'PENDENTE' " +
                        "ORDER BY id_artista " +
                        "LIMIT ?";

        String sqlAtualizar =
                "UPDATE artista " +
                        "SET genero = ?, genero_status = ? " +
                        "WHERE id_artista = ?";

        MusicBrainzService musicBrainz = new MusicBrainzService();

        // Carrega os artistas do lote antes de iniciar as consultas.
        List<ArtistaPendente> artistas = new java.util.ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement buscar = conn.prepareStatement(sqlBuscar)) {

            buscar.setInt(1, limite);

            try (ResultSet rs = buscar.executeQuery()) {
                while (rs.next()) {
                    artistas.add(new ArtistaPendente(
                            rs.getInt("id_artista"),
                            rs.getString("nome"),
                            rs.getString("url_artista")
                    ));
                }
            }
        }

        int processados = 0;
        int encontrados = 0;
        int semResultado = 0;

        try (Connection conn = Conexao.conectar();
             PreparedStatement atualizar =
                     conn.prepareStatement(sqlAtualizar)) {

            for (ArtistaPendente artista : artistas) {

                try {
                    String genero = musicBrainz.buscarGeneroArtista(
                            artista.nome(),
                            artista.urlSpotify()
                    );

                    String status;

                    if (genero != null && !genero.isBlank()) {
                        status = "OK";
                    } else {
                        status = "SEM_RESULTADO";
                        genero = null;
                    }

                    atualizar.setString(1, genero);
                    atualizar.setString(2, status);
                    atualizar.setInt(3, artista.id());

                    atualizar.executeUpdate();

                    processados++;

                    if (status.equals("OK")) {
                        encontrados++;
                    } else {
                        semResultado++;
                    }

                    System.out.println(
                            "[" + processados + "/" + artistas.size() + "] "
                                    + artista.nome()
                                    + " -> "
                                    + (genero == null ? "Sem gênero" : genero)
                    );

                } catch (Exception e) {

                    String mensagem = e.getMessage() == null
                            ? ""
                            : e.getMessage();

                    // Evita insistir caso a API esteja limitando chamadas.
                    if (mensagem.contains("HTTP 429")
                            || mensagem.contains("HTTP 503")
                            || mensagem.contains("MusicBrainz indisponível")) {

                        System.err.println(
                                "MusicBrainz limitou ou interrompeu as consultas: "
                                        + mensagem
                        );

                        System.err.println(
                                "Classificação interrompida. "
                                        + "Os artistas restantes continuam pendentes."
                        );

                        break;
                    }

                    System.err.println(
                            "Erro ao classificar " + artista.nome()
                                    + ": " + mensagem
                    );

                    // Erros temporários não mudam o status.
                    // O artista continua PENDENTE para outra execução.
                }
            }
        }

        System.out.println("\n========== RESUMO ==========");
        System.out.println("Artistas no lote: " + artistas.size());
        System.out.println("Processados: " + processados);
        System.out.println("Com gênero: " + encontrados);
        System.out.println("Sem resultado: " + semResultado);
        System.out.println("============================");

        return processados;
    }

    private record ArtistaPendente(
            int id,
            String nome,
            String urlSpotify
    ) {}

}