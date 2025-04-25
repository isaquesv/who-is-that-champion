package apis;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.SelfCertificatedServer;

/** 
 * Classe responsável por realizar requisições e interações com a API Who Is That Champion Data
 */
public class WhoIsThatChampionData {
    
    public static final HttpClient HTTP_CLIENT = SelfCertificatedServer.createHttpClient();
    
    /**
     * Retorna todos os dados disponíveis
     */
    public static JSONObject getAllChampionsData() {
        try {
            final String ALL_CHAMPIONS_ENDPOINT = "https://who-is-that-champion-data-api.vercel.app/api";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ALL_CHAMPIONS_ENDPOINT))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro na requisição: " + response.statusCode() + " - " + response.body());
            } else {
                JSONObject championsInAllLanguages = new JSONObject(response.body());
                
                return championsInAllLanguages.getJSONObject("champions_data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
