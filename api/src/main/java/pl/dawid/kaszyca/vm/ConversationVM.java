package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dawid.kaszyca.dto.MessageDTO;

import javax.persistence.OneToOne;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConversationVM {

    private Long id;

    @OneToOne
    private String name;

    private Long partnerId;

    private List<MessageDTO> partnerMessages;

    private List<MessageDTO> yourMessages;

}
