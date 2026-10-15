package project.sonora.poi.apache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection conectar() throws SQLException {
        // Conexão direta com IP local (127.0.0.1) e sem senha ("")
        String url = "jdbc:mysql://127.0.0.1:3306/sonora";
        String usuario = "root";
        String senha = "";

        return DriverManager.getConnection(url, usuario, senha);
    }
}