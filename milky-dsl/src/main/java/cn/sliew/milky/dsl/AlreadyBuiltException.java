package cn.sliew.milky.dsl;

/**
 * Thrown when {@link AbstractBuilder#build()} is two or more times.
 */
public class AlreadyBuiltException extends IllegalStateException {

	private static final long serialVersionUID = -5891004752785553015L;

	public AlreadyBuiltException() {

	}

	public AlreadyBuiltException(String message) {
		super(message);
	}

	public AlreadyBuiltException(Throwable cause) {
		super(cause);
	}

	public AlreadyBuiltException(String message, Throwable cause) {
		super(message, cause);
	}
}
