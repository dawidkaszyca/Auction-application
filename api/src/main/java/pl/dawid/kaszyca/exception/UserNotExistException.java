package pl.dawid.kaszyca.exception;

public class UserNotExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserNotExistException() {
        super("Cannot found user by activation key");
    }
}
