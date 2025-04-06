import { getLanguage } from "./language.js";
import { scrollToChampionGameStatus, smoothScrollTo } from "./game-controls.js";
import { showOrHideElements, hideLoadings, addOrRemoveClass } from "./utils.js";

document.addEventListener("DOMContentLoaded", function() {
    smoothScrollTo(0);
    prepareGame();
    
    // Realizando a pesquisa de campeão sempre que o input de pesquisa estiver em foco e uma tecla for pressionada
    let searchChampionInput = document.querySelector("#search-champion-input");
    searchChampionInput.addEventListener("keyup", function() {
        findChampion();
    });
    
    document.querySelector("#play-again").addEventListener("click", function() {
        playAgain();
    });
});

let canSelectChampion = true;

// Inicializa o jogo, configurando o estado e os elementos iniciais
function prepareGame() {
    $.ajax({
        method: "POST",
        url: "PrepareGameServlet",
        data: {
            language: getLanguage()
        },
        dataType: "json",
        success: function(prepareGameResponse) {
            enableDropdown();
            
            if (prepareGameResponse.is_game_ready) {
                if (prepareGameResponse.is_game_ended) {
                    document.querySelector("#play-again").style.display = "block";
                    updateGameStatus(prepareGameResponse.actual_game_status, prepareGameResponse.round, prepareGameResponse.is_game_ended, prepareGameResponse.player_wins, prepareGameResponse.selected_champions_characteristics_status, prepareGameResponse.selected_champions_characteristics, prepareGameResponse.mysterious_champion_characteristics_revealed, prepareGameResponse.last_selected_champion_splash_art_path, prepareGameResponse.mysterious_champion_splash_art_path);
                } else {
                    updateGameStatus(prepareGameResponse.actual_game_status, prepareGameResponse.round, prepareGameResponse.is_game_ended, prepareGameResponse.player_wins, prepareGameResponse.selected_champions_characteristics_status, prepareGameResponse.selected_champions_characteristics, prepareGameResponse.mysterious_champion_characteristics_revealed, prepareGameResponse.last_selected_champion_splash_art_path, "");
                    
                    // Habilitando o input de pesquisa de campeões.
                    const searchChampionInput = document.querySelector("#search-champion-input");
                    searchChampionInput.removeAttribute("disabled");

                    createChampionsListItens(prepareGameResponse.champions_basic_info, prepareGameResponse.selected_champions_keys);
                }
                
                hideLoadings();
                prepareChat(prepareGameResponse.chat_messages);
            }
        },
        error: function (xhr, status, error) {
            alert("Ops! Um erro inesperado ocorreu enquanto iniciavamos seu jogo. Por favor, recarregue esta página ou entre em contato conosco via GitHub!");
            console.error("erro: " + error);
        }
    });
}

// Filtra os campeões da lista com base no texto digitado
function findChampion() {
    let searchChampionInput = document.querySelector("#search-champion-input");
    
    let searchFilter = searchChampionInput.value.toUpperCase();
    let championsListUl = document.querySelector("#champions-list-content");
    let championsItensLi = championsListUl.querySelectorAll("li");
    let visibleItens = 0;
    let championNotFoundError = document.querySelector("#champion-not-found-error");
    
    for (let i = 0; i < championsItensLi.length; i++) {
        let championNameValue = championsItensLi[i].textContent;
        
        if (championNameValue.toUpperCase().indexOf(searchFilter) > -1) {
            showOrHideElements(true, championsItensLi[i]);
            visibleItens++;
        } else {
            showOrHideElements(false, championsItensLi[i]);
        }
    }
    
    if (visibleItens == 0) {
        showOrHideElements(true, championNotFoundError);
    } else {
        showOrHideElements(false, championNotFoundError);
    }
}

// Atualiza o status do jogo com base na rodada e nas ações do jogador
function updateGameStatus(actualGameStatus, round, isGameEnded, playerWins, selectedChampionsCharacteristicsStatus, selectedChampionsCharacteristics, mysteriousChampionCharacteristicsRevealed, lastSelectedChampionSplashArtPath, mysteriousChampionSplashArtPath) {
    const GAME_STATUS = document.querySelector("#game-status");
    GAME_STATUS.innerHTML = actualGameStatus;

    const CHAMPION_CHARACTERISTICS = [
        ".champion-gender",
        ".champion-lane",
        ".champion-region",
        ".champion-resource",
        ".champion-functions",
        ".champion-range-type",
        ".champion-skins-count",
        ".champion-release-year",
        ".champion-passive-name",
        ".champion-ultimate-name"
    ];

    if (round > 1) {
        const SELECTED_CHAMPION_SPLASH_ART_CONTENT = document.querySelector("#selected-champion-characteristics .champion-splash-art");
        const SELECTED_CHAMPION_SPLASH_ART_IMG = document.querySelector("#last-selected-champion-splash-art-img");
        const SELECTED_CHAMPION_SPLASH_ART_BOX = document.querySelector("#selected-champion-characteristics .champion-splash-art-box");
        
        showOrHideElements(true, SELECTED_CHAMPION_SPLASH_ART_IMG);
        showOrHideElements(false, SELECTED_CHAMPION_SPLASH_ART_BOX);
        
        // Adiciona efeito de desfoque e fade-out antes da troca
        updateSplashArt(SELECTED_CHAMPION_SPLASH_ART_IMG, lastSelectedChampionSplashArtPath);
        
        addOrRemoveClass(false, "correct", SELECTED_CHAMPION_SPLASH_ART_CONTENT);
        addOrRemoveClass(false, "parcial", SELECTED_CHAMPION_SPLASH_ART_CONTENT);
        addOrRemoveClass(false, "incorrect", SELECTED_CHAMPION_SPLASH_ART_CONTENT);
        
        if (playerWins) {
            addOrRemoveClass(true, "correct", SELECTED_CHAMPION_SPLASH_ART_CONTENT);
            addOrRemoveClass(false, "incorrect", SELECTED_CHAMPION_SPLASH_ART_CONTENT);
        } else {
            addOrRemoveClass(false, "correct", SELECTED_CHAMPION_SPLASH_ART_CONTENT);
            addOrRemoveClass(true, "incorrect", SELECTED_CHAMPION_SPLASH_ART_CONTENT);
        }
    }
    
    if (isGameEnded) {
        const MYSTERIOUS_CHAMPION_SPLASH_ART_CONTENT = document.querySelector("#mysterious-champion-characteristics .champion-splash-art");
        const MYSTERIOUS_CHAMPION_SPLASH_ART_IMAGE = document.querySelector("#mysterious-champion-splash-art-img");
        const MYSTERIOUS_CHAMPION_SPLASH_ART_BOX = document.querySelector("#mysterious-champion-characteristics .champion-splash-art-box");

        showOrHideElements(true, MYSTERIOUS_CHAMPION_SPLASH_ART_IMAGE);
        showOrHideElements(false, MYSTERIOUS_CHAMPION_SPLASH_ART_BOX);
        
        // Adiciona efeito de desfoque e fade-out antes da troca
        updateSplashArt(MYSTERIOUS_CHAMPION_SPLASH_ART_IMAGE, mysteriousChampionSplashArtPath);
        
        addOrRemoveClass(true, "correct", MYSTERIOUS_CHAMPION_SPLASH_ART_CONTENT);
    }
    
    if (playerWins) {
        for (let i = 0; i < CHAMPION_CHARACTERISTICS.length; i++) {
            // Removendo a classe 'current' de todos os elementos
            addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[i])[0]);
            addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[i])[1]);
            
            // Adicionando suas respectivas classes e textos
            addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[i], document.querySelectorAll(CHAMPION_CHARACTERISTICS[i])[0]);
            addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[i])[1]);
            
            if (i == 6 || i == 7) {
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[i] + " p")[0].innerHTML = selectedChampionsCharacteristics[i];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[i] + " p")[1].innerHTML = mysteriousChampionCharacteristicsRevealed[i];
            } else {
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[i] + " p")[0].textContent = selectedChampionsCharacteristics[i];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[i] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[i];
            }
        }
    } else {
        switch (round) {
            case 1:
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);
                break;
            case 2:
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];
                
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                break;
            case 3:
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];

                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[1], document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[0].textContent = selectedChampionsCharacteristics[1];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[1];
                
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);
                break;
            case 4:
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[1], document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[2], document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);
                
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[1]);

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[0].textContent = selectedChampionsCharacteristics[1];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[1];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[0].textContent = selectedChampionsCharacteristics[2];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[2];
                break;
            case 5:
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[1], document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[2], document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[3], document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[1]);
                
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[1]);

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[0].textContent = selectedChampionsCharacteristics[1];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[1];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[0].textContent = selectedChampionsCharacteristics[2];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[2];

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[0].textContent = selectedChampionsCharacteristics[3];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[3];
                break;
            case 6:
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[1], document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[2], document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[3], document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[4], document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[1]);
                
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[1]);

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[0].textContent = selectedChampionsCharacteristics[1];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[1];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[0].textContent = selectedChampionsCharacteristics[2];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[2];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[0].textContent = selectedChampionsCharacteristics[3];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[3];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[0].textContent = selectedChampionsCharacteristics[4];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[4];
                break;
            case 7:
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[1], document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[2], document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[3], document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[4], document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[5], document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[1]);
                
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[1]);

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[0].textContent = selectedChampionsCharacteristics[1];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[1];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[0].textContent = selectedChampionsCharacteristics[2];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[2];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[0].textContent = selectedChampionsCharacteristics[3];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[3];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[0].textContent = selectedChampionsCharacteristics[4];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[4];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[0].textContent = selectedChampionsCharacteristics[5];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[5];
                break;
            case 8:
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[1], document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[2], document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[3], document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[4], document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[5], document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[6], document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[1]);
                
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[1]);

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[0].textContent = selectedChampionsCharacteristics[1];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[1];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[0].textContent = selectedChampionsCharacteristics[2];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[2];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[0].textContent = selectedChampionsCharacteristics[3];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[3];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[0].textContent = selectedChampionsCharacteristics[4];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[4];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[0].textContent = selectedChampionsCharacteristics[5];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[5];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[6] + " p")[0].innerHTML = selectedChampionsCharacteristics[6];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[6] + " p")[1].innerHTML = mysteriousChampionCharacteristicsRevealed[6];
                break;
            case 9:
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[1], document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[2], document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[3], document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[4], document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[5], document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[6], document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[7], document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[1]);
                
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[8])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[8])[1]);

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[0].textContent = selectedChampionsCharacteristics[1];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[1];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[0].textContent = selectedChampionsCharacteristics[2];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[2];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[0].textContent = selectedChampionsCharacteristics[3];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[3];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[0].textContent = selectedChampionsCharacteristics[4];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[4];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[0].textContent = selectedChampionsCharacteristics[5];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[5];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[6] + " p")[0].innerHTML = selectedChampionsCharacteristics[6];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[6] + " p")[1].innerHTML = mysteriousChampionCharacteristicsRevealed[6];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[7] + " p")[0].innerHTML = selectedChampionsCharacteristics[7];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[7] + " p")[1].innerHTML = mysteriousChampionCharacteristicsRevealed[7];
                break;
            case 10:
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[8])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[8])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[1], document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[2], document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[3], document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[4], document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[5], document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[6], document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[7], document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[8], document.querySelectorAll(CHAMPION_CHARACTERISTICS[8])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[8])[1]);
                
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[9])[0]);
                addOrRemoveClass(true, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[9])[1]);

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[0].textContent = selectedChampionsCharacteristics[1];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[1];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[0].textContent = selectedChampionsCharacteristics[2];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[2];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[0].textContent = selectedChampionsCharacteristics[3];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[3];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[0].textContent = selectedChampionsCharacteristics[4];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[4];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[0].textContent = selectedChampionsCharacteristics[5];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[5];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[6] + " p")[0].innerHTML = selectedChampionsCharacteristics[6];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[6] + " p")[1].innerHTML = mysteriousChampionCharacteristicsRevealed[6];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[7] + " p")[0].innerHTML = selectedChampionsCharacteristics[7];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[7] + " p")[1].innerHTML = mysteriousChampionCharacteristicsRevealed[7];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[8] + " p")[0].textContent = selectedChampionsCharacteristics[8];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[8] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[8];
                break;
            case 11:
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[9])[0]);
                addOrRemoveClass(false, "current", document.querySelectorAll(CHAMPION_CHARACTERISTICS[9])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[0], document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[0])[1]);

                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[1], document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[1])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[2], document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[2])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[3], document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[3])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[4], document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[4])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[5], document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[5])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[6], document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[6])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[7], document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[7])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[8], document.querySelectorAll(CHAMPION_CHARACTERISTICS[8])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[8])[1]);
                
                addOrRemoveClass(true, selectedChampionsCharacteristicsStatus[9], document.querySelectorAll(CHAMPION_CHARACTERISTICS[9])[0]);
                addOrRemoveClass(true, "correct", document.querySelectorAll(CHAMPION_CHARACTERISTICS[9])[1]);

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[0].textContent = selectedChampionsCharacteristics[0];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[0] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[0];

                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[0].textContent = selectedChampionsCharacteristics[1];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[1] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[1];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[0].textContent = selectedChampionsCharacteristics[2];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[2] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[2];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[0].textContent = selectedChampionsCharacteristics[3];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[3] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[3];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[0].textContent = selectedChampionsCharacteristics[4];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[4] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[4];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[0].textContent = selectedChampionsCharacteristics[5];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[5] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[5];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[6] + " p")[0].innerHTML = selectedChampionsCharacteristics[6];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[6] + " p")[1].innerHTML = mysteriousChampionCharacteristicsRevealed[6];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[7] + " p")[0].innerHTML = selectedChampionsCharacteristics[7];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[7] + " p")[1].innerHTML = mysteriousChampionCharacteristicsRevealed[7];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[8] + " p")[0].textContent = selectedChampionsCharacteristics[8];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[8] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[8];
                
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[9] + " p")[0].textContent = selectedChampionsCharacteristics[9];
                document.querySelectorAll(CHAMPION_CHARACTERISTICS[9] + " p")[1].textContent = mysteriousChampionCharacteristicsRevealed[9];
                break;
        }
    }
}

// Atualiza a imagem do campeão com transição suave de opacidade e desfoque
function updateSplashArt(splashArtImgElement, imagePath) {
    splashArtImgElement.style.transition = "opacity 0.6s ease, filter 0.6s ease";
    splashArtImgElement.style.opacity = "0";
    splashArtImgElement.style.filter = "blur(10px)";

    setTimeout(() => {
        splashArtImgElement.src = imagePath;

        // Suaviza a imagem de volta com fade-in e remoção do blur
        setTimeout(() => {
            splashArtImgElement.style.opacity = "1";
            splashArtImgElement.style.filter = "blur(0px)";
        }, 100);
    }, 600);
}

// Cria e exibe os campeões disponíveis na lista, ignorando os já selecionados
function createChampionsListItens(championsBasicInfo, selectedChampionsKeys) {
    const championsListElement = document.querySelector("#champions-list-content");
    
    for (let i = 0; i < championsBasicInfo.length; i++) {
        if (selectedChampionsKeys.includes(championsBasicInfo[i].key)) {
            continue;
        }
        
        let championItemElement = document.createElement("li");
        championItemElement.id = championsBasicInfo[i].key;
        championItemElement.addEventListener("click", function() {
            if (canSelectChampion) {
                championItemElement.remove();
                findChampion();

                compareChampionsCharacteristic(championsBasicInfo[i].key, championsBasicInfo[i].name);
            }
        });

        let championImgElement = document.createElement("img");
        championImgElement.src = championsBasicInfo[i].icon_path;

        let championName = document.createTextNode(" " + championsBasicInfo[i].name);

        championItemElement.appendChild(championImgElement);
        championItemElement.appendChild(championName);

        championsListElement.appendChild(championItemElement);
    }
}

// Remove todos os campeões exibidos na lista
function removeChampionsListItens() {
    const championsListElement = document.querySelector("#champions-list-content");
    let championsItensElements = document.querySelectorAll("#champions-list-content li");
    
    for (let i = 0; i < championsItensElements.length; i++) {
        championsItensElements[i].remove();
    }
}

// Adiciona as mensagens do chat com base no array fornecido
function prepareChat(chatMessages) {
    for (let i = 0;  i < chatMessages.length; i++) {
        createMessageElement(chatMessages[i].message, chatMessages[i].is_game_message);
    }
}

// Cria e exibe uma nova mensagem no chat, identificando se é do jogo ou do jogador
function createMessageElement(messageText, isGameMessage) {
    let chatWindow = document.querySelector("#chat-window");
    
    let messageContainer = document.createElement("div");
    messageContainer.classList.add("chat-message");
    
    let messageParagraph = document.createElement("p");
    messageParagraph.innerHTML = messageText;
    
    if (isGameMessage == true) {
        messageContainer.classList.add("game-message");
    } else {
        messageContainer.classList.add("player-message");
    }
    
    messageContainer.appendChild(messageParagraph);
    chatWindow.appendChild(messageContainer);

    chatWindow.scrollTop = chatWindow.scrollHeight;
}

// Remove todas as mensagens exibidas no chat
function removeMessagesElements() {
    let messagesElements = document.querySelectorAll(".chat-message");
    
    for (let i = 0; i < messagesElements.length; i++) {
        messagesElements[i].remove();
    }
}

// Envia o campeão selecionado para comparação e atualiza o jogo com base na resposta
function compareChampionsCharacteristic(selectedChampionKey, selectedChampionName) {
    let searchChampionInput = document.querySelector("#search-champion-input");
    searchChampionInput.setAttribute("disabled", true);
    searchChampionInput.value = "";
    findChampion();
    
    canSelectChampion = false;
    $.ajax({
        method: "POST",
        url: "CompareCharacteristicsServlet",
        data: {
            selectedChampionKey: selectedChampionKey,
            selectedChampionName: selectedChampionName,
            language: getLanguage()
        },
        dataType: "json",
        success: function(compareCharacteristicsResponse) {
            if (compareCharacteristicsResponse.are_characteristics_compared) {
                if (compareCharacteristicsResponse.is_game_ended) {
                    document.querySelector("#play-again").style.display = "block";
                    updateGameStatus(compareCharacteristicsResponse.actual_game_status, compareCharacteristicsResponse.round, compareCharacteristicsResponse.is_game_ended, compareCharacteristicsResponse.player_wins, compareCharacteristicsResponse.selected_champions_characteristics_status, compareCharacteristicsResponse.selected_champions_characteristics, compareCharacteristicsResponse.mysterious_champion_characteristics_revealed, compareCharacteristicsResponse.last_selected_champion_splash_art_path, compareCharacteristicsResponse.mysterious_champion_splash_art_path);
                    removeChampionsListItens();
                    scrollToChampionGameStatus();
                } else {
                    updateGameStatus(compareCharacteristicsResponse.actual_game_status, compareCharacteristicsResponse.round, compareCharacteristicsResponse.is_game_ended, compareCharacteristicsResponse.player_wins, compareCharacteristicsResponse.selected_champions_characteristics_status, compareCharacteristicsResponse.selected_champions_characteristics, compareCharacteristicsResponse.mysterious_champion_characteristics_revealed, compareCharacteristicsResponse.last_selected_champion_splash_art_path, "");
                    searchChampionInput.removeAttribute("disabled");
                    canSelectChampion = true;
                }

                createMessageElement(selectedChampionName, false);
                createMessageElement(compareCharacteristicsResponse.game_message, true);
            }
        },
        error: function (xhr, status, error) {
            alert("Ops! Um erro inesperado ocorreu enquanto comparavamos as caracterísiticas do campeão selecionado com o campeão misterioso. Por favor, recarregue esta página ou entre em contato conosco via GitHub!");
            console.error("erro: " + error);
        }
    });
}

// Atualiza o idioma do jogo e recarrega os dados exibidos com base na nova linguagem
export function updateGameLanguage(language) {
    $.ajax({
        method: "POST",
        url: "UpdateGameLanguageServlet",
        data: {
            language: language
        },
        dataType: "json",
        success: function(updateGameLanguageResponse) {
            if (updateGameLanguageResponse.is_game_ready) {
                if (updateGameLanguageResponse.is_game_ended) {
                    document.querySelector("#play-again").style.display = "block";
                    updateGameStatus(updateGameLanguageResponse.actual_game_status, updateGameLanguageResponse.round, updateGameLanguageResponse.is_game_ended, updateGameLanguageResponse.player_wins, updateGameLanguageResponse.selected_champions_characteristics_status, updateGameLanguageResponse.selected_champions_characteristics, updateGameLanguageResponse.mysterious_champion_characteristics_revealed, updateGameLanguageResponse.last_selected_champion_splash_art_path, updateGameLanguageResponse.mysterious_champion_splash_art_path);
                } else {
                    updateGameStatus(updateGameLanguageResponse.actual_game_status, updateGameLanguageResponse.round, updateGameLanguageResponse.is_game_ended, updateGameLanguageResponse.player_wins, updateGameLanguageResponse.selected_champions_characteristics_status, updateGameLanguageResponse.selected_champions_characteristics, updateGameLanguageResponse.mysterious_champion_characteristics_revealed, updateGameLanguageResponse.last_selected_champion_splash_art_path, "");
                }
                
                removeMessagesElements();
                prepareChat(updateGameLanguageResponse.chat_messages);
            }
        },
        error: function (xhr, status, error) {
            alert("Ops! Um erro inesperado ocorreu enquanto atualizavamos o idioma de seu jogo. Por favor, recarregue esta página ou entre em contato conosco via GitHub!");
            console.error("erro: " + error);
        }
    });
}

// Destroi a sessão do jogo e recarrega a página
function playAgain() {
    $.ajax({
        method: "POST",
        url: "PlayAgainServlet",
        dataType: "json"
    });
    
    window.location.reload();
}

// Habilita o dropdown
function enableDropdown() {
    const LANGUAGE_DROPDOWN = document.querySelector("#language-dropdown");

    if (LANGUAGE_DROPDOWN.hasAttribute("disabled")) {
        LANGUAGE_DROPDOWN.removeAttribute("disabled");
    }

}