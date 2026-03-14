package logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The class with the tests for the logic. It should be substituted by well-named
 * Testclasses for the written classes in the logic-package.
 */

public class LogicTest {

    @Test
    public void simpleTest() {
        Logic logic = new Logic();
        assertEquals(1, logic.getOne());
    }
}
