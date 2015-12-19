
/**
 * This class extends the Exception class, and is used to tell the user that, when asked to continue playing,
 * his/her input is not valid. 
 * @author Cristobal Padilla
 */
public class InvalidKeepPlayingInputException extends Exception{

	/**
	 * No-args constructor, which uses a call to the Exception Class's constructor, 
	 * passing a string argument message.
	 */
	public InvalidKeepPlayingInputException() {
		super("Error: Invalid Input!");
		
	}
}
