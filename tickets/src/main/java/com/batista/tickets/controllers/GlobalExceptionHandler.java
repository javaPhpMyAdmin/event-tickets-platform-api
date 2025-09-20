package com.batista.tickets.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.batista.tickets.domain.dtos.ErrorDTO;
import com.batista.tickets.exceptions.UserNotFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorDTO> handleUserNotFoundException(UserNotFoundException ex) {
    log.error("Caught UserNotFoundException", ex);
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setError("User not found");

    return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

    log.error("Caught MethodArgumentNotValidException", ex);
    ErrorDTO errorDTO = new ErrorDTO();

    BindingResult bindingResult = ex.getBindingResult();
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();
    String errorMessage = fieldErrors.stream().findFirst()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .orElse("Validation error occurred");

    errorDTO.setError(errorMessage);

    return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorDTO> handleConstraintViolation(ConstraintViolationException ex) {

    log.error("Caught ConstraintViolationException", ex);
    ErrorDTO errorDTO = new ErrorDTO();

    String errorMessage = ex.getConstraintViolations().stream().findFirst()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .orElse("Constraint violation occurred");

    errorDTO.setError(errorMessage);

    return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(exception = Exception.class)
  public ResponseEntity<ErrorDTO> handleException(Exception ex) {
    log.error("Caught Exception", ex);
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setError("An unknown error occurred");
    return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
