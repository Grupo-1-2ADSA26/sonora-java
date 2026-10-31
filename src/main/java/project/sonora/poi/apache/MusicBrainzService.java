
package project.sonora.poi.apache;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicBrainzService {

    private static final String BASE_URL =
            "https://musicbrainz.org/ws/2/";

    private static final long INTERVALO_MS = 1100;
    private static long ultimaRequisicao = 0;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public String buscarGeneroArtista(
            String nomeArtista,
            String urlSpotify
    ) throws Exception {

        String nome = CorrigirTextoUtil.corrigir(nomeArtista).trim();

        // Primeiro tenta encontrar o artista pela URL do Spotify.
        String mbid = buscarMbidPeloSpotify(urlSpotify);

        // Se não houver relacionamento cadastrado,
        // procura o artista pelo nome.
        if (mbid == null) {
            System.out.println(
                    "Spotify não vinculado no MusicBrainz. "
                            + "Buscando por nome: " + nome
            );

            mbid = buscarMbidPorNome(nome);
        }

        if (mbid == null) {
            System.out.println("Artista não identificado: " + nome);
            return null;
        }

        System.out.println("MusicBrainz ID: " + mbid);

        // Consulta os gêneros associados ao artista identificado.
        JsonNode artista = requisitar(
                "artist/" + mbid + "?inc=genres&fmt=json"
        );

        JsonNode generos = artista.path("genres");

        if (!generos.isArray() || generos.isEmpty()) {
            System.out.println(
                    "O artista foi encontrado, mas não possui gêneros cadastrados."
            );
            return null;
        }

        List<JsonNode> listaGeneros = new ArrayList<>();
        generos.forEach(listaGeneros::add);

        listaGeneros.sort(
                (a, b) -> Integer.compare(
                        b.path("count").asInt(0),
                        a.path("count").asInt(0)
                )
        );

        List<String> lista = new ArrayList<>();

        for (JsonNode genero : listaGeneros) {
            String nomeGenero = genero.path("name").asText("").trim();

            if (!nomeGenero.isBlank() && !lista.contains(nomeGenero)) {
                lista.add(nomeGenero);
            }

            if (lista.size() == 5) {
                break;
            }
        }

        return lista.isEmpty() ? null : String.join(", ", lista);
    }

    private String buscarMbidPeloSpotify(String urlSpotify)
            throws Exception {

        String urlNormalizada = normalizarUrlSpotify(urlSpotify);

        if (urlNormalizada == null) {
            return null;
        }

        String caminho = "url?resource="
                + codificar(urlNormalizada)
                + "&inc=artist-rels&fmt=json";

        JsonNode resposta = requisitarOuNull404(caminho);

        if (resposta == null) {
            return null;
        }

        JsonNode relacoes = resposta.path("relations");

        for (JsonNode relacao : relacoes) {
            JsonNode artista = relacao.path("artist");

            String mbid = artista.path("id").asText("");

            if (!mbid.isBlank()) {
                return mbid;
            }
        }

        return null;
    }

    private String buscarMbidPorNome(String nome)
            throws Exception {

        // Pesquisa o nome como expressão exata.
        String busca = "artist:\"" + nome
                .replace("\\", "\\\\")
                .replace("\"", "\\\"") + "\"";

        JsonNode resposta = requisitar(
                "artist?query=" + codificar(busca)
                        + "&limit=10&fmt=json"
        );

        JsonNode artistas = resposta.path("artists");

        String nomeNormalizado = normalizarNome(nome);

        for (JsonNode artista : artistas) {
            String encontrado = artista.path("name").asText("");
            String score = artista.path("score").asText("0");

            if (normalizarNome(encontrado).equals(nomeNormalizado)
                    && Integer.parseInt(score) >= 90) {

                return artista.path("id").asText(null);
            }
        }

        // Não seleciona um artista diferente apenas
        // por aparecer primeiro na pesquisa.
        return null;
    }

    private JsonNode requisitarOuNull404(String caminho)
            throws Exception {

        return requisitarInterno(caminho, true);
    }

    private JsonNode requisitar(String caminho)
            throws Exception {

        return requisitarInterno(caminho, false);
    }


    private JsonNode requisitarInterno(
            String caminho,
            boolean permitir404
    ) throws Exception {

        String contato = System.getenv("MUSICBRAINZ_CONTACT");

        if (contato == null || contato.isBlank()) {
            throw new IllegalStateException(
                    "Configure MUSICBRAINZ_CONTACT com seu e-mail."
            );
        }

        String userAgent = "Sonora/1.0 (" + contato + ")";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + caminho))
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .GET()
                .build();

        final int MAX_TENTATIVAS = 3;

        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {

            aguardarIntervalo();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            int status = response.statusCode();

            if (permitir404 && status == 404) {
                return null;
            }

            if (status == 200) {
                return mapper.readTree(response.body());
            }

            if (status == 429 || status == 503) {

                if (tentativa == MAX_TENTATIVAS) {
                    throw new IOException(
                            "MusicBrainz indisponível após "
                                    + MAX_TENTATIVAS
                                    + " tentativas. HTTP " + status
                    );
                }

                long espera = 5000L * tentativa;

                System.out.println(
                        "MusicBrainz retornou HTTP " + status
                                + ". Tentativa " + tentativa
                                + "/" + MAX_TENTATIVAS
                                + ". Nova tentativa após "
                                + (espera / 1000)
                                + " segundos."
                );

                Thread.sleep(espera);
                continue;
            }

            throw new IOException(
                    "Erro MusicBrainz HTTP " + status
                            + ": " + response.body()
            );
        }

        throw new IOException(
                "Não foi possível concluir a consulta ao MusicBrainz."
        );
    }


    private static synchronized void aguardarIntervalo()
            throws InterruptedException {

        long agora = System.currentTimeMillis();
        long espera = INTERVALO_MS
                - (agora - ultimaRequisicao);

        if (espera > 0) {
            Thread.sleep(espera);
        }

        ultimaRequisicao = System.currentTimeMillis();
    }

    private String normalizarUrlSpotify(String url) {

        if (url == null || url.isBlank()) {
            return null;
        }

        String id = null;

        if (url.startsWith("spotify:artist:")) {
            id = url.substring("spotify:artist:".length());

        } else if (url.contains("/artist/")) {
            id = url.substring(
                    url.indexOf("/artist/") + "/artist/".length()
            );
        }

        if (id == null) {
            return null;
        }

        id = id.split("[/?#]", 2)[0];

        if (!id.matches("[A-Za-z0-9]{22}")) {
            return null;
        }

        return "https://open.spotify.com/artist/" + id;
    }

    private String normalizarNome(String texto) {

        String normalizado = Normalizer.normalize(
                texto.toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
        );

        return normalizado
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String codificar(String texto) {
        return URLEncoder.encode(
                texto, StandardCharsets.UTF_8
        );
    }

}
