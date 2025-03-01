import java.util.Arrays;
import java.util.stream.IntStream;

public class BoyerMoore{
	public static int search(String pattern, String text) {
		if (pattern.length() == 0) return 0;
		if (text.length() == 0) return -1;

		// Build character jump table
		var charTable = new int[Character.MAX_VALUE + 1];

		for (int i = 0; i < pattern.length(); i++) {
			charTable[pattern.charAt(i)] = pattern.length() - 1 - i;
		}

		// Mismatch table
		var bcrTable = new int[pattern.length()];
		var prefixPositionTransient = pattern.length();

		// Iterate backwards through the string, trying to find an ideal prefix
		for (int patternReverseIndex = pattern.length(); patternReverseIndex > 0; patternReverseIndex--) {
			int capturedIndex = patternReverseIndex; // have to copy a variable for a lambda :malding:

			// for all x in (i..pattern_size), verify that pattern[x] == pattern[x-current_position_in_pattern]
			if (IntStream.range(patternReverseIndex, pattern.length()).allMatch(p -> pattern.charAt(p) == pattern.charAt(p - capturedIndex))) {
				prefixPositionTransient = patternReverseIndex;
			}

			bcrTable[pattern.length() - patternReverseIndex] = prefixPositionTransient - patternReverseIndex + pattern.length();
		}

		// Apply good-suffix rule
		for (int i = 0; i < pattern.length() - 1; i++) {
			int bestSuffixLength = 0;
			var forwardsTracker = i;
			var backwardsTracker = pattern.length() - 1;

			while (forwardsTracker >= 0 && pattern.charAt(forwardsTracker) == pattern.charAt(backwardsTracker)) {
			    bestSuffixLength += 1;
				forwardsTracker--;
				backwardsTracker--;
			}

			bcrTable[bestSuffixLength] = pattern.length() - 1 - i + bestSuffixLength;
		}

		// Actually search for the string
		var positionIndicator = pattern.length() - 1;
		int backwardsSearch;
		while (positionIndicator < text.length()) {
			for (backwardsSearch = pattern.length() - 1;
				 pattern.charAt(backwardsSearch) == text.charAt(positionIndicator);
				 positionIndicator--, backwardsSearch--) {

				if (backwardsSearch == 0) {
					return positionIndicator;
				}
			}

			var possibleBcrJump = bcrTable[pattern.length() - 1 - backwardsSearch];
			var possibleCharJump = charTable[text.charAt(positionIndicator)];

			positionIndicator += Math.max(possibleBcrJump, possibleCharJump);
		}
		return -1;
	}
}
