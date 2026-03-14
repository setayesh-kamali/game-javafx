package logic;

/**
 * Represents a position on the board specified by x and y.
 *
 * @param x the x coordinate
 * @param y the y coordinate
 *
 * @author nvk
 */
public record Position(int x, int y) {

    /**
     * Returns the next Position on the board following this position, depending on the given width and height of the
     * board. If there is no next position, e.g. the new position would be out of bounds an IllegalArgumentException is thrown.
     *
     * @param width width of the board
     * @param height height of the board
     * @return next position
     */
    public Position nextPos(int width, int height) {
        if (x >= width - 1 && y >= height - 1)
            throw new IllegalArgumentException("Next Position of " + this + " would be out of bounds.");
        int newX = x + 1;
        int newY = y;
        if (newX == width) {
            newX = 0;
            newY++;
        }
        return new Position(newX, newY);
    }
}
