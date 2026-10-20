package project.sonora.poi.apache;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String TIPO = "Java";

    private static final DateTimeFormatter FORMATO_BRASILEIRO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final LogRepository repository;

    public Logger() {
        this.repository = new LogRepository();
    }

    public void info(String mensagem) {
        registrar(mensagem, "INFO");
    }

    public void sucesso(String mensagem) {
        registrar(mensagem, "SUCESSO");
    }

    public void aviso(String mensagem) {
        registrar(mensagem, "AVISO");
    }

    public void erro(String mensagem) {
        registrar(mensagem, "ERRO");
    }

    private void registrar(String mensagem, String status) {

        LocalDateTime dataHora = LocalDateTime.now();

        try {
            repository.salvar(TIPO, mensagem, status, dataHora);

            System.out.println(
                    "[" + dataHora.format(FORMATO_BRASILEIRO) + "] "
                            + "[" + status + "] "
                            + mensagem
            );

        } catch (Exception e) {
            System.err.println(
                    "Não foi possível salvar o log: " + e.getMessage()
            );
        }
    }
}