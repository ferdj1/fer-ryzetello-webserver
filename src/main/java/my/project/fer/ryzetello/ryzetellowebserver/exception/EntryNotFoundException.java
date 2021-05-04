package my.project.fer.ryzetello.ryzetellowebserver.exception;

public class EntryNotFoundException extends RuntimeException {

    public EntryNotFoundException() {
    }

    public EntryNotFoundException(final String message) {
        super(message);
    }

    public EntryNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EntryNotFoundException(final Throwable cause) {
        super(cause);
    }

    public EntryNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
