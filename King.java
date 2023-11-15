public class King extends Piece {
    private boolean moved;
    private boolean captured;

    public King(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WK");
        }else{
            setLabel("BK");
        }
        
        this.moved = false;
        this.captured = false;
    }

    public boolean isMoved() {
        return moved;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
}