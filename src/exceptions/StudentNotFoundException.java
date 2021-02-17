package exceptions;

public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String errMessage) {
        super(errMessage);
    }

    public StudentNotFoundException() {
        super();
    }
}
