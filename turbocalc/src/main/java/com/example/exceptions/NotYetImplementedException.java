package com.example.exceptions;

/**
 * Exception which can be thrown to indicate that a piece of code is a stub.
 */
public class NotYetImplementedException extends NotImplementedException {

	private static final long serialVersionUID = -6592564834854431314L;

	public NotYetImplementedException() {
	}

	public NotYetImplementedException(String message) {
		super(message);
	}

	public NotYetImplementedException(Throwable cause) {
		super(cause);
	}

	public NotYetImplementedException(String message, Throwable cause) {
		super(message, cause);
	}

}
