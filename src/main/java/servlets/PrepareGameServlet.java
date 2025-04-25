package servlets;

import apis.DataDragonVersion;
import apis.WhoIsThatChampionData;
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
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.GameSession;

@WebServlet(name = "PrepareGameServlet", urlPatterns = {"/PrepareGameServlet"})
public class PrepareGameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject prepareGameResponse = new JSONObject();
        String language = request.getParameter("language");

        try {
            if (language != null && !language.trim().isEmpty()) {
                // Capturando a versão mais atual da API Data Dragon
                String dataDragonLatestVersion = DataDragonVersion.getLatestVersion();

                if (!dataDragonLatestVersion.trim().isEmpty()) {
                    Champions.createTable();
                    DataDragonVersions.createTable();

                    // Verificando se esta versão existe no banco de dados com os 3 idiomas suportados pelo jogo
                    int dataDragonLatestVersionPtBRId = DataDragonVersions.getVersionId(dataDragonLatestVersion, "pt_BR");
                    int dataDragonLatestVersionEsESId = DataDragonVersions.getVersionId(dataDragonLatestVersion, "es_ES");
                    int dataDragonLatestVersionEnUSId = DataDragonVersions.getVersionId(dataDragonLatestVersion, "en_US");

                    // Caso não estejam registradas no banco, cadastra esta versão no banco de dados com os 3 idiomas suportados pelo jogo
                    if (dataDragonLatestVersionPtBRId == 0 || dataDragonLatestVersionEsESId == 0 || dataDragonLatestVersionEnUSId == 0) {
                        DataDragonVersions.deleteOldVersions(dataDragonLatestVersion);

                        dataDragonLatestVersionPtBRId = DataDragonVersions.addVersion(dataDragonLatestVersion, "pt_BR");
                        dataDragonLatestVersionEsESId = DataDragonVersions.addVersion(dataDragonLatestVersion, "es_ES");
                        dataDragonLatestVersionEnUSId = DataDragonVersions.addVersion(dataDragonLatestVersion, "en_US");
                    }

                    // Verificando se a versão recente da API esta cadastrada no banco de dados
                    if (dataDragonLatestVersionPtBRId > 0 && dataDragonLatestVersionEsESId > 0 && dataDragonLatestVersionEnUSId > 0) {
                        Champions.deleteOldChampions(dataDragonLatestVersion);
                        DataDragonVersions.deleteOldVersions(dataDragonLatestVersion);
                        
                        JSONArray championsDataPtBR = Champions.getAllChampions(dataDragonLatestVersionPtBRId, "pt_BR");
                        JSONArray championsDataEsES = Champions.getAllChampions(dataDragonLatestVersionEsESId, "es_ES");
                        JSONArray championsDataEnUS = Champions.getAllChampions(dataDragonLatestVersionEnUSId, "en_US");
                        
                        // Caso os campeões não estejam registrados no banco, cadastra eles no banco de dados com os 3 idiomas suportados pelo jogo
                        if (championsDataPtBR.length() == 0 && championsDataEsES.length() == 0 && championsDataEnUS.length() == 0) {
                            JSONObject championsInAllLanguages = WhoIsThatChampionData.getAllChampionsData();
                            
                            championsDataPtBR = championsInAllLanguages.getJSONArray("pt_br");
                            championsDataEsES = championsInAllLanguages.getJSONArray("es_es");
                            championsDataEnUS = championsInAllLanguages.getJSONArray("en_us");
                            
                            // Registrando cada campeão
                            registerChampions(championsDataPtBR, dataDragonLatestVersionPtBRId);
                            registerChampions(championsDataEsES, dataDragonLatestVersionEsESId);
                            registerChampions(championsDataEnUS, dataDragonLatestVersionEnUSId);
                        }

                        HttpSession gameSession;
                        // Verificando a existência e validade de um jogo anterior
                        boolean needsToCreateGameSession = GameSession.shouldCreateGameSession(request, dataDragonLatestVersion);
                        
                        // Se não existir, é necessário criar uma session
                        if (needsToCreateGameSession) {
                            // Capturando um campeão misterioso e registrando suas informações na sessão
                            int mysteriousChampionIndex = getMysteriousChampionIndex(championsDataPtBR);

                            JSONArray mysteriousChampionInAllLanguages = new JSONArray();
                            mysteriousChampionInAllLanguages.put(championsDataPtBR.getJSONObject(mysteriousChampionIndex));
                            mysteriousChampionInAllLanguages.put(championsDataEsES.getJSONObject(mysteriousChampionIndex));
                            mysteriousChampionInAllLanguages.put(championsDataEnUS.getJSONObject(mysteriousChampionIndex));
                            
                            gameSession = GameSession.createGameSession(request, mysteriousChampionInAllLanguages, dataDragonLatestVersion, language);
                            gameSession = request.getSession(false);
                        } else {
                            gameSession = request.getSession(false);
                        }

                        if ((boolean) gameSession.getAttribute("is_game_ended") == true) {
                            prepareGameResponse.put("is_game_ready", true);
                            prepareGameResponse.put("message", "Sucesso ao preparar o ambiente. O jogo já foi encerrado.");
                            prepareGameResponse.put("mysterious_champion_splash_art_path", (String) gameSession.getAttribute("mysterious_champion_splash_art_path"));
                        } else {
                            prepareGameResponse.put("is_game_ready", true);
                            prepareGameResponse.put("message", "Sucesso ao preparar o ambiente. O jogo ainda esta em andamento.");
                        }

                        // Capturando as demais informações necessárias para que o jogo seja iniciado corretamente
                        prepareGameResponse.put("is_game_ended", (boolean) gameSession.getAttribute("is_game_ended"));
                        prepareGameResponse.put("player_wins", (boolean) gameSession.getAttribute("player_wins"));
                        prepareGameResponse.put("round", (int) gameSession.getAttribute("round"));

                        String actualGameStatus = getActualGameStatus((int) gameSession.getAttribute("round"), (boolean) gameSession.getAttribute("player_wins"), language);
                        prepareGameResponse.put("actual_game_status", actualGameStatus);
                        
                        if (language.equals("pt_BR")) {
                            prepareGameResponse.put("chat_messages", (JSONArray) gameSession.getAttribute("chat_messages_pt_br"));
                            prepareGameResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_pt_br"));
                            prepareGameResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_pt_br"));
                            prepareGameResponse.put("champions_basic_info", getChampionsBasicInfo(championsDataPtBR));
                        } else if (language.equals("es_ES")) {
                            prepareGameResponse.put("chat_messages", (JSONArray) gameSession.getAttribute("chat_messages_es_es"));
                            prepareGameResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_es_es"));
                            prepareGameResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_es_es"));
                            prepareGameResponse.put("champions_basic_info", getChampionsBasicInfo(championsDataEsES));
                        } else {
                            prepareGameResponse.put("chat_messages", (JSONArray) gameSession.getAttribute("chat_messages_en_us"));
                            prepareGameResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_en_us"));
                            prepareGameResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_en_us"));
                            prepareGameResponse.put("champions_basic_info", getChampionsBasicInfo(championsDataEnUS));
                        }

                        prepareGameResponse.put("selected_champions_characteristics_status", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_status"));
                        prepareGameResponse.put("selected_champions_keys", (ArrayList<String>) gameSession.getAttribute("selected_champions_keys"));
                        prepareGameResponse.put("last_selected_champion_splash_art_path", (String) gameSession.getAttribute("last_selected_champion_splash_art_path"));
                    } else {
                        // Se a versão mais recente da API não estiver cadastrada, ela será registrada no banco de dados
                        DataDragonVersions.deleteOldVersions(dataDragonLatestVersion);

                        dataDragonLatestVersionPtBRId = DataDragonVersions.addVersion(dataDragonLatestVersion, "pt_BR");
                        dataDragonLatestVersionEsESId = DataDragonVersions.addVersion(dataDragonLatestVersion, "es_ES");
                        dataDragonLatestVersionEnUSId = DataDragonVersions.addVersion(dataDragonLatestVersion, "en_US");
                        
                        if (dataDragonLatestVersionPtBRId > 0 && dataDragonLatestVersionEsESId > 0 && dataDragonLatestVersionEnUSId > 0) {
                            Champions.deleteOldChampions(dataDragonLatestVersion);
                            DataDragonVersions.deleteOldVersions(dataDragonLatestVersion);

                            JSONArray championsDataPtBR = Champions.getAllChampions(dataDragonLatestVersionPtBRId, "pt_BR");
                            JSONArray championsDataEsES = Champions.getAllChampions(dataDragonLatestVersionEsESId, "es_ES");
                            JSONArray championsDataEnUS = Champions.getAllChampions(dataDragonLatestVersionEnUSId, "en_US");

                            // Caso os campeões não estejam registrados no banco, cadastra eles no banco de dados com os 3 idiomas suportados pelo jogo
                            if (championsDataPtBR.length() == 0 && championsDataEsES.length() == 0 && championsDataEnUS.length() == 0) {
                                JSONObject championsInAllLanguages = WhoIsThatChampionData.getAllChampionsData();
                                
                                championsDataPtBR = championsInAllLanguages.getJSONArray("pt_br");
                                championsDataEsES = championsInAllLanguages.getJSONArray("es_es");
                                championsDataEnUS = championsInAllLanguages.getJSONArray("en_us");
                                
                                // Registrando cada campeão
                                registerChampions(championsDataPtBR, dataDragonLatestVersionPtBRId);
                                registerChampions(championsDataEsES, dataDragonLatestVersionEsESId);
                                registerChampions(championsDataEnUS, dataDragonLatestVersionEnUSId);
                            }

                            HttpSession gameSession;
                            boolean needsToCreateGameSession = GameSession.shouldCreateGameSession(request, dataDragonLatestVersion);

                            // Se não existir, é necessário criar uma session
                            if (needsToCreateGameSession) {
                                int mysteriousChampionIndex = getMysteriousChampionIndex(championsDataPtBR);

                                JSONArray mysteriousChampionInAllLanguages = new JSONArray();
                                mysteriousChampionInAllLanguages.put(championsDataPtBR.getJSONObject(mysteriousChampionIndex));
                                mysteriousChampionInAllLanguages.put(championsDataEsES.getJSONObject(mysteriousChampionIndex));
                                mysteriousChampionInAllLanguages.put(championsDataEnUS.getJSONObject(mysteriousChampionIndex));

                                gameSession = GameSession.createGameSession(request, mysteriousChampionInAllLanguages, dataDragonLatestVersion, language);
                                gameSession = request.getSession(false);
                            } else {
                                gameSession = request.getSession(false);
                            }

                            if ((boolean) gameSession.getAttribute("is_game_ended") == true) {
                                prepareGameResponse.put("mysterious_champion_splash_art_path", (String) gameSession.getAttribute("mysterious_champion_splash_art_path"));
                                prepareGameResponse.put("is_game_ready", true);
                                prepareGameResponse.put("message", "Sucesso ao preparar o ambiente. O jogo já foi encerrado.");
                                // Se o jogo estiver finalizado, o status do jogo e o caminho da splash art do campeão misterioso são capturados.
                                prepareGameResponse.put("player_wins", (boolean) gameSession.getAttribute("player_wins"));
                            } else {
                                prepareGameResponse.put("is_game_ready", true);
                                prepareGameResponse.put("message", "Sucesso ao preparar o ambiente. O jogo ainda esta em andamento.");
                            }

                            // Capturando as demais informações necessárias para que o jogo seja iniciado corretamente
                            prepareGameResponse.put("is_game_ended", (boolean) gameSession.getAttribute("is_game_ended"));
                            prepareGameResponse.put("player_wins", (boolean) gameSession.getAttribute("player_wins"));
                            prepareGameResponse.put("round", (int) gameSession.getAttribute("round"));

                            String actualGameStatus = getActualGameStatus((int) gameSession.getAttribute("round"), (boolean) gameSession.getAttribute("player_wins"), language);
                            prepareGameResponse.put("actual_game_status", actualGameStatus);
                            
                            if (language.equals("pt_BR")) {
                                prepareGameResponse.put("chat_messages", (JSONArray) gameSession.getAttribute("chat_messages_pt_br"));
                                prepareGameResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_pt_br"));
                                prepareGameResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_pt_br"));
                                prepareGameResponse.put("champions_basic_info", getChampionsBasicInfo(championsDataPtBR));
                            } else if (language.equals("es_ES")) {
                                prepareGameResponse.put("chat_messages", (JSONArray) gameSession.getAttribute("chat_messages_es_es"));
                                prepareGameResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_es_es"));
                                prepareGameResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_es_es"));
                                prepareGameResponse.put("champions_basic_info", getChampionsBasicInfo(championsDataEsES));
                            } else {
                                prepareGameResponse.put("chat_messages", (JSONArray) gameSession.getAttribute("chat_messages_en_us"));
                                prepareGameResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_en_us"));
                                prepareGameResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_en_us"));
                                prepareGameResponse.put("champions_basic_info", getChampionsBasicInfo(championsDataEnUS));
                            }

                            prepareGameResponse.put("selected_champions_characteristics_status", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_status"));
                            prepareGameResponse.put("selected_champions_keys", (ArrayList<String>) gameSession.getAttribute("selected_champions_keys"));
                            prepareGameResponse.put("last_selected_champion_splash_art_path", (String) gameSession.getAttribute("last_selected_champion_splash_art_path"));
                        } else {
                            prepareGameResponse.put("is_game_ready", false);
                            prepareGameResponse.put("message", "Falha ao prepararar o ambiente. Não foi possível cadastrar e nem identificar a última versão da API Data Dragon.");
                        }
                    }
                } else {
                    prepareGameResponse.put("is_game_ready", false);
                    prepareGameResponse.put("message", "Falha ao prepararar o ambiente. Não foi possível identificar a última versão da API Data Dragon.");
                }
            } else {
                prepareGameResponse.put("is_game_ready", false);
                prepareGameResponse.put("message", "Falha ao prepararar o ambiente. Não foi possível identificar o 'language'.");
            }
        } catch (Exception e) {
            prepareGameResponse.put("is_game_ready", false);
            prepareGameResponse.put("message", "Erro inesperado: " + e.getMessage());
        }

        response.getWriter().write(prepareGameResponse.toString());
    }

    /**
     * Retorna o índice aleatório de um campeão misterioso a partir da lista de
     * campeões disponíveis
     */
    public static int getMysteriousChampionIndex(JSONArray championsData) {
        int championsCount = championsData.length() - 1;
        int mysteriousChampionIndex = (int) (Math.random() * championsCount);

        return mysteriousChampionIndex;
    }

    /**
     * Registra todos os campeões recebidos no banco de dados, caso ainda não
     * estejam cadastrados
     */
    public static void registerChampions(JSONArray championsData, int dataDragonLatestVersionId) {
        if (championsData.isEmpty()) {
            return;
        }

        String registerChampionsLogs = "Cadastro de campeões: " + dataDragonLatestVersionId + " -";
        
        for (int i = 0; i < championsData.length(); i++) {
            JSONObject championData = championsData.getJSONObject(i);
            boolean isChampionRegistred = Champions.addChampion(championData, dataDragonLatestVersionId);
            
            registerChampionsLogs += " " + championData.getString("key") + "-" + isChampionRegistred;
        }
        
        System.out.println(registerChampionsLogs);
    }

    /**
     * Retorna o status atual do jogo com base na rodada, no resultado do
     * jogador e no idioma selecionado
     */
    public static String getActualGameStatus(int round, boolean playerWins, String language) {
        if (playerWins) {
            if (language.equals("pt_BR")) {
                return "Fim de jogo! <b>Vitória</b>";
            } else if (language.equals("es_ES")) {
                return "¡Fin del juego! <b>Victoria</b>";
            } else {
                return "Game over! <b>Victory</b>";
            }
        } else if (round == 11) {
            if (language.equals("pt_BR")) {
                return "Fim de jogo! <b>Derrota</b>";
            } else if (language.equals("es_ES")) {
                return "¡Fin del juego! <b>Derrota</b>";
            } else {
                return "Game over! <b>Defeat</b>";
            }
        }

        switch (round) {
            case 1:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Gênero</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Género</b>";
                } else {
                    return "Round: <b>Gender</b>";
                }
            case 2:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Rota / Lane</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Línea</b>";
                } else {
                    return "Round: <b>Lane</b>";
                }
            case 3:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Região</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Región</b>";
                } else {
                    return "Round: <b>Region</b>";
                }
            case 4:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Recurso</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Recurso</b>";
                } else {
                    return "Round: <b>Resource</b>";
                }
            case 5:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Funções</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Funciones</b>";
                } else {
                    return "Round: <b>Roles</b>";
                }
            case 6:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Tipo de alcance</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Tipo de alcance</b>";
                } else {
                    return "Round: <b>Range type</b>";
                }
            case 7:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Quantidade de skins</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Cantidad de aspectos</b>";
                } else {
                    return "Round: <b>Skins count</b>";
                }
            case 8:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Ano de lançamento</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Año de lanzamiento</b>";
                } else {
                    return "Round: <b>Release year</b>";
                }
            case 9:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Passiva</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Pasiva</b>";
                } else {
                    return "Round: <b>Passive</b>";
                }
            case 10:
                if (language.equals("pt_BR")) {
                    return "Rodada: <b>Ultimate</b>";
                } else if (language.equals("es_ES")) {
                    return "Ronda: <b>Definitiva</b>";
                } else {
                    return "Round: <b>Ultimate</b>";
                }
            default:
                throw new IllegalArgumentException("Erro: O round fornecido não é válido.");
        }
    }

    /**
     * Retorna uma lista com informações básicas de cada campeão, contendo apenas a chave, nome, ícone e splash_art
     */
    public static JSONArray getChampionsBasicInfo(JSONArray championsData) {
        JSONArray championsBasicInfo = new JSONArray();

        for (int i = 0; i < championsData.length(); i++) {
            JSONObject championData = championsData.getJSONObject(i);
            String championName = championData.getString("name");
            String championKey = championData.getString("key");
            String championIconPath = championData.getString("icon_path");
            String championSplashArtPath = championData.getString("splash_art_path");

            JSONObject championBasicInfo = new JSONObject();
            championBasicInfo.put("name", championName);
            championBasicInfo.put("key", championKey);
            championBasicInfo.put("icon_path", championIconPath);
            championBasicInfo.put("splash_art_path", championSplashArtPath);

            championsBasicInfo.put(championBasicInfo);
        }

        return championsBasicInfo;
    }

    @Override
    public String getServletInfo() {
        return "Servlet responsável por preparar o ambiente do jogo.";
    }
}
