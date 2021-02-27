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

    ChatService chatService;

    public ChatController(ChatService messageService) {
        this.chatService = messageService;
    }

    @GetMapping("/messages")
    public ResponseEntity getMessagesByUser() {
        try {
            List<ConversationVM> messages = chatService.getAllMessageRelatedWithCurrentUser();
            return messages.isEmpty() ? new ResponseEntity(HttpStatus.NO_CONTENT) : new ResponseEntity(messages, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during getting  messages");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PostMapping("/messages")
    public ResponseEntity sendMessage(@RequestBody MessageDispatchVM messageDispatchVM) {
        try {
            MessageDTO messageDTO = chatService.sendMessage(messageDispatchVM);
            return new ResponseEntity(messageDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during sending message");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PutMapping("/messages")
    public ResponseEntity setDisplayMessage(@RequestBody Long id) {
        try {
            chatService.updateDisplayMessagesById(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during update message display");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity getConversationById(@PathVariable long id) {
        try {
            ConversationVM conversation = chatService.getConversationById(id);
            return new ResponseEntity(conversation, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Cannot get conversation by id");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }
}
