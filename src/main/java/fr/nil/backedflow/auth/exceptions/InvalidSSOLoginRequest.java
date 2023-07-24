package fr.nil.backedflow.auth.exceptions;

public class InvalidSSOLoginRequest extends RuntimeException{

    /*
     * An exception that's thrown when the user request to login using Google SSO but the AZP doesn't match the Fuel-Finder AZP
     * Or that the AZP is not equal to the AUD.
     * */

    public InvalidSSOLoginRequest() {
        super("Invalid SSO Login request");
    }
}
