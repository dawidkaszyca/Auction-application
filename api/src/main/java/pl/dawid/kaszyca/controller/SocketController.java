package pl.dawid.kaszyca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pl.dawid.kaszyca.service.WebSocketService;

@Controller
@MessageMapping("/queue")
public class SocketController {

    WebSocketService webSocketService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public SocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/user/{name}")
    public void sendMessageToSpecificUser(@Payload String message, @DestinationVariable String name) {
        simpMessagingTemplate.convertAndSend("/queue/user/" + name, message);
    }
}
