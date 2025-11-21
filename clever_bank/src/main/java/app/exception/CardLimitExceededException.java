package app.exception;

public class CardLimitExceededException extends RuntimeException {

    public CardLimitExceededException() {
    }

    public CardLimitExceededException(String message) {
        super(message);
    }

}

