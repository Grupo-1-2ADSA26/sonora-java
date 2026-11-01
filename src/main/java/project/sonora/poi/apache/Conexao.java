package project.sonora.poi.apache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection conectar() throws SQLException {
        // Conexão direta com IP local (127.0.0.1) e sem senha ("")

        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PASSWORD");

        return DriverManager.getConnection(url, usuario, senha);
    }
}