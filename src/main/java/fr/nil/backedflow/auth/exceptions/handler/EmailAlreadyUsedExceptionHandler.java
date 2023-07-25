package fr.nil.backedflow.auth.exceptions.handler;


import fr.nil.backedflow.auth.exceptions.EmailAlreadyUsedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor


/*
 * An Exception handler that handles an error and sent the error to the User as a response
 * */

public class EmailAlreadyUsedExceptionHandler {


    @ResponseBody
    @ExceptionHandler(EmailAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String emailAlreadyUsedException(EmailAlreadyUsedException e){
        return e.getMessage();
    }

}
