package pl.dawid.kaszyca.vm;

import lombok.Data;
import pl.dawid.kaszyca.dto.MessageDTO;

@Data
public class StatusVM {
    Long id;
    MessageDTO message;
}
