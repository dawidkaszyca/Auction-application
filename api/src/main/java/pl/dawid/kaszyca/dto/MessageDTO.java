package pl.dawid.kaszyca.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {

    private String content;

    private boolean displayed;

    private Instant sentDate;
}
