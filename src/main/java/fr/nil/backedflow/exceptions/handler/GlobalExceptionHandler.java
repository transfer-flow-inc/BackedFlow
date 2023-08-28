package fr.nil.backedflow.exceptions.handler;

import fr.nil.backedflow.stats.MetricsEnum;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final MeterRegistry meterRegistry;

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> exception(Exception exception) {
        logger.error("An error has occurred during a HTTP request : " + exception.getMessage());
        logger.debug("Exception happened during a HTTP request : ");
        exception.printStackTrace();
        meterRegistry.counter(MetricsEnum.ERROR_HANDLER_COUNT.getMetricName()).increment();
        return new ResponseEntity<>("Well that's cringe, something wrong happened try refresh or contact admin@transfer-flow. (Exception Message: " + exception.getMessage() + ").", HttpStatus.INTERNAL_SERVER_ERROR);

    }
}