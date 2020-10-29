package pl.dawid.kaszyca.exception;

public class AuctionNotExistException  extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuctionNotExistException() {
        super("Cannot find auction");
    }

}
