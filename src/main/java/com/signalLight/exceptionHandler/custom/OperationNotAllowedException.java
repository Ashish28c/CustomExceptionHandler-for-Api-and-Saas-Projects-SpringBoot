package com.signalLight.exceptionHandler.custom;
/**
 * To pass custom error message for Operation not allowed exception
 * @param message
 * @param cause
 * @author Ashish Chauhan
 */
public class OperationNotAllowedException extends RuntimeException {
	  
	  private static final long serialVersionUID = 1L;

	public OperationNotAllowedException(String message) {
	    super(message);
	  }

	  public OperationNotAllowedException(String message, Throwable cause) {
	    super(message, cause);
	  }
	}
