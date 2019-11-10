package app.exception;

public class NoSuchAccountException extends RuntimeException {
    public NoSuchAccountException(long id) {
        super("There is no account with id: " + id);
    }
}
