public class Piece {
    private String label;
    private char side;

    public char getSide() {
        return side;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSide(char side) {
        this.side = side;
    }

    protected boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
