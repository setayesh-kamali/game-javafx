package logic;

import java.util.Arrays;
import java.util.Random;

/**
 * Logic of the game "Pairs". In this game two player play against each other on
 * a 4x3 grid. The players alternatingly select two cards of which they would
 * like to see the symbols. If the symbols match, they found a pair. Aim of this
 * game is to find more pairs than the other player.
 *
 * @author cei
 */
public class PairsLogic {

    /**
     * Name of the players in an array. Length must be 2.
     */
    private final String[] players;
    /**
     * Index of the player. Must be either 0 or 1. Always start with 0 (strictly
     * speaking initialization is not necessary, but we do it anyway).
     */
    private int idxCurrPlayer = 0;
    /**
     * Connection to the gui.
     */
    private final GUIConnector gui;

    /**
     * Enum for the symbol of the cards. More symbols necessary than needed for
     * a standard-sized (4x3) game to make it more interesting.
     */
    public enum Symbol {
        BEE, HONEY, BEAR, PIG,
        CAKE, OWL, GHOST, BOOK, BAT,
        POO, DOG, BUG
    };

    /**
     * The 2-dimensional grid of cards.
     */
    private final Symbol[][] cards;

    /**
     * Enum for information about who discovered (solved) a pair. Either it was
     * the first player, the second player or the pair has not been yet
     * discovered. The ordinal value of the respective symbol of a player
     * corresponds with the index of this player in the player array.
     */
    enum Solved {
        FST, SND, NOT
    }

    /**
     * To remember at which positions pairs have already been found by which
     * player. Important once all pairs have been found.
     */
    private final Solved[][] cardsSolved;

    /**
     * The positions of the cards that have currently been revealed. This array
     * always has the length two! If the two contained arrays are both empty,
     * that means either the game has just started or a player has just found a
     * pair. If only there is only a position at the first index, this means the
     * player has only selected one card to be revealed yet and we need to wait
     * for him/her to select a second card. If there are two positions stored in
     * here, the previous player had selected two cards (not a pair).
     */
    private final int[][] posOpenCards = new int[2][0];

    /**
     * Constructor for a game of pairs. Initializes the field.
     * <br>
     * This constructor creates a shuffled deck with exactly two of each symbol,
     * places them into the 2D grid and initializes all "solved" states to NOT.
     *
     * @param p1 name of the first player
     * @param p2 name of the second player
     * @param width width of the game field
     * @param height height of the game field
     * @param gui connection to the gui
     */
    public PairsLogic(String p1, String p2, int width, int height,
                      GUIConnector gui) {
        if (width < 1 || height < 1 || width * height % 2 != 0)
            throw new AssertionError("A " + width + "*" + height +  " playfield is useless.");
        if (gui == null)
            throw new AssertionError("GUI connection must not be null.");

        int pairs = width * height / 2;
        if (pairs > Symbol.values().length)
            throw new AssertionError("Not enough symbols for a " + width + "*" + height + " playfield.");

        this.players = new String[]{p1, p2};
        this.cardsSolved = new Solved[width][height];
        this.gui = gui;
        this.cards = new Symbol[width][height];

        // At the beginning, no pair has been found yet.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cardsSolved[x][y] = Solved.NOT;
            }
        }

        /*
         Build the deck: for each symbol we put exactly two cards into the array.
         The deck size is width*height, so there are pairs = (width*height)/2 symbols used.
         */
        Symbol[] deck = new Symbol[width * height];
        for (int i = 0; i < pairs; i++) {
            Symbol s = Symbol.values()[i];
            deck[2 * i] = s;
            deck[2 * i + 1] = s;
        }

        // Shuffle the deck so the positions are random.
        shuffle(deck);

        // Deal the deck into the 2D card grid.
        int idx = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cards[x][y] = deck[idx];
                idx++;
            }
        }

        // No open cards at the start.
        posOpenCards[0] = new int[0];
        posOpenCards[1] = new int[0];

        // At game start the GUI should not show a current player yet.
    }

    /**
     * Constructor for tests (see PubPairsLogicTest).
     * <br>
     * This constructor allows tests to pass pre-defined card layouts and solved
     * states instead of generating a random deck.
     *
     * @param players players array (length 2)
     * @param cards symbols grid (width x height)
     * @param cardsSolved solved grid (width x height)
     * @param gui connection to the gui
     */
    public PairsLogic(String[] players, Symbol[][] cards, Solved[][] cardsSolved, GUIConnector gui) {
        if (players == null || players.length != 2)
            throw new AssertionError("Players must contain exactly 2 names.");
        if (cards == null || cards.length < 1 || cards[0] == null || cards[0].length < 1)
            throw new AssertionError("Cards must be a non-empty grid.");
        if (cardsSolved == null || cardsSolved.length != cards.length || cardsSolved[0].length != cards[0].length)
            throw new AssertionError("cardsSolved must match size of cards.");
        if (gui == null)
            throw new AssertionError("GUI connection must not be null.");

        this.players = players;
        this.cards = cards;
        this.cardsSolved = cardsSolved;
        this.gui = gui;

        this.idxCurrPlayer = 0;
        posOpenCards[0] = new int[0];
        posOpenCards[1] = new int[0];
    }

    /**
     * Handles the turn of a player.
     * <br>
     * A turn works like this:
     * - If two non-matching cards were open from the previous turn, hide them first.
     * - Open the chosen card (first card of the turn) and wait for the second pick.
     * - If it is the second card of the turn, compare both symbols:
     *   - If they match: mark them as solved for the current player.
     *   - If all pairs are solved: determine winner and end the game.
     * - After the second pick (regardless of match), switch player and update the GUI.
     *
     * @param pos coordinate in the field at which the player wants to reveal a card
     */
    public void playerTurn(int[] pos) {
        if (pos == null || pos.length != 2)
            throw new AssertionError("Given Position should contain 2 values: " + Arrays.toString(pos));

        int x = pos[0];
        int y = pos[1];

        if (x < 0 || x >= cards.length || y < 0 || y >= cards[0].length)
            throw new AssertionError("Position out of bounds: " + Arrays.toString(pos));

        // Already solved positions cannot be selected again.
        if (cardsSolved[x][y] != Solved.NOT)
            return;

        // Prevent selecting the exact same open card again.
        if (posOpenCards[0].length == 2 && posOpenCards[0][0] == x && posOpenCards[0][1] == y)
            return;
        if (posOpenCards[1].length == 2 && posOpenCards[1][0] == x && posOpenCards[1][1] == y)
            return;

        /*
         If two cards are still open, they were a mismatch from the previous turn.
         Before opening a new card, we hide both again and clear the stored positions.
         */
        if (posOpenCards[0].length == 2 && posOpenCards[1].length == 2) {
            gui.hideCard(posOpenCards[0]);
            gui.hideCard(posOpenCards[1]);
            posOpenCards[0] = new int[0];
            posOpenCards[1] = new int[0];
        }

        /*
         First pick of the current turn:
         Store the coordinate and reveal the card in the GUI.
         */
        if (posOpenCards[0].length == 0) {
            posOpenCards[0] = new int[]{x, y};
            gui.showCard(posOpenCards[0], cards[x][y]);
            return;
        }

        /*
         Second pick of the current turn:
         Store the coordinate and reveal the second card.
         Then compare both symbols and decide whether it is a pair.
         */
        posOpenCards[1] = new int[]{x, y};
        gui.showCard(posOpenCards[1], cards[x][y]);

        int[] fst = posOpenCards[0];
        int[] snd = posOpenCards[1];

        Symbol s1 = cards[fst[0]][fst[1]];
        Symbol s2 = cards[snd[0]][snd[1]];

        /*
         If the two revealed symbols match, the current player has found a pair.
         We mark both positions in cardsSolved, reset open cards, and check if the game ends.
         */
        if (s1 == s2) {
            Solved solved = (idxCurrPlayer == 0) ? Solved.FST : Solved.SND;
            cardsSolved[fst[0]][fst[1]] = solved;
            cardsSolved[snd[0]][snd[1]] = solved;

            posOpenCards[0] = new int[0];
            posOpenCards[1] = new int[0];

            // If every card is solved, the game is over.
            if (allSolved()) {
                gui.onGameEnd(getWinnerNameOrNull());
                return;
            }
        }

        // Turn ends after the second pick: switch to the other player.
        idxCurrPlayer = 1 - idxCurrPlayer;
        gui.setCurrentPlayer(players[idxCurrPlayer]);
    }

    /**
     * Convenience overload for using the Position record/class instead of int[].
     * Converts Position to an int[] and delegates to playerTurn(int[]).
     *
     * @param pos the Position object containing x and y
     */
    public void playerTurn(Position pos) {
        if (pos == null)
            throw new AssertionError("Position must not be null.");
        playerTurn(new int[]{pos.x(), pos.y()});
    }

    /**
     * Returns the name of the current player.
     *
     * @return current player's name
     */
    public String getNameCurrentPlayer() {
        return players[idxCurrPlayer];
    }

    /**
     * Checks whether all cards have been solved by either player.
     * If there is still at least one NOT entry in cardsSolved, the game is not finished.
     *
     * @return true if all positions are solved, otherwise false
     */
    public boolean allSolved() {
        for (int x = 0; x < cardsSolved.length; x++) {
            for (int y = 0; y < cardsSolved[0].length; y++) {
                if (cardsSolved[x][y] == Solved.NOT)
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns the winner's name after the game has finished.
     * If the game is not finished yet, this method throws an AssertionError.
     * If the game ended in a draw, null is returned.
     *
     * @return winner name, or null if it is a draw
     */
    public String getWinnerName() {
        if (!allSolved())
            throw new AssertionError("Game is not finished yet.");
        return getWinnerNameOrNull();
    }

    /**
     * Returns a string representation of the card layout.
     * <br>
     * This is mostly useful for testing and debugging. It prints the symbols
     * in a grid-like layout with spacing so that columns line up.
     *
     * @return the card grid as a formatted string
     */
    public String cardsToString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < cards[0].length; y++) {
            for (int x = 0; x < cards.length; x++) {
                String name = String.valueOf(cards[x][y]);
                sb.append(name);
                int spaces = 6 - name.length();
                for (int i = 0; i < spaces; i++) {
                    sb.append(' ');
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Determines the winner without throwing an error if the game is not finished.
     * Counts how many solved positions belong to player 1 and player 2 and compares them.
     * If both counts are equal, the game is a draw and null is returned.
     *
     * @return winner name, or null if it is a draw
     */
    private String getWinnerNameOrNull() {
        int fst = 0;
        int snd = 0;

        /*
         Count solved fields.
         Each pair consists of two positions, so the winner is the player
         who has solved more positions overall.
         */
        for (int x = 0; x < cardsSolved.length; x++) {
            for (int y = 0; y < cardsSolved[0].length; y++) {
                if (cardsSolved[x][y] == Solved.FST)
                    fst++;
                else if (cardsSolved[x][y] == Solved.SND)
                    snd++;
            }
        }

        if (fst > snd)
            return players[0];
        if (snd > fst)
            return players[1];
        return null;
    }

    /**
     * Shuffles a deck of symbols in-place using the Fisher-Yates algorithm.
     * This randomizes the order of the symbols so each game has a different layout.
     *
     * @param deck array of symbols that will be shuffled
     */
    private static void shuffle(Symbol[] deck) {
        Random rnd = new Random();
        for (int i = deck.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            Symbol tmp = deck[i];
            deck[i] = deck[j];
            deck[j] = tmp;
        }
    }

}