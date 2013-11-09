package server;

public class LoginExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public LoginExistsException() { }
	
	public LoginExistsException(final String string) {
		super(string);
	}
}
