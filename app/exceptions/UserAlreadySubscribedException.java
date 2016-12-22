package exceptions;

public class UserAlreadySubscribedException extends Exception {

	private static final long serialVersionUID = -7407838480386493387L;

	public UserAlreadySubscribedException(String message) {
        super(message);
    }
	
}
