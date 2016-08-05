package com.xebia.exception;

/**
 * Exception related to authentication while connecting to server
 * should be handled in this Exception.
 */
public class AuthenticationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2051184481514949713L;

	public AuthenticationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	

}
