public class King extends Piece {
    private boolean captured;

    public King(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WK");
        }else{
            setLabel("BK");
        }
        
        this.captured = false;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
}