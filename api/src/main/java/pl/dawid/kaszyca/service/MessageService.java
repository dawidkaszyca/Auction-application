package pl.dawid.kaszyca.service;

import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.dto.MessageDTO;
import pl.dawid.kaszyca.exception.RecipientNotExistException;
import pl.dawid.kaszyca.model.Message;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.repository.MessageRepository;
import pl.dawid.kaszyca.util.MapperUtils;
import pl.dawid.kaszyca.vm.MessageDispatchVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    UserService userService;
    MessageRepository messageRepository;
    WebSocketService webSocketService;

    public MessageService(UserService userService, MessageRepository messageRepository, WebSocketService webSocketService) {
        this.userService = userService;
        this.messageRepository = messageRepository;
        this.webSocketService = webSocketService;
    }

    public List<MessageDTO> getAllMessagesForCurrentUser() {
        Optional<User> user = userService.getCurrentUserObject();
        if (user.isPresent()) {
            List<Message> messages = messageRepository.findAllByToOrderBySentDateDesc(user.get());
            return MapperUtils.mapAll(messages, MessageDTO.class);
        }
        return new ArrayList<>();
    }

    public void sendMessage(MessageDispatchVM messageDispatchVM) {
        Message message = new Message();
        Optional<User> sender = userService.getCurrentUserObject();
        User recipient = userService.getUserObjectByLogin(messageDispatchVM.getTo());
        if (sender.isPresent() && recipient != null) {
            message.setContext(messageDispatchVM.getContent());
            message.setFrom(sender.get());
            message.setTo(recipient);
            message.setAdminOnlyChat(messageDispatchVM.getIsAdminChat());
            messageRepository.save(message);
            webSocketService.sendMessages(recipient.getLogin(), "Received new Message");
        } else {
            throw new RecipientNotExistException();
        }
    }
}
