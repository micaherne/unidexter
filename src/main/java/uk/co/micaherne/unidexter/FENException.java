package uk.co.micaherne.unidexter;

import uk.co.micaherne.unidexter.notation.NotationException;

public class FENException extends NotationException {

	private static final long serialVersionUID = 1L;

	public FENException(String message) {
		super(message);
	}

	public FENException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FENException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public FENException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
