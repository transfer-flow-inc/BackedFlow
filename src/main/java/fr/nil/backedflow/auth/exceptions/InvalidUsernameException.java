package fr.nil.backedflow.auth.exceptions;

import java.util.Arrays;
import java.util.List;


/*
 * An exception that's thrown when the user request to create an account but the name request is included in the blacklist.
 * */

public class InvalidUsernameException extends RuntimeException{


   final static String[] blacklistedUsernames = new String[] { "admin", "administrator", "root", "sysadmin", "test", "guest", "demo", "support", "help", "api", "guestuser", "anonymous", "default","sql","mysql","user","username" };
    public static final List<String> BLACKLISTED_USERNAME_LIST = Arrays.asList(blacklistedUsernames);

    public InvalidUsernameException(String providedUsername) {
        super("Provided username is invalid. (Username : " + providedUsername + ").");
    }
}
