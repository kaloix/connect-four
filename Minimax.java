import java.util.LinkedList;

class Minimax {
	// METHODS OF THIS CLASS
	// private Minimax()
	// public void setOptimizations(boolean, boolean, boolean, int)
	// public Config minimaxDecicion(Config)
	// private Config maxValue(Config, short, short)
	// private Config minValue(Config, short, short)
	// public void printOptimizations()
	// public void printForecast()

	// OPTIMIZATIONS
	public static boolean alphaBetaSearch;
	public static boolean sortSuccessors;
	public static boolean cutoff;
	public static int cutoffDepth;
	// ATTRIBUTES
	private static Config bestEnd;
	private static Config decicion;
	private static boolean firstLevel;

	// Private Constructor to prevent instantiation
	private Minimax() {
	}

	// Sets the given optimizations
	public static void setOptimizations(boolean alphaBetaSearch, boolean sortSuccessors, boolean cutoff, int cutoffDepth) throws Exception {
		Minimax.alphaBetaSearch = alphaBetaSearch;
		Minimax.sortSuccessors = sortSuccessors;
		Minimax.cutoff = cutoff;
		if (cutoff && cutoffDepth < 1)
			throw new Exception("search depth must be at least 1");
		Minimax.cutoffDepth = cutoffDepth;
	}

	// Calculates the best sucessor for MAX
	public static Config minimaxDecicion(Config playingField) throws Exception {
		playingField.resetConfig();
		Minimax.firstLevel = true;
		Minimax.decicion = null;
		Minimax.bestEnd = maxValue(playingField, Short.MIN_VALUE, Short.MAX_VALUE);
		if (Minimax.decicion == null)
			throw new Exception("decicion not set");
		return Minimax.decicion;
	}

	// Calculates the maximum utility (alpha value) for a given node
	private static Config maxValue(Config playingField, short alpha, short beta) throws Exception {
		boolean firstLevel = false;
		if (Minimax.firstLevel) {
			Minimax.firstLevel = false;
			firstLevel = true;
		}
		// if end of recursion then return value
		if (playingField.terminal || cutoff && playingField.cutoffTest)
			return playingField;
		// check successors
		Config valueConf = null;
		short value = Short.MIN_VALUE;
		for (Config successor : playingField.successors()) {
			Config minValueConf = minValue(successor, alpha, beta);
			short minValue = (cutoff) ? minValueConf.eval : minValueConf.utility;
			if (minValue > value || valueConf == null) {
				valueConf = minValueConf;
				value = minValue;
				if (firstLevel)
					Minimax.decicion = successor;
			}
			if (firstLevel)
				System.out.print("#");
			if (alphaBetaSearch && value >= beta)
				return valueConf;
			alpha = (short) Math.max(alpha, value);
		}
		return valueConf;
	}

	// Calculates the minimum utility (beta value) for a given node
	private static Config minValue(Config playingField, short alpha, short beta) throws
			Exception {
		// if end of recursion then return value
		if (playingField.terminal || cutoff && playingField.cutoffTest)
			return playingField;
		// check successors
		Config valueConf = null;
		short value = Short.MAX_VALUE;
		for (Config successor : playingField.successors()) {
			Config maxValueConf = maxValue(successor, alpha, beta);
			short maxValue = (cutoff) ? maxValueConf.eval : maxValueConf.utility;
			if (maxValue < value || valueConf == null) {
				valueConf = maxValueConf;
				value = maxValue;
			}
			if (alphaBetaSearch && value <= alpha)
				return valueConf;
			beta = (short) Math.min(beta, value);
		}
		return valueConf;
	}
	
	// Printes the used optimizations
	public static void printOptimizations() throws Exception {
		if (cutoff)
			System.out.println("Using cutoff test with depth "+cutoffDepth+".");
		if (alphaBetaSearch)
			System.out.println("Using alpha beta search.");
		if (sortSuccessors)
			System.out.println("Using sorted successors.");
	}
	
	// Printes the forecast of the end
	public static void printForecast() throws Exception {
		System.out.print("If you play optimal, ");
		if (bestEnd.eval == Short.MAX_VALUE)
			System.out.println("I will win.");
		else if (bestEnd.eval > 0)
			System.out.print("I will probably win.");
		else if (bestEnd.eval == 0 && bestEnd.terminal)
			System.out.println("the game will end in a draw.");
		else if (bestEnd.eval == 0)
			System.out.println("the game will probably end in a draw.");
		else if (bestEnd.eval > Short.MIN_VALUE)
			System.out.print("you will probably win.");
		else
			System.out.println("you will win.");
		if (!bestEnd.terminal && bestEnd.eval != 0)
			System.out.println(" (eval="+bestEnd.eval+")");
	}
}
