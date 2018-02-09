package org.celllife.idart.integration.idartweb;

/**
 * Exception that indicates that something went wrong when trying to communicate with idartweb.
 *
 * This exception is used to indicate to the rest of the application that previous actions
 * should be rolled back or compensating actions should be performed.
 */
public class IdartWebException extends Exception {

	private static final long serialVersionUID = -6440254264364931800L;

	public IdartWebException(String message, Throwable cause) {
		super(message, cause);
	}

	public IdartWebException(String message) {
		super(message);
	}

	public IdartWebException(Throwable cause) {
		super(cause);
	}
}
