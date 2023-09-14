package fr.nil.backedflow.exceptions;

public class UnverifiedLoginException extends RuntimeException {

    public UnverifiedLoginException() {
        super("Cannot login, your account is not verified.");
    }
}
