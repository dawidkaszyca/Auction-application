package pl.dawid.kaszyca.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Instant;

public class MessageDTO {

    private Long id;

    private UserBaseDTO from;

    private UserBaseDTO to;

    private String context;

    private boolean displayed = false;

    private boolean isAdminOnlyChat = false;

    @CreatedDate
    @Column(name = "sent_date", updatable = false)
    @JsonIgnore
    private Instant sentDate = Instant.now();
}
