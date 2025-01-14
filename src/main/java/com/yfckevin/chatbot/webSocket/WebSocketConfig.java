package com.yfckevin.chatbot.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry endpointRegistry) {
        // 註冊一個給 Client 連接到 WebSocket Server 的節點
        endpointRegistry.addEndpoint("/chatroom")
                .setAllowedOriginPatterns("*")  // 設置 CORS 允許的來源
                .withSockJS();                  // 啟用 SockJS 支援
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry brokerRegister) {

        // 啟用一個訊息代理並設定訊息發送目地的前綴路徑
        brokerRegister.enableSimpleBroker("/badminton", "/bingBao", "/inkCloud", "/bingBaoTest")
                .setTaskScheduler(heartBeatScheduler());

        // 設定導向至訊息處理器的前綴路徑
        brokerRegister.setApplicationDestinationPrefixes("/ai");
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }

}
