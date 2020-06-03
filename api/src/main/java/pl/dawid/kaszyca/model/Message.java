package pl.dawid.kaszyca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    private String content;

    private boolean displayed = false;

    @CreatedDate
    @Column(name = "sent_date", updatable = false)
    @JsonIgnore
    private Instant sentDate = Instant.now();
}
