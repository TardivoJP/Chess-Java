public class King extends Piece {
    private boolean moved;

    public King(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WK");
        }else{
            setLabel("BK");
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