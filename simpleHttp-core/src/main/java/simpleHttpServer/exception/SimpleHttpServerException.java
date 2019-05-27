package simpleHttpServer.exception;

public class SimpleHttpServerException extends RuntimeException {
    public SimpleHttpServerException() {
        super();
    }

    public SimpleHttpServerException(String message) {
        super(message);
    }

    public SimpleHttpServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleHttpServerException(Throwable cause) {
        super(cause);
    }

    protected SimpleHttpServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
