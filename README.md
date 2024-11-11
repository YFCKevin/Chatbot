<h1 align=center>Chatbot 聊天機器人</h1>
<p align=center>「羽球配對」和「冰寶」的AI聊天機器人，讓使用者輸入想要查詢的羽球團資訊，或是冰箱內的食材庫存資訊後，後端經由RAG技術，從Neo4j Vector裡比對向量取得所屬資料，再把資料與prompt喂給LLM，生成回覆。

## 目錄
 - [技術](#技術)
 - [致謝](#致謝)

<p align="center">
  <img src="https://img.shields.io/badge/Language-Java-orange.svg" alt="">  <img src="https://img.shields.io/badge/Language-Javascript-yellow.svg" alt="">
</p>

<p>羽球配對聊天機器人：</p>
<p align="left">
  <img src="https://github.com/YFCKevin/Chatbot/blob/main/badminton-chatbot.gif" width="30%" alt="描述">
</p><br>
<p>冰寶聊天機器人：</p>
<p align="left">
  <img src="https://github.com/YFCKevin/Chatbot/blob/main/bingBao-chatbot.gif" width="30%" alt="描述">
</p><br>

[羽球配對首頁](https://gurula.cc/badminton/index)<br><br>

## 技術：
  - **後端：** Spring Boot
  - **前端：** Alpine.js
  - **語言：** Java 17, JavaScript
  - **資料庫：** MongoDB, Neo4j
  - **雲端部署：** Google Cloud
  - **第三方 API：** openAI (gpt-4o-mini, text-embedding-3-small)
  - **即時通訊：** WebSocket
<p align="center">
  <img src="https://github.com/YFCKevin/Chatbot/blob/main/architecture-diagram.png" width="100%" alt="架構圖">
</p>

## 致謝：
  - [凱文大叔的鐵人賽](https://ithelp.ithome.com.tw/users/20161290/ironman/7070)
  - [dhaifullah](https://www.creative-tim.com/twcomponents/component/chat-box)
  - [Bootstrap](https://getbootstrap.com)
  - [Jquery](https://jquery.com)
  - [fontawesome](https://fontawesome.com/)
  - [jieba](https://github.com/fxsjy/jieba)
