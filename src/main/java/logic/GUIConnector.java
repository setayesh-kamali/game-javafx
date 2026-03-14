package logic;

import logic.PairsLogic.Symbol;

/**
 * Interface used for the logic of the game pairs to communicate with the gui.
 *
 * @author cei
 */
public interface GUIConnector {

    /**
     * Shows the symbol of a card at a given position.
     *
     * @param pos the position at which in the field the given card is revealed
     * @param symbol the symbol which should be displayed at the given coordinates
     */
    void showCard(int[] pos, Symbol symbol);

    /**
     * Hides the symbol of the card at the given position.
     *
     * @param pos position at which the symbol of a given card should be hidden
     */
    void hideCard(int[] pos);

    /**
     * Sets the name of the current player on the gui.
     *
     * @param name name of the current player
     */
    void setCurrentPlayer(String name);

    /**
     * Called when the game is won by a player. Needs to display the name of the
     * winner on the gui and has to ensure that the user cannot continue playing
     * (e.g. by disabling components). If there is no winner, because both
     * player found an even number of pairs, no player has won (but in a way the
     * computer). Then this method can be used to display that "no one" is the
     * winner on the gui.
     *
     * @param winnerName name of the player than won; null if there is no
     * winner, but the game has ended
     */
    void onGameEnd(String winnerName);

}
