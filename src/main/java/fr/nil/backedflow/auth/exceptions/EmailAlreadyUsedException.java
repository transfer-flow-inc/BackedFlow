package fr.nil.backedflow.auth.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {

    /*
    * An exception that's thrown when the user request to create an account but the email is already registered onto the DB
    * */

    public EmailAlreadyUsedException() {
        super("This email is already used.");
    }
}
