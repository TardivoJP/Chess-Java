public class Rook extends Piece {
    public Rook(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WR");
        }else{
            setLabel("BR");
        }
    }
}
