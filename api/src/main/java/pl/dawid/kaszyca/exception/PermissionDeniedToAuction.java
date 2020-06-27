package pl.dawid.kaszyca.exception;

public class PermissionDeniedToAuction extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PermissionDeniedToAuction() {
        super("Invalid permission to auction");
    }

}
