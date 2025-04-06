package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Classe responsável por controlar os dados de uma sessão de jogo
 */
public class GameSession {

    /**
     * Verifica se uma nova sessão de jogo deve ser criada com base na versão
     * atual e nos dados da requisição
     */
    public static boolean shouldCreateGameSession(HttpServletRequest request, String version) {
        HttpSession gameSession = request.getSession(false);

        // Verifica se já existe uma sessão de jogo.
        if (gameSession == null) {
            return true;
        }

        Boolean isGameStarted = (Boolean) gameSession.getAttribute("is_game_started");
        if (Boolean.TRUE.equals(isGameStarted)) {
            String sessionVersion = (String) gameSession.getAttribute("version");

            // Invalida a sessão se a versão do jogo não for a mais recente.
            if (!sessionVersion.equals(version)) {
                deleteGameSession(gameSession, request);
                return true;
            }

            // A sessão é válida.
            return false;
        }

        // Se o jogo não foi iniciado, precisa criar a sessão.
        return true;
    }

    /**
     * Cria uma nova sessão de jogo e define os atributos iniciais necessários
     * para o funcionamento da partida
     */
    public static HttpSession createGameSession(HttpServletRequest request, JSONArray mysteriousChampionInAllLanguages, String version, String language) {
        HttpSession gameSession = request.getSession(true);

        JSONArray chatMessagesPtBR = new JSONArray();
        JSONArray chatMessagesEsES = new JSONArray();
        JSONArray chatMessagesEnUS = new JSONArray();
        ArrayList<String> mysteriousChampionCharacteristicsRevealedPtBR = new ArrayList<String>();
        ArrayList<String> mysteriousChampionCharacteristicsRevealedEsES = new ArrayList<String>();
        ArrayList<String> mysteriousChampionCharacteristicsRevealedEnUS = new ArrayList<String>();
        ArrayList<String> selectedChampionsCharacteristicsPtBR = new ArrayList<String>();
        ArrayList<String> selectedChampionsCharacteristicsEsES = new ArrayList<String>();
        ArrayList<String> selectedChampionsCharacteristicsEnUS = new ArrayList<String>();
        ArrayList<String> selectedChampionsKeys = new ArrayList<String>();
        ArrayList<String> selectedChampionsCharacteristicsStatus = new ArrayList<String>();

        // Definição dos atributos da sessão
        gameSession.setAttribute("is_game_started", true);
        gameSession.setAttribute("is_game_ended", false);
        gameSession.setAttribute("player_wins", false);
        gameSession.setAttribute("version", version);
        gameSession.setAttribute("round", 1);
        gameSession.setAttribute("mysterious_champion_key", mysteriousChampionInAllLanguages.getJSONObject(0).getString("key"));
        gameSession.setAttribute("mysterious_champion_splash_art_path", mysteriousChampionInAllLanguages.getJSONObject(0).getString("splash_art_path"));
        gameSession.setAttribute("mysterious_champion_pt_br", mysteriousChampionInAllLanguages.getJSONObject(0));
        gameSession.setAttribute("mysterious_champion_es_es", mysteriousChampionInAllLanguages.getJSONObject(1));
        gameSession.setAttribute("mysterious_champion_en_us", mysteriousChampionInAllLanguages.getJSONObject(2));

        // Armazena listas na sessão
        gameSession.setAttribute("mysterious_champion_characteristics_revealed_pt_br", mysteriousChampionCharacteristicsRevealedPtBR);
        gameSession.setAttribute("mysterious_champion_characteristics_revealed_es_es", mysteriousChampionCharacteristicsRevealedEsES);
        gameSession.setAttribute("mysterious_champion_characteristics_revealed_en_us", mysteriousChampionCharacteristicsRevealedEnUS);
        gameSession.setAttribute("selected_champions_characteristics_pt_br", selectedChampionsCharacteristicsPtBR);
        gameSession.setAttribute("selected_champions_characteristics_es_es", selectedChampionsCharacteristicsEsES);
        gameSession.setAttribute("selected_champions_characteristics_en_us", selectedChampionsCharacteristicsEnUS);
        gameSession.setAttribute("selected_champions_keys", selectedChampionsKeys);
        gameSession.setAttribute("selected_champions_characteristics_status", selectedChampionsCharacteristicsStatus);
        gameSession.setAttribute("last_selected_champion_splash_art_path", "");
        gameSession.setAttribute("chat_messages_pt_br", chatMessagesPtBR);
        gameSession.setAttribute("chat_messages_es_es", chatMessagesEsES);
        gameSession.setAttribute("chat_messages_en_us", chatMessagesEnUS);

        addChatMessage(request, getGameFirstMessage(request, "pt_BR"), true, "pt_BR");
        addChatMessage(request, getGameFirstMessage(request, "es_ES"), true, "es_ES");
        addChatMessage(request, getGameFirstMessage(request, "en_US"), true, "en_US");

        return gameSession;
    }

    /**
     * Retorna a primeira mensagem exibida ao jogador no início da sessão de
     * jogo, de acordo com o idioma definido
     */
    public static String getGameFirstMessage(HttpServletRequest request, String language) {
        if (language.equals("pt_BR")) {
            return "Estou pronto, vamos lá! Em que campeão estou pensando?";
        } else if (language.equals("es_ES")) {
            return "¡Estoy listo, vamos! ¿En qué campeón estoy pensando?";
        } else if (language.equals("en_US")) {
            return "I'm ready, let's go! Which champion am I thinking of?";
        }

        throw new IllegalArgumentException("Erro ao capturar a primeira mensagem do jogo! Os únicos idiomas disponíveis no momento são: pt_BR, es_ES e en_US. Idioma solicitado: " + language);
    }

    /**
     * Adiciona uma nova mensagem ao histórico do chat da sessão, podendo ser
     * uma mensagem do jogo ou do usuário
     */
    public static void addChatMessage(HttpServletRequest request, String messageText, boolean isGameMessage, String language) {
        HttpSession gameSession = request.getSession(false);
        if (gameSession == null) {
            throw new IllegalStateException("Erro ao adicionar mensagem do chat! Sessão inválida ou expirada.");
        }

        JSONObject message = new JSONObject();
        message.put("is_game_message", isGameMessage);
        message.put("message", messageText);

        if (language.equals("pt_BR")) {
            JSONArray chatMessages = (JSONArray) gameSession.getAttribute("chat_messages_pt_br");
            chatMessages.put(message);
            gameSession.setAttribute("chat_messages_pt_br", chatMessages);
        } else if (language.equals("es_ES")) {
            JSONArray chatMessages = (JSONArray) gameSession.getAttribute("chat_messages_es_es");
            chatMessages.put(message);
            gameSession.setAttribute("chat_messages_es_es", chatMessages);
        } else if (language.equals("en_US")) {
            JSONArray chatMessages = (JSONArray) gameSession.getAttribute("chat_messages_en_us");
            chatMessages.put(message);
            gameSession.setAttribute("chat_messages_en_us", chatMessages);
        }
    }

    /**
     * Adiciona a chave do campeão selecionado à lista de tentativas feitas na
     * sessão de jogo
     */
    public static void addSelectedChampionKey(HttpSession gameSession, String selectedChampionKey) {
        if (gameSession == null) {
            throw new IllegalStateException("Erro ao adicionar a key do campeão selecionado! Sessão inválida ou expirada.");
        }

        ArrayList<String> selectedChampionsKeys = (ArrayList<String>) gameSession.getAttribute("selected_champions_keys");
        selectedChampionsKeys.add(selectedChampionKey);
        gameSession.setAttribute("selected_champions_keys", selectedChampionsKeys);
    }

    /**
     * Adiciona uma característica do campeão selecionado ao histórico da sessão
     * de jogo, de acordo com o idioma definido
     */
    public static void addSelectedChampionCharacteristic(HttpSession gameSession, String currentSelectedChampionCharacteristic, String language) {
        if (gameSession == null) {
            throw new IllegalStateException("Erro ao adicionar a característica atual do campeão selecionado! Sessão inválida ou expirada.");
        }

        if (language.equals("pt_BR")) {
            ArrayList<String> selectedChampionsCharacteristicsPtBR = (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_pt_br");
            selectedChampionsCharacteristicsPtBR.add(currentSelectedChampionCharacteristic);
            gameSession.setAttribute("selected_champions_characteristics_pt_br", selectedChampionsCharacteristicsPtBR);
        } else if (language.equals("es_ES")) {
            ArrayList<String> selectedChampionsCharacteristicsEsES = (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_es_es");
            selectedChampionsCharacteristicsEsES.add(currentSelectedChampionCharacteristic);
            gameSession.setAttribute("selected_champions_characteristics_es_es", selectedChampionsCharacteristicsEsES);
        } else if (language.equals("en_US")) {
            ArrayList<String> selectedChampionsCharacteristicsEnUS = (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_en_us");
            selectedChampionsCharacteristicsEnUS.add(currentSelectedChampionCharacteristic);
            gameSession.setAttribute("selected_champions_characteristics_en_us", selectedChampionsCharacteristicsEnUS);
        }
    }

    /**
     * Adiciona o status da característica do campeão selecionado ao histórico
     * da sessão de jogo
     */
    public static void addSelectedChampionCharacteristicStatus(HttpSession gameSession, String championCharacteristicStatus) {
        if (gameSession == null) {
            throw new IllegalStateException("Erro ao adicionar o status da característica atual do campeão selecionado! Sessão inválida ou expirada.");
        }

        ArrayList<String> selectedChampionsCharacteristicsStatus = (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_status");
        selectedChampionsCharacteristicsStatus.add(championCharacteristicStatus);
        gameSession.setAttribute("selected_champions_characteristics_status", selectedChampionsCharacteristicsStatus);
    }

    /**
     * Normaliza os resultados das comparações entre características dos
     * campeões, ajustando o histórico da sessão de jogo
     */
    public static void normalizeComparisonResult(HttpSession gameSession) {
        ArrayList<String> selectedChampionsCharacteristicsStatus = (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_status");

        for (int i = 0; i < selectedChampionsCharacteristicsStatus.size(); i++) {
            String status = selectedChampionsCharacteristicsStatus.get(i);

            // Substituindo "greater than" ou "less than" por "incorrect"
            if (status.equals("greater than")) {
                selectedChampionsCharacteristicsStatus.set(i, "incorrect");
            } else if (status.equals("less than")) {
                selectedChampionsCharacteristicsStatus.set(i, "incorrect");
            }
        }

        gameSession.setAttribute("selected_champions_characteristics_status", selectedChampionsCharacteristicsStatus);
    }

    /**
     * Adiciona uma nova característica revelada do campeão misterioso ao
     * histórico da sessão de jogo, considerando o idioma definido
     */
    public static void addRevealedMysteriousChampionCharacteristic(HttpSession gameSession, String currentMysteriousChampionCharacteristic, String language) {
        if (gameSession == null) {
            throw new IllegalStateException("Erro ao adicionar a característica atual revelada do campeão misterioso! Sessão inválida ou expirada.");
        }

        if (language.equals("pt_BR")) {
            ArrayList<String> mysteriousChampionCharacteristicsRevealedPtBR = (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_pt_br");
            mysteriousChampionCharacteristicsRevealedPtBR.add(currentMysteriousChampionCharacteristic);
            gameSession.setAttribute("mysterious_champion_characteristics_revealed_pt_br", mysteriousChampionCharacteristicsRevealedPtBR);
        } else if (language.equals("es_ES")) {
            ArrayList<String> mysteriousChampionCharacteristicsRevealedEsES = (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_es_es");
            mysteriousChampionCharacteristicsRevealedEsES.add(currentMysteriousChampionCharacteristic);
            gameSession.setAttribute("mysterious_champion_characteristics_revealed_es_es", mysteriousChampionCharacteristicsRevealedEsES);
        } else if (language.equals("en_US")) {
            ArrayList<String> mysteriousChampionCharacteristicsRevealedEnUS = (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_en_us");
            mysteriousChampionCharacteristicsRevealedEnUS.add(currentMysteriousChampionCharacteristic);
            gameSession.setAttribute("mysterious_champion_characteristics_revealed_en_us", mysteriousChampionCharacteristicsRevealedEnUS);
        }
    }

    /**
     * Atualiza o caminho da splash art do último campeão selecionado na sessão
     * de jogo
     */
    public static void updateLastSelectedChampionSplashArtPath(HttpSession gameSession, String selectedChampionSplashArtPath) {
        if (gameSession == null) {
            throw new IllegalStateException("Erro ao atualizar a splashart do último campeão selecionado! Sessão inválida ou expirada.");
        }

        gameSession.setAttribute("last_selected_champion_splash_art_path", selectedChampionSplashArtPath);
    }

    /**
     * Encerra e remove a sessão de jogo atual, limpando os dados associados à
     * requisição
     */
    public static void deleteGameSession(HttpSession gameSession, HttpServletRequest request) {
        if (gameSession != null) {
            gameSession.invalidate();
        }
    }
}
