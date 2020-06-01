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

    @OneToOne
    private User from;

    @OneToOne
    private User to;

    private String context;

    private boolean displayed = false;

    private boolean isAdminOnlyChat = false;

    @CreatedDate
    @Column(name = "sent_date", updatable = false)
    @JsonIgnore
    private Instant sentDate = Instant.now();
}
