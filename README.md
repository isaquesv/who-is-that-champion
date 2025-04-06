
# Quem é esse campeão?

Descubra quem é o campeão misterioso de **League of Legends**. Este projeto é um jogo interativo que desafia você a adivinhar o campeão sorteado com base em suas características, reveladas rodada por rodada.

## Demonstração

Confira o jogo em funcionamento no vídeo abaixo:

## Como funciona o jogo

A cada rodada, você deve tentar adivinhar quem é o campeão misterioso de League of Legends.

Digite o nome de um campeão e, ao confirmar, uma característica será comparada entre o seu palpite e o campeão oculto.

Se você acertar o campeão antes da 10ª rodada, o jogo é encerrado com vitória e todas as características restantes são reveladas!
Caso não acerte, você continuará tentando até a última rodada.

Cada rodada revela uma nova característica dos campeões. Essas características são exibidas em retângulos coloridos, com as seguintes interpretações:

| **Cor**               | **Descrição**                                                |
| ----------------- | ---------------------------------------------------------------- |
| Azul       | característica analisada na rodada atual. |
| Verde      | característica idêntica à do campeão misterioso. |
| Laranja       | característica parcialmente compatível. |
| Vermelho       | característica diferente. |
| Setas (⬆️⬇️)       | indicam se o valor está acima ou abaixo do correto (quando aplicável). |

## Funcionalidades

- Suporte a múltiplos idiomas: pt-BR, es-ES e en-US.
- Interface responsiva para diferentes tamanhos de tela.
- Chat interativo com registro dos palpites e dicas a cada rodada.
- Pesquisa com sugestões automáticas e validação dos nomes dos campeões.
- Comparação rodada a rodada das características entre o campeão escolhido e o campeão misterioso.
- Sistema de cores e setas para indicar o nível de correspondência das características.
- Reinício manual da partida após o término do jogo.
- Armazenamento local das informações dos campeões usando SQLite.
- Feedback visual ao longo da partida (vitória, derrota e progresso das rodadas).

## Stack utilizada

**Front-end:** HTML, CSS, Bootstrap e JavaScript.

**Back-end:** Java (com Maven) e SQL.

**Servidor:** Apache Tomcat 10.1.39.

**Database:** SQLite JDBC.

**JDK:** 19.0.2.

**APIs:** DataDragon (Riot Games) e LoLdleData (Kerrders), para capturar as informações dos campeões.

## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/isaquesv/who-is-that-champion.git
```

Entre no diretório do projeto

```bash
  cd who-is-that-champion
```

Abra o projeto em uma IDE compatível (recomendado: NetBeans 22).

Compile o projeto e execute em um servidor compatível com Jakarta EE 10 (recomendado: Apache Tomcat 10.1.39).

## Aprendizados

 - Pratiquei a consulta e integração com APIs REST externas.
 - Aprofundei o uso de banco de dados com SQLite.
 - Implementei pela primeira vez a troca de idiomas com arquivos `.json` criados por mim.
 - Aprofundei o uso de sessões em Java para controlar o fluxo do jogo.
 - Aprofundei meus conhecimentos em Java e desenvolvimento web.
 
## Suporte

Para suporte, caso você encontre algum problema, tenha sugestões de melhorias ou algo do tipo, fique à vontade para adicionar uma **issue** [clicando aqui](https://github.com/isaquesv/who-is-that-champion/issues/new)!

## Licença

[MIT](https://choosealicense.com/licenses/mit/)

## Autores

- [@isaquesv](https://www.github.com/isaquesv)

## Referência

 - [League of Legends](https://www.leagueoflegends.com/)
 - [DataDragon API](https://developer.riotgames.com/docs/lol)
 - [LoLdleData API](https://github.com/Kerrders/LoLdleData)
 - [LoLdle](https://loldle.net)
