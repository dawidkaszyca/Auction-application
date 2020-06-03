package pl.dawid.kaszyca.service;

import org.springframework.stereotype.Service;
import pl.dawid.kaszyca.dto.MessageDTO;
import pl.dawid.kaszyca.exception.RecipientNotExistException;
import pl.dawid.kaszyca.model.Conversation;
import pl.dawid.kaszyca.model.Message;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.repository.ConversationRepository;
import pl.dawid.kaszyca.util.MapperUtils;
import pl.dawid.kaszyca.vm.ConversationVM;
import pl.dawid.kaszyca.vm.MessageDispatchVM;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    UserService userService;
    ConversationRepository conversationRepository;
    WebSocketService webSocketService;

    public ChatService(UserService userService, ConversationRepository messageRepository, WebSocketService webSocketService) {
        this.userService = userService;
        this.conversationRepository = messageRepository;
        this.webSocketService = webSocketService;
    }

    public List<ConversationVM> getAllMessageRelatedWithCurrentUser() {
        Optional<User> user = userService.getCurrentUserObject();
        if (user.isPresent()) {
            List<Conversation> conversations = conversationRepository.findAllBySender(user.get());
            return prepareDataToModel(conversations);
        }
        return new ArrayList<>();
    }

    private List<ConversationVM> prepareDataToModel(List<Conversation> conversations) {
        List<ConversationVM> conversationModel = new ArrayList<>();
        for (Conversation conversation : conversations) {
            ConversationVM conversationVM = new ConversationVM();
            conversationVM.setName(getFullNameFromRecipient(conversation.getRecipient()));
            conversationVM.setYourMessages(getMessagesDTOFromConversation(conversation));
            conversationVM.setPartnerMessages(getMessagesDTOFromConversation(conversation.getRecipientMessage()));
            conversationVM.setId(conversation.getId());
            conversationVM.setPartnerId(conversation.getRecipient().getId());
            conversationModel.add(conversationVM);
        }
        return conversationModel;
    }

    private List<MessageDTO> getMessagesDTOFromConversation(Conversation conversation) {
        List<MessageDTO> messages =  MapperUtils.mapAll(conversation.getSentMessages(), MessageDTO.class);
         messages.sort(Comparator.comparing(MessageDTO::getSentDate));
        return messages;
    }

    private String getFullNameFromRecipient(User recipient) {
        return recipient.getFirstName() + " " + recipient.getFirstName();
    }

    public MessageDTO sendMessage(MessageDispatchVM messageDispatchVM) {
        Message message = new Message();
        Optional<User> sender = userService.getCurrentUserObject();
        User recipient = userService.getUserObjectById(messageDispatchVM.getTo());
        if (sender.isPresent() && recipient != null) {
            Conversation conversation = getConversationObjectToChat(sender.get(), recipient);
            List<Message> messages = conversation.getSentMessages();
            if (messages == null) {
                messages = new ArrayList<>();
            }
            message.setContent(messageDispatchVM.getContent());
            message.setConversation(conversation);
            messages.add(message);
            conversationRepository.save(conversation);
            webSocketService.sendMessages(recipient.getLogin(), getFullNameFromRecipient(sender.get()));
            return MapperUtils.map(message, MessageDTO.class);
        } else {
            throw new RecipientNotExistException();
        }
    }

    private Conversation getConversationObjectToChat(User sender, User recipient) {
        Optional<Conversation> conversation = conversationRepository.findFirstBySenderAndRecipient(recipient, sender);
        if (conversation.isPresent())
            return conversation.get();
        return createTwoSideConversationObjectAndGetForCurrentUser(sender, recipient);
    }

    private Conversation createTwoSideConversationObjectAndGetForCurrentUser(User sender, User recipient) {
        Conversation recipientObj = new Conversation();
        Conversation currentUserObj = new Conversation();
        setRecipientAndSender(recipientObj, recipient, sender, currentUserObj);
        setRecipientAndSender(currentUserObj, sender, recipient, recipientObj);
        conversationRepository.save(recipientObj);
        return currentUserObj;
    }

    private void setRecipientAndSender(Conversation conversation, User sender, User recipient, Conversation partnerObj) {
        conversation.setSender(sender);
        conversation.setRecipient(recipient);
        conversation.setRecipientMessage(partnerObj);
    }
}
