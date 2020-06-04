package pl.dawid.kaszyca.service;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.dto.MessageDTO;
import pl.dawid.kaszyca.model.Message;
import pl.dawid.kaszyca.util.MapperUtils;
import pl.dawid.kaszyca.vm.StatusVM;

import java.util.*;

@Service
public class WebSocketService {

    /**
     * @key - websocket session id
     * @value - login with url to send with simpMessagingTemplate
     * before put it's always check if key and value are unique
     */
    Map<String, String> userLogin;


    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String WS_MESSAGE_TRANSFER_DESTINATION = "/queue/user/";

    public WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userLogin = new HashMap();
    }

    public void addUser(String sessionId, String login) {
        if (!userLogin.containsKey(sessionId) && !userLogin.containsValue(login))
            userLogin.put(sessionId, login);
    }

    public void removeUserBySessionId(String sessionId) {
        userLogin.remove(sessionId, userLogin.get(sessionId));
    }

    public void sendMessages(String to, Long id, Message message) {
        StatusVM statusVM = new StatusVM();
        statusVM.setId(id);
        statusVM.setMessage(MapperUtils.map(message, MessageDTO.class));
        simpMessagingTemplate.convertAndSend(WS_MESSAGE_TRANSFER_DESTINATION + to, statusVM);
    }
}
