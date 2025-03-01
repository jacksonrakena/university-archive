
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

record CompressionTuple(int offset, int length, char symbol) {
    @Override
    public String toString() {
        return "[" + offset + "|" + length + "|" + symbol + "]";
    }

    public static CompressionTuple parse(String text) {
        text = text.replace("[", "");
        String[] components = text.split("\\|");
        var offset = Integer.parseInt(components[0]);
        var length = Integer.parseInt(components[1]);
        var symbol = components[2].charAt(0);
        return new CompressionTuple(offset, length, symbol);
    }

    public static List<CompressionTuple> parseGroup(String text) {
        var tuples = new ArrayList<CompressionTuple>();

        for (String group : text.split("]")) {
            tuples.add(CompressionTuple.parse(group));
        }

        return tuples;
    }
}

public class LempelZiv {
    /**
     * Take uncompressed input as a text string, compress it, and return it as a
     * text string.
     */
    public static String compress(String input) {
        var cursor = 0;
        var windowSize = 100;
        var lookaheadBufferSize = 8;
        var compression = new ArrayList<CompressionTuple>();

        while (cursor < input.length()) {
            var length = 1;
            var prevMatch = -1;
            while (true) {
                var pattern = "-1";
                var searchTarget = "0";

                if (cursor + length < input.length()) {
                    pattern = input.substring(cursor, cursor+length);
                    searchTarget = input.substring(cursor < windowSize ? 0 : cursor - windowSize, cursor);
                }

                var match = -1;
                if (cursor < input.length() - 1) match = searchTarget.indexOf(pattern);

                if (match != -1 && length < lookaheadBufferSize) {
                    prevMatch = match + ((cursor < 100) ? 0 : cursor - 100);
                    length++;
                } else {
                    compression.add(
                        new CompressionTuple(
                            // Compute the offset.
                            ((prevMatch != -1) ? (cursor - prevMatch) : 0),

                            // Record the length of this substitution.
                            length - 1,

                            // Record the character.
                            input.charAt(cursor+length-1)
                        )
                    );
                    cursor = cursor + length;
                    break;
                }
            }
        }
        return compression.stream().map(CompressionTuple::toString).collect(Collectors.joining());
    }

    /**
     * Take compressed input as a text string, decompress it, and return it as a
     * text string.
     */
    public static String decompress(String compressed) {
        var tuples = CompressionTuple.parseGroup(compressed);

        var output = new StringBuilder();
        var cursor = 0;
        for (var tuple : tuples) {
            if (tuple.offset() == 0 && tuple.length() == 0) {
                output.append(tuple.symbol());
                cursor++;
            } else {
                for (int j = 0; j < tuple.length(); j++) {
                    output.append(output.charAt(cursor-tuple.offset()));
                    cursor++;
                }
                cursor++;
                output.append(tuple.symbol());
            }
        }
        return output.toString();
    }

    /**
     * The getInformation method is here for your convenience, you don't need to
     * fill it in if you don't want to. It is called on every run and its return
     * value is displayed on-screen. You can use this to print out any relevant
     * information from your compression.
     */
    public String getInformation() {
        return "";
    }
}
