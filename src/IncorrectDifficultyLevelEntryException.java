
/**
 * This class extends the Exception class, and is used to tell the user that, when asking for a difficulty level,
 * his/her input is not a valid option. This is a personalized exception mainly used for the
 * hangman class, but it can be used for any situation which has "Hard" or "Easy" modes.
 * @author Cristobal Padilla
 */
public class IncorrectDifficultyLevelEntryException extends Exception {
	
	/**
	 * No-args constructor, which uses a call to the Exception Class's constructor, 
	 * passing a string argument message.
	 */
	IncorrectDifficultyLevelEntryException() {
		super("Error: you entered an incorrect option for the difficulty level. Please enter 'H' for hard or 'E' for easy." );
		
	}
}
