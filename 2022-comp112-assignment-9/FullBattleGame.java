// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 9
 * Name:
 * Username:
 * ID:
 */

import ecs100.UI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

// Challenge version
public class FullBattleGame{

    // Constants for the game
    public static final int NUM_HAND = 5;      // Number of cards in hand
    public static final int NUM_REPLACE = 3;   // Number of cards the player is allowed to replace per game

    // Fields for the game: deck, hand, and table
    private ArrayList<Card> playerDeck;
    private ArrayList<Card> computerDeck;
    private Card[] hand = new Card[NUM_HAND];                       // the hand (fixed size array of Cards)
    private ArrayList<Card> tableComputer = new ArrayList<Card>();  // the list of Cards that the computer has played
    private ArrayList<Card> tablePlayer = new ArrayList<Card>();    // the list of Cards that the player has played

    private int selectedPos = 0;      // selected position in the hand.

    private Card lastWinningCard = null;

    private int remainingReplaces = NUM_REPLACE;  //

    // Constants for the layout
    public static final Color RACK_COLOR = new Color(122,61,0);

    public static final int HAND_LEFT = 60;      // x-position of the leftmost Card in the hand
    public static final int HAND_TOP = 500;      // y-Position of all the Cards in the hand
    public static final int CARD_SPACING = 80;   // spacing is the distance from left side of a card
    // to left side of the next card in the hand
    public static final int CARD_OVERLAP = 15;   // overlap is the distance from left side of a card
    // to left side of the next card on the table
    public static final int CARD_HEIGHT = 110;

    public static final int TABLE_LEFT = 10;
    public static final int TABLE_TOP_COMPUTER = 80;
    public static final int TABLE_TOP_PLAYER   = TABLE_TOP_COMPUTER+CARD_HEIGHT+10;

    public static final int SCORES_TOP = 20;

    /**
     * CORE
     *
     * Restarts the game:
     *  get a new shuffled deck,
     *  set the compScore, playScore and remainingReplaces to their initial values
     *  set the table to be empty,
     *  refill the hand from the deck
     */
    public void restart(){
        ArrayList<Card> deck = Card.getShuffledDeck();
        this.playerDeck = new ArrayList<Card>();
        this.computerDeck = new ArrayList<Card>();
        for (int i = 0; i < deck.size()/2; i++) {
            this.playerDeck.add(deck.get(i));
        }
        for (int i = deck.size()/2; i < deck.size(); i++) {
            this.computerDeck.add(deck.get(i));
        }
        this.remainingReplaces = NUM_REPLACE;
        this.tableComputer.clear();
        this.tablePlayer.clear();

        // Add NUM_HAND cards to the player's hand
        for (int i = 0; i < NUM_HAND; i++) {
            pickupCard();
        }

        this.redraw();
    }


    /**
     * CORE
     *
     * If the deck is not empty and there is at least one empty position on the hand, then
     * pick up the top card from the deck and put it into the first empty position on the hand.
     * (needs to search along the array for an empty position.)
     */
    public void pickupCard(){
        // Check if the deck ran out
        if (playerDeck.isEmpty()) {
            UI.printMessage("Cannot draw a card. The deck is empty.");
            return;
        }

        // Remove the card from the deck
        Card card = playerDeck.remove(0);
        boolean drew = false;

        // Search for an empty spot
        for (int i = 0; i < NUM_HAND; i++) {

            // If it's empty, place it and stop searching
            if (hand[i] == null) {
                hand[i] = card;
                drew = true;
                break;
            }
        }

        // Notify the user
        if (drew) UI.printMessage("You drew a " + card.getSuit() + " of rank " + card.getRank());
        else {
            playerDeck.add(card);
            UI.printMessage("Cannot draw another card. Your hand is full.");
        }

        this.redraw();
    }

    /**
     * CORE
     *
     * Draws all the Cards in the hand,
     *  This MUST use the constants:  (in order to make the selection work!)
     *   - CARD_SPACING, HAND_LEFT, HAND_TOP
     *   See the descriptions where these fields are defined.
     */
    public void drawHandCards(){
        // Call drawCardsToScreen with the provided parameters
        drawCardsToScreen(Arrays.stream(hand).collect(Collectors.toList()),HAND_LEFT, HAND_TOP, CARD_SPACING);
    }

    /**
     * Draws a collection of cards to the screen, using the provided (left, top) positions for the origin
     * and the provided 'spacing' value between each card.
     */
    public void drawCardsToScreen(Collection<Card> cards, double left, double top, double spacing) {
        // Loop over the cards to draw
        for (int i = 0; i < cards.size(); i++) {
            Card card = (Card) cards.toArray()[i];

            // Don't draw non-existent cards
            if (card != null) {

                // Highlight this card if it won the last battle
                if (card == lastWinningCard) {
                    UI.setColor(Color.yellow.darker());
                    UI.drawRect(left + (i*spacing) - 2, top - 2, CARD_SPACING, CARD_HEIGHT+4);

                }

                // Draw the card
                card.draw(left+(i*spacing), top);
            }
        }
    }

    /**
     * CORE
     * Draws all the Cards in both the computer and player tables in two rows.
     *   See the descriptions of TABLE_LEFT, TABLE_TOP_COMPUTER, TABLE_TOP_PLAYER and CARD_OVERLAP.
     *
     * COMPLETION:
     * - The card with the highest rank in the last battle is outlined
     *
     */
    public void drawTableCards(){
        // Draw both card collections to screen
        drawCardsToScreen(tableComputer, TABLE_LEFT, TABLE_TOP_COMPUTER, CARD_OVERLAP);
        drawCardsToScreen(tablePlayer, TABLE_LEFT, TABLE_TOP_PLAYER, CARD_OVERLAP);
    }

    public int calculateSizeOfPlayerHoldings() {
        return playerDeck.size() + (int) Arrays.stream(hand).filter(Objects::nonNull).count();
    }

    /**
     * CORE
     *
     * If there is a card in the leftmost position in the hand, then
     * - place it on the table
     * - gets the top card from the deck for the computer player and places it to the table
     * - compare the ranks of the two cards and award a point to the player with the highest card.
     * - redraw the table and hand [this.redraw()]
     * - if the player or the computer have reached the target,  end the game.
     */
    public void playBattle(){
        if (tablePlayer.size() > 20) {
            tablePlayer.clear();
            tableComputer.clear();
        }
        // Select the player's first card, add it to the table, and remove it from their hand
        Card playerCard = hand[0];
        if (playerCard == null) {
            UI.printMessage("You don't have a card in your first slot. Try moving one.");
            return;
        }
        tablePlayer.add(playerCard);
        hand[0] = null;

        // Select the computer's card and add it to the table
        Card computerCard = computerDeck.remove(0);
        tableComputer.add(computerCard);

        // Compare the card ranks
        if (computerCard.getRank() > playerCard.getRank()) {
            UI.printMessage("The computer won this battle.");
            this.computerDeck.add(playerCard);
            this.computerDeck.add(computerCard);
            lastWinningCard = computerCard;
        } else if (computerCard.getRank() < playerCard.getRank()) {
            UI.printMessage("You won this battle.");
            this.playerDeck.add(computerCard);
            this.playerDeck.add(playerCard);
            lastWinningCard = playerCard;
        } else {
            ArrayList<Card> warCards = new ArrayList<Card>();
            for (int i = 0; i < 3; i++) {
                Card playerWarCard = playerDeck.remove(0);
                playerWarCard.setFaceDown(true);
                Card computerWarCard = computerDeck.remove(0);
                computerWarCard.setFaceDown(true);

                tableComputer.add(computerWarCard);
                tablePlayer.add(playerWarCard);
                warCards.add(computerWarCard);
                warCards.add(playerWarCard);
            }
            Card winnerPlayerCard = playerDeck.remove(0);
            Card winnerComputerCard = computerDeck.remove(0);
            while (winnerPlayerCard.getRank() == winnerComputerCard.getRank()) {
                warCards.add(winnerPlayerCard);
                warCards.add(winnerComputerCard);
                winnerPlayerCard.setFaceDown(true);
                winnerComputerCard.setFaceDown(true);
                tableComputer.add(winnerComputerCard);
                tablePlayer.add(winnerPlayerCard);

                winnerPlayerCard = playerDeck.remove(0);
                winnerComputerCard = computerDeck.remove(0);
                if (winnerPlayerCard.getRank() != winnerComputerCard.getRank()) {
                    winnerPlayerCard.setFaceDown(false);
                    winnerComputerCard.setFaceDown(false);
                } else {
                    playerDeck.add(winnerPlayerCard);
                    computerDeck.add(winnerComputerCard);
                    warCards.add(winnerPlayerCard);
                    warCards.add(winnerComputerCard);
                }
            }
            tableComputer.add(winnerComputerCard);
            tablePlayer.add(winnerPlayerCard);
            if (winnerPlayerCard.getRank() > winnerComputerCard.getRank()) {
                playerDeck.addAll(warCards);
                UI.printMessage("You won the war! You get " + warCards.size() + " cards.");
            } else {
                UI.printMessage("You lost the war. Computer gets " + warCards.size() + " cards.");
                computerDeck.addAll(warCards);
            }
        }

        // Check if the game is over
        if (computerDeck.isEmpty() || calculateSizeOfPlayerHoldings() == 0) endGame();

        redraw();
    }

    /**
     * COMPLETION
     *
     * If there is a card at the selected position in the hand,
     * replace it by a card from the deck.
     */
    public void replaceCard() {
        if (remainingReplaces > 0) {
            // Erase the selected card and replace it with the next card from the deck
            hand[selectedPos] = playerDeck.remove(0);

            remainingReplaces--;
        } else UI.printMessage("You don't have any replaces left.");
        this.redraw();
    }

    /**
     * COMPLETION
     *
     * Swap the contents of the selected position on hand with the
     * position on its left (if there is such a position)
     * and also decrement the selected position to follow the card
     */
    public void moveLeft(){
        if (selectedPos == 0) {
            UI.printMessage("You can't move this card to the left.");
            return;
        }
        Card original = hand[selectedPos];
        Card replace = hand[selectedPos-1];
        hand[selectedPos] = replace;
        hand[selectedPos-1] = original;
        selectedPos--;
        this.redraw();
    }

    /** ---------- The code below is already written for you ---------- **/

    /**
     * Allows the user to select a position in the hand using the mouse.
     * If the mouse is released over the hand, then sets  selectedPos
     * to be the index into the hand array.
     * Redraws the hand and table
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            if (y >= HAND_TOP && y <= HAND_TOP+CARD_HEIGHT &&
                    x >= HAND_LEFT && x <= HAND_LEFT + NUM_HAND*CARD_SPACING) {
                this.selectedPos = (int) ((x-HAND_LEFT)/CARD_SPACING);
                //UI.clearText();UI.println("selected "+this.selectedPos);
                this.redraw();
            }
        }
    }

    /**
     * Displays a win/lose message
     */
    public void endGame(){
        UI.setFontSize(40);
        UI.setColor(Color.red);
        if (this.computerDeck.isEmpty()){
            UI.drawString("YOU WIN!!!", 500, HAND_TOP-80);
        }
        else{
            UI.drawString("YOU LOSE", 500, HAND_TOP-80);
        }
        UI.sleep(3000);
        this.restart();
    }

    /**
     *  Redraw the table and the hand.
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(20);
        UI.setColor(Color.black);
        UI.drawString("Player cards left: " + calculateSizeOfPlayerHoldings() + " Computer cards left: " + computerDeck.size(), TABLE_LEFT+150, SCORES_TOP);
        UI.drawString("Remaining replaces: " + remainingReplaces, TABLE_LEFT+600, SCORES_TOP);

        // outline the hand and the selected position
        UI.setLineWidth(2);
        UI.setColor(Color.black);
        UI.drawRect(HAND_LEFT-4, HAND_TOP-4, (CARD_SPACING)*NUM_HAND+4, CARD_HEIGHT+8);

        UI.setColor(Color.green);
        int selLeft = HAND_LEFT + (this.selectedPos * (CARD_SPACING)) - 2;
        UI.drawRect(selLeft, HAND_TOP - 2, CARD_SPACING, CARD_HEIGHT+4);

        // draw the rack top
        UI.setColor(RACK_COLOR);
        UI.fillRect(HAND_LEFT-10, HAND_TOP-28, (CARD_SPACING)*NUM_HAND+20, 20);

        this.drawHandCards();
        this.drawTableCards();
    }

    /**
     * Set up the user interface
     */
    public void setupGUI(){
        UI.setMouseListener( this::doMouse );
        UI.addButton("Battle", this::playBattle);
        UI.addButton("Pickup", this::pickupCard);
        UI.addButton("Replace",  this::replaceCard);
        UI.addButton("Left", this::moveLeft);
        UI.addButton("Restart", this::restart);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100,650);
        UI.setDivider(0.0);

    }

    public static void main(String[] args){
        FullBattleGame bg = new FullBattleGame();
        bg.setupGUI();
        bg.restart();
    }
}
