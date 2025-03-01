// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.cards.util;

import java.util.*;
import java.util.stream.Collectors;

import swen221.cards.core.*;
import swen221.cards.core.Player.Direction;
import swen221.cards.variations.ClassicWhist;
import swen221.cards.variations.KnockOutWhist;
import swen221.cards.variations.SingleHandWhist;

/**
 * Represents an abstract whist-like card game. This provides a common
 * implementation of the CardGame interface, which the different variations on
 * the game can extend and modify. For example, some variations will score
 * differently. Others will deal different numbers of cards, etc.
 *
 * @author David J. Pearce
 *
 */
public abstract class AbstractCardGame implements CardGame {

	/**
	 * A map of positions around the table to the players in the game.
	 */
	protected Map<Player.Direction, Player> players = new HashMap<>();

	/**
	 * Keeps track of the number of tricks each player has won in the current
	 * round.
	 */
	protected Map<Player.Direction,Integer> tricks = new HashMap<>();

	/**
	 * Keeps track of the player scores. In some games, this may equal the number
	 * of tricks. In others, this may include certain bonuses that were
	 * obtained.
	 */
	protected Map<Player.Direction,Integer> scores = new HashMap<>();

	/**
	 * Keep track of which suit is currently trumps. Here, "null" may be used to
	 * indicate no trumps.
	 */
	protected Card.Suit trumps = Card.Suit.HEARTS;

	/**
	 * The current trick being played.
	 */
	protected Trick currentTrick;

	/**
	 * Construct a new (abstract) card game where each player initially has taken no
	 * tricks, and has no score.
	 */
	public AbstractCardGame() {
		for(Player.Direction d : Player.Direction.values()) {
			players.put(d, new Player(d));
			tricks.put(d, 0);
			scores.put(d, 0);
		}
	}

	// ========================================================
	// Methods required for Cloneable
	// ========================================================

	@Override
	public CardGame clone() {
		try {
			// Clone self so it creates the correct descendant of AbstractCardGame.
			AbstractCardGame clone = (AbstractCardGame) super.clone();

			// Deep-clone the player state map by cloning the individual Player objects.
			// The Player clone is responsible for cloning itself with Player#clone.
			clone.players = this.players.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, a -> a.getValue().clone()));

			// The Trick class is responsible for cloning itself with Player#clone
			if (this.currentTrick != null) clone.currentTrick = this.currentTrick.clone();

			// Scores and tricks only use enums or primitives for keys and values,
			// so the keys and values don't need to be deep-cloned.
			clone.scores = new HashMap<>(this.scores);
			clone.tricks = new HashMap<>(this.tricks);

			// Trumps is an enum so doesn't need to be cloned.
			clone.trumps = this.trumps;
			return clone;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	// ========================================================
	// Methods required for CardGame
	// ========================================================

	@Override
	public Player getPlayer(Player.Direction d) {
		return players.get(d);
	}

	@Override
	public Trick getTrick() {
		return currentTrick;
	}

	@Override
	public boolean isHandFinished() {
		for (Player.Direction d : Player.Direction.values()) {
			if (players.get(d).getHand().size() > 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Set<Direction> getWinnersOfGame() {
		int maxScore = 0;
		// first, calculate winning score
		for (Player.Direction d : Player.Direction.values()) {
			maxScore = Math.max(maxScore, scores.get(d));
		}

		// second, calculate winners
		HashSet<Direction> winners = new HashSet<>();
		for (Player.Direction d : Player.Direction.values()) {
			if(scores.get(d) == maxScore) {
				winners.add(d);
			}
		}
		return winners;
	}

	@Override
	public Map<Player.Direction,Integer> getTricksWon() {
		return tricks;
	}

	@Override
	public Map<Player.Direction,Integer> getOverallScores() {
		return scores;
	}

	@Override
	public void play(Direction player, Card card) throws IllegalMove {
		Player pl = players.get(player);
		currentTrick.play(pl, card);
	}

	@Override
	public void startRound() {
		// First, decide who the leader is for this round
		Player.Direction d = Player.Direction.NORTH;
		if(currentTrick != null) {
			d = currentTrick.getWinner();
		}
		// Second, start a new trick
		currentTrick = new Trick(d,trumps);
	}

	@Override
	public void endRound() {
		// Score previous round
		Player.Direction winner = currentTrick.getWinner();
		tricks.put(winner, tricks.get(winner) + 1);
	}

	@Override
	public void endHand() {
		// Update scores since we've completed a hand
		scoreHand();
		resetTricksWon();
		// now cycle trumps
		trumps = nextTrumps(currentTrick.getTrumps());
	}

	// ========================================================
	// Helper methods
	// ========================================================

	private void scoreHand() {
		int maxScore = 0;
		// first, calculate winning score
		for (Player.Direction d : Player.Direction.values()) {
			maxScore = Math.max(maxScore, tricks.get(d));
		}
		// second, update winners
		for (Player.Direction d : Player.Direction.values()) {
			if(tricks.get(d) == maxScore) {
				scores.put(d, scores.get(d) + 1);
			}
		}
	}

	private void resetTricksWon() {
		for(Player.Direction d : Player.Direction.values()) {
			tricks.put(d, 0);
		}
	}

	private void resetOverallScores() {
		for(Player.Direction d : Player.Direction.values()) {
			scores.put(d, 0);
		}
	}

	/**
	 * Create a complete deck of 52 cards.
	 *
	 * @return Returns a list of the 52 cards making up a deck.
	 */
	public static List<Card> createDeck() {
		ArrayList<Card> deck = new ArrayList<>();
		for (Card.Suit suit : Card.Suit.values()) {
			for (Card.Rank rank : Card.Rank.values()) {
				deck.add(new Card(suit, rank));
			}
		}
		return deck;
	}

	/**
	 * Determine the next trumps in the usual sequence of Hearts, Clubs, Diamonds,
	 * Spades, No Trumps.
	 *
	 * @param s The current suite of trumps.
	 * @return The next suite of trumps in order, where <code>null</code> signals
	 *         "no trumps".
	 */
	protected static Card.Suit nextTrumps(Card.Suit s) {
		if(s == null) {
			return Card.Suit.HEARTS;
		}
		switch(s) {
		case HEARTS:
			return Card.Suit.CLUBS;
		case CLUBS:
			return Card.Suit.DIAMONDS;
		case DIAMONDS:
			return Card.Suit.SPADES;
		case SPADES:
			return null;
		}
		// dead code!
		return Card.Suit.HEARTS;
	}
}
