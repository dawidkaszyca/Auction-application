package pl.dawid.kaszyca.exception;

public class MailSendingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MailSendingException() {
        super("Something went wrong during sending mail");
    }
}
