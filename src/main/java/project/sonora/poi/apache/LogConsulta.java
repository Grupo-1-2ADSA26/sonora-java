package project.sonora.poi.apache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import java.util.Scanner;

public class LogConsulta {

    private static final DateTimeFormatter FORMATO_BRASILEIRO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public void exibirLogs() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n===== CONSULTA DE LOGS =====");
        System.out.println("1 - Todos os logs");
        System.out.println("2 - INFO");
        System.out.println("3 - SUCESSO");
        System.out.println("4 - AVISO");
        System.out.println("5 - ERRO");
        System.out.print("Escolha uma opção: ");

        int opcao;

        try {
            opcao = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida!");
            return;
        }

        String status = switch (opcao) {
            case 1 -> null;
            case 2 -> "INFO";
            case 3 -> "SUCESSO";
            case 4 -> "AVISO";
            case 5 -> "ERRO";
            default -> {
                System.out.println("Opção inválida!");
                yield "";
            }
        };

        if ("".equals(status)) {
            return;
        }

        consultarLogs(status);
    }

    private void consultarLogs(String status) {

        String sql;

        if (status == null) {
            sql = """
                    SELECT id_log, tipo, mensagem, data_hora, status
                    FROM log
                    ORDER BY data_hora DESC
                    """;
        } else {
            sql = """
                    SELECT id_log, tipo, mensagem, data_hora, status
                    FROM log
                    WHERE status = ?
                    ORDER BY data_hora DESC
                    """;
        }

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (status != null) {
                stmt.setString(1, status);
            }

            try (ResultSet resultado = stmt.executeQuery()) {

                System.out.println("\n===== LOGS ENCONTRADOS =====");

                boolean encontrou = false;

                while (resultado.next()) {

                    encontrou = true;

                    int id = resultado.getInt("id_log");
                    String tipo = resultado.getString("tipo");
                    String mensagem = resultado.getString("mensagem");
                    String dataHora = resultado.getTimestamp("data_hora")
                            .toLocalDateTime()
                            .format(FORMATO_BRASILEIRO);
                    String statusLog = resultado.getString("status");

                    System.out.println("----------------------------------------");
                    System.out.println("Data/Hora: " + dataHora);
                    System.out.println("ID: " + id);
                    System.out.println("Tipo: " + tipo);
                    System.out.println("Mensagem: " + mensagem);
                    System.out.println("Status: " + statusLog);
                }

                if (!encontrou) {
                    System.out.println("Nenhum log encontrado.");
                }

                System.out.println("----------------------------------------");
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erro ao consultar os logs: " + e.getMessage()
            );
        }
    }
}