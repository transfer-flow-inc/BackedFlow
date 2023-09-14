package fr.nil.backedflow.exceptions;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException(String message) {
        super("Password doesn't match : " + message);
    }

    public PasswordMismatchException() {
        super("Password doesn't match");
    }
}
