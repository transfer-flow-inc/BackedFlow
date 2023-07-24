package fr.nil.backedflow.auth.exceptions;

public class InvalidEmailException extends RuntimeException {

    /*
     * An exception that's thrown when the user request to create an account but the email is invalid (i.e missing @ )
     * */

    public InvalidEmailException() {
        super("The provided email is incorrect.");

    }
}
