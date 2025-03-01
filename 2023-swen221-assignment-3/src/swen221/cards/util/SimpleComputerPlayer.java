// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.cards.util;

import swen221.cards.core.Card;
import swen221.cards.core.Player;
import swen221.cards.core.Trick;

/**
 * Implements a simple computer player who plays the highest card available when
 * the trick can still be won, otherwise discards the lowest card available. In
 * the special case that the player must win the trick (i.e. this is the last
 * card in the trick), then the player conservatively plays the least card
 * needed to win.
 *
 * @author David J. Pearce
 *
 */
public class SimpleComputerPlayer extends AbstractComputerPlayer {

    /**
     * Construct a new (simple) computer player with the given player information.
     *
     * @param player Key player informmation.
     */
    public SimpleComputerPlayer(Player player) {
        super(player);
    }

    @Override
    public Card getNextCard(Trick trick) {
        Card.Suit trumpSuit = trick.getTrumps();

        // If the player is the first to play,
        // just play the best card in the hand.
        if (trick.getCardsPlayed().size() == 0) {
            return player.getHand().getBestCard(trumpSuit, true);
        }

        Card.Suit leadSuit = trick.getLeadSuit();

        // If the player holds a card in the lead suit
        if (player.getHand().matches(leadSuit).size() > 0) {
            // If the player can win
            if (isTrickWinnable(trick)) {
                // If the player is the last to play,
                // just play the lowest card that beats
                // the best card in th lead suit
                if(trick.isAtLastTurn()) {
                    return player.getHand().getLowestCardHigherThan(trick.getHighestCardOfSuit(leadSuit));
                }

                // Otherwise, just play the best card the player has in the lead suit.
                return player.getHand().getHighestCardInSuit(leadSuit);
            }

            // The player cannot win. Discard the worst card.
            return player.getHand().getLowestCardInSuit(leadSuit);
        }

        // If the player holds the trump suit
        if(player.getHand().matches(trumpSuit).size() > 0 && isTrickWinnable(trick)) {
            // If the player is the last to play,
            // play the lowest trump card that beats
            // the best currently played trump card.
            if (trick.isAtLastTurn()) {
                return player.getHand().getLowestCardHigherThan(trick.getHighestCardOfSuit(trumpSuit));
            }

            // Otherwise, just play the best card the player has in the trump suit.
            return player.getHand().getHighestCardInSuit(trumpSuit);
        }

        // At this point, the player does not have a winnable trump card or the lead suit.
        // Just play the worst card.
        return player.getHand().getWorstCard();
    }

    /**
     * Determines whether this trick is winnable.
     */
    private boolean isTrickWinnable(Trick trick) {
        Card.Suit leadSuit = trick.getLeadSuit();
        Card currentWinningCard = trick.getHighestCardOfSuit(leadSuit);

        Card.Suit trumpSuit = trick.getTrumps();

        if (player.getHand().matches(leadSuit).size() > 0) {
            // Check whether trumps are in play.
            if(trick.haveTrumpsBeenPlayed() && trumpSuit != leadSuit) {
                // The player cannot win here, as they have a card in the lead suit
                // (and must play it), but trumps have been played.
                return false;
            }

            // The trump suit has not been played.
            // Do we have a card in the same suit that beats the current winning card?
            return player.getHand().hasSameSuitCardBetterThan(currentWinningCard);
        }

        // The player does not have any cards in the lead suit.
        // Check for trump cards.
        if (player.getHand().matches(trumpSuit).size() > 0) {
            if (trick.haveTrumpsBeenPlayed()) {
                return player.getHand().hasSameSuitCardBetterThan(trick.getHighestCardOfSuit(trumpSuit));
            }

            // The trump suit hasn't been played,
            // but we have a trump card. We could win by playing it.
            return true;
        }

        // Does not have the trump suit, or any cards that can beat the leader.
        // We cannot win.
        return false;
    }
}