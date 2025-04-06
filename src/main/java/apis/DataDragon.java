package apis;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import utils.SelfCertificatedServer;
import utils.TextManipulation;

/** 
 * Classe responsável por realizar requisições e interações com a API Data Dragon do League of Legends
 */
public class DataDragon {
    
    public static final HttpClient HTTP_CLIENT = SelfCertificatedServer.createHttpClient();

    /**
     * Obtém a versão mais recente dos dados disponíveis na API Data Dragon
     */
    public static String getLatestVersion() {
        final String ALL_VERSIONS_ENDPOINT = "https://ddragon.leagueoflegends.com/api/versions.json";
        String dataDragonLatestVersion = "";
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ALL_VERSIONS_ENDPOINT))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro na requisição: " + response.statusCode() + " - " + response.body());
            } else {
                JSONArray allVersions = new JSONArray(response.body());
                dataDragonLatestVersion = allVersions.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dataDragonLatestVersion;
    }
    
    /**
     * Retorna os dados de todos os campeões disponíveis para uma determinada versão e localidade,
     * utilizando a API Data Dragon
     */
    public static JSONArray getAllChampionsData(String dataDragonLatestVersion, int dataDragonLatestVersionId, String language) {
        JSONArray allChampionsData = new JSONArray();
        
        try {
            final String ALL_CHAMPIONS_ENDPOINT = "https://ddragon.leagueoflegends.com/cdn/" + dataDragonLatestVersion + "/data/" + language + "/champion.json";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ALL_CHAMPIONS_ENDPOINT))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro na requisição: " + response.statusCode() + " - " + response.body());
            } else {
                JSONObject champions = new JSONObject(response.body());
                JSONObject championsDataMap = champions.getJSONObject("data");
                
                // Obtendo todas as chaves dos campeões
                List<String> championsKeys = new ArrayList<>(championsDataMap.keySet());
                
                // Tratando os dados de cada campeão
                for (String championIdKey : championsKeys) {
                    JSONObject championData = championsDataMap.getJSONObject(championIdKey);
                    String championKey = championData.getString("id");

                    allChampionsData.put(getChampionCharacteristics(dataDragonLatestVersion, dataDragonLatestVersionId, championKey, language));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return allChampionsData;
    }
    
    /**
     * Retorna as características detalhadas de um campeão específico a partir da API Data Dragon,
     * com base na versão e localidade fornecidas
     */
    public static JSONObject getChampionCharacteristics(String dataDragonLatestVersion, int dataDragonLatestVersionId, String championKey, String language) {
        JSONObject championData = new JSONObject();
        
        try {
            final String CHAMPION_DATA_ENDPOINT = "https://ddragon.leagueoflegends.com/cdn/" + dataDragonLatestVersion + "/data/" + language + "/champion/" + championKey + ".json";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(CHAMPION_DATA_ENDPOINT))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro na requisição: " + response.statusCode() + " - " + response.body());
            } else {
                JSONObject champion = new JSONObject(response.body());
                JSONObject data = champion.getJSONObject("data").getJSONObject(championKey);
                
                final String SPLASH_ART_ENDPOINT = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + championKey + "_0.jpg";
                final String ICON_ENDPOINT = "https://ddragon.leagueoflegends.com/cdn/" + dataDragonLatestVersion + "/img/champion/" + data.getJSONObject("image").getString("full");
                
                championData.put("key", championKey);
                championData.put("name", data.getString("name"));
                championData.put("title", data.getString("title"));
                
                // Recurso
                String resource = data.getString("partype");
                // Caso o campeão não tenha um recurso definido
                if (resource.trim().isEmpty()) {
                    resource = "Sem custo";
                }
                championData.put("resource", resource);
                
                // Funções
                JSONArray tags = data.getJSONArray("tags");
                String functions = "";
                
                for (int i = 0; i < tags.length(); i++) {
                    String function = tags.getString(i);
                    function = TextManipulation.getTranslateFunction(function, language);
                    
                    if (i == 0) {
                        functions = function;
                    } else {
                        functions += " / " + function;
                    }
                }
                
                championData.put("functions", functions);
                // Quantidade de skins
                championData.put("skins_count", data.getJSONArray("skins").length() - 1);
                
                // Nome da passiva
                String randomChampionPassive = data.getJSONObject("passive").getString("name");
                championData.put("passive_name", randomChampionPassive);
                
                // Nome da ultimate
                String randomChampionUltimate = data.getJSONArray("spells").getJSONObject(3).getString("name");
                championData.put("ultimate_name", randomChampionUltimate);
                
                championData.put("icon_path", ICON_ENDPOINT);
                championData.put("splash_art_path", SPLASH_ART_ENDPOINT);
                championData.put("version_id", dataDragonLatestVersionId);
                
                // Capturando as demais características presentes em outra API
                championData = KerrdersLoLdleData.getKerrdersChampionData(championData, championKey, language);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return championData;
    }
}
