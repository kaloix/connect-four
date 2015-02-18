import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;

abstract class BasicConfig {
	// METHODS OF THIS CLASS
	// protected Config(Token)
	// protected Config(Config, byte, Token)
	// protected Config(String[], Token)
	// private boolean terminalTest()
	// private short calcUtility()
	// private byte[][] countSequences()
	// public void printConfig()
	// public void printSymbols()

	// CONSTANTS
	protected static final byte COLS = 7; // standard is 7
	protected static final byte ROWS = 6; // standard is 6
	protected static final byte WIN = 4; // standard is 4
	protected static final byte DIAGS = ROWS + COLS - 1;
	private static final char MAX_SYMBOL = 'X';
	private static final char MIN_SYMBOL = 'O';
	// ATTRIBUTES
	public final boolean terminal;
	public final short utility; // only valid if terminal
	public final byte lastColumn; // only used in printConfig()
	public final Token lastPlayer;
	public static long instances;
	protected final Token[][] playingField;
	protected final byte[][] sequences; // [WIN-1][2]
	
	// Constructs an empty playing field
	protected BasicConfig(Token beginner) throws Exception {
		// parameter check
		if (COLS + ROWS > Byte.MAX_VALUE)
			throw new Exception("playing field too big");
		if (beginner != Token.MAX && beginner != Token.MIN)
			throw new Exception("only MAX and MIN can begin");
		// initialization
		this.playingField = new Token[COLS][ROWS];
		for (Token[] col : playingField)
			Arrays.fill(col, Token.EMPTY);
		this.sequences = new byte[WIN-1][2];
		this.terminal = false;
		this.utility = -1; // not valid
		this.lastColumn = -1; // not valid
		this.lastPlayer = (beginner == Token.MAX) ? Token.MIN : Token.MAX;
		this.instances = 1;
	}
	
	// Copy constructor with additional Token
	protected BasicConfig(BasicConfig game, byte column, Token player) throws Exception {
		// parameter check
		if (player == game.lastPlayer)
			throw new Exception(player+" tried to move twice");
		if (column < 0 || column >= COLS)
			throw new Exception("this column is out of bounds");
		if (game.playingField[column][ROWS-1] == Token.MAX || game.playingField[column][ROWS-1] == Token.MIN)
			throw new Exception("this column is full");
		// initialization
		this.playingField = new Token[COLS][ROWS];
		for (byte col = 0; col < COLS; ++col)
			playingField[col] = Arrays.copyOf(game.playingField[col], ROWS);
		for (byte row = 0; row < ROWS; ++row)
			if (game.playingField[column][row] == Token.EMPTY) {
				playingField[column][row] = player;
				break;
			}
		this.sequences = countSequences();
		this.terminal = terminalTest();
		this.utility = calcUtility();
		this.lastColumn = column;
		this.lastPlayer = player;
		++this.instances;
	}
	
	// Constructs a configuration from Strings to start from custom state
	// @layout Array of Strings with a's and i's
	protected BasicConfig(String[] layout, Token beginner) throws Exception {
		// parameter check
		if (layout.length != ROWS)
			throw new Exception("layout parameter has wrong number of rows");
		if (layout[0].length() != COLS)
			throw new Exception("layout parameter has wrong number of clolumns");
		if (beginner != Token.MAX && beginner != Token.MIN)
			throw new Exception("only MAX and MIN can begin");
		// initialization
		char c;
		int ax = 0;
		int is = 0;
		this.playingField = new Token[COLS][ROWS];
		for (byte col = 0; col < COLS; ++col) {
			for (byte row = 0; row < ROWS; ++row) {
				c = layout[ROWS-1-row].charAt(col);
				switch (c) {
					case 'a':
						playingField[col][row] = Token.MAX;
						++ax;
						break;
					case 'i':
						playingField[col][row] = Token.MIN;
						++is;
						break;
					default:
						playingField[col][row] = Token.EMPTY;
						break;
				}
			}
		}
		if (ax != is)
			throw new Exception(ax+" a, "+is+" i, should be equal");
		this.sequences = countSequences();
		this.terminal = terminalTest();
		this.utility = calcUtility();
		this.lastColumn = -1; // not valid
		this.lastPlayer = (beginner == Token.MAX) ? Token.MIN : Token.MAX;
		this.instances = 1;
	}
	
	// Calculates whether a configuration is a final one
	private boolean terminalTest() {
		if (sequences[WIN-2][0] >= 1 || sequences[WIN-2][1] >= 1)
			return true;
		else {
			boolean draw = true;
			for (byte col = 0; col < COLS; ++col)
				if (playingField[col][ROWS-1] == Token.EMPTY) {
					draw = false;
					break;
				}
			return draw;
		}
	}
	
	// Calculates the utility for final configurations
	private short calcUtility() {
		if (sequences[WIN-2][0] >= 1)
			return Short.MAX_VALUE; // MAX wins
		else if (sequences[WIN-2][1] >= 1)
			return Short.MIN_VALUE; // MIN wins
		else
			return 0;
	}
	
	// Counts the sequences of a specified length
	// @return quantities for MAX and MIN
	private byte[][] countSequences() {
		byte[][] stats = new byte[WIN-1][2];

		// check vertical and horizontal
		byte maxSeqVert, minSeqVert, maaxeqHori, minSeqHori;
		Token cell;
		for (byte a = 0; a < Math.max(COLS, ROWS); ++a) {
			maxSeqVert = minSeqVert = maaxeqHori = minSeqHori = 0;
			for (byte b = 0; b < Math.max(COLS, ROWS); ++b) {
				// vertical --> [|||]
				if (a < COLS && b < ROWS) {
					cell = playingField[a][b];
					if (cell == Token.MAX)
						++maxSeqVert;
					else {
						addSequence(stats, maxSeqVert, Token.MAX);
						maxSeqVert = 0;
					}
					if (cell == Token.MIN)
						++minSeqVert;
					else {
						addSequence(stats, minSeqVert, Token.MIN);
						minSeqVert = 0;
					}
				}
				// horizontal --> [=]
				if (b < COLS && a < ROWS) {
					cell = playingField[b][a];
					if (cell == Token.MAX)
						++maaxeqHori;
					else {
						addSequence(stats, maaxeqHori, Token.MAX);
						maaxeqHori = 0;
					}
					if (cell == Token.MIN)
						++minSeqHori;
					else {
						addSequence(stats, minSeqHori, Token.MIN);
						minSeqHori = 0;
					}
				}
			}
			// add sequences ending on palying field border
			addSequence(stats, maxSeqVert, Token.MAX);
			addSequence(stats, minSeqVert, Token.MIN);
			addSequence(stats, maaxeqHori, Token.MAX);
			addSequence(stats, minSeqHori, Token.MIN);
		}
		
		// check diagonals, see "diagonal.ods" for indices magic
		byte maxSeqDiag1, minSeqDiag1, maxSeqDiag2, minSeqDiag2, row;
		for (byte diag = 0; diag < DIAGS; ++diag) {
			maxSeqDiag1 = minSeqDiag1 = maxSeqDiag2 = minSeqDiag2 = 0;
			for (byte col = 0; col <= diag; ++col) {
				// diagonal 1 --> [\\\]
				row = (byte) (diag - col);
				if (col < COLS && row < ROWS) {
					cell = playingField[col][row];
					if (cell == Token.MAX)
						++maxSeqDiag1;
					else {
						addSequence(stats, maxSeqDiag1, Token.MAX);
						maxSeqDiag1 = 0;
					}
					if (cell == Token.MIN)
						++minSeqDiag1;
					else {
						addSequence(stats, minSeqDiag1, Token.MIN);
						minSeqDiag1 = 0;
					}
				}
				// diagonal 2 --> [///]
				row = (byte) (ROWS-1 - diag + col);
				if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
					cell = playingField[col][row];
					if (cell == Token.MAX)
						++maxSeqDiag2;
					else {
						addSequence(stats, maxSeqDiag2, Token.MAX);
						maxSeqDiag2 = 0;
					}
					if (cell == Token.MIN)
						++minSeqDiag2;
					else {
						addSequence(stats, minSeqDiag2, Token.MIN);
						minSeqDiag2 = 0;
					}
				}
			}
			// add sequences ending on palying field border
			addSequence(stats, maxSeqDiag1, Token.MAX);
			addSequence(stats, minSeqDiag1, Token.MIN);
			addSequence(stats, maxSeqDiag2, Token.MAX);
			addSequence(stats, minSeqDiag2, Token.MIN);
		}
		return stats;
	}
	
	// Updates the sequence array
	// @param stats The array that should be updated
	// @param length Length of sequence to count
	// @param player Target for given sequence
	private void addSequence(byte[][] stats, byte length, Token player) {
		byte target = (player == Token.MAX) ? (byte) 0 : (byte) 1;
		if (length >= 2 && length <= WIN)
			++stats[length-2][target];
		else if (length > WIN)
			++stats[WIN-2][target];
	}

	// Plots the playing field in unicode art, prints winner
	public void printConfig() throws Exception {
		// plot part
		if (COLS > 9 || ROWS > 9)
			throw new Exception("can only plot single digit row and column numbers");
		// bottom line
		System.out.print("  #");
		for (byte col = 0; col < COLS; ++col)
			if (col == lastColumn)
				System.out.print("#|");
			else
				System.out.print("##");
		System.out.println("##");
		// playing field
		for (byte row = ROWS-1; row >= 0; --row) {
			System.out.print(" "+(row+1)+"# ");
			for (byte col = 0; col < COLS; ++col)
				switch (playingField[col][row]) {
					case MAX:
						System.out.print(MAX_SYMBOL+" ");
						break;
					case MIN:
						System.out.print(MIN_SYMBOL+" ");
						break;
					case EMPTY:
						System.out.print("  ");
						break;
				}
			System.out.println("#");
		}
		// top line with column numbers
		System.out.print("  #");
		for (byte col = 0; col < COLS; ++col)
			System.out.print("#"+(col+1)+"");
		System.out.println("##");
		// end part
		if (terminal)
			switch (utility) {
				case Short.MAX_VALUE:
					System.out.println("*** AI wins. ***");
					break;
				case Short.MIN_VALUE:
					System.out.println("*** PLAYER wins. ***");
					break;
				case 0:
					System.out.println("*** The game is a draw. ***");
					break;
				default:
					throw new Exception("reached unreachable statement");
			}
	}
	
	// Printes the player symbols
	public static void printSymbols() {
		System.out.println("PLAYER = \'"+MIN_SYMBOL+"\' , AI = \'"+MAX_SYMBOL+"\'.");
	}
}
