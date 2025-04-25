<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quem é esse campeão?</title>
        <%@include file="WEB-INF/jspf/html-head-libs.jspf" %>
    </head>
    <body>
        <nav>
            <h1 id="who-is-that-champion-title">Quem é esse campeão?</h1>
        </nav>
        
        <main>
            <section id="select-champion-content">
                <div id="chat-window">
                    <div class="loading">
                        <div class="spinner-border text-primary custom-spinner" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                    </div>
                </div>

                <search id="search-champion-content" class="input-group">
                    <input id="search-champion-input" class="form-control" type="text" placeholder="Digite o nome do campeão..." disabled>
                </search>

                <ul id="champions-list-content">
                    <div class="loading">
                        <div class="spinner-border text-primary custom-spinner" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                    </div>
                    
                    <small id="champion-not-found-error">Nenhum campeão foi encontrado.</small>
                </ul>
            </section>
            
            <section id="champions-characteristics-content">
                <header>
                    <h2 id="game-status">Iniciando o jogo..</h2>
                </header>
                
                <div id="selected-champion-characteristics">
                    <div class="champion-splash-art">
                        <img id="last-selected-champion-splash-art-img" data-bs-toggle="modal" data-bs-target="#selected-champion-splash-art-modal" alt="Selected champion image">
                        <div class="champion-splash-art-box"><b>?</b></div>
                    </div>
                    
                    <div class="champion-characteristics-row">
                        <div class="champion-characteristic champion-gender" title="Gênero">
                            <p id="selected-champion-gender">Gênero</p>
                        </div>
                        <div class="champion-characteristic champion-lane" title="Rota / Lane">
                            <p id="selected-champion-lane">Rota / Lane</p>
                        </div>
                        <div class="champion-characteristic champion-region" title="Região">
                            <p id="selected-champion-region">Região</p>
                        </div>
                    </div>
                    
                    <div class="champion-characteristics-row">
                        <div class="champion-characteristic champion-resource" title="Recurso">
                            <p id="selected-champion-resource">Recurso</p>
                        </div>
                        <div class="champion-characteristic champion-functions" title="Funções">
                            <p id="selected-champion-functions">Funções</p>
                        </div>
                        <div class="champion-characteristic champion-range-type" title="Tipo de alcance">
                            <p id="selected-champion-range-type">Tipo de alcance</p>
                        </div>
                    </div>
                    
                    <div class="champion-characteristics-row">
                        <div class="champion-characteristic champion-skins-count" title="Quantidade de skins">
                            <p id="selected-champion-skins-count">Quantidade de skins</p>
                        </div>
                        <div class="champion-characteristic champion-release-year" title="Ano de lançamento">
                            <p id="selected-champion-release-year">Ano de lançamento</p>
                        </div>
                    </div>
                    
                    <div class="champion-characteristics-row">
                        <div class="champion-characteristic champion-passive-name" title="Passiva">
                            <p id="selected-champion-passive-name">Passiva</p>
                        </div>
                        <div class="champion-characteristic champion-ultimate-name" title="Ultimate">
                            <p id="selected-champion-ultimate-name">Ultimate</p>
                        </div>
                    </div>
                </div>
                
                <div id="mysterious-champion-characteristics">
                    <div class="champion-splash-art">
                        <img id="mysterious-champion-splash-art-img" data-bs-toggle="modal" data-bs-target="#mysterious-champion-splash-art-modal" alt="Mysterious champion image">
                        <div class="champion-splash-art-box"><b>?</b></div>
                    </div>
                    
                    <div class="champion-characteristics-row">
                        <div class="champion-characteristic champion-gender" title="Gênero">
                            <p id="mysterious-champion-gender">Gênero</p>
                        </div>
                        <div class="champion-characteristic champion-lane" title="Rota / Lane">
                            <p id="mysterious-champion-lane">Rota / Lane</p>
                        </div>
                        <div class="champion-characteristic champion-region" title="Região">
                            <p id="mysterious-champion-region">Região</p>
                        </div>
                    </div>
                    
                    <div class="champion-characteristics-row">
                        <div class="champion-characteristic champion-resource" title="Recurso">
                            <p id="mysterious-champion-resource">Recurso</p>
                        </div>
                        <div class="champion-characteristic champion-functions" title="Funções">
                            <p id="mysterious-champion-functions">Funções</p>
                        </div>
                        <div class="champion-characteristic champion-range-type" title="Tipo de alcance">
                            <p id="mysterious-champion-range-type">Tipo de alcance</p>
                        </div>
                    </div>
                    
                    <div class="champion-characteristics-row">
                        <div class="champion-characteristic champion-skins-count" title="Quantidade de skins">
                            <p id="mysterious-champion-skins-count">Quantidade de skins</p>
                        </div>
                        <div class="champion-characteristic champion-release-year" title="Ano de lançamento">
                            <p id="mysterious-champion-release-year">Ano de lançamento</p>
                        </div>
                    </div>
                    
                    <div class="champion-characteristics-row">
                        <div class="champion-characteristic champion-passive-name" title="Passiva">
                            <p id="mysterious-champion-passive-name">Passiva</p>
                        </div>
                        <div class="champion-characteristic champion-ultimate-name" title="Ultimate">
                            <p id="mysterious-champion-ultimate-name">Ultimate</p>
                        </div>
                    </div>
                </div>
                
                <button class="btn" type="button" id="play-again">Jogar novamente</button>
            </section>
        </main>
        
        <footer>
            <a id="copyright" href="https://github.com/isaquesv" target="_blank">© 2025 <b>isaquesv</b>. Todos os direitos reservados.</a>
            <a id="github-repository-link" href="https://github.com/isaquesv/who-is-that-champion" target="_blank">Repositório GitHub</a>
            <a id="about-game-link" href="https://github.com/isaquesv/who-is-that-champion/blob/main/README.md" target="_blank">Saiba mais sobre o projeto</a>
        </footer>
        
        <!-- Botões da direita -->
        <button type="button" class="game-controls" id="go-to-champions-characteristics" title="Ir para as características"><i class="bi bi-arrow-down"></i></button>
        <button type="button" class="game-controls" id="go-to-top" title="Ir para o topo"><i class="bi bi-arrow-up"></i></button>
        <button type="button" class="game-controls" id="show-or-hide-mysterious-champion-characteristics" title="Exibir ou esconder características do campeão misterioso"><i class="bi bi-eye-slash"></i></button>
        <button type="button" class="game-controls" id="show-settings-modal" data-bs-toggle="modal" data-bs-target="#settings-modal" title="Configurações"><i class="bi bi-gear"></i></button>
        <button type="button" class="game-controls" id="show-instructions-modal" title="Instruções"><i class="bi bi-question-circle" data-bs-toggle="modal" data-bs-target="#instructions-modal"></i></button>
        
        <!-- Modal - Configurações -->
        <div class="modal fade" id="settings-modal" tabindex="-1" aria-labelledby="settings-modal-title" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        <h2 class="modal-title" id="settings-modal-title">Configurações</h2>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="dropdown">
                            <label id="language-dropdown-label">Escolha o idioma em que deseja jogar.</label>
                            
                            <button class="dropdown-toggle d-flex align-items-center" type="button" id="language-dropdown" data-bs-toggle="dropdown" aria-expanded="false" disabled>
                                <img id="selected-language-img" src="assets/img/brazil.png" alt="Selected language flag">
                                <span id="selected-language-text">Português (Brasil)</span>
                            </button>
                            
                            <ul class="dropdown-menu" aria-labelledby="language-dropdown">
                                <li class="dropdown-item" id="pt-br-option">
                                    <img src="assets/img/brazil.png" alt="Brazil flag">
                                    <span id="pt-br-text">Português (Brasil)</span>
                                </li>
                                <li class="dropdown-item" id="es-es-option">
                                    <img src="assets/img/spain.png" alt="Spain flag">
                                    <span id="es-es-text">Espanhol (Espanha)</span>
                                </li>
                                <li class="dropdown-item" id="en-us-option">
                                    <img src="assets/img/usa.png" alt="USA flag">
                                    <span id="en-us-text">Inglês(EUA)</span>
                                </li>
                            </ul>
                        </div>
                        
                        <p id="language-instructions-phrase-1">Você poderá jogar com os nomes das características e habilidades traduzidas.</p>
                        <p id="language-instructions-phrase-2">As traduções são geradas por inteligência artificial e podem conter erros. Para sugerir melhorias, <a href="https://github.com/isaquesv/who-is-that-champion/issues/new" target="_blank">clique aqui</a>. Agradecemos muito sua contribuição!</p>
                    </div>
                </div>
                
                <div class="modal-content">
                    <div class="modal-body">
                        <p id="riot-disclaimer">
                            <b>Quem é esse campeão?</b> não é endossado pela <b>Riot Games</b> e não reflete as visões ou opiniões da <b>Riot Games</b> ou de qualquer pessoa oficialmente envolvida na produção ou gerenciamento de propriedades da <b>Riot Games</b>.
                            A <b>Riot Games</b> e todas as propriedades associadas são marcas comerciais ou marcas registradas da <b>Riot Games, Inc</b>.
                        </p>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Modal - Instruções -->
        <div class="modal fade" id="instructions-modal" tabindex="-1" aria-labelledby="instruction-title-modal-1" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        <h2 class="modal-title" id="instruction-title-modal-1">Instruções</h2>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <article>
                            <p class="instruction-paragraph" id="instruction-paragraph-1">
                                Descubra quem é o campeão misterioso de <a href="https://www.leagueoflegends.com/pt-br/">League of Legends</a>, da Riot Games.
                            </p>
                            
                            <article>
                                <p class="instruction-paragraph" id="instruction-paragraph-2">
                                    O jogo é dividido em 10 rodadas. A cada rodada, uma característica do campeão misterioso e do campeão selecionado por você são reveladas e comparadas.
                                </p>
                                <p class="instruction-paragraph" id="instruction-paragraph-3">
                                    Para jogar, basta digitar o nome de um campeão. Uma de suas características, baseado na rodada atual será analisada e revelada.
                                </p>
                            </article>
                            
                            <article>
                                <p class="instruction-paragraph" id="instruction-paragraph-4">
                                    A cor dos retângulos indicará o quão próximo você está de descobrir o campeão misterioso.
                                </p>
                                <p class="instruction-paragraph" id="instruction-paragraph-5">
                                    <b class="current">Azul</b> destaca a característica analisada na rodada atual.
                                </p>

                                <p class="instruction-paragraph" id="instruction-paragraph-6">
                                    <b class="correct">Verde</b> indica que o campeão escolhido compartilha essa característica com o campeão misterioso.
                                </p>

                                <p class="instruction-paragraph" id="instruction-paragraph-7">
                                    <b class="parcial">Laranja</b> revela que há semelhanças parciais entre os campeões.
                                </p>

                                <p class="instruction-paragraph" id="instruction-paragraph-8">
                                    <b class="incorrect">Vermelho</b> mostra que a característica não corresponde ao campeão misterioso.
                                </p>

                                <p class="instruction-paragraph" id="instruction-paragraph-9">
                                    As setas (<i class="bi bi-arrow-down"></i><i class="bi bi-arrow-up"></i>) também ajudam, indicando se o valor da característica está abaixo ou acima do correto.
                                </p>
                            </article>
                        </article>
                    </div>
                </div>
                
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 class="modal-title" id="instruction-title-modal-2">Características</h2>
                    </div>
                    <div class="modal-body">
                        <article>
                            <p class="instruction-paragraph" id="instruction-paragraph-10">
                                Aqui estão os detalhes de cada um dos retângulos de características.
                            </p>

                            <article>
                                <b class="instruction-title" id="instruction-title-1">Rodada 1 - Gênero:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-11">
                                    <span>Possíveis valores:</span> Masculino, Feminino ou Outro.
                                </p>
                            </article>
                            <article>
                                <b class="instruction-title" id="instruction-title-2">Rodada 2 - Rota / Lane:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-12">
                                    <span>Possíveis valores:</span> Topo, Selva, Meio, Atirador ou Suporte.
                                </p>
                            </article>
                            <article>
                                <b class="instruction-title" id="instruction-title-3">Rodada 3 - Região:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-13">
                                    De onde vem o campeão ou onde ele reside.
                                </p>
                                <p class="instruction-paragraph" id="instruction-paragraph-14">
                                    <span>Possíveis valores:</span> Ionia, Shurima, Runeterra, etc...
                                </p>
                            </article>
                            <article>
                                <b class="instruction-title" id="instruction-title-4">Rodada 4 - Recurso:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-15">
                                    O recurso utilizado pelo campeão no jogo.
                                </p>
                                <p class="instruction-paragraph" id="instruction-paragraph-16">
                                    <span>Possíveis valores:</span> Mana, Energia, Fúria, etc...
                                </p>
                            </article>
                            <article>
                                <b class="instruction-title" id="instruction-title-5">Rodada 5 - Funções:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-17">
                                    A função (ou as funções) do campeão no jogo.
                                </p>
                                <p class="instruction-paragraph" id="instruction-paragraph-18">
                                    <span>Possíveis valores:</span> Assassino, Tanque, Mago, etc...
                                </p>
                            </article>
                            <article>
                                <b class="instruction-title" id="instruction-title-6">Rodada 6 - Tipo de alcance:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-19">
                                    Como o campeão atinge outros campeões ao clicar com o botão direito no jogo.
                                </p>
                                <p class="instruction-paragraph" id="instruction-paragraph-20">
                                    <span>Possíveis valores:</span> Corpo-a-Corpo, Longo Alcance ou Corpo-a-Corpo / Longo Alcance.
                                </p>
                            </article>
                            <article>
                                <b class="instruction-title" id="instruction-title-7">Rodada 7 - Quantidade de skins:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-21">
                                    Qualquer valor igual ou acima de 0.
                                </p>
                            </article>
                            <article>
                                <b class="instruction-title" id="instruction-title-8">Rodada 8 - Ano de lançamento:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-22">
                                    Ano em que o campeão foi lançado para ser jogado.
                                </p>
                                <p class="instruction-paragraph" id="instruction-paragraph-23">
                                    <span>Possíveis valores:</span> Qualquer ano entre 2009 e hoje.
                                </p>
                            </article>
                            <article>
                                <b class="instruction-title" id="instruction-title-9">Rodada 9 - Passiva:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-24">
                                    O nome da passiva do campeão em jogo.
                                </p>
                                <p class="instruction-paragraph" id="instruction-paragraph-25">
                                    <span>Possíveis valores:</span> Qualquer nome de passiva de campeão.
                                </p>
                            </article>
                            <article>
                                <b class="instruction-title" id="instruction-title-10">Rodada 10 - Ultimate:</b>
                                <p class="instruction-paragraph" id="instruction-paragraph-26">
                                    O nome da ultimate do campeão em jogo.
                                </p>
                                <p class="instruction-paragraph" id="instruction-paragraph-27">
                                    <span>Possíveis valores:</span> Qualquer nome de ultimate de campeão.
                                </p>
                            </article>
                        </article>
                    </div>
                </div>

                <div class="modal-content">
                    <div class="modal-header">
                        <h2 class="modal-title" id="instruction-title-modal-3">Exemplo</h2>
                    </div>
                    <div class="modal-body">
                        <article>
                            <p class="instruction-paragraph" id="instruction-paragraph-28">
                                Aqui esta o exemplo de uma partida. Considere que o campeão misterioso é <a href="https://www.leagueoflegends.com/pt-br/champions/nami/">Nami, a Conjuradora das Marés</a>.
                            </p>

                            <hr>
                            
                            <article>
                                <b class="instruction-title" id="instruction-title-11">Rodada 1 - Gênero:</b>

                                <p class="instruction-paragraph" id="instruction-paragraph-29">
                                    Na Rodada 1, a característica <b>Gênero</b> será destacada em <b class="current">azul</b>, e você deverá adivinhar o gênero do campeão misterioso.
                                </p>

                                <div class="champion-characteristic current">
                                    <p id="selected-champion-gender-modal-1" title="Gênero">Gênero</p>
                                </div>
                            </article>
                            <article>
                                <p class="instruction-paragraph" id="instruction-paragraph-30">
                                    Se você escolher <a href="https://www.leagueoflegends.com/pt-br/champions/irelia/">Irelia</a>, a característica <b>Gênero</b> será destacada em <b class="correct">verde</b>, sinalizando que o campeão misterioso compartilha o mesmo gênero de <a href="https://www.leagueoflegends.com/pt-br/champions/irelia/">Irelia</a> (Feminino).
                                </p>

                                <div class="champion-characteristic correct">
                                    <p id="selected-champion-gender-modal-2" title="Gênero">Feminino</p>
                                </div>
                            </article>
                            
                            <hr>
                            
                            <article>
                                <b class="instruction-title" id="instruction-title-12">Rodada 2 - Rota / Lane:</b>

                                <p class="instruction-paragraph" id="instruction-paragraph-31">
                                    Na Rodada 2, a característica <b>Rota / Lane</b> será destacada em <b class="current">azul</b>, e você deverá adivinhar a rota (ou as rotas) em que o campeão misterioso costuma estar presente.
                                </p>
                            </article>
                            <article>
                                <p class="instruction-paragraph" id="instruction-paragraph-32">
                                    Se você escolher <a href="https://www.leagueoflegends.com/pt-br/champions/karma/">Karma</a>, a característica <b>Rota / Lane</b> será destacada em <b class="parcial">laranja</b>, o que é parcialmente correto, já que <a href="https://www.leagueoflegends.com/pt-br/champions/karma/">Karma</a> pode ser jogada tanto como Suporte quanto no Meio, enquanto <a href="https://www.leagueoflegends.com/pt-br/champions/nami/">Nami</a> é jogada apenas como Suporte.
                                </p>

                                <div class="champion-characteristic parcial">
                                    <p id="selected-champion-lane-modal-1" title="Rota / Lane">Meio / Suporte</p>
                                </div>
                            </article>
                            
                            <hr>
                            
                            <article>
                                <b class="instruction-title" id="instruction-title-13">Rodada 7 - Quantidade de skins:</b>

                                <p class="instruction-paragraph" id="instruction-paragraph-33">
                                    Na Rodada 7, a característica <b>Quantidade de skins</b> será destacada em <b class="current">azul</b>, e você deverá adivinhar a quantidade de skins que o campeão misterioso possui.
                                </p>
                            </article>
                            <article>
                                <p class="instruction-paragraph" id="instruction-paragraph-34">
                                    Se você escolher <a href="https://www.leagueoflegends.com/pt-br/champions/ambessa/">Ambessa</a>, a característica <b>Quantidade de skins</b> será destacada em <b class="incorrect">vermelho</b>, sinalizando que <a href="https://www.leagueoflegends.com/pt-br/champions/ambessa/">Ambessa</a> possui menos skins que <a href="https://www.leagueoflegends.com/pt-br/champions/nami/">Nami</a>.
                                </p>

                                <div class="champion-characteristic incorrect">
                                    <p id="selected-champion-skins-count-modal-1" title="Quantidade de skins">1<i class="bi bi-arrow-up"></i></p>
                                </div>
                            </article>
                        </article>
                    </div>
                </div>
            </div>
        </div>
        
        <%@include file="WEB-INF/jspf/html-body-libs.jspf" %>
    </body>
</html>
