import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;

class Config extends BasicConfig implements Comparable<Config> {
	// METHODS OF THIS CLASS
	// public Config(Token)
	// public Config(Config, byte, Token)
	// public Config(String[], Token)
	// public void resetConfig()
	// private short calcEval()
	// private boolean calcCutoffTest()
	// public LinkedList successors()
	// public int compareTo(Config)

	// ATTRIBUTES
	public final short eval;
	public boolean cutoffTest; // not final because of resetConfig
	public long depth;

	public Config(Token beginner) throws Exception {
		super(beginner);
		// initialization
		this.eval = (short) 0;
		this.depth = 0;
		this.cutoffTest = calcCutoffTest();
	}
	
	public Config(Config game, byte column, Token player) throws Exception {
		super(game, column, player);
		// initialization
		this.eval = calcEval();
		this.depth = game.depth+1;
		this.cutoffTest = calcCutoffTest();
	}
	
	public Config(String[] layout, Token beginner) throws Exception {
		super(layout, beginner);
		// initialization
		this.eval = calcEval();
		this.depth = 0; // this constructor is used to increase possible depth
		this.cutoffTest = calcCutoffTest();
	}
	
	// Resets attributes for usage as start configuration
	public void resetConfig() {
		this.depth = 0;
		Config.instances = 1;
		this.cutoffTest = calcCutoffTest();
	}
	
	// Calculates the eval, additional MAX, subtractitional MIN
	// @return evaluation
	private short calcEval() {
		// make sure to match utility on terminal configurations
		if (super.terminal)
			return super.utility;
		// else guess
		short rank = 0;
		short weight = 1;
		for (byte length = 2; length < super.WIN; ++length) {
			rank += weight * (super.sequences[length-2][0] - super.sequences[length-2][1]);
			weight *= 4; // experimental decicion
		}
		return rank;
	}
	
	// Decides whether the recursive calling should be stopped
	// @return true if this configuration should be the last one
	private boolean calcCutoffTest() {
		if (super.terminal)
			return true;
		else
			return depth >= Minimax.cutoffDepth;
	}
	
	// Returns all possible successor configurations
	// @return List of successors
	public LinkedList<Config> successors() throws Exception {
		final LinkedList<Config> successors = new LinkedList<Config>();
		final Token next = (super.lastPlayer == Token.MAX) ? Token.MIN : Token.MAX;
		Config successor;
		for (byte col = 0; col < COLS; ++col)
			if (playingField[col][ROWS-1] != Token.MAX && playingField[col][ROWS-1] != Token.MIN)
				successors.add(new Config(this, col, next));
		if (Minimax.sortSuccessors)
			if (next == Token.MAX)
				// Sort in reverse order for max utility first for MAX
				Collections.sort(successors, Collections.reverseOrder());
			else
				Collections.sort(successors);
		if (successors.isEmpty())
			throw new Exception("returning empty successor list");
		return successors;
	}
	
	// Implementation of Comparable interface
	// @return whether this object is less, equal or greater
	@Override
	public int compareTo(Config other) {
		if (eval < other.eval)
			return -1;
		else if (eval > other.eval)
			return 1;
		else
			return 0;
	}
}
