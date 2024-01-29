package com.signalLight.exceptionHandler.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

/**
 * To initialize body for passing custom error messages in json format
 * @author Ashish Chauhan
 */
@Getter
@JsonTypeName("error")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class ApiError {

  private HttpStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;
  private List<ApiFieldError> errors;
  private String message;
  private String debugMessage;

  private ApiError() {
    timestamp = LocalDateTime.now();
  }

  public ApiError(HttpStatus status) {
    this();
    this.status = status;
  }

  public ApiError(HttpStatus status, String message, Throwable cause) {
    this();
    this.status = status;
    this.message = message;
    this.debugMessage = cause != null? cause.getLocalizedMessage(): null;
  }

  public ApiError(HttpStatus status, Throwable ex) {
    this();
    this.status = status;
    this.message = ex.getMessage();
    this.debugMessage = ex.getLocalizedMessage();
  }

  /**
   * Add error fields.
   * 
   * @param error error field
   * @author CHiRAG RATHOD
   */
  private void addSubError(ApiFieldError error) {
    if (errors == null) {
      errors = new ArrayList<>();
    }

    errors.add(error);
  }

  /**
   * Add a validation to the error field.
   * 
   * @param fieldErrors list of error fields
   * @author CHiRAG RATHOD
   */
  public void addValidationErrors(List<FieldError> fieldErrors) {
    fieldErrors.forEach(field -> 
      addSubError(new ApiFieldError(field.getObjectName(), field.getField(), field.getDefaultMessage())));
  }

  private void addValidationError(String object, String field, Object rejectedValue, String message) {
    addSubError(new ApiFieldError(object, field, message));
  }

  /**
    * Utility method for adding error of ConstraintViolation. Usually when a @Validated validation fails.
    *
    * @param cv the ConstraintViolation
    */
  private void addValidationError(ConstraintViolation<?> cv) {
    this.addValidationError(
      cv.getRootBeanClass().getSimpleName(),
      ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
      cv.getInvalidValue(),
      cv.getMessage());
  }

  public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
      constraintViolations.forEach(this::addValidationError);
  }
}
