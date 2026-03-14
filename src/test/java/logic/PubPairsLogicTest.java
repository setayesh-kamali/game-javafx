package logic;

import logic.PairsLogic.Solved;
import logic.PairsLogic.Symbol;
import org.junit.jupiter.api.Test;

import static logic.PairsLogic.Solved.*;
import static logic.PairsLogic.Symbol.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Test cases for the logic of the Game Pairs. While these test cases cover a
 * lot, they do not cover all possible scenarios. It is still possible for the
 * logic to malfunction, albeit in special cases. Thinking about own test cases
 * is recommended.
 *
 * @author cei, nvk
 */
public class PubPairsLogicTest {

    @Test
    public void testFieldToStringAndTestConstructor4x3() {
        PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                    {BEE, BAT, OWL},
                    {BEE, BAT, OWL},
                    {PIG, POO, BUG},
                    {PIG, POO, BUG}
                },
                new Solved[][]{
                    {NOT, NOT, NOT},
                    {NOT, NOT, NOT},
                    {NOT, NOT, NOT},
                    {NOT, NOT, NOT}
                }, new FakeGUI());
        //expecting the additional whitespace and linebreak after each symbol
        //or line makes the implementation easier. As this method is just for 
        //testing anyway, this is acceptable behaviour. 
        assertEquals("BEE   BEE   PIG   PIG   \n"
                + "BAT   BAT   POO   POO   \n"
                + "OWL   OWL   BUG   BUG   \n",
                game.cardsToString());
    }

    @Test
    public void testFieldToStringAndTestConstructor2x2() {
        PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                    {BEE, BAT},
                    {BEE, BAT},},
                new Solved[][]{
                    {NOT, NOT},
                    {NOT, NOT},}, new FakeGUI());
        //expecting the additional whitespace and linebreak after each symbol
        //or line makes the implementation easier. As this method is just for 
        //testing anyway, this is acceptable behaviour. 
        assertEquals("BEE   BEE   \n"
                + "BAT   BAT   \n", game.cardsToString());
    }

    @Test
    public void testFieldToStringAndTestConstructor4x3_NotJustLength3() {
        PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                                         new Symbol[][]{
                                                 {GHOST, BAT, OWL},
                                                 {GHOST, BAT, OWL},
                                                 {PIG, POO, CAKE},
                                                 {PIG, POO, CAKE}
                                         },
                                         new Solved[][]{
                                                 {NOT, NOT, NOT},
                                                 {NOT, NOT, NOT},
                                                 {NOT, NOT, NOT},
                                                 {NOT, NOT, NOT}
                                         }, new FakeGUI());
        //expecting the additional whitespace and linebreak after each symbol
        //or line makes the implementation easier. As this method is just for
        //testing anyway, this is acceptable behaviour.
        assertEquals("GHOST GHOST PIG   PIG   \n"
                             + "BAT   BAT   POO   POO   \n"
                             + "OWL   OWL   CAKE  CAKE  \n",
                     game.cardsToString());
    }

    @Test
    public void testPlayerDoesNotChangeAfterFirstCard2x2() {
        PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                    {BEE, BAT},
                    {BEE, BAT},},
                new Solved[][]{
                    {NOT, NOT},
                    {NOT, NOT},}, new FakeGUI());
        String currPlayer = game.getNameCurrentPlayer();
        game.playerTurn(new Position(0, 0));
        assertEquals(currPlayer, game.getNameCurrentPlayer());
    }
    
    @Test
    public void testPlayerChangesAfterSecondCard2x2() {
        PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                    {BEE, BAT},
                    {BEE, BAT},},
                new Solved[][]{
                    {NOT, NOT},
                    {NOT, NOT},}, new FakeGUI());
        String currPlayer = game.getNameCurrentPlayer();
        game.playerTurn(new Position(0, 0));
        game.playerTurn(new Position(1, 1));
        assertNotEquals(currPlayer, game.getNameCurrentPlayer());
    }
    
    @Test
    public void testAllSolved_NothingSolved() {
         PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                    {BEE, BAT, OWL},
                    {BEE, BAT, OWL},
                    {PIG, POO, BUG},
                    {PIG, POO, BUG}
                },
                new Solved[][]{
                    {NOT, NOT, NOT},
                    {NOT, NOT, NOT},
                    {NOT, NOT, NOT},
                    {NOT, NOT, NOT}
                }, new FakeGUI());
         assertFalse(game.allSolved());
    }
    
    @Test
    public void testAllSolved_SomethingsSolved() {
         PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                    {BEE, BAT, OWL},
                    {BEE, BAT, OWL},
                    {PIG, POO, BUG},
                    {PIG, POO, BUG}
                },
                new Solved[][]{
                    {FST, NOT, FST},
                    {FST, NOT, FST},
                    {NOT, SND, NOT},
                    {NOT, SND, NOT}
                }, new FakeGUI());
         assertFalse(game.allSolved());
    }
    
    @Test
    public void testAllSolved_Everything() {
         PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                    {BEE, BAT, OWL},
                    {BEE, BAT, OWL},
                    {PIG, POO, BUG},
                    {PIG, POO, BUG}
                },
                new Solved[][]{
                    {FST, FST, FST},
                    {FST, FST, FST},
                    {SND, SND, SND},
                    {SND, SND, SND}
                }, new FakeGUI());
         assertTrue(game.allSolved());
    }
    
    @Test
    public void testAllSolvedNoWinner() {
         PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                    {BEE, BAT, OWL},
                    {BEE, BAT, OWL},
                    {PIG, POO, BUG},
                    {PIG, POO, BUG}
                },
                new Solved[][]{
                    {FST, FST, FST},
                    {FST, FST, FST},
                    {SND, SND, SND},
                    {SND, SND, SND}
                }, new FakeGUI());
         assertTrue(game.allSolved());
         assertNull(game.getWinnerName());
    }
    
    @Test
    public void testAllSolvedAWinner() {
        PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                    {BEE, BAT, OWL},
                    {BEE, BAT, OWL},
                    {PIG, POO, BUG},
                    {PIG, POO, BUG}
                },
                new Solved[][]{
                    {SND, FST, FST},
                    {SND, FST, FST},
                    {SND, SND, SND},
                    {SND, SND, SND}
                }, new FakeGUI());
         assertTrue(game.allSolved());
         assertEquals("Cordula", game.getWinnerName());
    }
    

    @Test
    public void testCardAlreadySolvedPlayerCanChooseAgain() {
        PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                        {BEE, BAT, OWL},
                        {BEE, BAT, OWL},
                        {PIG, POO, BUG},
                        {PIG, POO, BUG}
                },
                new Solved[][]{
                        {FST, NOT, FST},
                        {FST, NOT, FST},
                        {NOT, SND, NOT},
                        {NOT, SND, NOT}
                }, new FakeGUI());
        String currPlayer = game.getNameCurrentPlayer();
        game.playerTurn(new Position(0, 0));
        assertEquals(currPlayer, game.getNameCurrentPlayer());
        game.playerTurn(new Position(1, 1));
        assertEquals(currPlayer, game.getNameCurrentPlayer());
        game.playerTurn(new Position(2, 2));
        assertNotEquals(currPlayer, game.getNameCurrentPlayer());
    }

    @Test
    public void testGetWinnerNameGameNotFinished() {
        PairsLogic game = new PairsLogic(new String[]{"Marcus", "Cordula"},
                new Symbol[][]{
                        {BEE, BUG, OWL},
                        {BEE, BAT, OWL},
                        {PIG, POO, BAT},
                        {PIG, POO, BUG}
                },
                new Solved[][]{
                        {FST, SND, FST},
                        {FST, NOT, FST},
                        {NOT, SND, NOT},
                        {NOT, SND, SND}
                }, new FakeGUI());
        assertThrows(AssertionError.class,
                game::getWinnerName,
                "Muss einen AssertionError erzeugen, wenn das Spiel noch nicht zuende ist."
        );
    }


}
