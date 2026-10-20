package project.sonora.poi.apache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LogRepository {

    public void salvar(String tipo, String mensagem, String status,
                       LocalDateTime dataHora) throws SQLException {

        String sql = """
                INSERT INTO log (tipo, mensagem, data_hora, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, tipo);
            stmt.setString(2, mensagem);
            stmt.setTimestamp(3, Timestamp.valueOf(dataHora));
            stmt.setString(4, status);

            stmt.executeUpdate();
        }
    }
}