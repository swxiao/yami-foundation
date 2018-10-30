package com.bubble.foundation.datagram.exception;

public class DatagramException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2139872581220417340L;

	public DatagramException() {
		super();
	}

	public DatagramException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DatagramException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatagramException(String message) {
		super(message);
	}

	public DatagramException(Throwable cause) {
		super(cause);
	}

}
