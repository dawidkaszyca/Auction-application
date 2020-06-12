package pl.dawid.kaszyca.exception;

public class CannotSendEmptyMessageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CannotSendEmptyMessageException() {
        super("Cannot send empty message!!!");
    }

}
