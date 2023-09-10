package fr.nil.backedflow.exceptions.handler;

import fr.nil.backedflow.exceptions.UnauthorizedFolderCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class UnauthorizedFolderCreationHandler {

    @ResponseBody
    @ExceptionHandler(UnauthorizedFolderCreationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unauthorizedFolderCreationException(UnauthorizedFolderCreationException e) {
        return e.getMessage();
    }

}
