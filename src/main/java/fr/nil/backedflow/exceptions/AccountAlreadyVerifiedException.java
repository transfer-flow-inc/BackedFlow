package fr.nil.backedflow.exceptions;

public class AccountAlreadyVerifiedException extends RuntimeException {
    public AccountAlreadyVerifiedException() {
        super("Can't verify account : The account is already verified");
    }
}
