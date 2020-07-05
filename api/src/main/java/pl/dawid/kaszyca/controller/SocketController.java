package pl.dawid.kaszyca.controller;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import pl.dawid.kaszyca.service.WebSocketService;

@Controller
@MessageMapping("/queue")
public class SocketController {

    WebSocketService webSocketService;

    public SocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/users/{name}")
    public void getAmountOfNotifications(@DestinationVariable String name) throws JSONException {
        webSocketService.sendStatusMessageAfterLogin(name);
    }
}
