package com.example.task_manager.exception;

import javax.persistence.EntityNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static class ErrorBody {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private List<Map<String,String>> fieldErrors;

        public ErrorBody() {}

        public ErrorBody(Instant timestamp, int status, String error, String message, String path, List<Map<String,String>> fieldErrors) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.fieldErrors = fieldErrors;
        }

        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public List<Map<String,String>> getFieldErrors() { return fieldErrors; }
        public void setFieldErrors(List<Map<String,String>> fieldErrors) { this.fieldErrors = fieldErrors; }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex,
                                                      org.springframework.web.context.request.WebRequest req) {
        List<Map<String, String>> fe = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> {
                    java.util.Map<String, String> map = new java.util.HashMap<>();
                    map.put("field", f.getField());
                    map.put("message", f.getDefaultMessage());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(new ErrorBody(Instant.now(), 400, "Bad Request", "Validation failed", path(req), fe));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorBody> handleNotFound(EntityNotFoundException ex,
                                                    org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(new ErrorBody(Instant.now(), 404, "Not Found", ex.getMessage(), path(req), java.util.Collections.emptyList()));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorBody> handleConflict(OptimisticLockingFailureException ex,
                                                    org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(new ErrorBody(Instant.now(), 409, "Conflict", "Version conflict", path(req), java.util.Collections.emptyList()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneric(Exception ex,
                                                   org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(new ErrorBody(Instant.now(), 500, "Internal Server Error", "Unexpected error", path(req), java.util.Collections.emptyList()));
    }

    private String path(org.springframework.web.context.request.WebRequest req) {
        String d = req.getDescription(false); // uri=/api/...
        return d != null && d.startsWith("uri=") ? d.substring(4) : d;
    }
}
