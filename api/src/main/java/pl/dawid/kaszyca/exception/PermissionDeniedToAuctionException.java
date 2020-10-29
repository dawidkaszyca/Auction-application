package pl.dawid.kaszyca.exception;

public class PermissionDeniedToAuctionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PermissionDeniedToAuctionException() {
        super("Invalid permission to auction");
    }

}
