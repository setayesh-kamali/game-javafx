package logic;

import logic.PairsLogic.Symbol;

/**
 * Fake GUI used for testing the logic of the game Pairs. All methods do nothing. To ensure that the logic calls the
 * correct methods for the gui, it could be possibly to add package private boolean attributes, that tell if a certain
 * method has been called.
 * <p>
 * Should be placed in test folder.
 *
 * @author cei
 */
public class FakeGUI implements GUIConnector {

    @Override
    public void showCard(int[] pos, Symbol symbol) {
    }

    @Override
    public void hideCard(int[] pos) {
    }

    @Override
    public void setCurrentPlayer(String name) {
    }

    @Override
    public void onGameEnd(String winnerName) {
    }


}
