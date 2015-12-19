
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * This class replicates a game of Hangman, using an instance of the Hangman class for 
 * to get the secret words and stuff. Unlike the Hangman Class, this class also provides
 * graphics to show where in the game the user is. 
 * @author Cristobal Padilla
 *
 */
public class HangmanConsoleGame {
	
	// Instance fields.
	private static boolean gameOver;
	private static boolean playGame;
	private static boolean exceptionStatus;
	private static char[] alphabet;
	private static char[] charsUsed;
	private static Hangman game;
	private static String secretWordDisplay;
	private static Scanner keyboard;
	private static Exception currentGameException;
	
	// Constant used to represent the size of the English alphabet.
	public static final int ENGLISH_ALPHABET_SIZE = 26;
	public static final String NEXT_PAGE = "\n\n\n\n\n\n\n\n\n\n";
	
	public static void main(String[] args) {
		
		gameOver = false;
		playGame = false;
		exceptionStatus = false;
		keyboard = new Scanner(System.in);
		
		setupGame();
			
		if(playGame)														 
			playGame();
			
		System.out.println("\t Goodbye.");
			
	}
	
	/**
	 * Method used to set up the game. First, it ask the user to enter the address of the 
	 * dictionary file that will be used for the game. The user has the option to
	 * use the default dictionary if he/she prefers. If the user enters "quit" the program
	 * exits. Also, this method also asks the user to choose the difficulty level. Easy mode
	 * means the words to be guessed are at least 5 characters long. Hard mode means the words
	 * to be guessed are at most 4 characters long. This method also handles three exceptions:
	 * FileNotFoundException, IOException, and IncorrectDifficultyLevelEntryException.
	 */
	public static void setupGame() {
		System.out.println();																					// Consume line.
		
		boolean validDictionary = false;
		boolean validDifficultyLevel = false;
		String userInput;
		
		while(!validDictionary) {
		
			try {																								// Try block to get the dictionary file address.
				System.out.print("\tEnter the dictionary file name (or \"default\" to use " +
								 "the default dictionary. Enter \"E\" to exit): ");
				userInput = keyboard.nextLine();
				
				if(userInput.equalsIgnoreCase("default")) {																// Use default dictionary
					game = new Hangman();
					playGame = true;
				
				} else if(userInput.equalsIgnoreCase("E") || userInput.equalsIgnoreCase("exit")) {						// Exit the program.
					break;
					
				} else {
					game = new Hangman(new File(userInput));															// Use user's file as dictionary
					playGame = true;
					
				}
				
				while (!validDifficultyLevel) {                                                 					
					
					try { 																						// Try block to get the difficulty level.
						System.out.print("\tPick difficulty level ('H' for hard, or 'E' for easy): ");		
						userInput = keyboard.nextLine();
						
						if (userInput.equalsIgnoreCase("H") || userInput.equalsIgnoreCase("hard")){
							game.setDifficultyLevel(Hangman.HARD);
							
						} else if (userInput.equalsIgnoreCase("E") || userInput.equalsIgnoreCase("easy")) {
							game.setDifficultyLevel(Hangman.EASY);
							
						} else {
							throw new IncorrectDifficultyLevelEntryException();
							
						}
						game.populateDictionaryArray();
						validDifficultyLevel = true;
						
					} catch (IncorrectDifficultyLevelEntryException exception) {
						System.out.println("\t" + exception.getMessage());
						
					}
				}
				validDictionary = true;
			
			} catch (FileNotFoundException exception) {															
				System.out.println("\t" + exception.getMessage());
				
			} catch (IOException exception) {
				System.out.println("\t" + exception.getMessage());
				
			}
		}
	}
	
	/**
	 * Method used to control the game. This method sets up the game's "graphics", calls the Hangman's  
	 * methods to randomly select a secret word, and begins asking the user for input. The user's input
	 * is then compared to the characters in the secret word to see if there's a match. It continues to 
	 * do this until the user has exhausted all the given guesses, or the user guesses the word correctly.
	 * At the end of the game, it updates the game stats and then it ask the user if he/she wishes to 
	 * continue playing. 
	 */
	public static void playGame() {

		char validUserGuess;
		
		do 
		{	
			game.pickSecretWord();
			game.populateSecretWordLineChars();
			generateSecretWordDisplay();
			generateAlphabetArrays();																				
			updateGraphics();
			
			while ( (game.getNumWrongGuesses() < Hangman.MAX_GUESSES) && !game.checkGameStatus() ) {				// Continue playing until guesses have been
																													// exhausted or word has been guessed.
				validUserGuess = validateUserGuess();																
				updateAlphabets(validUserGuess);
				updateSecretWordDisplay(validUserGuess);															// This method only updates the secret word if the char passed to it
																													// is in the secret word. Otherwise, it does nothing. So, no need to check.
				if (!game.checkGuess(validUserGuess)) {																// Check if the char is not in the secret word. If so, increase the 
					game.setNumWrongGuesses(game.getNumWrongGuesses() + 1);											// amount of wrong guesses.
					
				}	
				updateGraphics();
				
			}
			gameOver = true;																						// Current game is over at this point.
			updateGameStats();																						// Record results.
			updateGraphics();																							
			
			keepPlaying();
			
		} while (playGame);	
	}
	
	/**
	 * Method used to check if the character entered has been entered before in the current game.
	 * @param userValidGuess char entered by the user.
	 * @return priorUse True or False.
	 */
	private static boolean checkCharPriorUse(char userValidGuess) {
		
		boolean priorUse = false;
		int index = 0;
		
		while(index < charsUsed.length) {
			if (userValidGuess == charsUsed[index]) {																// once a match has been found, set priorUse to true
				priorUse = true;																					// and exit.
				break;
				
			}
			index++;
			
		}
		return priorUse;
		
	}
	
	/**
	 * Method asks the user for input and validates it. First, it makes sure that the input is a single character.
	 * Then, it checks that the input is a alphabet letter. If any of these conditions are not met, exceptions are
	 * thrown and handled. If input is valid, then a valid char is returned.
	 * @return validCharEntry Valid char input.
	 */
	private static char validateUserGuess() {
		
		boolean validGuess = false;
		String userInput;
		char validCharEntry = ' ';
		
		while (!validGuess) {
			
			System.out.print("\t Enter your guess: ");
		
			try{
				userInput = keyboard.nextLine();
				
				if (userInput.length() > 1) {																			// Test for multiple characters.
					throw new MultipleCharsEnteredException();
					
				} else if (!Character.isLetter(userInput.charAt(0))) {													// Test for non-alphabetic character. 
					throw new NotAlphabetCharacterException();
					
				} else if (checkCharPriorUse(userInput.toLowerCase().charAt(0))){										// Test for previous use. This line also throws an exception if
					throw new PreviouslyUsedCharacterException(userInput.charAt(0));									// the empty string is passed as an argument.
					
				} else {	
					validGuess = true;
					validCharEntry = userInput.toLowerCase().charAt(0);
				
				}	
				
			} catch (MultipleCharsEnteredException exception ) {
				updateExceptionStatus(exception);
			
			} catch (NotAlphabetCharacterException exception) {
				updateExceptionStatus(exception);
				
			} catch (PreviouslyUsedCharacterException exception) {
				updateExceptionStatus(exception);
			
			} catch (StringIndexOutOfBoundsException exception) {														// Handle the empty string.
				updateExceptionStatus(exception);																							
				
			}
		}
		return validCharEntry;
		
	}
	
	/**
	 * Method used to ask the user if he/she wishes to continue playing, and based on the response
	 * this method updates the status of the playGame boolean variable. 
	 */
	public static void keepPlaying() {
		
		boolean validInput = false;
		String userInput;
		
		while(!validInput) {
			
			try {
				System.out.print("\t Would you like to play again? (Y/N) : ");
				userInput = keyboard.nextLine();
			
				if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes")) {
					resetGame();
					validInput = true;
				
				} else if (userInput.equalsIgnoreCase("n") || userInput.equalsIgnoreCase("no")) {
					validInput = true;
					playGame = false;
				
				} else {
					throw new InvalidKeepPlayingInputException();
					
				}
			
			} catch (InvalidKeepPlayingInputException exception) {
				updateExceptionStatus(exception);
				
			}
		}
	}
	
	/**
	 * This method, when called, sets the exceptionStatus boolean to true and sets the currentGameException equal
	 * to whatever exception is passed as a parameter. Then, it updates the game graphics according to the new
	 * exception status. Finally, it sets the exceptionStatus back to false (resets it, if you will).
	 * @param exception 
	 */
	private static void updateExceptionStatus(Exception exception) {
		exceptionStatus = true;
		currentGameException = exception;
		updateGraphics();
		exceptionStatus = false;
		
	}
	
	/**
	 * Method used to update the number of games played and the number of user's wins and losses.
	 */
	public static void updateGameStats() {
		
		game.setNumGames(game.getNumGames() + 1);
		
		if (game.checkGameStatus()) {
			game.setNumWins(game.getNumWins() + 1);
			
		} else {
			game.setNumLosses(game.getNumLosses() + 1);
			
		}
	}
	
	/**
	 * Method used to display the game stats after each game. This method uses the Hangman's
	 * methods to get the number of games, number of wins, number of losses, and, using this
	 * information, calculate the user's winning percentage and display it. 
	 */
	private static String displayGameStats() {
		String str = " ";
		DecimalFormat formatter = new DecimalFormat("0.00 %");
		
		if (gameOver) {
		
			double winPercentage = (double)game.getNumWins() / game.getNumGames() ;
			str = "Total games played: " + game.getNumGames() +
				  "\tGames won: " + game.getNumWins() +
				  "\tGames lost: " + game.getNumLosses() +
				  "\tWinning percentage: " + formatter.format(winPercentage);
		
		}
		return str;
		
	}
	
	/**
	 * This method resets the number of wrong guesses to 0 and sets the gameOver to false, 
	 * so that a new game can be played.
	 */
	public static void resetGame() {
		
		game.setNumWrongGuesses(0);
		gameOver = false;
		
	}
	
	/**
	 * This method is used to update the graphics of the game. The control variable for the 
	 * graphics is the number of wrong guesses.
	 */
	public static void updateGraphics() {
		
		if(game.checkGameStatus()) {
			System.out.print(NEXT_PAGE + userWinGraphic());
			
		} else {
			
			switch (game.getNumWrongGuesses())
			{
				case 0: 
						System.out.print(NEXT_PAGE + startGraphic());
						break;
				case 1:	
						System.out.print(NEXT_PAGE + firstWrongGuessGraphic());
						break;
					
				case 2:
						System.out.print(NEXT_PAGE + secondWrongGuessGraphic());
						break;
					
				case 3:
						System.out.print(NEXT_PAGE + thirdWrongGuessGraphic());
						break;
					
				case 4:
						System.out.print(NEXT_PAGE + fourthWrongGuessGraphic());
						break;
					
				case 5: 
						System.out.print(NEXT_PAGE + fifthWrongGuessGraphic());
						break;
					
				case 6:
						System.out.print(NEXT_PAGE + sixthWrongGuessGraphic());
						break;
						
			}
		}
	}
	
	/**
	 * This method is used to generate the secret word that's displayed to the user, using the
	 * existing chars in the secretWordLineChars array in the Hangman's class.
	 */
	public static void generateSecretWordDisplay() {
		
		secretWordDisplay = " ";																					// Note: This must stay so that the string get reset, 
																													// otherwise the string keeps getting longer and longer.	
		for (int index = 0; index < game.getSecretWordLineChars().length; index++) {
			secretWordDisplay += game.getSecretWordLineChars()[index] + " ";
			
		}
	}
	
	/**
	 * This method is used to update the chars in the secretWordLineChars array using
	 * the input from the user. If the char is in the secret word, then the char is 
	 * saved in the secretWordLineChars array, in the appropriate spot. Finally, this method
	 * calls the generateSecretWordDisplay to generate the secredWordDisplay String with 
	 * the most updated information.
	 * @param userValidGuess
	 */
	public static void updateSecretWordDisplay(char validUserGuess) {
		
		for (int index = 0; index < game.getSecretWord().length(); index++) {
			
			 if (game.getSecretWord().toLowerCase().charAt(index) == validUserGuess) 
				game.getSecretWordLineChars()[index] = validUserGuess;
								
		}
		generateSecretWordDisplay();												
		
	}
	
	/**
	 * This method generates two arrays the size of the English alphabet. One array holds the lowercase
	 * characters of the alphabet, the other holds whitespace. 
	 */
	public static void generateAlphabetArrays() {
		
		alphabet = new char[ENGLISH_ALPHABET_SIZE];
		charsUsed = new char[ENGLISH_ALPHABET_SIZE];
		
		for (int index = 0; index < ENGLISH_ALPHABET_SIZE; index++) {
			alphabet[index] = (char) ((int)'a' + index);
			charsUsed[index] = ' ';
			
		}
	}
	
	/**
	 * This method generates a string containing the first half of the characters in the
	 * alphabet array. 
	 * @return str 
	 */
	private static String lowerAlphAvailable() {
		
		String str = "";
		
		for (int index = 0; index < (ENGLISH_ALPHABET_SIZE / 2); index++) {	
			str += alphabet[index] + " ";
			
		}
		return str;
		
	}
	
	/**
	 * This method generates a string containing the characters from the second half of
	 * the alphabet array.
	 * @return str
	 */
	private static String upperAlphAvailable() {
		
		String str = "";
		
		for (int index = (ENGLISH_ALPHABET_SIZE / 2); index < ENGLISH_ALPHABET_SIZE; index++) {
			str += alphabet[index] + " ";
			
		}
		return str;
		
	}
	
	/**
	 * This method generates a string containing the characters from the first part of the 
	 * charsUsed array. 
	 * @return str
	 */
	public static String lowerAlphabetUsed() {
		
		String str = "";
		
		for (int index = 0; index < (ENGLISH_ALPHABET_SIZE / 2); index++){
			str += charsUsed[index] + " ";
			
		}
		return str;
				
	}
	
	/**
	 * This method generates a string containing the characters from the second part of
	 * the charsUsed array.
	 * @return str
	 */
	public static String upperAlphabetUsed() {
		String str = "";
		
		for (int index = (ENGLISH_ALPHABET_SIZE / 2); index < ENGLISH_ALPHABET_SIZE; index++){
			str += charsUsed[index] + " ";
			
		}
		return str;
		
	}
	
	/**
	 * This method takes in a valid char guess and does two things: it removed the char from the
	 * alphabet array, placing a whitespace in it's place, and then saving the char in the 
	 * charsUsed array. 
	 * @param userValidGuess
	 */
	public static void updateAlphabets(char validUserGuess) {
		
		for (int index = 0; index < alphabet.length; index++){
			
			if(alphabet[index] == validUserGuess){
				alphabet[index] = ' ';
				charsUsed[index] = validUserGuess;
			
			}	
		}	
	}
	
	/**
	 * This method returns a string containing the secret word. Note that this method only returns 
	 * a string only if the gameOver boolean is true, meaning the game is finished. Otherwise, 
	 * this method returns whitespace.
	 * @return str Secret word.
	 */
	private static String revealSecretWord() {
		String str = " ";
		
		if (gameOver) { 
			
			if (!game.checkGameStatus())
				str = "The secret word is: " + game.getSecretWord();
			
		}
		return str;
		
	}
	
	/**
	 * This method returns string containing an error message. The error message used is based on whatever
	 * exception the currentGameException points to. Note: if no exception has been thrown, then this method
	 * returns whitespace.
	 * @return str
	 */ 
	public static String errorMessageDisplay() {
		String str = " ";
		
		if (exceptionStatus) {
			
			if (currentGameException instanceof StringIndexOutOfBoundsException)
				str = "Error: You entered an empty string. Please enter a valid character.";
			
			else
				str = currentGameException.getMessage();
			
		}
		return str;
		
	}
	
	/**
	 * This method return a string containing the number of guesses left in the current game. Note:
	 * this method only works while the game is still being played. If the current game has ended, this method 
	 * returns whitespace.
	 * @return str
	 */
	public static String numGuessesLeftDisplay() {
		String str = " ";
		 																												// Only display the number of guesses left if there are still any left.
		if (game.getNumWrongGuesses() != Hangman.MAX_GUESSES && !game.checkGameStatus()) {              				// Ignore if the the user lost or won.
																										 
			if(game.getNumWrongGuesses() == (Hangman.MAX_GUESSES - 1)) {												// Check to see if user is down to his/her 
				str = " You have only one guess left. Make it a good one!" ;											// last guess. If so, let them know, haha.
		
			} else {																									
				str = " You have " + (Hangman.MAX_GUESSES - game.getNumWrongGuesses())	+ " guesses left." ;
		
			}
		}
		return str;
	}
	
	/**
	 * This method returns the outcome of the game, revealing whether the user won or lost. Note that this 
	 * method works only if the gameOver boolean is true, meaning the game is over. Otherwise, it
	 * return whitespace.
	 * @return str Game outcome.
	 */
	private static String gameOutcomeDisplay() {
		String str = " ";
		
		if (gameOver) {
			
			if (game.checkGameStatus()) {
				str = "Congratulations!!! You've guessed the word correctly.";
			
			} else {
				str =  "Sorry, you lose.";
			
			}
		}
		return str;
		
	}
	
	/**
	 * Method used to get the value of gameOver boolean.
	 * @return gameOver
	 */
	public static boolean getGameOver() {
		return gameOver;
		
	}

	/**
	 * Method used to set the value of gameOver boolean.
	 * @param gameOver 
	 */
	public static void setGameOver(boolean gameOver) {
		HangmanConsoleGame.gameOver = gameOver;
		
	}

	/**
	 * Method used to get the value of playGame boolean.
	 * @return playGame
	 */
	public static boolean getPlayGame() {
		return playGame;
		
	}

	/**
	 * Method used to set the playGame boolean.
	 * @param playGame
	 */
	public static void setPlayGame(boolean playGame) {
		HangmanConsoleGame.playGame = playGame;
		
	}

	/**
	 * Method used to get the alphabet char array.
	 * @return alphabet.
	 */
	public static char[] getAlphabet() {
		return alphabet;
		
	}

	/**
	 * Method used to set the alphabet char array.
	 * @param alphabet
	 */
	public static void setAlphabet(char[] alphabet) {
		HangmanConsoleGame.alphabet = alphabet;
		
	}

	/**
	 * Method used to get the charsUsed char array.
	 * @return charsUsed
	 */
	public static char[] getCharsUsed() {
		return charsUsed;
		
	}

	/**
	 * Method used to set the charsUsed array.
	 * @param charsUsed
	 */
	public static void setCharsUsed(char[] charsUsed) {
		HangmanConsoleGame.charsUsed = charsUsed;
		
	}

	/**
	 * Method used to get the Hangman object.
	 * @return Hangman
	 */
	public static Hangman getGame() {
		return game;
		
	}

	/**
	 * Method used to set the Hangman object.
	 * @param game
	 */
	public static void setGame(Hangman game) {
		HangmanConsoleGame.game = game;
		
	}

	/**
	 * Method used to get the secredWordDisplay string.
	 * @return secretWordDisplay
	 */
	public static String getSecretWordDisplay() {
		return secretWordDisplay;
		
	}
	
	/**
	 * Method used to set the secretWordDisplay string.
	 * @param secretWordDisplay
	 */
	public static void setSecretWordDisplay(String secretWordDisplay) {
		HangmanConsoleGame.secretWordDisplay = secretWordDisplay;
		
	}
	
	/**
	 * Method used to generate the starting point graphic.
	 * @return str 
	 */
	private static String startGraphic() {

		String str = "\t\t\t---------------------------------" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t                               --"        +	  "\t\t" +    "     Characters Available" 	    + "\n" +					
					 "\t\t\t                               --"        +   "\t\t" + 	  "*******************************" + "\n" +				
					 "\t\t\t                               --"        +	  "\t\t" +	  "*  "+ lowerAlphAvailable() +" *" + "\n" +	
					 "\t\t\t                               --"        +   "\t\t" +    "*  "+ upperAlphAvailable() +" *" + "\n" +
					 "\t\t\t                               --"        +   "\t\t" +    "*******************************" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --"        +   "\t\t" +    "       Characters Used"          + "\n" +    
					 "\t\t\t                               --"        +   "\t\t" + 	  "*******************************" + "\n" + 
					 "\t\t\t                               --"        +	  "\t\t" +	  "*  " + lowerAlphabetUsed() +" *" + "\n" +          
					 "\t\t\t                               --"        +   "\t\t" +    "*  " + upperAlphabetUsed() +" *" + "\n" +
					 "\t\t\t                               --"        +   "\t\t" +    "*******************************" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" + 
					 "\t\t\t                               --"        +   "\t\t" +           secretWordDisplay          + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --"        +   "\t\t" +           revealSecretWord()         + "\n" +
					 "\t\t\t                               --" 		  +   "\t\t" +		    gameOutcomeDisplay()		+ "\n" +
					 "\t\t\t                               --" + "\n" +  
					 "\t\t\t                               --" + "\n" +  
					 "\t\t\t                               --" + "\n" +  
					 "\t\t\t                               --" + "\n" +    
					 "\t\t\t                               --" + "\n" +  
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +  
				     "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t----------------------------------------------------------" + "\n" +
					 "\t "+ 		errorMessageDisplay()	   + "\n" +
					 "\t" + numGuessesLeftDisplay()	+ displayGameStats() + "\n" ;					// This line displays either the number of guesses left or the game stats, not both.
					 
		return str;

	}

	/**
	 * Method used to generate the head graphic, which represents first wrong guess.
	 * @return str
	 */
	private static String firstWrongGuessGraphic() {
	
		String str = "\t\t\t---------------------------------" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
			       "\t\t   ***********    \t               --"        +	  "\t\t" +    "     Characters Available" 	    + "\n" +
			       "\t\t  *           *   \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
			       "\t\t  *   *   *   *   \t               --"        +	  "\t\t" +	  "*  "+ lowerAlphAvailable() +" *" + "\n" +
			       "\t\t  *     *     *   \t               --"        +   "\t\t" +    "*  "+ upperAlphAvailable() +" *" + "\n" +
			       "\t\t  *           *   \t               --"        +   "\t\t" +    "*******************************" + "\n" +
			       "\t\t  *    ---    *   \t               --" + "\n" +
			       "\t\t  *           *   \t               --"        +   "\t\t" +    "       Characters Used"          + "\n" +
			       "\t\t   ***********    \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
				     "\t\t\t                               --"        +	  "\t\t" +	  "*  " + lowerAlphabetUsed() +" *" + "\n" +  
				     "\t\t\t                               --"        +   "\t\t" +    "*  " + upperAlphabetUsed() +" *" + "\n" +   
				     "\t\t\t                               --"        +   "\t\t" +    "*******************************" + "\n" +
				     "\t\t\t                               --" + "\n" +
				     "\t\t\t                               --" + "\n" +
			 	     "\t\t\t                               --"        +   "\t\t" +           secretWordDisplay          + "\n" +
			         "\t\t\t                               --" + "\n" +
			   	     "\t\t\t                               --" + "\n" +
			 	     "\t\t\t                               --"        +   "\t\t" +           revealSecretWord()         + "\n" +
			  	     "\t\t\t                               --" 		  +   "\t\t" +			gameOutcomeDisplay()		+ "\n" +
				     "\t\t\t                               --" + "\n" +
			 	     "\t\t\t                               --" + "\n" +
			 	     "\t\t\t                               --" + "\n" +
		    	     "\t\t\t                               --" + "\n" +   
				     "\t\t\t                               --" + "\n" +
				     "\t\t\t                               --" + "\n" +
			 	     "\t\t\t                               --" + "\n" +
				     "\t\t\t                               --" + "\n" +
				     "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
			   	     "\t\t\t                               --" + "\n" +
			   	     "\t\t\t                               --" + "\n" +
					 "\t----------------------------------------------------------" + "\n" + 
					 "\t "+ 		errorMessageDisplay()	   + "\n" +
					 "\t" + numGuessesLeftDisplay()	+ displayGameStats() + "\n" ;					// This line displays either the number of guesses left or the game stats, not both.

		return str;

	}

	/**
	 * Method used to generate the head an body graphic, which represents two wrong guesses.
	 * @return
	 */
	private static String secondWrongGuessGraphic() {
	
		String str = "\t\t\t---------------------------------" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
				   "\t\t   ***********    \t               --"        +	  "\t\t" +    "     Characters Available" 	    + "\n" +
				   "\t\t  *           *   \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
				   "\t\t  *   *   *   *   \t               --"        +	  "\t\t" +	  "*  "+ lowerAlphAvailable() +" *" + "\n" +
				   "\t\t  *     *     *   \t               --"        +   "\t\t" +    "*  "+ upperAlphAvailable() +" *" + "\n" +
				   "\t\t  *           *   \t               --"        +   "\t\t" +    "*******************************" + "\n" +
				   "\t\t  *    ---    *   \t               --" + "\n" +
				   "\t\t  *           *   \t               --"        +   "\t\t" +    "       Characters Used"          + "\n" +
				   "\t\t   ***********    \t               --"        +   "\t\t" +    "*******************************" + "\n" +
				   "\t\t        *         \t               --"        +	  "\t\t" +	  "*  " + lowerAlphabetUsed() +" *" + "\n" +
				   "\t\t        *         \t               --"        +   "\t\t" +    "*  " + upperAlphabetUsed() +" *" + "\n" +
				   "\t\t        *         \t               --"        +   "\t\t" +    "*******************************" + "\n" +
				   "\t\t        *         \t               --" + "\n" +
				   "\t\t        *         \t               --" + "\n" +
				   "\t\t        *         \t               --"        +   "\t\t" +           secretWordDisplay          + "\n" +
				   "\t\t        *         \t               --" + "\n" +
				   "\t\t        *         \t               --" + "\n" +
		       	   "\t\t        *         \t               --"        +   "\t\t" +           revealSecretWord()         + "\n" +
		       	   "\t\t        *         \t               --" 		  +   "\t\t" +			gameOutcomeDisplay()		+ "\n" +
		       	   "\t\t        *         \t               --" + "\n" +
		       	   "\t\t        *         \t               --" + "\n" +
		       	   "\t\t        *         \t               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t----------------------------------------------------------" + "\n" + 
		       	   	 "\t "+ 		errorMessageDisplay()	   + "\n" +
		       	   	 "\t" + numGuessesLeftDisplay()	+ displayGameStats() + "\n" ;					// This line displays either the number of guesses left or the game stats, not both.

		return str;

	}
	
	/**
	 * Method used to generate the head, body, and one arm graphic. This represents three wrong guesses.
	 * @return str
	 */
	private static String thirdWrongGuessGraphic() {
	
		String str = "\t\t\t---------------------------------" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
				   "\t\t   ***********    \t               --"        +	  "\t\t" +    "     Characters Available" 	    + "\n" +
		           "\t\t  *           *   \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
		           "\t\t  *   *   *   *   \t               --"        +	  "\t\t" +	  "*  " + lowerAlphAvailable() +" *" + "\n" +
		           "\t\t  *     *     *   \t               --"        +   "\t\t" +    "*  " + upperAlphAvailable() +" *" + "\n" +
		           "\t\t  *           *   \t               --"        +   "\t\t" +    "*******************************" + "\n" +
		           "\t\t  *    ---    *   \t               --" + "\n" +
		           "\t\t  *           *   \t               --"        +   "\t\t" +    "       Characters Used"          + "\n" +
		       	   "\t\t   ***********    \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
		       	   "\t\t        *         \t               --"        +	  "\t\t" +	  "*  " + lowerAlphabetUsed() +" *" + "\n" +
		       	   "\t\t        *         \t               --"        +   "\t\t" +    "*  " + upperAlphabetUsed() +" *" + "\n" +
		       	   "\t\t        *         \t               --"        +   "\t\t" +    "*******************************" + "\n" +
		       	   "\t\t       **         \t               --" + "\n" +
		       	   "\t\t      * *         \t               --" + "\n" +
		       	   "\t\t     *  *         \t               --"        +   "\t\t" +           secretWordDisplay          + "\n" +
		       	   "\t\t    *   *         \t               --" + "\n" +
		       	   "\t\t   *    *         \t               --" + "\n" +
		       	   "\t\t  *     *         \t               --"        +   "\t\t" +           revealSecretWord()         + "\n" +
		       	   "\t\t *      *         \t               --" 		  +   "\t\t" +			gameOutcomeDisplay()		+ "\n" +
		       	   "\t\t        *         \t               --" + "\n" +
		       	   "\t\t        *         \t               --" + "\n" +
		       	   "\t\t        *         \t               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t\t\t                               --" + "\n" +
		       	   	 "\t----------------------------------------------------------" + "\n" + 
		       	   	 "\t "+ 		errorMessageDisplay()	   + "\n" +
		       	   	 "\t" + numGuessesLeftDisplay()	+ displayGameStats() + "\n" ;					// This line displays either the number of guesses left or the game stats, not both.

		return str;

	}

	/**
	 * Method used to generate the head, body, and both arms graphic. This represents 4 wrong guesses.
	 * @return str
	 */
	private static String fourthWrongGuessGraphic() {
	
		String str = "\t\t\t---------------------------------" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
		           "\t\t   ***********    \t               --"        +	  "\t\t" +    "     Characters Available" 	    + "\n" +
		           "\t\t  *           *   \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
		           "\t\t  *   *   *   *   \t               --"        +	  "\t\t" +	  "*  "+ lowerAlphAvailable() +" *" + "\n" +
		           "\t\t  *     *     *   \t               --"        +   "\t\t" +    "*  "+ upperAlphAvailable() +" *" + "\n" +
		           "\t\t  *           *   \t               --"        +   "\t\t" +    "*******************************" + "\n" +
		           "\t\t  *    ---    *   \t               --" + "\n" +
		           "\t\t  *           *   \t               --"        +   "\t\t" +    "       Characters Used"          + "\n" +
		           "\t\t   ***********    \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
		           "\t\t        *         \t               --"        +	  "\t\t" +	  "*  " + lowerAlphabetUsed() +" *" + "\n" +
		           "\t\t        *         \t               --"        +   "\t\t" +    "*  " + upperAlphabetUsed() +" *" + "\n" +
		           "\t\t        *         \t               --"        +   "\t\t" +    "*******************************" + "\n" +
		           "\t\t       ***        \t               --" + "\n" +
		           "\t\t      * * *       \t               --" + "\n" +
		           "\t\t     *  *  *      \t               --"        +   "\t\t" +           secretWordDisplay          + "\n" +
		           "\t\t    *   *   *     \t               --" + "\n" +
		           "\t\t   *    *    *    \t               --" + "\n" +
		           "\t\t  *     *     *   \t               --"        +   "\t\t" +           revealSecretWord()         + "\n" +
		           "\t\t *      *      *  \t               --" 		  +   "\t\t" +		    gameOutcomeDisplay()		+ "\n" +
		           "\t\t        *         \t               --" + "\n" +
		           "\t\t        *         \t               --" + "\n" +
		           "\t\t        *         \t               --" + "\n" +
		           	 "\t\t\t                               --" + "\n" +
		           	 "\t\t\t                               --" + "\n" +
		           	 "\t\t\t                               --" + "\n" +
		           	 "\t\t\t                               --" + "\n" +
		           	 "\t\t\t                               --" + "\n" +
		           	 "\t\t\t                               --" + "\n" +
		           	 "\t\t\t                               --" + "\n" +
		           	 "\t\t\t                               --" + "\n" +
		           	 "\t\t\t                               --" + "\n" +
		           	 "\t----------------------------------------------------------" + "\n" +
		           	 "\t "+ 		errorMessageDisplay()	   + "\n" +
		           	 "\t" + numGuessesLeftDisplay()	+ displayGameStats() + "\n" ;					// This line displays either the number of guesses left or the game stats, not both.

		return str;

	}

	/**
	 * Method used to generate the head, body, arms, and a leg graphic. This represents five wrong guesses.
	 * @return str
	 */
	private static String fifthWrongGuessGraphic() {
	
		String str = "\t\t\t---------------------------------" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
				   "\t\t   ***********    \t               --"        +	  "\t\t" +    "     Characters Available" 	    + "\n" +
				   "\t\t  *           *   \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
				   "\t\t  *   *   *   *   \t               --"        +	  "\t\t" +	  "*  "+ lowerAlphAvailable() +" *" + "\n" +
				   "\t\t  *     *     *   \t               --"        +   "\t\t" +    "*  "+ upperAlphAvailable() +" *" + "\n" +
				   "\t\t  *           *   \t               --"        +   "\t\t" +    "*******************************" + "\n" +
				   "\t\t  *     O     *   \t               --" + "\n" +
				   "\t\t  *           *   \t               --"        +   "\t\t" +    "       Characters Used"          + "\n" +
				   "\t\t   ***********    \t               --"        +   "\t\t" +    "*******************************" + "\n" +
				   "\t\t        *         \t               --"        +	  "\t\t" +	  "*  " + lowerAlphabetUsed() +" *" + "\n" +
				   "\t\t        *         \t               --"        +   "\t\t" +    "*  " + upperAlphabetUsed() +" *" + "\n" +
				   "\t\t        *         \t               --"        +   "\t\t" +    "*******************************" + "\n" +
				   "\t\t       ***        \t               --" + "\n" +
				   "\t\t      * * *       \t               --" + "\n" +
				   "\t\t     *  *  *      \t               --"        +   "\t\t" +           secretWordDisplay          + "\n" +
				   "\t\t    *   *   *     \t               --" + "\n" +
				   "\t\t   *    *    *    \t               --" + "\n" +
				   "\t\t  *     *     *   \t               --"        +   "\t\t" +           revealSecretWord()         + "\n" +
				   "\t\t *      *      *  \t               --" 		  +   "\t\t" +			gameOutcomeDisplay()		+ "\n" +
				   "\t\t        *         \t               --" + "\n" +
				   "\t\t        *         \t               --" + "\n" +
				   "\t\t       **         \t               --" + "\n" +
				   "\t\t      *           \t               --" + "\n" +
				   "\t\t     *            \t               --" + "\n" +
		       	   "\t\t    *             \t               --" + "\n" +
		       	   "\t\t   *              \t               --" + "\n" +
		       	   "\t\t  *               \t               --" + "\n" +
		       	   "\t\t *                \t               --" + "\n" +
		       	     "\t\t\t                               --" + "\n" +
		       	     "\t\t\t                               --" + "\n" +
		             "\t\t\t                               --" + "\n" +
		             "\t----------------------------------------------------------" + "\n" +
		             "\t "+ 		errorMessageDisplay()	   + "\n" +
		             "\t" + numGuessesLeftDisplay()	+ displayGameStats() + "\n" ;					// This line displays either the number of guesses left or the game stats, not both.

		return str;

	}
	
	/**
	 * Method used to generate the entire body, which represents six wrong guesses. By the way, this means the game is over and the
	 * user lost.
	 * @return str
	 */
	private static String sixthWrongGuessGraphic() {
		
		String str = "\t\t\t---------------------------------" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
					 "\t\t\t-                              --" + "\n" +
				   "\t\t   ***********    \t               --"        +	  "\t\t" +    "     Characters Available" 	    + "\n" +
				   "\t\t  *           *   \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
				   "\t\t  *   X   X   *   \t               --"        +	  "\t\t" +	  "*  "+ lowerAlphAvailable() +" *" + "\n" +
				   "\t\t  *     *     *   \t               --"        +   "\t\t" +    "*  "+ upperAlphAvailable() +" *" + "\n" +
				   "\t\t  *           *   \t               --"        +   "\t\t" +    "*******************************" + "\n" +
				   "\t\t  *     /     *   \tGoodbye        --" + "\n" +
				   "\t\t  *           *   \tcruel world!   --"        +   "\t\t" +    "       Characters Used"          + "\n" +
				   "\t\t   ***********    \t               --"        +   "\t\t" +    "*******************************" + "\n" +
				   "\t\t        *         \t               --"        +	  "\t\t" +	  "*  " + lowerAlphabetUsed() +" *" + "\n" +
				   "\t\t        *         \t               --"        +   "\t\t" +    "*  " + upperAlphabetUsed() +" *" + "\n" +
				   "\t\t        *         \t               --"        +   "\t\t" +    "*******************************" + "\n" +
				   "\t\t       ***        \t               --" + "\n" +
				   "\t\t      * * *       \t               --" + "\n" +
				   "\t\t     *  *  *      \t               --"        +   "\t\t" +           secretWordDisplay          + "\n" +
				   "\t\t    *   *   *     \t               --" + "\n" +
				   "\t\t   *    *    *    \t               --" + "\n" +
				   "\t\t  *     *     *   \t               --"        +   "\t\t" +           revealSecretWord()         + "\n" +
				   "\t\t *      *      *  \t               --" 		  +   "\t\t" +			gameOutcomeDisplay()		+ "\n" +
				   "\t\t        *         \t               --" + "\n" +
				   "\t\t        *         \t               --" + "\n" +
				   "\t\t       ***        \t               --" + "\n" +
				   "\t\t      *   *       \t               --" + "\n" +
				   "\t\t     *     *      \t               --" + "\n" +
		       	   "\t\t    *       *     \t               --" + "\n" +
		       	   "\t\t   *         *    \t               --" + "\n" +
		       	   "\t\t  *           *   \t               --" + "\n" +
		       	   "\t\t *             *  \t               --" + "\n" +
		       	     "\t\t\t                               --" + "\n" +
		       	     "\t\t\t                               --" + "\n" +
		             "\t\t\t                               --" + "\n" +
		             "\t----------------------------------------------------------" + "\n" +
		             "\t "+ 		errorMessageDisplay()	   + "\n" +
		             "\t" + numGuessesLeftDisplay()	+ displayGameStats() + "\n" ;					// This line displays either the number of guesses left or the game stats, not both.

		return str;

	}

	/**
	 * Method used to generate the user's victory.
	 * @return str
	 */
	private static String userWinGraphic() {
		
		String str = "\t\t\t---------------------------------" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
					 "\t\t\t                               --" + "\n" +
			       "\t\t   ***********    \t               --"        +	  "\t\t" +    "     Characters Available" 	    + "\n" +
			       "\t\t  *           *   \t               --"        +   "\t\t" + 	  "*******************************" + "\n" +
			       "\t\t  *   *   *   *   \t               --"        +	  "\t\t" +	  "*  "+ lowerAlphAvailable() +" *" + "\n" +
			       "\t\t  *     *     *   \t               --"        +   "\t\t" +    "*  "+ upperAlphAvailable() +" *" + "\n" +
			       "\t\t  *           *   \t               --"        +   "\t\t" +    "*******************************" + "\n" +
			       "\t\t  *    ===    *   \t Woohoo!!!     --" + "\n" +
			       "\t\t  *           *   \t I'm still     --"        +   "\t\t" +    "       Characters Used"          + "\n" +
			       "\t\t*  ***********  * \t alive         --"        +   "\t\t" + 	  "*******************************" + "\n" +
			       "\t\t *      *      *  \t               --"        +	  "\t\t" +	  "*  " + lowerAlphabetUsed() +" *" + "\n" +
			       "\t\t  *     *     *   \t               --"        +   "\t\t" +    "*  " + upperAlphabetUsed() +" *" + "\n" +
			       "\t\t   *    *    *    \t               --"        +   "\t\t" +    "*******************************" + "\n" +
			       "\t\t    *********     \t               --" + "\n" +
			       "\t\t        *         \t               --" + "\n" +
			   	   "\t\t        *         \t               --"        +   "\t\t" +           secretWordDisplay          + "\n" +
			   	   "\t\t        *         \t               --" + "\n" +
			   	   "\t\t        *         \t               --" + "\n" +
			   	   "\t\t        *         \t               --"        +   "\t\t" +           revealSecretWord()         + "\n" +
			   	   "\t\t        *         \t               --" 		  +   "\t\t" +			gameOutcomeDisplay()		+ "\n" +
			   	   "\t\t        *         \t               --" + "\n" +
			   	   "\t\t        *         \t               --" + "\n" +
			   	   "\t\t       ***        \t               --" + "\n" +
			   	   "\t\t      *   *       \t               --" + "\n" +
			   	   "\t\t     *     *      \t               --" + "\n" +
			   	   "\t\t    *       *     \t               --" + "\n" +
			   	   "\t\t   *         *    \t               --" + "\n" +
			   	   "\t\t  *           *   \t               --" + "\n" +
			   	   "\t\t *             *  \t               --" + "\n" +
			   	     "\t\t\t                               --" + "\n" +
			   	     "\t\t\t                               --" + "\n" +
			   	     "\t\t\t                               --" + "\n" +
			   	     "\t----------------------------------------------------------" + "\n" +
			   	     "\t "+ 		errorMessageDisplay()	   + "\n" +
			   	     "\t" + numGuessesLeftDisplay()	+ displayGameStats() + "\n" ;					// This line displays either the number of guesses left or the game stats, not both.
	
		return str;
				
	}
}
