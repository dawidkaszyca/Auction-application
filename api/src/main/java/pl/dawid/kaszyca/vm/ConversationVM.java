package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dawid.kaszyca.dto.MessageDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConversationVM {

    Long id;

    String name;

    Long partnerId;

    List<MessageDTO> partnerMessages;

    List<MessageDTO> yourMessages;

}
