package com.hr.admin.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void pushToUser(Long userId, String type, Object data) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("data", data);
        message.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                message
        );
    }

    public void pushToAll(String type, Object data) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("data", data);
        message.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/messages", message);
    }

    public void pushNotification(Long userId, String title, String content) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("content", content);
        pushToUser(userId, "notification", notification);
    }
}
