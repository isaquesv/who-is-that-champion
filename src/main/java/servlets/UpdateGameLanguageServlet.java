package servlets;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import static servlets.PrepareGameServlet.getActualGameStatus;

@WebServlet(name = "UpdateGameLanguageServlet", urlPatterns = {"/UpdateGameLanguageServlet"})
public class UpdateGameLanguageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject updateGameLanguageResponse = new JSONObject();
        String language = request.getParameter("language");

        try {
            if (language != null && !language.trim().isEmpty()) {
                HttpSession gameSession = request.getSession(false);

                if ((boolean) gameSession.getAttribute("is_game_ended") == true) {
                    updateGameLanguageResponse.put("is_game_ready", true);
                    updateGameLanguageResponse.put("message", "Sucesso ao atualizar o idioma. O jogo já foi encerrado.");
                } else {
                    updateGameLanguageResponse.put("is_game_ready", true);
                    updateGameLanguageResponse.put("message", "Sucesso ao atualizar o idioma. O jogo ainda esta em andamento.");
                }

                // Capturando as demais informações necessárias para que o jogo seja iniciado corretamente
                updateGameLanguageResponse.put("is_game_ended", (boolean) gameSession.getAttribute("is_game_ended"));
                updateGameLanguageResponse.put("player_wins", (boolean) gameSession.getAttribute("player_wins"));
                updateGameLanguageResponse.put("round", (int) gameSession.getAttribute("round"));
                
                if ((boolean) gameSession.getAttribute("is_game_ended") == true) {
                    updateGameLanguageResponse.put("mysterious_champion_splash_art_path", (String) gameSession.getAttribute("mysterious_champion_splash_art_path"));
                }
                
                String actualGameStatus = getActualGameStatus((int) gameSession.getAttribute("round"), (boolean) gameSession.getAttribute("player_wins"), language);
                updateGameLanguageResponse.put("actual_game_status", actualGameStatus);

                if (language.equals("pt_BR")) {
                    updateGameLanguageResponse.put("chat_messages", (JSONArray) gameSession.getAttribute("chat_messages_pt_br"));
                    updateGameLanguageResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_pt_br"));
                    updateGameLanguageResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_pt_br"));
                } else if (language.equals("es_ES")) {
                    updateGameLanguageResponse.put("chat_messages", (JSONArray) gameSession.getAttribute("chat_messages_es_es"));
                    updateGameLanguageResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_es_es"));
                    updateGameLanguageResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_es_es"));
                } else {
                    updateGameLanguageResponse.put("chat_messages", (JSONArray) gameSession.getAttribute("chat_messages_en_us"));
                    updateGameLanguageResponse.put("mysterious_champion_characteristics_revealed", (ArrayList<String>) gameSession.getAttribute("mysterious_champion_characteristics_revealed_en_us"));
                    updateGameLanguageResponse.put("selected_champions_characteristics", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_en_us"));
                }

                updateGameLanguageResponse.put("selected_champions_characteristics_status", (ArrayList<String>) gameSession.getAttribute("selected_champions_characteristics_status"));
                updateGameLanguageResponse.put("selected_champions_keys", (ArrayList<String>) gameSession.getAttribute("selected_champions_keys"));
                updateGameLanguageResponse.put("last_selected_champion_splash_art_path", (String) gameSession.getAttribute("last_selected_champion_splash_art_path"));
            } else {
                updateGameLanguageResponse.put("is_game_ready", false);
                updateGameLanguageResponse.put("message", "Falha ao prepararar o ambiente. Não foi possível identificar o 'language'.");
            }
        } catch (Exception e) {
            updateGameLanguageResponse.put("is_game_ready", false);
            updateGameLanguageResponse.put("message", "Erro inesperado: " + e.getMessage());
        }

        response.getWriter().write(updateGameLanguageResponse.toString());
    }
}
