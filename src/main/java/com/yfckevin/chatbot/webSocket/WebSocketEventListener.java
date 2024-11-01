package com.yfckevin.chatbot.webSocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket連線事件監聽器
 */
@Component
public class WebSocketEventListener {

    /** STOMP 訊息發送器 */
    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 連線時的處理
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("收到一個新的WebSocket連線");
    }

    /**
     * 離線時的處理
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("event = " + event);
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // 從WebSocket Session中取得使用者名稱
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            System.out.println("使用者" + username + "已離線");
        }
    }
}
