package apis;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.SelfCertificatedServer;
import utils.TextManipulation;

/** 
 * Classe responsável por realizar requisições e interações com a API do Kerrders (https://github.com/kerrders)
 */
public class KerrdersLoLdleData {
    
    public static final HttpClient HTTP_CLIENT = SelfCertificatedServer.createHttpClient();
    
    /**
     * Retorna os dados adicionais de um campeão específico utilizando a API do Kerrders,
     * com base nas informações previamente obtidas da API Data Dragon
     */
    public static JSONObject getKerrdersChampionData(JSONObject championData, String championKey, String language) {
        try {
            String championDataEndpoint = "https://gist.githubusercontent.com/Kerrders/0067d88dfd982c272e20dcb496f4dbc7/raw/e0d39fec90a590dc934cd6e60a257a079c15473b/champions.json";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(championDataEndpoint))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro na requisição: " + response.statusCode() + " - " + responseBody);
            } else {
                JSONArray champions = new JSONArray(responseBody);

                // Adicionando a campeã "Mel" (não esta na API)
                JSONObject melData = new JSONObject();
                melData.put("id", "Mel");
                melData.put("gender", "Female");
                melData.put("lane", "Mid");
                melData.put("attackType", "Range");
                melData.put("releaseDate", 2025);
                melData.put("region", "Noxus");
                champions.put(melData);

                // Iterando sobre o array para encontrar o campeão com a chave fornecida
                for (int i = 0; i < champions.length(); i++) {
                    JSONObject data = champions.getJSONObject(i);

                    if (data.getString("id").equals(championKey)) {
                        // Adicionando informações não encontradas da Ambessa e corrigindo os dados de outros campeões
                        if (championKey.equals("Ambessa")) {
                            data.put("lane", "Top");
                            data.put("releaseDate", 2024);
                        } else if (championKey.equals("Talon")) {
                            data.put("lane", "Mid,jungle");
                        } else if (championKey.equals("Samira")) {
                            data.put("attackType", "Close,range");
                        }

                        // Gênero
                        String gender = data.optString("gender", null);
                        if (gender == null || gender.trim().isEmpty()) {
                            championData.put("gender", "--");
                        } else {
                            gender = TextManipulation.capitalizeFirstLetter(gender.trim());
                            if (gender.equals("Divers")) {
                                gender = "Other";
                            }

                            gender = TextManipulation.getTranslateGender(gender, language);
                            championData.put("gender", gender);
                        }
                        
                        // Rota / Lane
                        String lane = data.optString("lane", null);
                        if (lane == null || lane.trim().isEmpty()) {
                            championData.put("lane", "--");
                        } else {
                            lane = lane.trim();

                            if (lane.contains(",")) {
                                String[] partsLanes = lane.split(",");
                                int countCommas = partsLanes.length;

                                for (int j = 0; j < countCommas; j++) {
                                    String actualLane = TextManipulation.capitalizeFirstLetter(partsLanes[j]);
                                    actualLane = TextManipulation.getTranslateLane(actualLane, language);

                                    if (j == 0) {
                                        lane = actualLane;
                                    } else {
                                        lane += " / " + actualLane;
                                    }
                                }
                            } else {
                                lane = TextManipulation.capitalizeFirstLetter(lane);
                                lane = TextManipulation.getTranslateLane(lane, language);
                            }

                            championData.put("lane", lane);
                        }

                        // Tipo de alcance
                        String rangeType = data.optString("attackType", null);
                        if (rangeType == null || rangeType.trim().isEmpty()) {
                            championData.put("range_type", "--");
                        } else {
                            rangeType = rangeType.trim();

                            if (rangeType.contains(",")) {
                                String[] partsRangeType = rangeType.split(",");
                                int countCommas = partsRangeType.length;

                                for (int j = 0; j < countCommas; j++) {
                                    String actualRangeType = TextManipulation.capitalizeFirstLetter(partsRangeType[j]);
                                    actualRangeType = TextManipulation.getTranslateRangeType(actualRangeType, language);

                                    if (j == 0) {
                                        rangeType = actualRangeType;
                                    } else {
                                        rangeType += " / " + actualRangeType;
                                    }
                                }
                            } else {
                                rangeType = TextManipulation.capitalizeFirstLetter(rangeType);
                                rangeType = TextManipulation.getTranslateRangeType(rangeType, language);
                            }

                            championData.put("range_type", rangeType);
                        }

                        // Ano de lançamento
                        int releaseYear = data.optInt("releaseDate", -1);
                        championData.put("release_year", releaseYear);

                        // Região
                        String region = data.optString("region", null);
                        if (region == null || region.trim().isEmpty()) {
                            championData.put("region", "--");
                        } else {
                            region = TextManipulation.capitalizeFirstLetter(region.trim());
                            region = TextManipulation.getTranslateRegion(region, language);

                            championData.put("region", region);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return championData;
    }
}
