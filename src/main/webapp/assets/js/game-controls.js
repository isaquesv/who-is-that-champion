import { showOrHideElements, addOrRemoveClass } from "./utils.js";

document.addEventListener("DOMContentLoaded", function () {
    const CHARACTERISTICS_CONTENT = document.querySelector("#champions-characteristics-content");
    const GO_TO_CHAMPIONS_CHARACTERISTICS_BUTTON = document.querySelector("#go-to-champions-characteristics");
    const GO_TO_TOP_BUTTON = document.querySelector("#go-to-top");
    const SHOW_OR_HIDE_MYSTERIOUS_CHAMPION_BUTTON = document.querySelector("#show-or-hide-mysterious-champion-characteristics");
    
    GO_TO_CHAMPIONS_CHARACTERISTICS_BUTTON.addEventListener("click", function () {
        const TARGET_POSITION = CHARACTERISTICS_CONTENT.getBoundingClientRect().top + window.scrollY;
        smoothScrollTo(TARGET_POSITION);
    });
    GO_TO_TOP_BUTTON.addEventListener("click", function () {
        smoothScrollTo(0);
    });
    SHOW_OR_HIDE_MYSTERIOUS_CHAMPION_BUTTON.addEventListener("click", function() {
        showOrHideMysteriousChampion();
    });

    // Eventos de scroll e resize
    window.addEventListener("scroll", checkVisibility);
    window.addEventListener("resize", checkVisibility);

    checkVisibility();
});

// Função para verificar se os elementos estão fora da tela
function checkVisibility() {
    let championGenderCharacteristic;
    if (document.querySelector("#mysterious-champion-characteristics").style.display == "none") {
        championGenderCharacteristic = document.querySelector("#mysterious-champion-gender");
    } else {
        championGenderCharacteristic = document.querySelector("#selected-champion-gender");
    }
    
    const SEARCH_CHAMPION_INPUT = document.querySelector("#search-champion-input");

    const CHAMPION_TITLE_VISIBLE = isFullyOutOfView(championGenderCharacteristic);
    const SEARCH_CHAMPION_VISIBLE = isFullyOutOfView(SEARCH_CHAMPION_INPUT);
}

// Verifica se o elemento está completamente fora da área visível da janela (fora da tela)
function isFullyOutOfView(element) {
    const ELEMENT_BOUNDS = element.getBoundingClientRect();
    return ELEMENT_BOUNDS.bottom < 0 || ELEMENT_BOUNDS.top > window.innerHeight;
}

// Realiza a rolagem suave da página até a posição alvo com uma animação usando easing cúbico
export function smoothScrollTo(targetPosition) {
    const START_POSITION = window.scrollY;
    const DISTANCE = targetPosition - START_POSITION;
    const DURATION = 1300;
    let startTime = null;

    function animation(currentTime) {
        if (!startTime) startTime = currentTime;

        const timeElapsed = currentTime - startTime;
        const progress = Math.min(timeElapsed / DURATION, 1);
        const easingValue = easeOutCubic(progress);
        window.scrollTo(0, START_POSITION + DISTANCE * easingValue);

        if (timeElapsed < DURATION) {
            requestAnimationFrame(animation);
        }
    }

    requestAnimationFrame(animation);
}

// Função de suavização para a rolagem
function easeOutCubic(progress) {
    return 1 - Math.pow(1 - progress, 3);
}

// Realiza a rolagem suave até a seção de status do jogo do campeão
export function scrollToChampionGameStatus() {
    const CHAMPION_GAME_STATUS = document.querySelector("#game-status");

    const TARGET_POSITION = CHAMPION_GAME_STATUS.getBoundingClientRect().top + window.scrollY;
    smoothScrollTo(TARGET_POSITION);
}

// Alterna entre exibir as características do campeão selecionado ou do campeão misterioso
function showOrHideMysteriousChampion() {
    const SELECTED_CHAMPIONS_CHARACTERISTICS = document.querySelector("#selected-champion-characteristics");
    const MYSTERIOUS_CHAMPION_CHARACTERISTICS = document.querySelector("#mysterious-champion-characteristics");
    
    const SHOW_OR_HIDE_MYSTERIOUS_CHAMPION_ICON = document.querySelector("#show-or-hide-mysterious-champion-characteristics i");
    const GO_TO_CHAMPIONS_CHARACTERISTICS_BUTTON = document.querySelector("#go-to-champions-characteristics");
    const GO_TO_TOP_BUTTON = document.querySelector("#go-to-top");
    
    if (SHOW_OR_HIDE_MYSTERIOUS_CHAMPION_ICON.classList.contains("bi-eye-slash")) {
        showOrHideElements(false, SELECTED_CHAMPIONS_CHARACTERISTICS);
        showOrHideElements(true, MYSTERIOUS_CHAMPION_CHARACTERISTICS);
        
        addOrRemoveClass(false, "bi-eye-slash", SHOW_OR_HIDE_MYSTERIOUS_CHAMPION_ICON);
        addOrRemoveClass(true, "bi-eye", SHOW_OR_HIDE_MYSTERIOUS_CHAMPION_ICON);
    } else {
        showOrHideElements(true, SELECTED_CHAMPIONS_CHARACTERISTICS);
        showOrHideElements(false, MYSTERIOUS_CHAMPION_CHARACTERISTICS);
        
        addOrRemoveClass(true, "bi-eye-slash", SHOW_OR_HIDE_MYSTERIOUS_CHAMPION_ICON);
        addOrRemoveClass(false, "bi-eye", SHOW_OR_HIDE_MYSTERIOUS_CHAMPION_ICON);
    }
}