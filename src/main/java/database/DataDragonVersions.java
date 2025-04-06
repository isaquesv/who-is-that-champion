package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe responsável por gerenciar a tabela de versões da API Data Dragon no
 * banco de dados
 */
public class DataDragonVersions {

    /*
     * Cria a tabela "data_dragon_versions" no banco de dados
     */
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS data_dragon_versions ("
                + "id INTEGER PRIMARY KEY, "
                + "version VARCHAR(50) NOT NULL, "
                + "language VARCHAR(5) NOT NULL"
                + ")";

        Connection databaseConnection = DBConnection.getConnection();
        Statement stmt = databaseConnection.createStatement();
        stmt.execute(sql);
    }

    /**
     * Recupera o identificador interno da versão da Data Dragon com base na
     * versão e localidade fornecidas
     */
    public static int getVersionId(String version, String language) throws Exception {
        int dataDragonVersionId = 0;

        try {
            Connection databaseConnection = DBConnection.getConnection();

            String sql = "SELECT id "
                    + "FROM data_dragon_versions "
                    + "WHERE version = ? "
                    + "AND language = ?";

            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, version);
            pStmt.setString(2, language);

            ResultSet result = pStmt.executeQuery();
            if (result.next()) {
                dataDragonVersionId = result.getInt("id");
            }

            result.close();
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return dataDragonVersionId;
    }

    /**
     * Adiciona uma nova versão da Data Dragon ao banco de dados para a
     * localidade especificada
     */
    public static int addVersion(String version, String language) throws SQLException {
        int dataDragonVersionIdRegistered = 0;

        try {
            Connection databaseConnection = DBConnection.getConnection();

            String sql = "INSERT INTO data_dragon_versions (version, language) "
                    + "VALUES(?, ?)";

            PreparedStatement pStmt = databaseConnection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, version);
            pStmt.setString(2, language);

            int result = pStmt.executeUpdate();

            if (result == 1) {
                ResultSet generatedCode = pStmt.getGeneratedKeys();
                if (generatedCode.next()) {
                    dataDragonVersionIdRegistered = generatedCode.getInt(1);
                }
            }

            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return dataDragonVersionIdRegistered;
    }

    /**
     * Remove do banco de dados todas as versões da Data Dragon anteriores à
     * versão mais recente especificada
     */
    public static void deleteOldVersions(String latestVersion) {
        try {
            Connection databaseConnection = DBConnection.getConnection();

            String sql = "DELETE FROM data_dragon_versions "
                    + "WHERE version != ?";

            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, latestVersion);

            pStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
