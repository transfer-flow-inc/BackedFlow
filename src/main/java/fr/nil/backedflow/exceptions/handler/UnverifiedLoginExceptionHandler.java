package fr.nil.backedflow.exceptions.handler;


import fr.nil.backedflow.exceptions.UnverifiedLoginException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor

public class UnverifiedLoginExceptionHandler {

    @ResponseBody
    @ExceptionHandler(UnverifiedLoginException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String unverifiedLoginException(UnverifiedLoginException e) {
        return e.getMessage();
    }

}
