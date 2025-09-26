package app.exception;

public class CardLimitExceededException extends RuntimeException {

    public CardLimitExceededException() {
        super("Card limit has been reached for this customer.");
    }

    public CardLimitExceededException(String message) {
        super(message);
    }
}

