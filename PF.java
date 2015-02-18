import java.lang.StringBuilder;
import java.util.Random;

class PF {
	// catalog of custom start configurations, i=Min, a=Max
	public static String[][] catalog = new String[][]{new String[]{
		"aiia  i", // 1, used in drawing
		"iiia  a",
		"aaaiaaa",
		"aiaaaii",
		"iaiiiaa",
		"iaiiaii"
	}, new String[]{
		"       ", // 2
		"    a  ",
		"i aiaia",
		"aaiaiai",
		"iiiaiai",
		"aiaaiia"
	}, new String[]{
		"       ", // 3
		"       ",
		"i      ",
		"aai  ai",
		"iaiaiai",
		"aiaaiia"
	}, new String[]{
		"       ", // 4
		"       ",
		"   i   ",
		"aaai  i",
		"aiiiaia",
		"iaiaaia"
	}, new String[]{
		"aiaaiai", // 5, draw
		"iiaaiia",
		"aiiaaai",
		"aaaiiia",
		"aiiiaia",
		"iaiaiai"
	}, new String[]{
		"       ", // 6, 2 rows empty, experiment
		"       ",
		"iaaiaai",
		"aiiaaii",
		"iaaiiaa",
		"aiiiaia"
	}, new String[]{
		"       ", // 7
		"aia   a",
		"aiiaiii",
		"iaaiiaa",
		"aiiaaii",
		"iaaaiai"
	}, new String[]{
		"       ", // 8, inverted
		"  i   i",
		"iaaiaaa",
		"aiiaaii",
		"iaaiiaa",
		"aiiiaia"
	}};

	// Converts the Min and Max tokens for a given playing field
	public static String[] invert(String[] in) {
		String[] inverted = new String[in.length];
		char i, o;
		StringBuilder sb;
		for (byte row = 0; row < in.length; ++row) {
			sb = new StringBuilder(in[row]);
			for (byte col = 0; col < in[0].length(); ++col) {
				i = in[row].charAt(col);
				o = (i=='a') ? 'i' : (i=='i') ? 'a' : ' ';
				sb.setCharAt(col, o);
			}
			inverted[row] = sb.toString();
		}
		return inverted;
	}
}
