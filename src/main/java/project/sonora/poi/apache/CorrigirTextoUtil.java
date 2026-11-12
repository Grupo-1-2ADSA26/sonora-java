package project.sonora.poi.apache;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public final class CorrigirTextoUtil {

    private static final Charset WINDOWS_1252 =
            Charset.forName("windows-1252");

    private CorrigirTextoUtil() {
    }

    public static String corrigir(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }

        String resultado = texto;

        // Tenta corrigir no máximo duas camadas de codificação incorreta.
        for (int i = 0; i < 2; i++) {
            if (!pareceCorrompido(resultado)) {
                break;
            }

            String tentativa = tentarCorrigir(resultado);

            // Só aceita a alteração se o texto parecer mais correto.
            if (tentativa == null
                    || pontuacao(tentativa) >= pontuacao(resultado)) {
                break;
            }

            resultado = tentativa;
        }

        return resultado;
    }

    private static String tentarCorrigir(String texto) {
        try {
            ByteBuffer bytes = WINDOWS_1252.newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .encode(CharBuffer.wrap(texto));

            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(bytes)
                    .toString();

        } catch (CharacterCodingException e) {
            // O texto não pode ser recuperado com essa transformação.
            return null;
        }
    }

    private static boolean pareceCorrompido(String texto) {
        return pontuacao(texto) > 0;
    }

    private static int pontuacao(String texto) {
        int pontos = 0;

        pontos += contar(texto, "Ã");
        pontos += contar(texto, "Â");
        pontos += contar(texto, "â€");
        pontos += contar(texto, "ðŸ");
        pontos += contar(texto, "\uFFFD") * 3;

        return pontos;
    }

    private static int contar(String texto, String trecho) {
        int total = 0;
        int indice = 0;

        while ((indice = texto.indexOf(trecho, indice)) != -1) {
            total++;
            indice += trecho.length();
        }

        return total;
    }
}