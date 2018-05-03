package com.example.exceptions;

public class NotImplementedException extends UnsupportedOperationException {

	private static final long serialVersionUID = -1845615491500871643L;

	public NotImplementedException() {
	}

	public NotImplementedException(String message) {
		super(message);
	}

	public NotImplementedException(Throwable cause) {
		super(cause);
	}

	public NotImplementedException(String message, Throwable cause) {
		super(message, cause);
	}

}
