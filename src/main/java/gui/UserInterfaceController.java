package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import logic.PairsLogic;

/**
 * Main class for the user interface.
 * <br>
 * This controller connects the JavaFX layout with the game logic.
 * It handles user interaction such as starting a game, resetting the UI
 * and forwarding button clicks from the grid to the game logic.
 *
 * @author mjo, cei
 */
public class UserInterfaceController {


    /** Button representing the grid position (0,0). */
    @FXML
    private Button btn00;

    /** Button representing the grid position (1,0). */
    @FXML
    private Button btn10;

    /** Button representing the grid position (2,0). */
    @FXML
    private Button btn20;

    /** Button representing the grid position (3,0). */
    @FXML
    private Button btn30;

    /** Button representing the grid position (0,1). */
    @FXML
    private Button btn01;

    /** Button representing the grid position (1,1). */
    @FXML
    private Button btn11;

    /** Button representing the grid position (2,1). */
    @FXML
    private Button btn21;

    /** Button representing the grid position (3,1). */
    @FXML
    private Button btn31;

    /** Button representing the grid position (0,2). */
    @FXML
    private Button btn02;

    /** Button representing the grid position (1,2). */
    @FXML
    private Button btn12;

    /** Button representing the grid position (2,2). */
    @FXML
    private Button btn22;

    /** Button representing the grid position (3,2). */
    @FXML
    private Button btn32;

    /** Label displaying the name of the current player. */
    @FXML
    private Label lblCurrentPlayer;

    /** Label showing the static "Winner is" text. */
    @FXML
    private Label lblWinner;

    /** Label displaying the name of the winning player. */
    @FXML
    private Label wnmn;

    /** Reference to the game logic instance controlling the game state. */
    private PairsLogic game;

    /** Connector object that allows the logic to update the JavaFX GUI. */
    private JavaFXGUI gui;

    /** GridPane containing the clickable game buttons. */
    @FXML
    private GridPane grdPnField;

    /** Button used to start the game. */
    @FXML
    private Button strbtn;

    /** Button used to reset the UI and clear the game. */
    @FXML
    private Button ngbtn;

    /** TextField where player 1 enters their name. */
    private TextField tfPlayer1;

    /** TextField where player 2 enters their name. */
    private TextField tfPlayer2;


    /**
     * Initializes the controller after the FXML layout is loaded.
     * <br>
     * This method prepares the UI for the initial state:
     * - no current player displayed
     * - winner labels hidden
     * - grid disabled until a game starts
     * - button actions connected to controller methods
     */
    @FXML
    public void initialize() {

        lblCurrentPlayer.setText("");

        lblWinner.setVisible(false);
        wnmn.setVisible(false);

        grdPnField.setDisable(true);

        strbtn.setOnAction(this::startGame);
        ngbtn.setOnAction(this::newGame);

        findTextFields();
    }


    /**
     * Searches the scene graph to find the TextFields that contain the
     * player names.
     * <br>
     * Because the TextFields are not directly injected with @FXML,
     * the controller navigates from the start button up to the BorderPane
     * and then scans the VBox children for TextFields.
     */
    private void findTextFields() {

        Node n = strbtn;

        while (n != null && !(n instanceof BorderPane)) {
            n = n.getParent();
        }

        if (!(n instanceof BorderPane))
            return;

        BorderPane pane = (BorderPane) n;

        Node center = pane.getCenter();

        if (!(center instanceof VBox))
            return;
        VBox box = (VBox) center;

        int found = 0;

        for (Node node : box.getChildren()) {

            if (node instanceof TextField) {

                if (found == 0)
                    tfPlayer1 = (TextField) node;
                else
                    tfPlayer2 = (TextField) node;

                found++;
            }
        }
    }


    /**
     * Starts a new game using the names entered by the players.
     * <br>
     * This method performs several steps:
     * - reads the player names
     * - resets the board buttons
     * - hides the winner display
     * - creates the GUI connector
     * - creates a new game logic instance
     * - enables the grid for playing
     *
     * @param e click event triggered by the start button
     */
    private void startGame(ActionEvent e) {

        String p1 = getPlayerName(tfPlayer1);
        String p2 = getPlayerName(tfPlayer2);

        Button[][] btnsGamePlay = {
                {btn00, btn01, btn02},
                {btn10, btn11, btn12},
                {btn20, btn21, btn22},
                {btn30, btn31, btn32}
        };

        /*
         Reset the board before starting a new game.
         This ensures that no symbols from a previous round remain visible.
         */
        for (Button[] col : btnsGamePlay) {
            for (Button b : col) {
                b.setText("");
                b.setDisable(false);
            }
        }
        lblWinner.setVisible(false);
        wnmn.setVisible(false);
        gui = new JavaFXGUI(btnsGamePlay, lblCurrentPlayer, lblWinner, wnmn);
        game = new PairsLogic(p1, p2, 4, 3, gui);
        grdPnField.setDisable(false);
    }
    /**
     * Resets the UI to its original state without starting a new game.
     * <br>
     * This method clears player names, hides the winner labels,
     * resets the board buttons and disables the grid.
     *
     * @param e click event triggered by the new game button
     */
    private void newGame(ActionEvent e) {
        if (tfPlayer1 != null)
            tfPlayer1.clear();

        if (tfPlayer2 != null)
            tfPlayer2.clear();

        lblCurrentPlayer.setText("");

        lblWinner.setVisible(false);
        wnmn.setVisible(false);

        Button[][] btnsGamePlay = {
                {btn00, btn01, btn02},
                {btn10, btn11, btn12},
                {btn20, btn21, btn22},
                {btn30, btn31, btn32}
        };

        /*
         Clear the board so that no card symbols remain from the previous game.
         This keeps the UI consistent before starting a new round.
         */
        for (Button[] col : btnsGamePlay) {
            for (Button b : col) {
                b.setText("");
                b.setDisable(false);
            }
        }
        grdPnField.setDisable(true);
        game = null;
        gui = null;
    }
    /**
     * Handles a click on one of the grid buttons.
     * <br>
     * The method determines the grid coordinate of the clicked button
     * and forwards that coordinate to the game logic.
     *
     * @param actionEvent event generated by clicking a grid button
     */
    @FXML
    private void HandlebtnOnClick(ActionEvent actionEvent) {

        if (game == null)
            return;

        Button clicked = (Button) actionEvent.getSource();

        Integer col = GridPane.getColumnIndex(clicked);
        Integer row = GridPane.getRowIndex(clicked);

        int x = col == null ? 0 : col;
        int y = row == null ? 0 : row;

        game.playerTurn(new int[]{x, y});
    }
    /**
     * Retrieves and normalizes the name entered in a TextField.
     * <br>
     * If the field is empty or null, an empty string is returned.
     * This prevents null values from entering the game logic.
     *
     * @param tf TextField containing the player's name
     * @return trimmed player name or empty string
     */
    private String getPlayerName(TextField tf) {
        if (tf == null)
            return "";
        String name = tf.getText();
        if (name == null)
            return "";
        name = name.trim();
        if (name.isEmpty())
            return "";
        return name;
    }

}