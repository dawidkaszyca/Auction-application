package pl.dawid.kaszyca.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.dto.MessageDTO;
import pl.dawid.kaszyca.model.Message;
import pl.dawid.kaszyca.util.MapperUtils;
import pl.dawid.kaszyca.vm.StatusVM;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String WS_MESSAGE_TRANSFER_DESTINATION = "/queue/user/";
    private ChatService chatService;

    public WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    public void sendMessages(String to, Long id, Message message) {
        StatusVM statusVM = new StatusVM();
        statusVM.setId(id);
        removeNano(message);
        statusVM.setMessage(MapperUtils.map(message, MessageDTO.class));
        simpMessagingTemplate.convertAndSend(WS_MESSAGE_TRANSFER_DESTINATION + to, statusVM);
    }

    private void removeNano(Message message) {
        Instant myRoundedUpInstant = message.getSentDate().truncatedTo(ChronoUnit.SECONDS);
        double round = message.getSentDate().getNano() / 1_000_000_000.0;
        if (Math.round(round) == 1) {
            myRoundedUpInstant = myRoundedUpInstant.plusSeconds(1);
        }
        message.setSentDate(myRoundedUpInstant);
    }

    public void sendStatusMessageAfterLogin(String user) throws JSONException {
        Integer amountOfNewUnReadMessage = chatService.getAmountOfUnViewedMessage(user);
        String jsonString = new JSONObject()
                .put("status", amountOfNewUnReadMessage)
                .toString();
        simpMessagingTemplate.convertAndSend(WS_MESSAGE_TRANSFER_DESTINATION + user, jsonString);
    }
}
