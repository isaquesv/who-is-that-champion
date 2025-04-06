// Exibe ou oculta um elemento HTML com base no valor booleano passado
export function showOrHideElements(isToShow, element) {
    if (isToShow) {
        element.style.display = "block";
    } else {
        element.style.display = "none";
    }
}

// Oculta todos os elementos com a classe "loading" da página
export function hideLoadings() {
    let loadingSections = document.querySelectorAll(".loading");
    
    for (let i = 0; i < loadingSections.length; i++) {
        loadingSections[i].style.display = "none";
    }
}

// Adiciona ou remove uma classe de um elemento, conforme o valor booleano passado
export function addOrRemoveClass(isToAdd, className, element) {
    if (isToAdd) {
        if (element.classList.contains(className) == false) {
            element.classList.add(className);
        }
    } else {
        if (element.classList.contains(className)) {
            element.classList.remove(className);
        }
    }
}

// Cria uma tag <meta> com o nome e conteúdo fornecidos, ou atualiza o conteúdo caso ela já exista
export function createMeta(name, content) {
    const EXISTING_META = document.querySelector("meta[name='" + name + "']");
    
    if (EXISTING_META == null) {
        const META = document.createElement("meta");
        META.name = name;
        META.content = content;
        
        document.head.appendChild(META);
    } else {
        EXISTING_META.setAttribute("content", content);
    }
}