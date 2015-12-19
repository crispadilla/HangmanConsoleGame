
/**
 * This class extends the Exception class, and is used to tell the user that, when asking for a character guess,
 * his/her input has been entered before in the current game. 
 * @author Cristobal Padilla
 */
public class PreviouslyUsedCharacterException extends Exception{

	/**
	 * No-args constructor, which uses a call to the Exception Class's constructor, 
	 * passing a string argument message.
	 */
	public PreviouslyUsedCharacterException(char validUserGuess) {
		super("You've used '" + validUserGuess + "' before!");
		
	}
}
