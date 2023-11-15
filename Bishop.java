public class Bishop extends Piece {
    public Bishop(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WB");
        }else{
            setLabel("BB");
        }
    }
}
