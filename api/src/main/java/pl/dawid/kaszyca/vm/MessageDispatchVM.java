package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDispatchVM {

    public String to;
    public String content;
    public Boolean isAdminChat = false;
}
