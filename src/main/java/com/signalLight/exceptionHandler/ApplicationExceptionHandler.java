package com.signalLight.exceptionHandler;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.signalLight.exceptionHandler.custom.BadRequestException;
import com.signalLight.exceptionHandler.custom.CloudUploadException;
import com.signalLight.exceptionHandler.custom.OperationNotAllowedException;
import com.signalLight.exceptionHandler.custom.ResourceNotFoundException;
import com.signalLight.exceptionHandler.model.ApiError;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;

@lombok.extern.slf4j.Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@ComponentScan
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Build response for the exception handler
   * @param apiError
   * @return
   * @author Ashish Chauhan
   * @last_modified 25 Jan,2024
   */
  private ResponseEntity<Object> buildResponse(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  /**
   * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid
   * validation.
   * 
   * @param ex      the MethodArgumentNotValidException that is thrown when @Valid
   *                validation fails
   * @param headers HttpHeaders
   * @param status  HttpStatus
   * @param request WebRequest
   * @return the {@link ApiError} object
   */
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, 
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    
    log.error(ex.getMessage(), ex);

    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.addValidationErrors(fieldErrors);

    return buildResponse(apiError);
  }

  /**
   * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
   *
   * @param ex HttpMessageNotReadableException
   * @param headers HttpHeaders
   * @param status  HttpStatus
   * @param request WebRequest
   * @return the {@link ApiError} object
   */
 
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
      
    log.error(ex.getMessage(), ex);  
    return buildResponse(new ApiError(HttpStatus.BAD_REQUEST, "MALFORMED_JSON_REQUEST", ex));
  }

  /**
   * Handle BadRequestException. Happens when something wrong received from client.
   * @param ex BadRequestException
   * @param request WebRequest
   * @return
   */
  @ExceptionHandler(BadRequestException.class)
  protected ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
    log.error(ex.getMessage(), ex);
    return buildResponse(new ApiError(HttpStatus.BAD_REQUEST, ex));
  }

  /**
   * Handle ResourceAccessException. Happens when the requested resource is not accessible/available
   * 
   * @param ex ResourceAccessException
   * @return
   */
  @ExceptionHandler(ResourceAccessException.class)
  public ResponseEntity<Object> handleResourceAccessException(ResourceAccessException ex) {
    log.error(ex.getMessage(), ex);
    return buildResponse(new ApiError(HttpStatus.NOT_FOUND, ex));
  }

  /**
   * Handle ResourceNotFoundException. Happens when the requested resource is not available
   * 
   * @param ex ResourceNotFoundException
   * @return
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
    log.error(ex.getMessage(), ex);
    return buildResponse(new ApiError(HttpStatus.NOT_FOUND, ex));
  }

  @ExceptionHandler(WebClientResponseException.class)
  public ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex) {
    log.error("WebClient status code {} , body parameter {}", ex.getStatusCode(), ex.getResponseBodyAsString());
    return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
    log.error(ex.getMessage(), ex);

    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.addValidationErrors(ex.getConstraintViolations());
    return buildResponse(apiError);
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
    log.error(ex.getMessage(), ex);
    return buildResponse(new ApiError(HttpStatus.UNAUTHORIZED, "User token expired", ex));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
    log.error(ex.getMessage(), ex);

    return buildResponse(new ApiError(HttpStatus.BAD_REQUEST, "Exceeds max file size uploaded", ex));
  }

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<Object> handleBadRequest(AccessDeniedException ex) {
    log.error(ex.getMessage(), ex);
    return buildResponse(new ApiError(HttpStatus.BAD_REQUEST, ex));
  }

  @ExceptionHandler(OperationNotAllowedException.class)
  public ResponseEntity<Object> handleOperationNotAllowedException(OperationNotAllowedException ex){
    log.error(ex.getMessage(), ex);
    return buildResponse(new ApiError(HttpStatus.NOT_FOUND, ex));
  }

  @ExceptionHandler(CloudUploadException.class)
  public ResponseEntity<Object> handleCloudUploadException(CloudUploadException ex){
    log.error(ex.getMessage(), ex);
    return buildResponse(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error(ex.getMessage(), ex);
    return buildResponse(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<Object> handleIOException(IOException ex) {
    log.error(ex.getMessage(), ex);
    return buildResponse(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
  }  
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex){
    	log.error(ex.getMessage(), ex);
    	return buildResponse(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,ex));
    }
    
}


