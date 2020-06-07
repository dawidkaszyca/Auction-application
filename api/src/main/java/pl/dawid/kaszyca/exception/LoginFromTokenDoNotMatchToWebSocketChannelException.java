package pl.dawid.kaszyca.exception;

public class LoginFromTokenDoNotMatchToWebSocketChannelException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoginFromTokenDoNotMatchToWebSocketChannelException() {
        super("Someone tried to connect to other chanel than is provide in token");
    }
}
