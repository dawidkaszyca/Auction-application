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
        List<MessageDTO> messages = MapperUtils.mapAll(conversation.getSentMessages(), MessageDTO.class);
        messages.sort(Comparator.comparing(MessageDTO::getSentDate));
        return messages;
    }

    private String getFullNameFromRecipient(User recipient) {
        return recipient.getFirstName() + " " + recipient.getLastName();
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
            webSocketService.sendMessages(recipient.getLogin(), conversation.getRecipientMessage().getId(), message);
            return MapperUtils.map(message, MessageDTO.class);
        } else {
            throw new RecipientNotExistException();
        }
    }

    private Conversation getConversationObjectToChat(User sender, User recipient) {
        Optional<Conversation> conversation = conversationRepository.findFirstBySenderAndRecipient(sender, recipient);
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

    public Integer getAmountOfUnViewedMessage(String login) {
        Optional<User> user = userService.getUserByLogin(login);
        Integer result = 0;
        if (user.isPresent()) {
            List<Conversation> listOfReceivedConversations = conversationRepository.findAllByRecipient(user.get());
            for (Conversation conversation : listOfReceivedConversations) {
                if (checkIfListContainsUnViewedMessage(conversation.getSentMessages()))
                    result++;
            }
        }
        return result;
    }

    private boolean checkIfListContainsUnViewedMessage(List<Message> messages) {
        for (Message msg : messages) {
            if (!msg.isDisplayed())
                return true;
        }
        return false;
    }

    public void updateDisplayMessagesById(Long id) {
        Optional<User> recipient = userService.getCurrentUserObject();
        User sender = userService.getUserObjectById(id);
        if (recipient.isPresent() && sender != null) {
            Optional<Conversation> conversation = conversationRepository.findFirstBySenderAndRecipient(sender, recipient.get());
            if (conversation.isPresent()) {
                for (Message message : conversation.get().getSentMessages()) {
                    message.setDisplayed(true);
                }
                conversationRepository.save(conversation.get());
            }
        }
    }
}
