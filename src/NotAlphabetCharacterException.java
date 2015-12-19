
/**
 * This class extends the Exception class, and is used to tell the user that, when playing Hangman,
 * his/her input is not an alphabetic character, which is not a valid guess option.
 * @author Cristobal Padilla
 */ 

public class NotAlphabetCharacterException extends Exception {
	
	/**
	 * No-args constructor, which uses a call to the Exception Class's constructor,
	 * passing a string argument message.
	 */
	NotAlphabetCharacterException()  {
		super("Error: You entered an invalid character. Only enter valid alphabet character, as indicated above.");
		
	}
}
