public class Pawn extends Piece {
    private boolean moved;
    private boolean doubleMoveLastTurn;

    public Pawn(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WP");
        }else{
            setLabel("BP");
        }

        this.moved = false;
        this.doubleMoveLastTurn = false;
    }

    public boolean isMoved() {
        return moved;
    }

    public boolean isDoubleMoveLastTurn() {
        return doubleMoveLastTurn;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public void setDoubleMoveLastTurn(boolean doubleMoveLastTurn) {
        this.doubleMoveLastTurn = doubleMoveLastTurn;
    }

}
