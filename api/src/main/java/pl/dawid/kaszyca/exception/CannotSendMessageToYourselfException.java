package pl.dawid.kaszyca.exception;

public class CannotSendMessageToYourselfException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CannotSendMessageToYourselfException() {
        super("Cannot send message to Yourself!!!");
    }

}
