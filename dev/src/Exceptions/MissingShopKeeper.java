package Exceptions;

public class MissingShopKeeper extends RuntimeException {
    public MissingShopKeeper(String message) {

        super(message);
    }
}
