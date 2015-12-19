
/**
 * This class extends the Exception class, and is used to tell the user that, when playing Hangman,
 * his/her input contains more than one character, which is not a valid guess option.
 * @author Cristobal Padilla
 */

public class MultipleCharsEnteredException extends Exception {
	
	/**
	 * No-args constructor, which uses a call to the Exception Class's constructor,
	 * passing a string argument message.
	 */
	MultipleCharsEnteredException () {
		super("Error: You entered multiple characters. Single-character guesses only.");
		
	}
}
