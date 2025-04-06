import { updateGameLanguage } from "./running-game.js";
import { createMeta, addOrRemoveClass } from "./utils.js";

document.addEventListener("DOMContentLoaded", function () {
    let language = getLanguage();

    const PATH = window.location.pathname;
    const PAGE_NAME = PATH.substring(PATH.lastIndexOf("/") + 1);
    loadPageLanguage(language, PAGE_NAME);

    const DROPDOWN_PT_BR_OPTION = document.querySelector("#pt-br-option");
    const DROPDOWN_ES_ES_OPTION = document.querySelector("#es-es-option");
    const DROPDOWN_EN_US_OPTION = document.querySelector("#en-us-option");

    DROPDOWN_PT_BR_OPTION.addEventListener("click", function () {
        changeLanguage("pt_BR");
        loadPageLanguage("pt_BR", PAGE_NAME);
        updateGameLanguage("pt_BR");
    });
    DROPDOWN_ES_ES_OPTION.addEventListener("click", function () {
        changeLanguage("es_ES");
        loadPageLanguage("es_ES", PAGE_NAME);
        updateGameLanguage("es_ES");
    });
    DROPDOWN_EN_US_OPTION.addEventListener("click", function () {
        changeLanguage("en_US");
        loadPageLanguage("en_US", PAGE_NAME);
        updateGameLanguage("en_US");
    });
});

// Retorna o idioma atual definido na sessão ou no navegador do usuário
export function getLanguage() {
    let storedLanguage = localStorage.getItem("language");

    if (storedLanguage == null) {
        let navigatorLanguage = navigator.language || navigator.userLanguage;

        if (navigatorLanguage.startsWith("pt")) {
            navigatorLanguage = "pt_BR";
        } else if (navigatorLanguage.startsWith("es")) {
            navigatorLanguage = "es_ES";
        } else {
            navigatorLanguage = "en_US";
        }

        localStorage.setItem("language", navigatorLanguage);
        return navigatorLanguage;
    } else {
        return storedLanguage;
    }
}

// Busca o conteúdo do arquivo .json correspondente ao idioma informado
async function getLanguageContent(language) {
    const LANGUAGE_FILE_PATH = "assets/json/" + language + ".json";

    try {
        const response = await fetch(LANGUAGE_FILE_PATH);
        if (!response.ok) {
            throw new Error("Falha ao carregar JSON!");
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Erro inesperado! Não foi possível atualizar o idioma da página: ", error);
        return null;
    }
}

// Atualiza o idioma selecionado na sessão
function changeLanguage(selectedLanguage) {
    localStorage.setItem("language", selectedLanguage);
}

// Carrega o conteúdo do idioma para a página atual e atualiza os textos de acordo com a página informada
async function loadPageLanguage(language, pageName) {
    let languageData = await getLanguageContent(language);

    if (languageData != null) {
        if (pageName == "index.jsp" || pageName == "") {
            updatePageLanguage(language, languageData);
        }
    }
}

// Atualiza os textos da página inicial com base nos dados do idioma fornecido
function updatePageLanguage(language, languageData) {
    document.title = languageData.index.title;
    createMeta("description", languageData.index.metaDescription);
    createMeta("keywords", languageData.index.metaKeywords);
    
    // Nav
    document.querySelector("#who-is-that-champion-title").textContent = languageData.index.whoIsThatChampionTitle;
    
    // Modais - Configurações
    document.querySelector("#settings-modal-title").textContent = languageData.index.settingsModalTitle;
    
    let selectedLanguageImg = document.querySelector("#selected-language-img");
    let selectedLanguageText = document.querySelector("#selected-language-text");
    
    let ptBrLanguageOption = document.querySelector("#pt-br-option");
    let esEsLanguageOption = document.querySelector("#es-es-option");
    let enUsLanguageOption = document.querySelector("#en-us-option");
    
    if (language == "pt_BR") {
        addOrRemoveClass(true, "active", ptBrLanguageOption);
        addOrRemoveClass(false, "active", esEsLanguageOption);
        addOrRemoveClass(false, "active", enUsLanguageOption);

        selectedLanguageImg.src = "assets/img/brazil.png";
        selectedLanguageText.textContent = languageData.index.ptBrOption;
    } else if (language == "es_ES") {
        addOrRemoveClass(false, "active", ptBrLanguageOption);
        addOrRemoveClass(true, "active", esEsLanguageOption);
        addOrRemoveClass(false, "active", enUsLanguageOption);

        selectedLanguageImg.src = "assets/img/spain.png";
        selectedLanguageText.textContent = languageData.index.esEsOption;
    } else {
        addOrRemoveClass(false, "active", ptBrLanguageOption);
        addOrRemoveClass(false, "active", esEsLanguageOption);
        addOrRemoveClass(true, "active", enUsLanguageOption);

        selectedLanguageImg.src = "assets/img/usa.png";
        selectedLanguageText.textContent = languageData.index.enUsOption;
    }
    
    document.querySelector("#language-dropdown-label").textContent = languageData.index.languageDropdownLabel;
    document.querySelector("#pt-br-text").textContent = languageData.index.ptBrOption;
    document.querySelector("#es-es-text").textContent = languageData.index.esEsOption;
    document.querySelector("#en-us-text").textContent = languageData.index.enUsOption;
    
    document.querySelector("#language-instructions-phrase-1").textContent = languageData.index.languageInstructionsPhrase1;
    document.querySelector("#language-instructions-phrase-2").innerHTML = languageData.index.languageInstructionsPhrase2;
    document.querySelector("#riot-disclaimer").innerHTML = languageData.index.riotDisclaimer;
    
    // Modais - Instruções
    document.querySelector("#instruction-title-modal-1").textContent = languageData.index.instructionTitleModal1;
    document.querySelector("#instruction-paragraph-1").innerHTML = languageData.index.instructionParagraph1;
    document.querySelector("#instruction-paragraph-2").textContent = languageData.index.instructionParagraph2;
    document.querySelector("#instruction-paragraph-3").textContent = languageData.index.instructionParagraph3;
    document.querySelector("#instruction-paragraph-4").textContent = languageData.index.instructionParagraph4;
    document.querySelector("#instruction-paragraph-5").innerHTML = languageData.index.instructionParagraph5;
    document.querySelector("#instruction-paragraph-6").innerHTML = languageData.index.instructionParagraph6;
    document.querySelector("#instruction-paragraph-7").innerHTML = languageData.index.instructionParagraph7;
    document.querySelector("#instruction-paragraph-8").innerHTML = languageData.index.instructionParagraph8;
    document.querySelector("#instruction-paragraph-9").innerHTML = languageData.index.instructionParagraph9;
    
    // Modais - Características
    document.querySelector("#instruction-title-modal-2").textContent = languageData.index.instructionTitleModal2;
    document.querySelector("#instruction-paragraph-10").textContent = languageData.index.instructionParagraph10;
    document.querySelector("#instruction-title-1").textContent = languageData.index.instructionTitle1;
    document.querySelector("#instruction-paragraph-11").innerHTML = languageData.index.instructionParagraph11;
    document.querySelector("#instruction-title-2").textContent = languageData.index.instructionTitle2;
    document.querySelector("#instruction-paragraph-12").innerHTML = languageData.index.instructionParagraph12;
    document.querySelector("#instruction-title-3").textContent = languageData.index.instructionTitle3;
    document.querySelector("#instruction-paragraph-13").textContent = languageData.index.instructionParagraph13;
    document.querySelector("#instruction-paragraph-14").innerHTML = languageData.index.instructionParagraph14;
    document.querySelector("#instruction-title-4").textContent = languageData.index.instructionTitle4;
    document.querySelector("#instruction-paragraph-15").textContent = languageData.index.instructionParagraph15;
    document.querySelector("#instruction-paragraph-16").innerHTML = languageData.index.instructionParagraph16;
    document.querySelector("#instruction-title-5").textContent = languageData.index.instructionTitle5;
    document.querySelector("#instruction-paragraph-17").textContent = languageData.index.instructionParagraph17;
    document.querySelector("#instruction-paragraph-18").innerHTML = languageData.index.instructionParagraph18;
    document.querySelector("#instruction-title-6").textContent = languageData.index.instructionTitle6;
    document.querySelector("#instruction-paragraph-19").textContent = languageData.index.instructionParagraph19;
    document.querySelector("#instruction-paragraph-20").innerHTML = languageData.index.instructionParagraph20;
    document.querySelector("#instruction-title-7").textContent = languageData.index.instructionTitle7;
    document.querySelector("#instruction-paragraph-21").textContent = languageData.index.instructionParagraph21;
    document.querySelector("#instruction-title-8").textContent = languageData.index.instructionTitle8;
    document.querySelector("#instruction-paragraph-22").textContent = languageData.index.instructionParagraph22;
    document.querySelector("#instruction-paragraph-23").innerHTML = languageData.index.instructionParagraph23;
    document.querySelector("#instruction-title-9").textContent = languageData.index.instructionTitle9;
    document.querySelector("#instruction-paragraph-24").textContent = languageData.index.instructionParagraph24;
    document.querySelector("#instruction-paragraph-25").innerHTML = languageData.index.instructionParagraph25;
    document.querySelector("#instruction-title-10").textContent = languageData.index.instructionTitle10;
    document.querySelector("#instruction-paragraph-26").textContent = languageData.index.instructionParagraph26;
    document.querySelector("#instruction-paragraph-27").innerHTML = languageData.index.instructionParagraph27;
    
    // Modais - Exemplo
    document.querySelector("#instruction-title-modal-3").textContent = languageData.index.instructionTitleModal3;
    document.querySelector("#instruction-paragraph-28").innerHTML = languageData.index.instructionParagraph28;
    document.querySelector("#instruction-title-11").textContent = languageData.index.instructionTitle1;
    document.querySelector("#instruction-paragraph-29").innerHTML = languageData.index.instructionParagraph29;
    document.querySelector("#selected-champion-gender-modal-1").textContent = languageData.index.championGenderText;
    document.querySelector("#selected-champion-gender-modal-1").title = languageData.index.championGenderText;
    document.querySelector("#instruction-paragraph-30").innerHTML = languageData.index.instructionParagraph30;
    document.querySelector("#selected-champion-gender-modal-2").textContent = languageData.index.selectedChampionGenderModal2;
    document.querySelector("#instruction-title-12").textContent = languageData.index.instructionTitle2;
    document.querySelector("#instruction-paragraph-31").innerHTML = languageData.index.instructionParagraph31;
    document.querySelector("#instruction-paragraph-32").innerHTML = languageData.index.instructionParagraph32;
    document.querySelector("#selected-champion-lane-modal-1").textContent = languageData.index.selectedChampionLaneModal1;
    document.querySelector("#selected-champion-lane-modal-1").title = languageData.index.championLaneText;
    document.querySelector("#instruction-title-13").textContent = languageData.index.instructionTitle7;
    document.querySelector("#instruction-paragraph-33").innerHTML = languageData.index.instructionParagraph33;
    document.querySelector("#instruction-paragraph-34").innerHTML = languageData.index.instructionParagraph34;
    document.querySelector("#selected-champion-skins-count-modal-1").innerHTML = languageData.index.selectedChampionSkinsCountModal1;
    document.querySelector("#selected-champion-skins-count-modal-1").title = languageData.index.championSkinsCountText;
    
    // Escolher campeão
    document.querySelector("#search-champion-input").placeholder = languageData.index.searchChampionInputPlaceholder;
    
    // Características dos campeões
    if (document.querySelector("#game-status").innerHTML == "Iniciando o jogo..." || document.querySelector("#game-status").innerHTML == "Iniciando juego..."  || document.querySelector("#game-status").innerHTML == "Starting game...") {
        document.querySelector("#game-status").innerHTML = languageData.index.initialGameStatus;
    }
    
    if (document.querySelectorAll(".champion-gender p")[0].textContent == "Gênero" || document.querySelectorAll(".champion-gender p")[0].textContent == "Género" || document.querySelectorAll(".champion-gender p")[0].textContent == "Gender") {
        document.querySelectorAll(".champion-gender p")[0].textContent = languageData.index.championGenderText;
        document.querySelectorAll(".champion-gender p")[1].textContent = languageData.index.championGenderText;
    }
    document.querySelectorAll(".champion-gender")[0].title = languageData.index.championGenderText;
    document.querySelectorAll(".champion-gender")[1].title = languageData.index.championGenderText;
    
    if (document.querySelectorAll(".champion-lane p")[0].textContent == "Rota / Lane" || document.querySelectorAll(".champion-lane p")[0].textContent == "Línea" || document.querySelectorAll(".champion-lane p")[0].textContent == "Lane") {
        document.querySelectorAll(".champion-lane p")[0].textContent = languageData.index.championLaneText;
        document.querySelectorAll(".champion-lane p")[1].textContent = languageData.index.championLaneText;
    }
    document.querySelectorAll(".champion-lane")[0].title = languageData.index.championLaneText;
    document.querySelectorAll(".champion-lane")[1].title = languageData.index.championLaneText;
    
    if (document.querySelectorAll(".champion-region p")[0].textContent == "Região" || document.querySelectorAll(".champion-region p")[0].textContent == "Región" || document.querySelectorAll(".champion-region p")[0].textContent == "Region") {
        document.querySelectorAll(".champion-region p")[0].textContent = languageData.index.championRegionText;
        document.querySelectorAll(".champion-region p")[1].textContent = languageData.index.championRegionText;
    }
    document.querySelectorAll(".champion-region")[0].title = languageData.index.championRegionText;
    document.querySelectorAll(".champion-region")[1].title = languageData.index.championRegionText;
    
    if (document.querySelectorAll(".champion-resource p")[0].textContent == "Recurso" || document.querySelectorAll(".champion-resource p")[0].textContent == "Recurso" || document.querySelectorAll(".champion-resource p")[0].textContent == "Resource") {
        document.querySelectorAll(".champion-resource p")[0].textContent = languageData.index.championResourceText;
        document.querySelectorAll(".champion-resource p")[1].textContent = languageData.index.championResourceText;
    }
    document.querySelectorAll(".champion-resource")[0].title = languageData.index.championResourceText;
    document.querySelectorAll(".champion-resource")[1].title = languageData.index.championResourceText;

    if (document.querySelectorAll(".champion-functions p")[0].textContent == "Funções" || document.querySelectorAll(".champion-functions p")[0].textContent == "Funciones" || document.querySelectorAll(".champion-functions p")[0].textContent == "Roles") {
        document.querySelectorAll(".champion-functions p")[0].textContent = languageData.index.championFunctionsText;
        document.querySelectorAll(".champion-functions p")[1].textContent = languageData.index.championFunctionsText;
    }
    document.querySelectorAll(".champion-functions")[0].title = languageData.index.championFunctionsText;
    document.querySelectorAll(".champion-functions")[1].title = languageData.index.championFunctionsText;

    if (document.querySelectorAll(".champion-range-type p")[0].textContent == "Tipo de alcance" || document.querySelectorAll(".champion-range-type p")[0].textContent == "Tipo de alcance" || document.querySelectorAll(".champion-range-type p")[0].textContent == "Range type") {
        document.querySelectorAll(".champion-range-type p")[0].textContent = languageData.index.championRangeTypeText;
        document.querySelectorAll(".champion-range-type p")[1].textContent = languageData.index.championRangeTypeText;
    }
    document.querySelectorAll(".champion-range-type")[0].title = languageData.index.championRangeTypeText;
    document.querySelectorAll(".champion-range-type")[1].title = languageData.index.championRangeTypeText;

    if (document.querySelectorAll(".champion-skins-count p")[0].textContent == "Quantidade de skins" || document.querySelectorAll(".champion-skins-count p")[0].textContent == "Cantidad de aspectos" || document.querySelectorAll(".champion-skins-count p")[0].textContent == "Skins count") {
        document.querySelectorAll(".champion-skins-count p")[0].textContent = languageData.index.championSkinsCountText;
        document.querySelectorAll(".champion-skins-count p")[1].textContent = languageData.index.championSkinsCountText;
    }
    document.querySelectorAll(".champion-skins-count")[0].title = languageData.index.championSkinsCountText;
    document.querySelectorAll(".champion-skins-count")[1].title = languageData.index.championSkinsCountText;

    if (document.querySelectorAll(".champion-release-year p")[0].textContent == "Ano de lançamento" || document.querySelectorAll(".champion-release-year p")[0].textContent == "Año de lanzamiento" || document.querySelectorAll(".champion-release-year p")[0].textContent == "Release year") {
        document.querySelectorAll(".champion-release-year p")[0].textContent = languageData.index.championReleaseYearText;
        document.querySelectorAll(".champion-release-year p")[1].textContent = languageData.index.championReleaseYearText;
    }
    document.querySelectorAll(".champion-release-year")[0].title = languageData.index.championReleaseYearText;
    document.querySelectorAll(".champion-release-year")[1].title = languageData.index.championReleaseYearText;

    if (document.querySelectorAll(".champion-passive-name p")[0].textContent == "Passiva" || document.querySelectorAll(".champion-passive-name p")[0].textContent == "Pasiva" || document.querySelectorAll(".champion-passive-name p")[0].textContent == "Passive") {
        document.querySelectorAll(".champion-passive-name p")[0].textContent = languageData.index.championPassiveNameText;
        document.querySelectorAll(".champion-passive-name p")[1].textContent = languageData.index.championPassiveNameText;
    }
    document.querySelectorAll(".champion-passive-name")[0].title = languageData.index.championPassiveNameText;
    document.querySelectorAll(".champion-passive-name")[1].title = languageData.index.championPassiveNameText;

    if (document.querySelectorAll(".champion-ultimate-name p")[0].textContent == "Ultimate" || document.querySelectorAll(".champion-ultimate-name p")[0].textContent == "Definitiva" || document.querySelectorAll(".champion-ultimate-name p")[0].textContent == "Ultimate") {
        document.querySelectorAll(".champion-ultimate-name p")[0].textContent = languageData.index.championUltimateNameText;
        document.querySelectorAll(".champion-ultimate-name p")[1].textContent = languageData.index.championUltimateNameText;
    }
    document.querySelectorAll(".champion-ultimate-name")[0].title = languageData.index.championUltimateNameText;
    document.querySelectorAll(".champion-ultimate-name")[1].title = languageData.index.championUltimateNameText;
    
    document.querySelector("#play-again").textContent = languageData.index.playAgain;
    
    // Footer
    document.querySelector("#github-repository-link").textContent = languageData.index.githubRepositoryLink;
    document.querySelector("#about-game-link").textContent = languageData.index.aboutGameLink;
    document.querySelector("#copyright").innerHTML = languageData.index.copyright;

    // Botões da direita
    document.querySelector("#go-to-champions-characteristics").title = languageData.index.goToChampionsCharacteristicsTitle;
    document.querySelector("#go-to-top").title = languageData.index.goToTopTitle;
    document.querySelector("#show-or-hide-mysterious-champion-characteristics").title = languageData.index.showOrHideMysteriousChampionCharacteristicsTitle;
    document.querySelector("#show-settings-modal").title = languageData.index.settingsModalTitle;
    document.querySelector("#show-instructions-modal").title = languageData.index.instructionTitleModal1;
}