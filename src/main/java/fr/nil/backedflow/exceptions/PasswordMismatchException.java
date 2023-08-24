package fr.nil.backedflow.exceptions;

public class PasswordMismatchException extends Exception {

    public PasswordMismatchException() {
        super("Password doesn't match");
    }
}
