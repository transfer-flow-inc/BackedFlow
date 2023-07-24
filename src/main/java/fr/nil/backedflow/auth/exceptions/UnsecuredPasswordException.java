package fr.nil.backedflow.auth.exceptions;

/*
 * An exception that's thrown when the user request to create an account but the password is too weak.
 * */

public class UnsecuredPasswordException extends RuntimeException{
    public UnsecuredPasswordException() {
        super("Password is too weak, requires at least 12 characters.");
    }
}
