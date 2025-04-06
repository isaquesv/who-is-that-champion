package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados SQLite
 */
public class DBConnection {

    public static final String CLASS_NAME = "org.sqlite.JDBC";
    public static final String DATABASE_URL = "jdbc:sqlite:who-is-that-champion.db?journal_mode=WAL";

    /**
     * Estabelece uma conexão com o banco de dados SQLite
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(CLASS_NAME);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado.");
            e.printStackTrace();
        }

        Connection databaseConnection = DriverManager.getConnection(DATABASE_URL);
        return databaseConnection;
    }
}
