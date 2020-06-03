package pl.dawid.kaszyca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.dawid.kaszyca.model.Conversation;
import pl.dawid.kaszyca.model.User;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findAllBySender(User user);

    Optional<Conversation> findFirstBySenderAndRecipient(User sender, User recipient);
}
