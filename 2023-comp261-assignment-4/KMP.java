/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 */
	public static int search(String pattern, String text) {
		if (pattern.length() == 0) return 0;
		if (text.length() == 0) return -1;
		var n = text.length();
		var m = pattern.length();
		var partialMatchTable = new int[m];
		var k = 0;
		var i = 0;
		partialMatchTable[0] = -1;
		if (partialMatchTable.length>1) partialMatchTable[1] = 0;
		var j = 0;
		var pos = 2;
		while (pos < m) {
			if (pattern.charAt(pos-1) == pattern.charAt(j)) {
				partialMatchTable[pos] = j+1;
				pos++;
				j++;
			} else if (j > 0) {
				j = partialMatchTable[j];
			}
			else {
				partialMatchTable[pos] = 0;
				pos++;
			}
		}

		while (k+i < n) {
			if (pattern.charAt(i) == text.charAt(k+i)) {
				i = i+1;
				if (i == m) return k;
			}
			else if (partialMatchTable[i] == -1) {
				k = k + i + 1;
				i = 0;
			} else {
				k = k + i - partialMatchTable[i];
				i = partialMatchTable[i];
			}
		}
		return -1;
	}
}
