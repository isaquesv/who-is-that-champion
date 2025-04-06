package servlets;

import database.Champions;
import database.DataDragonVersions;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.GameSession;
import static servlets.PrepareGameServlet.getActualGameStatus;

@WebServlet(name = "CompareCharacteristicsServlet", urlPatterns = {"/CompareCharacteristicsServlet"})
public class CompareCharacteristicsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JSONObject compareCharacteristicsResponse = new JSONObject();
        String selectedChampionKey = request.getParameter("selectedChampionKey");
        String selectedChampionName = request.getParameter("selectedChampionName");
        String language = request.getParameter("language");
        
        try {
            if ((selectedChampionKey != null && !selectedChampionKey.trim().isEmpty()) || (selectedChampionName != null && !selectedChampionName.trim().isEmpty()) || (language != null && !language.trim().isEmpty())) {
                // Obtém a sessão existente
                HttpSession gameSession = request.getSession(false);
                boolean isGameEnded = false;
                boolean playerWins = false;
                int round = (int) gameSession.getAttribute("round");
                String mysteriousChampionKey = (String) gameSession.getAttribute("mysterious_champion_key");
                
                // Recupera os dados do campeão misterioso em todos os idiomas
                JSONArray mysteriousChampionInAllLanguages = new JSONArray();
                JSONObject mysteriousChampionPtBR = (JSONObject) gameSession.getAttribute("mysterious_champion_pt_br");
                JSONObject mysteriousChampionEsES = (JSONObject) gameSession.getAttribute("mysterious_champion_es_es");
                JSONObject mysteriousChampionEnUS = (JSONObject) gameSession.getAttribute("mysterious_champion_en_us");
                mysteriousChampionInAllLanguages.put(mysteriousChampionEsES);
                mysteriousChampionInAllLanguages.put(mysteriousChampionEnUS);
                
                // Pega a versão atual usada para buscar os campeões na API Data Dragon
                String dataDragonVersion = (String) gameSession.getAttribute("version");
                int dataDragonVersionIdPtBR = DataDragonVersions.getVersionId(dataDragonVersion, "pt_BR");
                int dataDragonVersionIdEsES = DataDragonVersions.getVersionId(dataDragonVersion, "es_ES");
                int dataDragonVersionIdEnUS = DataDragonVersions.getVersionId(dataDragonVersion, "en_US");
                
                // Busca os dados do campeão selecionado em todos os idiomas
                JSONArray selectedChampionInAllLanguages = new JSONArray();
                JSONObject selectedChampionPtBR = Champions.getChampion(selectedChampionKey, dataDragonVersionIdPtBR);
                JSONObject selectedChampionEsES = Champions.getChampion(selectedChampionKey, dataDragonVersionIdEsES);
                JSONObject selectedChampionEnUS = Champions.getChampion(selectedChampionKey, dataDragonVersionIdEnUS);
                selectedChampionInAllLanguages.put(selectedChampionPtBR);
                selectedChampionInAllLanguages.put(selectedChampionEsES);
                selectedChampionInAllLanguages.put(selectedChampionEnUS);
                
                // Salva a chave do campeão selecionado na sessão
                GameSession.addSelectedChampionKey(gameSession, selectedChampionKey);
                
                // Verifica se o jogador acertou ou se atingiu o limite de tentativas
                if (selectedChampionKey.equals(mysteriousChampionKey)) {
                    isGameEnded = true;
                    playerWins = true;
                } else if (round == 10) {
                    isGameEnded = true;
                }
                
                // Compara as características entre os campeões
                String characteristicComparisonResultPtBR = processCharacteristicComparison(gameSession, round, isGameEnded, "pt_BR", mysteriousChampionPtBR, selectedChampionPtBR);
                String characteristicComparisonResultEsES = processCharacteristicComparison(gameSession, round, isGameEnded, "es_ES", mysteriousChampionEsES, selectedChampionEsES);
                String characteristicComparisonResultEnUS = processCharacteristicComparison(gameSession, round, isGameEnded, "en_US", mysteriousChampionEnUS, selectedChampionEnUS);
                
                // Caso o jogo tenha acabado, revela todas as características restantes
                if (isGameEnded) {
                    revealRemainingCharacteristics(gameSession, round, mysteriousChampionPtBR, "pt_BR");
                    revealRemainingCharacteristics(gameSession, round, mysteriousChampionEsES, "es_ES");
                    revealRemainingCharacteristics(gameSession, round, mysteriousChampionEnUS, "en_US");
                    revealRemainingCorrectsCharacteristics(gameSession, round);
                }
                // Salva os status da comparação de características
                GameSession.addSelectedChampionCharacteristicStatus(gameSession, characteristicComparisonResultPtBR);
                
                // Adiciona mensagens ao chat (para os 3 idiomas)
                String gameMessagePtBR = getGameMessagePtBR(gameSession, isGameEnded, round, characteristicComparisonResultPtBR, mysteriousChampionPtBR, selectedChampionPtBR);
                GameSession.addChatMessage(request, selectedChampionName, false, "pt_BR");
                GameSession.addChatMessage(request, gameMessagePtBR, true, "pt_BR");
                
                String gameMessageEsES = getGameMessageEsES(isGameEnded, round, characteristicComparisonResultEsES, mysteriousChampionEsES, selectedChampionEsES);
                GameSession.addChatMessage(request, selectedChampionName, false, "es_ES");
                GameSession.addChatMessage(request, gameMessageEsES, true, "es_ES");
                
                String gameMessageEnUS = getGameMessageEnUS(isGameEnded, round, characteristicComparisonResultEnUS, mysteriousChampionEnUS, selectedChampionEnUS);
                GameSession.addChatMessage(request, selectedChampionName, false, "en_US");
                GameSession.addChatMessage(request, gameMessageEnUS, true, "en_US");
                
                // Atualiza a sessão e a resposta com o status atual do jogo
                gameSession.setAttribute("is_game_ended", isGameEnded);
                compareCharacteristicsResponse.put("is_game_ended", isGameEnded);
                
                gameSession.setAttribute("player_wins", playerWins);
                compareCharacteristicsResponse.put("player_wins", playerWins);
                
                // Atualizando a contagem de rounds
                round++;
                gameSession.setAttribute("round", round);
                compareCharacteristicsResponse.put("round", round);
                
                // Atualiza o status geral da partida (em texto)
                String actualGameStatus = getActualGameStatus(round, playerWins, language);
                compareCharacteristicsResponse.put("actual_game_status", actualGameStatus);
                
                // Adiciona as mensagens e características específicas com base no idioma
                if (language.equals("pt_BR")) {
                    compareCharacteristicsResponse.put("game_message", gameMessagePtBR);
                    compareCharacteristicsResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_pt_br"));
                    compareCharacteristicsResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_pt_br"));
                } else if (language.equals("es_ES")) {
                    compareCharacteristicsResponse.put("game_message", gameMessageEsES);
                    compareCharacteristicsResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_es_es"));
                    compareCharacteristicsResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_es_es"));
                } else {
                    compareCharacteristicsResponse.put("game_message", gameMessageEnUS);
                    compareCharacteristicsResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_en_us"));
                    compareCharacteristicsResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_en_us"));
                }
                
                // Se o jogo acabou, retorna também o splash art do campeão misterioso
                if ((boolean) gameSession.getAttribute("is_game_ended") == true) {
                    compareCharacteristicsResponse.put("mysterious_champion_splash_art_path", (String) gameSession.getAttribute("mysterious_champion_splash_art_path"));
                }
                            
                compareCharacteristicsResponse.put("last_selected_champion_splash_art_path", (String) gameSession.getAttribute("last_selected_champion_splash_art_path"));
                compareCharacteristicsResponse.put("selected_champions_characteristics_status", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_status"));
                compareCharacteristicsResponse.put("are_characteristics_compared", true);
                compareCharacteristicsResponse.put("message", "As características foram comparadas com sucesso.");
            } else {
                compareCharacteristicsResponse.put("are_characteristics_compared", false);
                compareCharacteristicsResponse.put("message", "Falha ao comparar características. Não foi possível identificar o 'nome' e nem a 'key' do personagem selecionado.");
            }
        } catch(Exception e) {
            compareCharacteristicsResponse.put("are_characteristics_compared", false);
            compareCharacteristicsResponse.put("message", "Erro inesperado: " + e.getMessage());            
        }
        
        response.getWriter().write(compareCharacteristicsResponse.toString());
    }
    
    /**
     * Compara características do campeão escolhido com o misterioso e atualiza a sessão com as características reveladas
     */
    public static String processCharacteristicComparison(HttpSession gameSession, int round, boolean isGameEnded, String language, JSONObject mysteriousChampion, JSONObject selectedChampion) {
        String characteristicComparisonResult = "";
        String SelectedChampionSplashArt = selectedChampion.getString("splash_art_path");
        
        GameSession.updateLastSelectedChampionSplashArtPath(gameSession, SelectedChampionSplashArt);
        
        switch (round) {
            case 1:
                String revealedMysteriousChampionGender = mysteriousChampion.getString("gender");
                String selectedChampionGender = selectedChampion.getString("gender");
                //
                GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionGender, language);
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionGender, language);
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                break;
            case 2:
                String revealedMysteriousChampionLane = mysteriousChampion.getString("lane");
                String selectedChampionLane = selectedChampion.getString("lane");
                //
                GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionLane, language);
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionLane, language);
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                break;
            case 3:
                String revealedMysteriousChampionRegion = mysteriousChampion.getString("region");
                String selectedChampionRegion = selectedChampion.getString("region");
                //
                GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionRegion, language);
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionRegion, language);
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                break;
            case 4:
                String revealedMysteriousChampionResource = mysteriousChampion.getString("resource");
                String selectedChampionResource = selectedChampion.getString("resource");
                //
                GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionResource, language);
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionResource, language);
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                break;
            case 5:
                String revealedMysteriousChampionFunctions = mysteriousChampion.getString("functions");
                String selectedChampionFunctions = selectedChampion.getString("functions");
                //
                GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionFunctions, language);
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionFunctions, language);
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                break;
            case 6:
                String revealedMysteriousChampionRangeType = mysteriousChampion.getString("range_type");
                String selectedChampionRangeType = selectedChampion.getString("range_type");
                //
                GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionRangeType, language);
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                break;
            case 7:
                String revealedMysteriousChampionSkinsCount = String.valueOf(mysteriousChampion.getInt("skins_count"));
                String selectedChampionSkinsCount = String.valueOf(selectedChampion.getInt("skins_count"));
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                //
                if (characteristicComparisonResult.equals("greater than")) {
                    GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionSkinsCount + " <i class='bi bi-arrow-up'></i>", language);
                } else if (characteristicComparisonResult.equals("less than")) {
                    GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionSkinsCount + " <i class='bi bi-arrow-down'></i>", language);
                } else {
                    GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionSkinsCount, language);
                }
                
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);
                break;
            case 8:
                String revealedMysteriousChampionReleaseYear = String.valueOf(mysteriousChampion.getInt("release_year"));
                String selectedChampionReleaseYear = String.valueOf(selectedChampion.getInt("release_year"));
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                //
                if (characteristicComparisonResult.equals("greater than")) {
                    GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionReleaseYear + " <i class='bi bi-arrow-up'></i>", language);
                } else if (characteristicComparisonResult.equals("less than")) {
                    GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionReleaseYear + " <i class='bi bi-arrow-down'></i>", language);
                } else {
                    GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionReleaseYear, language);
                }
                
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);
                break;
            case 9:
                String revealedMysteriousChampionPassiveName = mysteriousChampion.getString("passive_name");
                String selectedChampionPassiveName = selectedChampion.getString("passive_name");
                //
                GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionPassiveName, language);
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionPassiveName, language);
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                break;
            case 10:
                String revealedMysteriousChampionUltimateName = mysteriousChampion.getString("ultimate_name");
                String selectedChampionUltimateName = selectedChampion.getString("ultimate_name");
                //
                GameSession.addSelectedChampionCharacteristic(gameSession, selectedChampionUltimateName, language);
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimateName, language);
                
                characteristicComparisonResult = getCharacteristicComparisonStatus(round, mysteriousChampion, selectedChampion);
                break;
        }
        
        return characteristicComparisonResult;
    }
    
    /**
     * Revela características restantes do campeão misterioso e atualiza as características da sessão
     */  
    public static void revealRemainingCharacteristics(HttpSession gameSession, int round, JSONObject mysteriousChampion, String language) {
        String revealedMysteriousChampionLane = mysteriousChampion.getString("lane");
        String revealedMysteriousChampionRegion = mysteriousChampion.getString("region");
        String revealedMysteriousChampionResource = mysteriousChampion.getString("resource");
        String revealedMysteriousChampionFunctions = mysteriousChampion.getString("functions");
        String revealedMysteriousChampionRangeType = mysteriousChampion.getString("range_type");
        String revealedMysteriousChampionSkinsCount = String.valueOf(mysteriousChampion.getInt("skins_count"));
        String revealedMysteriousChampionReleaseYear = String.valueOf(mysteriousChampion.getInt("release_year"));
        String revealedMysteriousChampionPassive = mysteriousChampion.getString("passive_name");
        String revealedMysteriousChampionUltimate = mysteriousChampion.getString("ultimate_name");
        
        switch(round) {
            case 1:
                // Rota / Lane
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionLane, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionLane, language);

                // Região
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionRegion, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionRegion, language);                    

                // Recurso
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionResource, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionResource, language);

                // Functions
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionFunctions, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionFunctions, language);

                // Tipo de alcance
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);

                // Quantidade de skins
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);

                // Ano de lançamento
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);

                // Passiva
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);

                // Ultimate
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                break;
            case 2:
                // Região
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionRegion, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionRegion, language);

                // Recurso
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionResource, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionResource, language);

                // Functions
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionFunctions, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionFunctions, language);

                // Tipo de alcance
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);

                // Quantidade de skins
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);

                // Ano de lançamento
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);

                // Passiva
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);

                // Ultimate
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                break;
            case 3:
                // Recurso
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionResource, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionResource, language);

                // Functions
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionFunctions, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionFunctions, language);

                // Tipo de alcance
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);

                // Quantidade de skins
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);

                // Ano de lançamento
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);

                // Passiva
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);

                // Ultimate
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                break;
            case 4:
                // Functions
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionFunctions, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionFunctions, language);

                // Tipo de alcance
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);

                // Quantidade de skins
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);

                // Ano de lançamento
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);

                // Passiva
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);

                // Ultimate
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                break;
            case 5:
                // Tipo de alcance
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionRangeType, language);

                // Quantidade de skins
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);

                // Ano de lançamento
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);

                // Passiva
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);

                // Ultimate
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                break;
            case 6:
                // Quantidade de skins
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionSkinsCount, language);

                // Ano de lançamento
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);

                // Passiva
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);

                // Ultimate
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                break;
            case 7:
                // Ano de lançamento
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionReleaseYear, language);

                // Passiva
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);

                // Ultimate
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                break;
            case 8:
                // Passiva
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionPassive, language);

                // Ultimate
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                break;
            case 9:
                // Ultimate
                GameSession.addRevealedMysteriousChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                GameSession.addSelectedChampionCharacteristic(gameSession, revealedMysteriousChampionUltimate, language);
                break;
        }
    }
    
    /**
     * Adiciona o status "correct" para as características restantes
     */
    public static void revealRemainingCorrectsCharacteristics(HttpSession gameSession, int round) {
        // Ex: Se for o round 1 as outras 9 características serão reveladas como corretas
        for (int i = 0; i < (10 - round); i++) {
            GameSession.addSelectedChampionCharacteristicStatus(gameSession, "correct");
        }
    }
    
    /**
     * Retorna uma característica do campeão em todos os idiomas
     */
    public static ArrayList<String> getSelectedChampionCharacteristicInAllLanguages(JSONArray selectedChampionInAllLanguages, String characteristic) {
        ArrayList<String> selectedChampionsCharacteristicInAllLanguages = new ArrayList<>();
        
        String selectedChampionCharacteristicsPtBR = selectedChampionInAllLanguages.getJSONObject(0).getString(characteristic);
        String selectedChampionCharacteristicsEsES = selectedChampionInAllLanguages.getJSONObject(1).getString(characteristic);
        String selectedChampionCharacteristicsEnUS = selectedChampionInAllLanguages.getJSONObject(2).getString(characteristic);

        selectedChampionsCharacteristicInAllLanguages.add(selectedChampionCharacteristicsPtBR);
        selectedChampionsCharacteristicInAllLanguages.add(selectedChampionCharacteristicsEsES);
        selectedChampionsCharacteristicInAllLanguages.add(selectedChampionCharacteristicsEnUS);
        
        return selectedChampionsCharacteristicInAllLanguages;
    }
    
    /**
     * Compara as características do campeão selecionado com o campeão misterioso
     */
    public static String getCharacteristicComparisonStatus(int round, JSONObject mysteriousChampion, JSONObject selectedChampion) {
        if (round == 0 || round > 10) {
            throw new IllegalArgumentException("Erro ao comparar características! O round fornecido não é suportado. Round fornecido: " + round);
        }
        
        switch(round) {
            case 1:
                String mysteriousChampionGender = mysteriousChampion.getString("gender");
                String selectedChampionGender = selectedChampion.getString("gender");
                
                return getTextCharacteristicComparisonStatus(mysteriousChampionGender, selectedChampionGender);
            case 2:
                String mysteriousChampionLane = mysteriousChampion.getString("lane");
                String selectedChampionLane = selectedChampion.getString("lane");
                
                return getTextCharacteristicComparisonStatus(mysteriousChampionLane, selectedChampionLane);
            case 3:
                String mysteriousChampionRegion = mysteriousChampion.getString("region");
                String selectedChampionRegion = selectedChampion.getString("region");
                
                return getTextCharacteristicComparisonStatus(mysteriousChampionRegion, selectedChampionRegion);
            case 4:
                String mysteriousChampionResource = mysteriousChampion.getString("resource");
                String selectedChampionResource = selectedChampion.getString("resource");
                
                return getTextCharacteristicComparisonStatus(mysteriousChampionResource, selectedChampionResource);
            case 5:
                String mysteriousChampionFunctions = mysteriousChampion.getString("functions");
                String selectedChampionFunctions = selectedChampion.getString("functions");
                
                return getTextCharacteristicComparisonStatus(mysteriousChampionFunctions, selectedChampionFunctions);
            case 6:
                String mysteriousChampionRangeType = mysteriousChampion.getString("range_type");
                String selectedChampionRangeType = selectedChampion.getString("range_type");
                
                return getTextCharacteristicComparisonStatus(mysteriousChampionRangeType, selectedChampionRangeType);
            case 7:
                int mysteriousChampionSkinsCount = mysteriousChampion.getInt("skins_count");
                int selectedChampionSkinsCount = selectedChampion.getInt("skins_count");
                
                return getNumericCharacteristicComparisonStatus(mysteriousChampionSkinsCount, selectedChampionSkinsCount);
            case 8:
                int mysteriousChampionReleaseYear = mysteriousChampion.getInt("release_year");
                int selectedChampionReleaseYear = selectedChampion.getInt("release_year");
                
                return getNumericCharacteristicComparisonStatus(mysteriousChampionReleaseYear, selectedChampionReleaseYear);
            case 9:
                String mysteriousChampionPassiveName = mysteriousChampion.getString("passive_name");
                String selectedChampionPassiveName = selectedChampion.getString("passive_name");
                
                return getTextCharacteristicComparisonStatus(mysteriousChampionPassiveName, selectedChampionPassiveName);
            case 10:
                String mysteriousChampionUltimateName = mysteriousChampion.getString("ultimate_name");
                String selectedChampionUltimateName = selectedChampion.getString("ultimate_name");
                
                return getTextCharacteristicComparisonStatus(mysteriousChampionUltimateName, selectedChampionUltimateName);
        }
        
        throw new RuntimeException("Erro inesperado ao comparar características. Round fornecido: " + round);
    }
    
    /**
     * Compara as características de texto entre o campeão misterioso e o selecionado
     */
    public static String getTextCharacteristicComparisonStatus(String mysteriousChampionCharacteristic, String selectedChampionCharacteristic) {
        // Se ambas as strings forem exatamente iguais, já retorna "correct"
        if (mysteriousChampionCharacteristic.equals(selectedChampionCharacteristic)) {
            return "correct";
        }

        // Quebra as strings em conjuntos (caso tenham múltiplos valores separados por " / ")
        Set<String> mysteriousSet = new HashSet<>(Arrays.asList(mysteriousChampionCharacteristic.split(" / ")));
        Set<String> selectedSet = new HashSet<>(Arrays.asList(selectedChampionCharacteristic.split(" / ")));

        // Verifica se há interseção entre os conjuntos
        mysteriousSet.retainAll(selectedSet);
        
        // Há pelo menos um elemento em comum
        if (!mysteriousSet.isEmpty()) {
            return "parcial";
        }
        
        return "incorrect";
    }
    
    /**
     * Compara as características numéricas entre o campeão misterioso e o selecionado
     */
    public static String getNumericCharacteristicComparisonStatus(int mysteriousChampionCharacteristic, int selectedChampionCharacteristic) {
        if (mysteriousChampionCharacteristic == selectedChampionCharacteristic) {
            return "correct";
        } else {
            if (mysteriousChampionCharacteristic > selectedChampionCharacteristic) {
                return "greater than";
            } else {
                return "less than";
            }
        }
    }
    
    /**
     * Gera a mensagem do jogo em português (PT-BR) com base no estado atual
     */
    public static String getGameMessagePtBR(HttpSession gameSession, boolean isGameEnded, int round, String characteristicComparisonResult, JSONObject mysteriousChampion, JSONObject selectedChampion) {
        if (round > 10) {
            throw new IllegalArgumentException("Erro ao capturar mensagem do jogo! O round fornecido não é suportado. Round fornecido: " + round);
        }
        if (!characteristicComparisonResult.equals("correct") && !characteristicComparisonResult.equals("incorrect") && !characteristicComparisonResult.equals("parcial") && !characteristicComparisonResult.equals("greater than") && !characteristicComparisonResult.equals("less than")) {
            throw new IllegalArgumentException("Erro ao capturar mensagem do jogo! O resultado de comparação de características fornecido não é suportado. Resultado fornecido: " + characteristicComparisonResult);
        }
        
        String mysteriousChampionName = mysteriousChampion.getString("name");
        String selectedChampionName = selectedChampion.getString("name");
        
        if (isGameEnded) {
            List<String> phraseStartList = Arrays.asList(
                "De primeira! Você acertou!",
                "Ótimo trabalho! Acertou no segundo palpite!",
                "Mandou bem! Terceira tentativa e já conseguiu!",
                "Boa! Acertou na quarta tentativa!",
                "Na metade do caminho! Você acertou na quinta tentativa!",
                "Foi por pouco! Sexta tentativa e você acertou!",
                "Quase no limite! Acertou no sétimo palpite!",
                "Demorou, mas valeu! Oitava tentativa certa!",
                "No sufoco! Você acertou na nona tentativa!",
                "Foi no limite! Você acertou na última tentativa!"
            );
            
            String mysteriousChampionTitle = mysteriousChampion.getString("title");
            return phraseStartList.get(round-1) + " O campeão em que eu estava pensando era <span>" + mysteriousChampionName + "</span>, <span>" + mysteriousChampionTitle + "</span>. Parabéns!";
        }

        String phraseStart = "";

        if (characteristicComparisonResult.equals("correct")) {
            phraseStart = "Boa tentativa! Assim como <span>" + selectedChampionName + "</span>, o campeão que estou pensando ";
        } else if (characteristicComparisonResult.equals("incorrect")) {
            phraseStart = "Não exatamente! Diferente de <span>" + selectedChampionName + "</span>, o campeão que estou pensando ";
        } else if (characteristicComparisonResult.equals("parcial")) {
            phraseStart = "Quase lá! O campeão que estou pensando ";
        } else if (characteristicComparisonResult.equals("greater than") || characteristicComparisonResult.equals("less than")) {
            phraseStart = "Errado! O campeão que estou pensando ";
        }

        switch(round) {
            case 1:
                String mysteriousChampionGender = mysteriousChampion.getString("gender");
                return phraseStart + "é do gênero <span>" + mysteriousChampionGender + "</span>.";

            case 2:
                String mysteriousChampionLane = mysteriousChampion.getString("lane");

                if (mysteriousChampionLane.split(" / ").length == 1) {
                    return phraseStart + "costuma estar presente na rota <span>" + mysteriousChampionLane + "</span>.";
                } else {
                    return phraseStart + "costuma estar presente nas rotas <span>" + mysteriousChampionLane + "</span>.";
                }

            case 3:
                String mysteriousChampionRegion = mysteriousChampion.getString("region");

                if (characteristicComparisonResult.equals("correct")) {
                    return phraseStart + "também vem de <span>" + mysteriousChampionRegion + "</span>.";
                } else {
                    return phraseStart + "vem de <span>" + mysteriousChampionRegion + "</span>.";
                }

            case 4:
                String mysteriousChampionResource = mysteriousChampion.getString("resource");

                if (characteristicComparisonResult.equals("correct")) {
                    return phraseStart + "também usa <span>" + mysteriousChampionResource + "</span> como recurso.";
                } else {
                    return phraseStart + "usa <span>" + mysteriousChampionResource + "</span> como recurso.";
                }

            case 5:
                String mysteriousChampionFunctions = mysteriousChampion.getString("functions");

                if (mysteriousChampionFunctions.split(" / ").length == 1) {
                    return phraseStart + "faz parte da função <span>" + mysteriousChampionFunctions + "</span>.";
                } else {
                    return phraseStart + "faz parte das funções <span>" + mysteriousChampionFunctions + "</span>.";
                }

            case 6:
                String mysteriousChampionRangeType = mysteriousChampion.getString("range_type");
                return phraseStart + "ataca a <span>" + mysteriousChampionRangeType + "</span>.";

            case 7:
                int mysteriousChampionSkinsCount = mysteriousChampion.getInt("skins_count");
                int selectedChampionSkinsCount = selectedChampion.getInt("skins_count");
                String characteristicSkinsCountComparisonResult = characteristicComparisonResult;

                GameSession.normalizeComparisonResult(gameSession);

                if (characteristicSkinsCountComparisonResult.equals("greater than")) {
                    return phraseStart + "possui mais que <span>" + selectedChampionSkinsCount + "</span> skins.";
                } else if (characteristicSkinsCountComparisonResult.equals("less than")) {
                    return phraseStart + "possui menos que <span>" + selectedChampionSkinsCount + "</span> skins.";
                } else {
                    return "possui <span>" + mysteriousChampionSkinsCount + "</span> skins.";
                }

            case 8:
                int mysteriousChampionReleaseYear = mysteriousChampion.getInt("release_year");
                int selectedChampionReleaseYear = selectedChampion.getInt("release_year");
                String characteristicReleaseYearComparisonResult = characteristicComparisonResult;

                GameSession.normalizeComparisonResult(gameSession);

                if (characteristicReleaseYearComparisonResult.equals("greater than")) {
                    return phraseStart + "foi lançado depois do ano de <span>" + selectedChampionReleaseYear + "</span>.";
                } else if (characteristicReleaseYearComparisonResult.equals("less than")) {
                    return phraseStart + "foi lançado antes do ano de <span>" + selectedChampionReleaseYear + "</span>.";
                } else {
                    return phraseStart + "foi lançado no ano de <span>" + mysteriousChampionReleaseYear + "</span>.";
                }

            case 9:
                String mysteriousChampionPassiveName = mysteriousChampion.getString("passive_name");
                return phraseStart + "possui a passiva <span>" + mysteriousChampionPassiveName + "</span>.";

            case 10:
                String mysteriousChampionUltimateName = mysteriousChampion.getString("ultimate_name");
                return phraseStart + "possui a ultimate <span>" + mysteriousChampionUltimateName + "</span>.";
        }
        
        throw new RuntimeException("Erro inesperado ao capturar a mensagem do jogo. Round fornecido: " + round + " - Idioma: pt_BR");
    }
    
    /**
     * Gera a mensagem do jogo em português (ES-ES) com base no estado atual
     */
    public static String getGameMessageEsES(boolean isGameEnded, int round, String characteristicComparisonResult, JSONObject mysteriousChampion, JSONObject selectedChampion) {
        if (round > 10) {
            throw new IllegalArgumentException("Erro ao capturar mensagem do jogo! O round fornecido não é suportado. Round fornecido: " + round);
        }
        if (!characteristicComparisonResult.equals("correct") && !characteristicComparisonResult.equals("incorrect") && !characteristicComparisonResult.equals("parcial") && !characteristicComparisonResult.equals("greater than") && !characteristicComparisonResult.equals("less than")) {
            throw new IllegalArgumentException("Erro ao capturar mensagem do jogo! O resultado de comparação de características fornecido não é suportado. Resultado fornecido: " + characteristicComparisonResult);
        }

        String mysteriousChampionName = mysteriousChampion.getString("name");
        String selectedChampionName = selectedChampion.getString("name");
        
        if (isGameEnded) {
            List<String> phraseStartList = Arrays.asList(
                "¡A la primera! ¡Lo lograste!",
                "¡Gran trabajo! ¡Lo adivinaste en el segundo intento!",
                "¡Bien hecho! ¡Tercera vez y lo conseguiste!",
                "¡Genial! ¡Lo lograste en el cuarto intento!",
                "¡A mitad del camino! ¡Lo adivinaste en el quinto intento!",
                "¡Por poco! ¡Sexto intento y lo lograste!",
                "¡Casi al límite! ¡Lo conseguiste en el séptimo intento!",
                "¡Tardaste, pero lo lograste! ¡Octavo intento correcto!",
                "¡Bajo presión! ¡Lo adivinaste en el noveno intento!",
                "¡En el último momento! ¡Lo lograste en el último intento!"
            );

            String mysteriousChampionTitle = mysteriousChampion.getString("title");
            return phraseStartList.get(round-1) + " El campeón en el que estaba pensando era <span>" + mysteriousChampionName + "</span>, <span>" + mysteriousChampionTitle + "</span>. ¡Felicidades!";
        }
        
        String phraseStart = "";
        
        if (characteristicComparisonResult.equals("correct")) {
            phraseStart = "¡Buen intento! Al igual que <span>" + selectedChampionName + "</span>, el campeón en el que estoy pensando ";
        } else if (characteristicComparisonResult.equals("incorrect")) {
            phraseStart = "¡No exactamente! A diferencia de <span>" + selectedChampionName + "</span>, el campeón en el que estoy pensando ";
        } else if (characteristicComparisonResult.equals("parcial")) {
            phraseStart = "¡Casi ahí! El campeón en el que estoy pensando ";
        } else if (characteristicComparisonResult.equals("greater than") || characteristicComparisonResult.equals("less than")) {
            phraseStart = "¡Incorrecto! El campeón en el que estoy pensando ";
        }

        switch(round) {
            case 1:
                String mysteriousChampionGender = mysteriousChampion.getString("gender");
                return phraseStart + "pertenece al género <span>" + mysteriousChampionGender + "</span>.";
            case 2:
                String mysteriousChampionLane = mysteriousChampion.getString("lane");

                if (mysteriousChampionLane.split(" / ").length == 1) {
                    return phraseStart + "suele estar en la línea <span>" + mysteriousChampionLane + "</span>.";
                } else {
                    return phraseStart + "suele estar en las líneas <span>" + mysteriousChampionLane + "</span>.";
                }
            case 3:
                String mysteriousChampionRegion = mysteriousChampion.getString("region");

                if (characteristicComparisonResult.equals("correct")) {
                    return phraseStart + "también proviene de <span>" + mysteriousChampionRegion + "</span>.";
                } else {
                    return phraseStart + "proviene de <span>" + mysteriousChampionRegion + "</span>.";
                }
            case 4:
                String mysteriousChampionResource = mysteriousChampion.getString("resource");

                if (characteristicComparisonResult.equals("correct")) {
                    return phraseStart + "también usa <span>" + mysteriousChampionResource + "</span> como recurso.";
                } else {
                    return phraseStart + "usa <span>" + mysteriousChampionResource + "</span> como recurso.";
                }
            case 5:
                String mysteriousChampionFunctions = mysteriousChampion.getString("functions");

                if (mysteriousChampionFunctions.split(" / ").length == 1) {
                    return phraseStart + "tiene el rol de <span>" + mysteriousChampionFunctions + "</span>.";
                } else {
                    return phraseStart + "tiene los roles de <span>" + mysteriousChampionFunctions + "</span>.";
                }
            case 6:
                String mysteriousChampionRangeType = mysteriousChampion.getString("range_type");
                return phraseStart + "ataca a <span>" + mysteriousChampionRangeType + "</span> de distancia.";
            case 7:
                int mysteriousChampionSkinsCount = mysteriousChampion.getInt("skins_count");
                int selectedChampionSkinsCount = selectedChampion.getInt("skins_count");

                if (characteristicComparisonResult.equals("greater than")) {
                    return phraseStart + "tiene más de <span>" + selectedChampionSkinsCount + "</span> aspectos.";
                } else if (characteristicComparisonResult.equals("less than")) {
                    return phraseStart + "tiene menos de <span>" + selectedChampionSkinsCount + "</span> aspectos.";
                } else {
                    return "tiene <span>" + mysteriousChampionSkinsCount + "</span> aspectos.";
                }
            case 8:
                int mysteriousChampionReleaseYear = mysteriousChampion.getInt("release_year");
                int selectedChampionReleaseYear = selectedChampion.getInt("release_year");

                if (characteristicComparisonResult.equals("greater than")) {
                    return phraseStart + "fue lanzado después del año <span>" + selectedChampionReleaseYear + "</span>.";
                } else if (characteristicComparisonResult.equals("less than")) {
                    return phraseStart + "fue lanzado antes del año <span>" + selectedChampionReleaseYear + "</span>.";
                } else {
                    return "fue lanzado en el año <span>" + mysteriousChampionReleaseYear + "</span>.";
                }
            case 9:
                String mysteriousChampionPassiveName = mysteriousChampion.getString("passive_name");
                return phraseStart + "tiene la pasiva <span>" + mysteriousChampionPassiveName + "</span>.";
            case 10:
                String mysteriousChampionUltimateName = mysteriousChampion.getString("ultimate_name");
                return phraseStart + "tiene la definitiva <span>" + mysteriousChampionUltimateName + "</span>.";
        }
        
        throw new RuntimeException("Erro inesperado ao capturar a mensagem do jogo. Round fornecido: " + round + " - Idioma: es_ES");
    }
    
    /**
     * Gera a mensagem do jogo em português (EN-US) com base no estado atual
     */
    public static String getGameMessageEnUS(boolean isGameEnded, int round, String characteristicComparisonResult, JSONObject mysteriousChampion, JSONObject selectedChampion) {
        if (round > 10) {
            throw new IllegalArgumentException("Erro ao capturar mensagem do jogo! O round fornecido não é suportado. Round fornecido: " + round);
        }
        if (!characteristicComparisonResult.equals("correct") && !characteristicComparisonResult.equals("incorrect") && !characteristicComparisonResult.equals("parcial") && !characteristicComparisonResult.equals("greater than") && !characteristicComparisonResult.equals("less than")) {
            throw new IllegalArgumentException("Erro ao capturar mensagem do jogo! O resultado de comparação de características fornecido não é suportado. Resultado fornecido: " + characteristicComparisonResult);
        }

        String mysteriousChampionName = mysteriousChampion.getString("name");
        String selectedChampionName = selectedChampion.getString("name");
        
        if (isGameEnded) {
            List<String> phraseStartList = Arrays.asList(
                "First try! You got it!",
                "Great job! You got it on the second guess!",
                "Well done! Third attempt and you nailed it!",
                "Nice! You got it on the fourth try!",
                "Halfway there! You guessed it on the fifth attempt!",
                "So close! Sixth try and you got it!",
                "Almost at the limit! You got it on the seventh guess!",
                "It took a while, but you did it! Eighth attempt correct!",
                "Under pressure! You got it on the ninth attempt!",
                "Right on the edge! You got it on the last try!"
            );

            String mysteriousChampionTitle = mysteriousChampion.getString("title");
            return phraseStartList.get(round-1) + " The champion I was thinking of was <span>" + mysteriousChampionName + "</span>, <span>" + mysteriousChampionTitle + "</span>. Congratulations!";
        }
        
        String phraseStart = "";
        
        if (characteristicComparisonResult.equals("correct")) {
            phraseStart = "Good try! Just like <span>" + selectedChampionName + "</span>, the champion I'm thinking of ";
        } else if (characteristicComparisonResult.equals("incorrect")) {
            phraseStart = "Not exactly! Unlike <span>" + selectedChampionName + "</span>, the champion I'm thinking of ";
        } else if (characteristicComparisonResult.equals("parcial")) {
            phraseStart = "Almost there! The champion I'm thinking of ";
        } else if (characteristicComparisonResult.equals("greater than") || characteristicComparisonResult.equals("less than")) {
            phraseStart = "Wrong! The champion I'm thinking of ";
        }

        switch(round) {
            case 1:
                String mysteriousChampionGender = mysteriousChampion.getString("gender");
                return phraseStart + "belongs to the <span>" + mysteriousChampionGender + "</span> gender.";
            case 2:
                String mysteriousChampionLane = mysteriousChampion.getString("lane");
                if (mysteriousChampionLane.split(" / ").length == 1) {
                    return phraseStart + "is usually found in the <span>" + mysteriousChampionLane + "</span> lane.";
                } else if (mysteriousChampionLane.split(" / ").length > 1) {
                    return phraseStart + "is usually found in the <span>" + mysteriousChampionLane + "</span> lanes.";
                }
                break;
            case 3:
                String mysteriousChampionRegion = mysteriousChampion.getString("region");
                if (characteristicComparisonResult.equals("correct")) {
                    return phraseStart + "also comes from <span>" + mysteriousChampionRegion + "</span>.";
                } else if (characteristicComparisonResult.equals("incorrect")) {
                    return phraseStart + "comes from <span>" + mysteriousChampionRegion + "</span>.";
                }
                break;
            case 4:
                String mysteriousChampionResource = mysteriousChampion.getString("resource");
                if (characteristicComparisonResult.equals("correct")) {
                    return phraseStart + "also uses <span>" + mysteriousChampionResource + "</span> as a resource.";
                } else if (characteristicComparisonResult.equals("incorrect")) {
                    return phraseStart + "uses <span>" + mysteriousChampionResource + "</span> as a resource.";
                }
                break;
            case 5:
                String mysteriousChampionFunctions = mysteriousChampion.getString("functions");
                if (mysteriousChampionFunctions.split(" / ").length == 1) {
                    return phraseStart + "has the role of <span>" + mysteriousChampionFunctions + "</span>.";
                } else if (mysteriousChampionFunctions.split(" / ").length > 1) {
                    return phraseStart + "has the roles of <span>" + mysteriousChampionFunctions + "</span>.";
                }
                break;
            case 6:
                String mysteriousChampionRangeType = mysteriousChampion.getString("range_type");
                return phraseStart + "attacks at <span>" + mysteriousChampionRangeType + "</span> range.";
            case 7:
                int mysteriousChampionSkinsCount = mysteriousChampion.getInt("skins_count");
                int selectedChampionSkinsCount = selectedChampion.getInt("skins_count");
                if (characteristicComparisonResult.equals("greater than")) {
                    return phraseStart + "has more than <span>" + selectedChampionSkinsCount + "</span> skins.";
                } else if (characteristicComparisonResult.equals("less than")) {
                    return phraseStart + "has fewer than <span>" + selectedChampionSkinsCount + "</span> skins.";
                } else {
                    return "has <span>" + mysteriousChampionSkinsCount + "</span> skins.";
                }
            case 8:
                int mysteriousChampionReleaseYear = mysteriousChampion.getInt("release_year");
                int selectedChampionReleaseYear = selectedChampion.getInt("release_year");
                if (characteristicComparisonResult.equals("greater than")) {
                    return phraseStart + "was released after the year <span>" + selectedChampionReleaseYear + "</span>.";
                } else if (characteristicComparisonResult.equals("less than")) {
                    return phraseStart + "was released before the year <span>" + selectedChampionReleaseYear + "</span>.";
                } else {
                    return "was released in the year <span>" + mysteriousChampionReleaseYear + "</span>.";
                }
            case 9:
                String mysteriousChampionPassiveName = mysteriousChampion.getString("passive_name");
                return phraseStart + "has the passive ability <span>" + mysteriousChampionPassiveName + "</span>.";
            case 10:
                String mysteriousChampionUltimateName = mysteriousChampion.getString("ultimate_name");
                return phraseStart + "has the ultimate ability <span>" + mysteriousChampionUltimateName + "</span>.";
        }
        
        throw new RuntimeException("Erro inesperado ao capturar a mensagem do jogo. Round fornecido: " + round + " - Idioma: en_US");
    }
}