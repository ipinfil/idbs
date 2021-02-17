package exceptions;

public class ApplicationNotFoundException extends Exception {
    public ApplicationNotFoundException(String message) {
        super(message);
    }
}
