package pl.dawid.kaszyca.exception;

public class RecipientNotExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RecipientNotExistException() {
        super("Cannot find user to send new message");
    }

}
