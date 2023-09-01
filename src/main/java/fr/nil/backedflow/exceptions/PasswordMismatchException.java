package fr.nil.backedflow.exceptions;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super("Password doesn't match");
    }
}
