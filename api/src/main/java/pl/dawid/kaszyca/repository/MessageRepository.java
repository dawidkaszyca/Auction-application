package pl.dawid.kaszyca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.dawid.kaszyca.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}

