public class Queen extends Piece {
    public Queen(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WQ");
        }else{
            setLabel("BQ");
        }
    }
}
