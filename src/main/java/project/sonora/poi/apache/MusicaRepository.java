package project.sonora.poi.apache;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicaRepository {

    public int salvarTodas(List<Musica> musicas) throws SQLException {
        String sqlArtista =
                "INSERT INTO artista (nome) VALUES (?)";

        String sqlBuscarArtista =
                "SELECT id_artista FROM artista WHERE nome = ?";

        String sqlMusica =
                "INSERT INTO musica (titulo, album, genero, id_artista) " +
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
                    String artista = musica.getNomeArtista();
                    String titulo = musica.getTrackMusica();

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
                    inserirMusica.setString(2, musica.getAlbum());
                    inserirMusica.setNull(3, Types.VARCHAR);
                    inserirMusica.setInt(4, idArtista);
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
}