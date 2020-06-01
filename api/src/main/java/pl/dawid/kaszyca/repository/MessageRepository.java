package pl.dawid.kaszyca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dawid.kaszyca.model.Message;
import pl.dawid.kaszyca.model.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByToOrderBySentDateDesc(User user);
}
