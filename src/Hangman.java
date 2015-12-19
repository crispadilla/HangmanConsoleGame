
import java.io.*;
import java.util.*;

/**
 * The Hangman Class holds necessary data to play the Hangman game. 
 * @author Cristobal Padilla
 *
 */

public class Hangman {
	
	// Instance fields.
	private int numGames;
	private int numWins;
	private int numLosses;
	private int numWrongGuesses;
	private int difficultyLevel;
	private char [] secretWordLineChars;
	private String secretWord;
	private File dictionaryFilePath;
	private ArrayList<String> dictionary;
	private BufferedReader words;

	// Static constants. 
	public final static int MAX_GUESSES = 6;
	public final static int EASY = 0;
	public final static int HARD = 1;
	public final static String DEFAULT_DICTIONARY_FILE_PATH = "../words.txt";
	public final String DEFAULT_SECRET_WORD = "Abracadabra";
	
	/**
	 * No-args constructor. This constructor uses the "words.txt" file as a dictionary.
	 * @throws IOException 
	 */
	public Hangman() throws IOException {
		this(new File(DEFAULT_DICTIONARY_FILE_PATH));
		
	}
	
	/**
	 * Constructor takes a string argument that's used as a path.
	 * @param dictionaryFilePath dictionary file.
	 * @throws IOException dictionary not found.
	 */
	public Hangman(File dictionaryFilePath) throws IOException {
		
		this.dictionaryFilePath = dictionaryFilePath;
		words = new BufferedReader(new FileReader(dictionaryFilePath));								// Note: having BufferedReader in here throws IOException right away if the
		dictionary = new ArrayList<String>();														// file is not found, instead of waiting until the the dictionary
		numGames = 0;																				// array is being filled.	
		numWins = 0;
		numLosses = 0;
		numWrongGuesses = 0;
		difficultyLevel = EASY;
		secretWord = DEFAULT_SECRET_WORD;
		
	}
	
	/**
	 * Method used to populate the dictionary array what will be used for the game. This method uses the
	 * difficultyLevel boolean as a control variable to determine which type of words to add into the
	 * array. If difficultyLevel is easy, then add words that are 5 characters long or more. If difficulty
	 * level is hard, then only add words that are 4 characters long or less. This method throws an exception 
	 * if the dictionary file is not found. 
	 * @throws IOException dictionary file not found.
	 */
	public void populateDictionaryArray() throws IOException {
	
		String str = " ";
		
		if (difficultyLevel == EASY) {
			
			while ( (str = words.readLine()) != null) {
				
				if (str.length() >= 5) {
					dictionary.add(str);
				
				}
			}
			
		} else if (difficultyLevel == HARD) {
			
			while ( (str = words.readLine()) != null) {
				
				if (str.length() <= 4) {
					dictionary.add(str);
				
				}
			}
		}
		words.close();
		
	}
	
	/**
	 * Method used to randomly select a word from the dictionary array and 
	 * assign it as the new secret word.
	 */
	public void pickSecretWord() {
		
		Random generator = new Random();
		secretWord = dictionary.get(generator.nextInt(dictionary.size()) - 1);
		
	}
	
	/**
	 * Method used to populate a char array of the same length as the current secret 
	 * word, but consisting only of '_' characters. 
	 */
	public void populateSecretWordLineChars() {
		
		secretWordLineChars = new char[secretWord.length()];
		
		for(int index = 0; index < secretWord.length(); index++) {
			secretWordLineChars[index] = '_';
			
		} 
	}
	
	/**
	 * Method used to check the status of the game by looking at the 
	 * secretWordLineChars and see how many '_' it still contain.
	 * If all the '_' are gone, then the user has guessed the word and
	 * the user wins the game. Else, the user lost.
	 * @return True of False, depending on the game.
	 */
	public boolean checkGameStatus() {
		
		int numUnderscores = 0;
		
		for (int index = 0; index < secretWordLineChars.length; index++) {					
			if(secretWordLineChars[index] == '_'){										// Check for '_' characters.
				numUnderscores++;
				
			}
		}
		
		if(numUnderscores == 0) {
			 return true;																// Meaning, user has guess the word completely.
			 
		} else {
			 return false;																// Meaning, user has not guessed the word yet.
			 
		}
	}
	
	/**
	 * Method used to compare the character the user entered with the characters in the
	 * secretWord String. If there's a match, return true; Else, return false.
	 * @param userValidGuess char the user entered.
	 * @return match True or False
	 */
	public boolean checkGuess(char userValidGuess) {
		
		boolean match = false;
		int index = 0;
		
		while(index < secretWord.length()) {
			
			if (userValidGuess == secretWord.charAt(index)) {
				match = true;
				break;
				
			}
			index++;
			
		}
		return match;
		
	}
	
	/**
	 * Method returns the current difficulty level.
	 * @return difficultyLevel
	 */
	public int getDifficultyLevel() {
		return difficultyLevel;
		
	}
	
	/**
	 * Method sets the difficulty level.
	 * @param difficultyLevel
	 */
	public void setDifficultyLevel(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
		
	}
	
	/**
	 * Method returns the secretWordLineChars array.
	 * @return secretWordLineChars
	 */
	public char[] getSecretWordLineChars() {
		return secretWordLineChars;
		
	}
	
	/**
	 * Method sets a new address for the secretWordLineChars.
	 * @param secretWordLineChars
	 */
	public void setSecretWordLineChars(char[] secretWordLineChars) {
		this.secretWordLineChars = secretWordLineChars;
		
	}
	
	/**
	 * Method used to get the number of wins.
	 * @return numWins
	 */
	public int getNumWins() {
		return numWins;
		
	}
	
	/**
	 * Method used to set the number of wins.
	 * @param numWins 
	 */
	public void setNumWins(int numWins) {
		this.numWins = numWins;
		
	}
	
	/**
	 * Method used to get the number of game losses.
	 * @return numLosses
	 */
	public int getNumLosses() {
		return numLosses;
		
	}
	
	/**
	 * Method used to set the number of losses.
	 * @param numLosses
	 */
	public void setNumLosses(int numLosses) {
		this.numLosses = numLosses;
		
	}
	
	/**
	 * Method used to get the number of games played.
	 * @return numGames
	 */
	public int getNumGames() {
		return numGames;
		
	}
	
	/**
	 * Method used to set the number of games played.
	 * @param numGames
	 */
	public void setNumGames(int numGames) {
		this.numGames = numGames;
		
	}
	
	/**
	 * Method used to get the number of wrong guesses.
	 * @return numGames
	 */
	public int getNumWrongGuesses() {
		return numWrongGuesses;
		
	}
	
	/**
	 * Method used to set the number of wrong guesses.
	 * @param numWrongGuesses
	 */
	public void setNumWrongGuesses(int numWrongGuesses) {
		this.numWrongGuesses = numWrongGuesses;
		
	}

	/**
	 * Method used to get the secret word String.
	 * @return secretWord.
	 */
	public String getSecretWord() {
		return secretWord;
		
	}

	/**
	 * Method used to set the secretWord String.
	 * @param secretWord
	 */
	public void setSecretWord(String secretWord) {
		this.secretWord = secretWord;
		
	}

	/**
	 * Method used to get the dictionaryFilePath File.
	 * @return dictionaryFilePath
	 */
	public File getDictionaryFilePath() {
		return dictionaryFilePath;
		
	}

	/**
	 * Method used to set the dictionaryFilePath File.
	 * @param dictionaryFilePath
	 */
	public void setDictionaryFilePath(File dictionaryFilePath) {
		this.dictionaryFilePath = dictionaryFilePath;
		
	}

	/**
	 * Method used to get the dictionary ArrayList.
	 * @return dictionary
	 */
	public ArrayList<String> getDictionary() {
		return dictionary;
		
	}

	/**
	 * Method used to set the dictionary ArrayList.
	 * @param dictionary
	 */
	public void setDictionary(ArrayList<String> dictionary) {
		this.dictionary = dictionary;
		
	}
}
