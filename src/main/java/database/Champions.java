package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Classe responsável por gerenciar os comandos presentes na tabela "champions"
 */
public class Champions {

    /**
     * Cria a tabela "champions" no banco de dados
     */
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS champions ("
                + "id INTEGER PRIMARY KEY, "
                + "key VARCHAR(50) NOT NULL, "
                + "name VARCHAR(60) NOT NULL, "
                + "title VARCHAR(80) NOT NULL, "
                + "gender VARCHAR(50) NOT NULL, "
                + "lane VARCHAR(50) NOT NULL, "
                + "region VARCHAR(50) NOT NULL, "
                + "resource VARCHAR(50) NOT NULL, "
                + "functions VARCHAR(50) NOT NULL, "
                + "range_type VARCHAR(50) NOT NULL, "
                + "skins_count INTEGER NOT NULL, "
                + "release_year INTEGER NOT NULL, "
                + "passive_name VARCHAR(80) NOT NULL, "
                + "ultimate_name VARCHAR(80) NOT NULL, "
                + "icon_path VARCHAR(150) NOT NULL, "
                + "splash_art_path VARCHAR(150) NOT NULL, "
                + "version_id INTEGER NOT NULL, "
                + "FOREIGN KEY (version_id) REFERENCES data_dragon_versions (id) ON DELETE CASCADE "
                + ")";

        Connection databaseConnection = DBConnection.getConnection();
        Statement stmt = databaseConnection.createStatement();
        stmt.execute(sql);
    }

    /**
     * Adiciona um registro na tabela "champions"
     */
    public static boolean addChampion(JSONObject champion, int versionId) {
        boolean isChampionRegistred = false;

        try {
            Connection databaseConnection = DBConnection.getConnection();
            String sql = "INSERT INTO champions (key, name, title, gender, lane, region, resource, functions, range_type, skins_count, release_year, passive_name, ultimate_name, icon_path, splash_art_path, version_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, champion.getString("key"));
            pStmt.setString(2, champion.getString("name"));
            pStmt.setString(3, champion.getString("title"));
            pStmt.setString(4, champion.getString("gender"));
            pStmt.setString(5, champion.getString("lane"));
            pStmt.setString(6, champion.getString("region"));
            pStmt.setString(7, champion.getString("resource"));
            pStmt.setString(8, champion.getString("functions"));
            pStmt.setString(9, champion.getString("range_type"));
            pStmt.setInt(10, champion.getInt("skins_count"));
            pStmt.setInt(11, champion.getInt("release_year"));
            pStmt.setString(12, champion.getString("passive_name"));
            pStmt.setString(13, champion.getString("ultimate_name"));
            pStmt.setString(14, champion.getString("icon_path"));
            pStmt.setString(15, champion.getString("splash_art_path"));
            pStmt.setInt(16, versionId);

            int result = pStmt.executeUpdate();
            if (result == 1) {
                isChampionRegistred = true;
            }

            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return isChampionRegistred;
    }

    /**
     * Obtém os dados de um campeão específico com base em sua chave e no
     * identificador da versão
     */
    public static JSONObject getChampion(String championKey, int versionId) {
        JSONObject champion = new JSONObject();

        try {
            Connection databaseConnection = DBConnection.getConnection();

            String sql = "SELECT key, name, title, gender, lane, region, resource, functions, range_type, skins_count, release_year, passive_name, ultimate_name, icon_path, splash_art_path "
                    + "FROM champions "
                    + "WHERE key = ? "
                    + "AND version_id = ?";

            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, championKey);
            pStmt.setInt(2, versionId);

            ResultSet result = pStmt.executeQuery();
            if (result.next()) {
                champion.put("key", result.getString("key"));
                champion.put("name", result.getString("name"));
                champion.put("title", result.getString("title"));
                champion.put("gender", result.getString("gender"));
                champion.put("lane", result.getString("lane"));
                champion.put("region", result.getString("region"));
                champion.put("resource", result.getString("resource"));
                champion.put("functions", result.getString("functions"));
                champion.put("range_type", result.getString("range_type"));
                champion.put("skins_count", result.getInt("skins_count"));
                champion.put("release_year", result.getInt("release_year"));
                champion.put("passive_name", result.getString("passive_name"));
                champion.put("ultimate_name", result.getString("ultimate_name"));
                champion.put("icon_path", result.getString("icon_path"));
                champion.put("splash_art_path", result.getString("splash_art_path"));
                champion.put("version_id", versionId);
            }

            result.close();
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return champion;
    }

    /**
     * Recupera uma lista com os dados de todos os campeões disponíveis em uma
     * determinada versão, utilizando a localidade especificada
     */
    public static JSONArray getAllChampions(int versionId, String language) {
        JSONArray allChampions = new JSONArray();

        try {
            Connection databaseConnection = DBConnection.getConnection();

            String sql = "SELECT key, name, title, gender, lane, region, resource, functions, range_type, skins_count, release_year, passive_name, ultimate_name, icon_path, splash_art_path "
                    + "FROM champions c "
                    + "JOIN data_dragon_versions ddv ON c.version_id = ddv.id "
                    + "WHERE c.version_id = ? "
                    + "AND ddv.language = ? "
                    + "ORDER BY name";

            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setInt(1, versionId);
            pStmt.setString(2, language);

            ResultSet result = pStmt.executeQuery();
            while (result.next()) {
                JSONObject champion = new JSONObject();
                champion.put("key", result.getString("key"));
                champion.put("name", result.getString("name"));
                champion.put("title", result.getString("title"));
                champion.put("gender", result.getString("gender"));
                champion.put("lane", result.getString("lane"));
                champion.put("region", result.getString("region"));
                champion.put("resource", result.getString("resource"));
                champion.put("functions", result.getString("functions"));
                champion.put("range_type", result.getString("range_type"));
                champion.put("skins_count", result.getInt("skins_count"));
                champion.put("release_year", result.getInt("release_year"));
                champion.put("passive_name", result.getString("passive_name"));
                champion.put("ultimate_name", result.getString("ultimate_name"));
                champion.put("icon_path", result.getString("icon_path"));
                champion.put("splash_art_path", result.getString("splash_art_path"));
                champion.put("version_id", versionId);

                allChampions.put(champion);
            }

            result.close();
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return allChampions;
    }

    /**
     * Remove os dados de campeões pertencentes a versões anteriores à versão
     * mais recente informada
     */
    public static void deleteOldChampions(String latestVersion) {
        try {
            Connection databaseConnection = DBConnection.getConnection();

            String sql = "DELETE FROM champions "
                    + "WHERE version_id IN ("
                    + "SELECT c.version_id "
                    + "FROM champions c "
                    + "JOIN data_dragon_versions ddv ON c.version_id = ddv.id "
                    + "WHERE ddv.version != ?"
                    + ")";

            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, latestVersion);

            pStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
