package fr.nil.backedflow.exceptions.handler;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {
private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> exception(Exception exception) {
        logger.error("An error has occurred during a HTTP request : " + exception.getMessage());
        logger.debug("Exception happened during a HTTP request : " + Arrays.toString(exception.getStackTrace()));
        return new ResponseEntity<>("Well that's cringe, something wrong happened try refresh or contact admin@transfer-flow.com", HttpStatus.INTERNAL_SERVER_ERROR);

    }
}