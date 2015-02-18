import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Locale;

class ConnectFour {
	// METHODS OF THIS CLASS
	// public void main(String[])
	// private Config gameFromArgs(String[])
	// private void setMinimaxFromArgs(String[])
	// private byte getUserMove()

	private static final int maxOptLevel = 3; // maximum number of optimizations

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("usage: java ConnectFour <playingField> <invertField> <optimizationLevel> [<cutoffDepth>]");
			System.out.println("  playingField: choose from range 0 to "+PF.catalog.length);
			System.out.println("  invertField: choose from range 0 to 1");
			System.out.println("  optimizationLevel: choose from range 0 to "+maxOptLevel);
			System.out.println("  cutoffDepth: choose from range 1 and up");
			System.exit(-1);
		}
		setMinimaxFromArgs(args); // configure Minimax first, as it's used in gameFromArgs
		Config game = gameFromArgs(args);

		// START GAME
		System.out.println("*** CONNECT FOUR ***");
		System.out.println("By Stefan Schindler, January 2014.");
		Minimax.printOptimizations();
		Config.printSymbols();
		System.out.println("End program with \'c\'.");
		game.printConfig();
		if (game.terminal) return;
		
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		while (true) {
			// AI MOVE
			System.out.print("MY MOVE: [       ]\rMY MOVE: [");
			long startTime = System.currentTimeMillis();
			game = Minimax.minimaxDecicion(game);
			long endTime = System.currentTimeMillis();
			if (game.lastPlayer != Token.MAX)
				throw new Exception("AI returned Config with "+game.lastPlayer+" as next player");
			System.out.print("\rMY MOVE: "+(game.lastColumn+1));
			System.out.println("  ("+nf.format(game.instances)+" configurations; "+nf.format((endTime-startTime)/1_000.0)+" seconds)");
			System.out.println("            ("+nf.format((game.instances*1_000.0)/(endTime-startTime))+" configs/second)");
			game.printConfig();
			if (game.terminal) return;
			Minimax.printForecast();
			
			// PLAYER MOVE
			Config userGame = new Config(Token.MIN); // initialization for try-catch
			boolean validMove;
			do {
				byte userMove = getUserMove();
				if (userMove == -42) {
					System.out.println("Canceled.");
					return;
				}
				validMove = true;
				try {
					userGame = new Config(game, userMove, Token.MIN);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					validMove = false;
				}
			} while (!validMove);
			game = userGame;
			game.printConfig();
			if (game.terminal) return;
		}
	}

	// Decides which start configuration to choose
	private static Config gameFromArgs(String[] args) throws Exception {
		int playingField = Integer.parseInt(args[0]);
		boolean invertField = Integer.parseInt(args[1]) == 1;
		if (playingField > PF.catalog.length)
			throw new Exception("maximum catalog entry is "+PF.catalog.length);
		Config game;
		if (playingField == 0)
			game = new Config(Token.MAX);
		else if (!invertField)
			game = new Config(PF.catalog[playingField-1], Token.MAX);
		else
			game = new Config(PF.invert(PF.catalog[playingField-1]), Token.MAX);
		return game;
	}

	// Chooses which implemented optimizations to use
	private static void setMinimaxFromArgs(String[] args) throws Exception {
		int optimizationLevel = Integer.parseInt(args[2]);
		if (optimizationLevel > maxOptLevel)
			throw new Exception("maximum optimization level is "+maxOptLevel);
		boolean cutoff = optimizationLevel >= 1;
		boolean alphaBetaSearch = optimizationLevel >= 2;
		boolean sortSuccessors = optimizationLevel >= 3;
		int cutoffDepth;
		if (cutoff)
			cutoffDepth = (args.length >= 4) ? Integer.parseInt(args[3]) : 10;
		else
			cutoffDepth = -1;
		Minimax.setOptimizations(alphaBetaSearch, sortSuccessors, cutoff, cutoffDepth);
	}
	
	// Reads column choice of user from stdin
	// @return choosen column, -42 means exit
	private static byte getUserMove() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input;
		int intColumn;
		while (true) {
			System.out.print("YOUR MOVE: ");
			input = in.readLine();
			if (input.length() > 4) {
				System.err.println("Input too long.");
				continue;
			}
			if (input.length() == 0)
				continue;
			if (input.length() == 1 && input.charAt(0) == 'c')
				return -42;
			try {
				intColumn = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				System.err.println("Could not parse number.");
				continue;
			}
			break;
		}
		return (byte) (intColumn-1);
	}
}
