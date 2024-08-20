package org.intelli.intellimentor.controller.advice;


import org.intelli.intellimentor.controller.advice.exception.DuplicateDataException;
import org.intelli.intellimentor.util.CustomJWTException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(CustomJWTException.class)
    protected ResponseEntity<?> handleJWTException(CustomJWTException e){
        return ResponseEntity.ok().body(Map.of("error",e.getMessage()));
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("ERROR_MESSAGE", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("ERROR_MESSAGE", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateDataException(DuplicateDataException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("ERROR_MESSAGE", ex.getMessage()));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        //"Request body is missing, but the process completed successfully."
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("ERROR_MESSAGE", ex.getMessage()));
    }
}
