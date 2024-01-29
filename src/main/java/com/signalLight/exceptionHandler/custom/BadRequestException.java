package com.signalLight.exceptionHandler.custom;

/**
 * To pass custom BadRequest message
 * @param message
 * @param cause
 * @return
 * @author Ashish Chauhan
 */
public class BadRequestException extends RuntimeException {
	  
	  private static final long serialVersionUID = 1L;

	public BadRequestException(String message) {
	    super(message);
	  }

	  public BadRequestException(String message, Throwable cause) {
	    super(message, cause);
	  }
	}