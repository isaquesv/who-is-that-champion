
# Quem é esse campeão?

Descubra quem é o campeão misterioso de **League of Legends**. Este projeto é um jogo interativo que desafia você a adivinhar o campeão sorteado com base em suas características, reveladas rodada por rodada.

---

## Demonstração

Confira o jogo em funcionamento no vídeo abaixo:

https://github.com/user-attachments/assets/f9fdc2e1-89e4-48ca-8a4f-77c49bbcd631

👉 Outra demonstração mais detalhada no YouTube: https://www.youtube.com/watch?v=_dQ0O7f3Ka4

---

## Como funciona o jogo

A cada rodada, você deve tentar adivinhar quem é o campeão misterioso de League of Legends.

Digite o nome de um campeão e, ao confirmar, uma característica será comparada entre o seu palpite e o campeão oculto.

Se você acertar o campeão antes da 10ª rodada, o jogo é encerrado com vitória e todas as características restantes são reveladas!
Caso não acerte, você continuará tentando até a última rodada.

Cada rodada revela uma nova característica dos campeões. Essas características são exibidas em retângulos coloridos, com as seguintes interpretações:

---

| **Indicadores**               | **Descrição**                                                |
| ----------------- | ---------------------------------------------------------------- |
| Azul       | característica analisada na rodada atual. |
| Verde      | característica idêntica à do campeão misterioso. |
| Laranja       | característica parcialmente compatível. |
| Vermelho       | característica diferente. |
| Setas (⬆️⬇️)       | indicam se o valor está acima ou abaixo do correto (quando aplicável). |

---

## Funcionalidades

- Suporte a múltiplos idiomas: `pt-BR`, `es-ES` e `en-US`.
- Interface responsiva para diferentes tamanhos de tela.
- Chat interativo com registro dos palpites e dicas a cada rodada.
- Pesquisa com sugestões automáticas e validação dos nomes dos campeões.
- Comparação rodada a rodada das características entre o campeão escolhido e o campeão misterioso.
- Sistema de cores e setas para indicar o nível de correspondência das características.
- Reinício manual da partida após o término do jogo.
- Armazenamento local das informações dos campeões usando SQLite.
- Feedback visual ao longo da partida (vitória, derrota e progresso das rodadas).

---

## Stack utilizada

### **Frontend:** HTML, CSS, Bootstrap e JavaScript.
[![My Skills](https://skillicons.dev/icons?i=html,css,bootstrap,js)](https://skillicons.dev)

### **Backend e Banco de dados:** Java (com Maven) e SQLite.  
[![My Skills](https://skillicons.dev/icons?i=java,maven,sqlite)](https://skillicons.dev)

### **Servidor:** Apache Tomcat 10.1.39.  
<img src="https://img.shields.io/badge/Apache%20Tomcat-F8DC75.svg?style=for-the-badge&logo=Apache-Tomcat&logoColor=black">

### **JDK:** 19.0.2.  
<img src="https://img.shields.io/badge/OpenJDK-000000.svg?style=for-the-badge&logo=OpenJDK&logoColor=white">

### **APIs:** *Who Is That Champion Data API* para capturar as informações dos campeões.  
<img src="https://img.shields.io/badge/League%20of%20Legends-C28F2C.svg?style=for-the-badge&logo=League-of-Legends&logoColor=white">

---

## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/isaquesv/who-is-that-champion.git
```

Abra o projeto em uma IDE compatível (recomendado: NetBeans 22).

Compile o projeto e execute em um servidor compatível com Jakarta EE 10 (recomendado: Apache Tomcat 10.1.39).

---

## Melhorias

- **Otimização do tempo de espera para inicialização do jogo:**  
  Antes, o projeto consultava diretamente as APIs do **Data Dragon** e do **Kerrders LoLdleData**, o que tornava o carregamento inicial lento — cerca de 5 minutos sempre que o sistema era iniciado ou detectava uma nova versão da API **Data Dragon**.

  Para resolver isso, desenvolvi a *[Who Is That Champion Data API](https://github.com/isaquesv/who-is-that-champion-data-api)* em Node.js, que integra apenas os dados necessários das APIs **Data Dragon**, **Kerrders LoLdleData** e **Universe Meeps LoL**.

  Com essa otimização, o tempo de carregamento foi reduzido para menos de 1 minuto, proporcionando uma experiência muito mais rápida e fluida para o usuário.

---

## Aprendizados

 - Pratiquei a consulta e integração com APIs REST externas.
 - Aprofundei o uso de banco de dados com SQLite.
 - Implementei pela primeira vez a troca de idiomas com arquivos `.json` criados por mim.
 - Aprofundei o uso de sessões em Java para controlar o fluxo do jogo.
 - Aprofundei meus conhecimentos em Java e desenvolvimento web.

---

## Suporte

Para suporte, caso você encontre algum problema, tenha sugestões de melhorias ou algo do tipo, fique à vontade para adicionar uma **issue** [clicando aqui](https://github.com/isaquesv/who-is-that-champion/issues/new)!

---

## Licença

[MIT](https://choosealicense.com/licenses/mit/)

---

## Autores

- [@isaquesv](https://www.github.com/isaquesv)

---

## Referência

 - [League of Legends](https://www.leagueoflegends.com/)
 - [Who Is That Champion Data API](https://github.com/isaquesv/who-is-that-champion-data-api)
 - [DataDragon API](https://developer.riotgames.com/docs/lol)
 - [LoLdleData API](https://github.com/Kerrders/LoLdleData)
 - [LoLdle](https://loldle.net)
