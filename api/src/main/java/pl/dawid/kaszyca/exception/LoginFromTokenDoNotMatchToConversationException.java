package pl.dawid.kaszyca.exception;

public class LoginFromTokenDoNotMatchToConversationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoginFromTokenDoNotMatchToConversationException() {
        super("Someone tried to get someone else conversation by id");
    }
}
