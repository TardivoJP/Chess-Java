public class Knight extends Piece {
    public Knight(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WN");
        }else{
            setLabel("BN");
        }
    }
}
