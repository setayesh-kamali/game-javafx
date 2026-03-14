package gui;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.OverrunStyle;
import logic.GUIConnector;
import logic.PairsLogic.Symbol;
/**
 * Connects the game logic to the JavaFX user interface. This class updates the
 * visible state of the GUI (card buttons, current player label, and winner
 * output) whenever the logic requests a change.
 * <br>
 * It is created by the controller and passed to the logic as a GUIConnector
 * implementation.
 * @author cei
 */

public class JavaFXGUI implements GUIConnector {
    /**
     * The buttons of the game field stored in an array (position in the array =
     * position on the surface).
     */
    private Button[][] btnsField;

    private Label lblCurrentPlayer;
    private Label lblWinner;
    private Label wnmn;

    /**
     * Creates a new GUI connector for JavaFX and stores all UI elements that are
     * changed by the logic.
     * <br>
     * The constructor also resets the UI to a clean start state:
     * - empty current player
     * - winner labels hidden
     * - all field buttons cleared and enabled
     *
     * @param btns the buttons representing the playfield
     * @param lblCurrentPlayer label that shows the current player's name
     * @param lblWinner label that shows the static "Winner is" text
     * @param wnmn label that shows the winner name (or "Draw")
     */
    public JavaFXGUI(Button[][] btns , Label lblCurrentPlayer , Label lblWinner , Label wnmn) {
        this.btnsField = btns;
        this.lblCurrentPlayer = lblCurrentPlayer;
        this.lblWinner = lblWinner;
        this.wnmn = wnmn;

        if (this.lblCurrentPlayer != null) {
            this.lblCurrentPlayer.setText("");
        }

        if (this.lblWinner != null) {
            this.lblWinner.setVisible(false);
            this.lblWinner.setText("Winner is");
            this.lblWinner.setWrapText(true);
            this.lblWinner.setTextOverrun(OverrunStyle.CLIP);
        }

        if (this.wnmn != null) {
            this.wnmn.setVisible(false);
            this.wnmn.setText("");
            this.wnmn.setWrapText(true);
            this.wnmn.setTextOverrun(OverrunStyle.CLIP);
        }

        if (this.btnsField != null) {
            // Reset the whole board: clear texts and make sure buttons are clickable.
            // This keeps the UI consistent when starting a new game.
            for (int x = 0; x < this.btnsField.length; x++) {
                for (int y = 0; y < this.btnsField[0].length; y++) {
                    if (this.btnsField[x][y] != null) {
                        this.btnsField[x][y].setText("");
                        this.btnsField[x][y].setDisable(false);
                    }
                }
            }
        }
    }

    /**
     * Reveals the symbol at the given coordinate by setting the corresponding
     * button text to an emoji.
     * <br>
     * Invalid input (null, wrong length, out of bounds) is ignored, because the
     * GUI should never crash due to a bad call from outside.
     *
     * @param coord coordinate in the playfield (x,y)
     * @param symbol the symbol that should be shown
     */
    @Override
    public void showCard(int[] coord, Symbol symbol) {

        if (coord == null || coord.length != 2)
            return;

        if (symbol == null)
            return;

        int x = coord[0];
        int y = coord[1];

        if (btnsField == null)
            return;

        if (x < 0 || x >= btnsField.length || y < 0 || y >= btnsField[0].length)
            return;

        Button b = btnsField[x][y];
        if (b == null)
            return;

        String emoji = symbolToEmoji(symbol);

        // If mapping is missing, do nothing instead of showing confusing text.
        if (emoji.isEmpty())
            return;

        b.setText(emoji);
    }

    /**
     * Hides the symbol at the given coordinate by clearing the button text.
     * <br>
     * Invalid input (null, wrong length, out of bounds) is ignored.
     *
     * @param pos coordinate in the playfield (x,y)
     */
    @Override
    public void hideCard(int[] pos) {

        if (pos == null || pos.length != 2)
            return;

        int x = pos[0];
        int y = pos[1];

        if (btnsField == null)
            return;

        if (x < 0 || x >= btnsField.length || y < 0 || y >= btnsField[0].length)
            return;

        Button b = btnsField[x][y];
        if (b == null)
            return;

        b.setText("");
    }

    /**
     * Updates the "Current Player" label.
     * <br>
     * Empty or null names are ignored to avoid showing placeholders or random
     * whitespace in the GUI.
     *
     * @param name name of the current player
     */
    @Override
    public void setCurrentPlayer(String name) {
        if (lblCurrentPlayer == null)
            return;

        if (name == null || name.trim().isEmpty())
            return;

        lblCurrentPlayer.setText(name.trim());
    }


    /**
     * Ends the game in the GUI:
     * - disables all playfield buttons so nothing can be clicked anymore
     * - shows the static winner label ("Winner is")
     * - shows the winner name in the separate label (or "Draw")
     *
     * @param winnerName the name of the winner, or null for a draw
     */
    @Override
    public void onGameEnd(String winnerName) {
        if (btnsField != null) {
            // Disable the entire grid so the state stays fixed after the game ended.
            for (int x = 0; x < btnsField.length; x++) {
                for (int y = 0; y < btnsField[0].length; y++) {
                    if (btnsField[x][y] != null) {
                        btnsField[x][y].setDisable(true);
                    }
                }
            }
        }

        if (lblWinner != null) {
            lblWinner.setVisible(true);
        }

        if (wnmn != null) {
            wnmn.setVisible(true);

            // Use a simple fallback if there is no single winner.
            if (winnerName == null || winnerName.trim().isEmpty())
                wnmn.setText("Draw");
            else
                wnmn.setText(winnerName.trim());
        }
    }

    /**
     * Maps a logic symbol to the emoji that is shown on the buttons.
     * <br>
     * The default case is used when a symbol is not explicitly mapped.
     *
     * @param symbol the logic symbol
     * @return the emoji string for the given symbol
     */
    private String symbolToEmoji(Symbol symbol) {

        return switch (symbol) {
            case BEE -> "🐝";
            case OWL -> "🦉";
            case PIG -> "🐷";
            case BEAR -> "🐻";
            case CAKE -> "🍰";
            case POO -> "💩";
            default -> "👻";
        };
    }

}