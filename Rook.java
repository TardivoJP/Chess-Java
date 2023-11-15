public class Rook extends Piece {
    private boolean moved;

    public Rook(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WR");
        }else{
            setLabel("BR");
        }

        this.moved = false;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
}
