package fr.nil.backedflow.exceptions.handler;

import fr.nil.backedflow.exceptions.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class PasswordMismatchExceptionHandler {

    @ResponseBody
    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String passwordMismatchException(PasswordMismatchException e) {
        return e.getMessage();
    }
}