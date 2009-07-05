package base.exception;

/**
 * Throw a CodeException when something seemingly impossible has happened because of a mistake in the code.
 * When you catch a CodeException, close the program and try to contact the programmer.
 */
public class CodeException extends MyException {
	
	// Make
	
	public CodeException() {
		this.message = null;
		this.exception = null;
	}
	
	public CodeException(String message) {
		this.message = message;
		this.exception = null;
	}
	
	public CodeException(Exception exception) {
		this.message = null;
		this.exception = exception;
	}
	
	public CodeException(String message, Exception exception) {
		this.message = message;
		this.exception = exception;
	}
	
	// Look

	/** A short text message that describes what happened, or null if none. */
	public final String message;
	/** The Java exception that happened first and that we wrapped in this one, or null if none. */
	public final Exception exception;
}
