// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.cards.core;

import java.util.*;

/**
 * Represents a hand of cards held by a player. As the current round proceeds,
 * the number of cards in the hand will decrease. When the round is over, new
 * cards will be delt and added to this hand.
 *
 * @author David J. Pearce
 *
 */
public class Hand implements Cloneable, Iterable<Card> {
	private SortedSet<Card> cards = new TreeSet<>();


	public Set<Card> getSet() {
		return new HashSet<>(cards);
	}
	@Override
	public Iterator<Card> iterator() {
		return cards.iterator();
	}

	/**
	 * Check whether a given card is contained in this hand, or not.
	 *
	 * @param card
	 * @return <code>true</code> if the card is contained in this hand;
	 *         <code>false</code> otherwise.
	 */
	public boolean contains(Card card) {
		return cards.contains(card);
	}

	/**
	 * Return all cards in this hand which match the given suit.
	 *
	 * @param suit
	 * @return The set of matching cards (if any).
	 */
	public Set<Card> matches(Card.Suit suit) {
		HashSet<Card> r = new HashSet<>();
		for(Card c : cards) {
			if(c.suit() == suit) {
				r.add(c);
			}
		}
		return r;
	}

	@Override
	public String toString() {
		return this.cards.toString();
	}


	/**
	 * Add a card to the hand.
	 * @param card The card to be added.
	 */
	public void add(Card card) {
		cards.add(card);
	}

	/**
	 * Remove a card from the hand.
	 * @param card The card to be removed.
	 */
	public void remove(Card card) {
		cards.remove(card);
	}

	/**
	 * Get number of cards in this hand.
	 *
	 * @return The size of this hand.
	 */
	public int size() {
		return cards.size();
	}

	/**
	 * Remove all cards from this hand.
	 */
	public void clear() {
		cards.clear();
	}

	@Override
	public Hand clone() {
		var h = new Hand();
		for (Card c : this.cards) {
			h.add(new Card(c.suit(), c.rank()));
		}
		return h;
	}

	/**
	 * Gets the best card in this hand, factoring in a possible trump suit,
	 * and whether this hand is currently leading the game.
	 */
	public Card getBestCard(Card.Suit trumps, boolean isSuitLeader) {
		if (matches(trumps).size() > 0) return getHighestCardInSuit(trumps);

		if (isSuitLeader) {
			var maxRank = cards.stream().max(Comparator.comparing(Card::rank)).get().rank();
			return cards.stream()
					.filter(card -> card.rank().equals(maxRank))
					.max(Card::compareTo)
					.get();
		}

		return cards.stream().max(Card::compareTo).get();
	}

	/**
	 * Returns the highest card held by this hand,
	 * in a given suit.
	 */
	public Card getHighestCardInSuit(Card.Suit s) {
		return cards.stream()
				.filter(card -> card.suit().equals(s))
				.max(Card::compareTo)
				.get();
	}

	/**
	 * Returns the lowest card that is better than the given card in this hand.
	 */
	public Card getLowestCardHigherThan(Card minimum) {
		return cards.stream()
				.filter(card -> card.compareTo(minimum) > 0)
				.min(Card::compareTo)
				.get();
	}

	/**
	 * Returns the lowest card held by this hand in a given suit.
	 */
	public Card getLowestCardInSuit(Card.Suit s) {
		return cards.stream()
				.filter(card -> card.suit().equals(s))
				.min(Card::compareTo)
				.get();
	}

	/**
	 * Returns true if a card exists in this hand that beats the supplied card,
	 * but has the same suit as the supplied card.
	 */
	public boolean hasSameSuitCardBetterThan(Card s) {
		return getHighestCardInSuit(s.suit()).compareTo(s) > 0;
	}

	public Card getWorstCard() {
		return cards.stream().min(Comparator.comparing(Card::suit)).get();
	}
}
