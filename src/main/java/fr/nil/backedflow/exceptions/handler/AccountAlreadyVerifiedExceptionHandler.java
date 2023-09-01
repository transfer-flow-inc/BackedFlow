package fr.nil.backedflow.exceptions.handler;

import fr.nil.backedflow.exceptions.AccountAlreadyVerifiedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class AccountAlreadyVerifiedExceptionHandler {

    @ResponseBody
    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String accountAlreadyVerifiedException(AccountAlreadyVerifiedException e) {
        return e.getMessage();
    }
}