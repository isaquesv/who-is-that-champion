package apis;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import utils.SelfCertificatedServer;

/** 
 * Classe responsável por realizar requisições e interações com a API Data Dragon do League of Legends
 */
public class DataDragonVersion {
    
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
}
