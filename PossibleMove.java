public class PossibleMove {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    public PossibleMove(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }
    
    public int getToRow() {
        return toRow;
    }
    
    public int getToCol() {
        return toCol;
    }
}
