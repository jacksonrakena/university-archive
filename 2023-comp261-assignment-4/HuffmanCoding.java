/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */

import java.util.*;

public class HuffmanCoding {
	private VNode root;
	private final HashMap<Character, Integer> frequency = new HashMap<>();
	private final HashMap<Character, String> codes = new HashMap<>();

	public HuffmanCoding(String text) {
		for (char c : text.toCharArray()) frequency.merge(c,1, Integer::sum);
	}

	public String encode(String text) {
		var queue = new PriorityQueue<VNode>();

		for (var frequencyEntry : frequency.entrySet()) queue.add(new Leaf(frequencyEntry.getKey(), frequencyEntry.getValue()));

		while (queue.size() > 1) queue.add(new Node(queue.poll(), queue.poll()));

		root = queue.poll();
		traverse(root, "");

		var encoded = new StringBuilder();
		for (var character : text.toCharArray()) encoded.append(codes.get(character));
		return encoded.toString();
	}

	private void traverse(VNode node, String code){
		if (node instanceof Leaf) {
			codes.put(((Leaf) node).character, code);
			return;
		}
		var nodeWithChildren = (Node) node;

		traverse(nodeWithChildren.left, code.concat("0"));
		traverse(nodeWithChildren.right, code.concat("1"));
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 */
	public String decode(String encoded) {
		StringBuilder decoded = new StringBuilder();
		VNode current = root;

		for (char character : encoded.toCharArray()){
			current = (character == '0' ? ((Node) current).left : ((Node) current).right);

			if(current instanceof Leaf){
				decoded.append(((Leaf) current).character);
				current = root;
			}
		}
		return decoded.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't wan to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 */
	public String getInformation() {
		return "";
	}
}

/**
 * Represents an abstract member of a Huffman tree.
 */
abstract class VNode implements Comparable<VNode> {
	public final int frequency;
	protected VNode(int freq) {
		this.frequency = freq;
	}

	@Override
	public int compareTo(VNode node){
		if (this.frequency != node.frequency) return Integer.compare(this.frequency, node.frequency);

		if (this instanceof Leaf && node instanceof Leaf) {
			return Character.compare(((Leaf) this).character, ((Leaf) node).character);
		}
		return 0;
	}
}

/**
 * Represents a non-terminal tree node, containing at least one child.
 */
class Node extends VNode {
	public VNode left;
	public VNode right;
	public Node(VNode left, VNode right){
		super((left == null ? 0 : left.frequency) + (right == null ? 0 : right.frequency));
		this.left = left;
		this.right = right;
	}
}

/**
 * Represents a terminal node, containing an encoded character.
 */
class Leaf extends VNode {
	public final char character;
	public Leaf(char c, int frequency) {
		super(frequency);
		this.character = c;
	}
}
