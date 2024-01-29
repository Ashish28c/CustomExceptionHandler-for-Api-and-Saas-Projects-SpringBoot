package com.signalLight.exceptionHandler.custom;

/**
 * To pass custom message for CloudUpload Exception
 * @param message
 * @param cause
 * @author Ashish Chauhan
 */
public class CloudUploadException extends RuntimeException {
	  
	  private static final long serialVersionUID = 1L;

	public CloudUploadException(String message) {
	    super(message);
	  }

	  public CloudUploadException(String message, Throwable cause) {
	    super(message, cause);
	  }
	}
