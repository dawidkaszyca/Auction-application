package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.dto.MessageDTO;
import pl.dawid.kaszyca.model.Message;
import pl.dawid.kaszyca.service.MessageService;
import pl.dawid.kaszyca.vm.MessageDispatchVM;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api")
public class MessageController {
    MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/messages")
    public ResponseEntity getMessageByUser() {
        try {
            List<MessageDTO> messages = messageService.getAllMessagesForCurrentUser();
            return messages.isEmpty() ? new ResponseEntity(HttpStatus.NO_CONTENT) : new ResponseEntity(messages, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during getting  messages");
        }
        return new ResponseEntity(HttpStatus.valueOf(500));
    }

    @PostMapping("/messages")
    public ResponseEntity sendMessage(@RequestBody MessageDispatchVM messageDispatchVM) {
        try {
            messageService.sendMessage(messageDispatchVM);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.info("Something went wrong during sending message");
        }
        return new ResponseEntity(HttpStatus.valueOf(500));
    }
}
