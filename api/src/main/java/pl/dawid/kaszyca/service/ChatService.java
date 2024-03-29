package pl.dawid.kaszyca.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.dawid.kaszyca.dto.MessageDTO;
import pl.dawid.kaszyca.exception.CannotSendEmptyMessageException;
import pl.dawid.kaszyca.exception.CannotSendMessageToYourselfException;
import pl.dawid.kaszyca.exception.LoginFromTokenDoNotMatchToConversationException;
import pl.dawid.kaszyca.exception.RecipientNotExistException;
import pl.dawid.kaszyca.model.Conversation;
import pl.dawid.kaszyca.model.Message;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.repository.ConversationRepository;
import pl.dawid.kaszyca.util.MapperUtil;
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

    StatisticService statisticService;

    public ChatService(UserService userService, ConversationRepository messageRepository, StatisticService statisticService) {
        this.userService = userService;
        this.conversationRepository = messageRepository;
        this.statisticService = statisticService;
    }

    @Autowired
    public void setWebSocketService(WebSocketService webSocketService) {
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
            conversationModel.add(convertConversationToVM(conversation));
        }
        return conversationModel;
    }

    public MessageDTO sendMessage(MessageDispatchVM messageDispatchVM) {
        Message message = new Message();
        if (!StringUtils.hasText(messageDispatchVM.getContent())) {
            throw new CannotSendEmptyMessageException();
        }
        Optional<User> sender = userService.getCurrentUserObject();
        User recipient = userService.getUserObjectById(messageDispatchVM.getTo());
        if (sender.isPresent() && recipient != null) {
            if (sender.get() == recipient) {
                throw new CannotSendMessageToYourselfException();
            }
            Conversation conversation = getConversationObjectToChat(sender.get(), recipient);
            List<Message> messages = conversation.getSentMessages();
            if (messages == null) {
                messages = new ArrayList<>();
            }
            message.setContent(messageDispatchVM.getContent());
            message.setConversation(conversation);
            messages.add(message);
            conversation.setSentMessages(messages);
            conversationRepository.save(conversation);
            webSocketService.sendMessages(recipient.getLogin(), conversation.getRecipientMessage().getId(), message);
            statisticService.incrementDailyMessages();
            return MapperUtil.map(message, MessageDTO.class);
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
            result = getAmountOfUnViewedConversation(listOfReceivedConversations);
        }
        return result;
    }

    private Integer getAmountOfUnViewedConversation(List<Conversation> listOfReceivedConversations) {
        Integer result = 0;
        for (Conversation conversation : listOfReceivedConversations) {
            if (checkIfListContainsUnViewedMessage(conversation.getSentMessages()))
                result++;
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
            setConversationMessagesAsDisplayed(conversation);
        }
    }

    private void setConversationMessagesAsDisplayed(Optional<Conversation> conversation) {
        if (conversation.isPresent()) {
            for (Message message : conversation.get().getSentMessages()) {
                message.setDisplayed(true);
            }
            conversationRepository.save(conversation.get());
        }
    }

    public ConversationVM getConversationById(long conversationId) {
        Optional<User> currentUser = userService.getCurrentUserObject();
        Optional<Conversation> conversationOptional = conversationRepository.findFirstById(conversationId);
        if (currentUser.isPresent() && conversationOptional.isPresent()) {
            Conversation conversation = conversationOptional.get();
            if (conversation.getSender() == currentUser.get())
                return convertConversationToVM(conversation);
            else
                throw new LoginFromTokenDoNotMatchToConversationException();
        }
        return null;
    }

    private ConversationVM convertConversationToVM(Conversation conversation) {
        ConversationVM conversationVM = new ConversationVM();
        conversationVM.setName(getFullNameFromRecipient(conversation.getRecipient()));
        conversationVM.setYourMessages(getMessagesDTOFromConversation(conversation));
        conversationVM.setPartnerMessages(getMessagesDTOFromConversation(conversation.getRecipientMessage()));
        conversationVM.setId(conversation.getId());
        conversationVM.setPartnerId(conversation.getRecipient().getId());
        return conversationVM;
    }

    private String getFullNameFromRecipient(User recipient) {
        return recipient.getFirstName() + " " + recipient.getLastName();
    }

    private List<MessageDTO> getMessagesDTOFromConversation(Conversation conversation) {
        List<MessageDTO> messages = MapperUtil.mapAll(conversation.getSentMessages(), MessageDTO.class);
        messages.sort(Comparator.comparing(MessageDTO::getSentDate));
        return messages;
    }
}
