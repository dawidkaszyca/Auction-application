package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.dto.MessageDTO;
import pl.dawid.kaszyca.service.ChatService;
import pl.dawid.kaszyca.vm.ConversationVM;
import pl.dawid.kaszyca.vm.MessageDispatchVM;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api")
public class ChatController {
    ChatService messageService;

    public ChatController(ChatService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/messages")
    public ResponseEntity getMessageByUser() {
        try {
            List<ConversationVM> messages = messageService.getAllMessageRelatedWithCurrentUser();
            return messages.isEmpty() ? new ResponseEntity(HttpStatus.NO_CONTENT) : new ResponseEntity(messages, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during getting  messages");
        }
        return new ResponseEntity(HttpStatus.valueOf(500));
    }

    @PostMapping("/messages")
    public ResponseEntity sendMessage(@RequestBody MessageDispatchVM messageDispatchVM) {
        try {
            MessageDTO messageDTO = messageService.sendMessage(messageDispatchVM);
            return new ResponseEntity(messageDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during sending message");
        }
        return new ResponseEntity(HttpStatus.valueOf(500));
    }
}