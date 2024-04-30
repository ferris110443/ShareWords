package org.yplin.project.error;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handler for MalformedJwtException
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException ex, WebRequest request) {
        ErrorResponse errorDetails = new ErrorResponse("Invalid JWT token");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorDetails.getMessage()));
    }

}