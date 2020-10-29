package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import pl.dawid.kaszyca.service.WebSocketService;

@Controller
@Slf4j
@MessageMapping("/queue")
public class SocketController {

    WebSocketService webSocketService;

    public SocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/users/{name}")
    public ResponseEntity getAmountOfNotifications(@DestinationVariable String name) {
        try {
            webSocketService.sendStatusMessageAfterLogin(name);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Cannot send status of notifications for current user");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }
}
