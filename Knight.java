public class Knight extends Piece {
    public Knight(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WN");
        }else{
            setLabel("BN");
        }
    }

    public boolean isValidKnightMove(Knight knight, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference){
        //Check squares difference in target move
        int rowDifference = Math.abs(toRow - fromRow);
        int colDifference = Math.abs(toCol - fromCol);

        //Check if move was perfomed in the "L" or "2 - 1" fashion
        if((rowDifference == 1 && colDifference == 2) || (rowDifference == 2 && colDifference == 1)){
            //Check if target square is empty or has an opposing piece
            if(boardStateReference[toRow][toCol].getPiece() == null || 
            boardStateReference[toRow][toCol].getPiece().getSide() != side){
                return true;
            }
        }
        return false;
    }

    public void possibleKnightMovements(Knight knight, char side, int row, int col, Cell[][] boardStateReference, int[][] whiteAttackingSquares, int[][] blackAttackingSquares){
        int[] possibleRows = {row + 2, row + 2, row - 2, row - 2, row + 1, row - 1, row + 1, row - 1};
        int[] possibleCols = {col + 1, col - 1, col + 1, col - 1, col + 2, col + 2, col - 2, col - 2};

        for(int i=0; i<possibleRows.length; i++){
            if(isValidCoordinate(possibleRows[i], possibleCols[i])){
                if(knight.isValidKnightMove(knight, side, row, col, possibleRows[i], possibleCols[i], boardStateReference)){
                    if(side == 'W'){
                        whiteAttackingSquares[possibleRows[i]][possibleCols[i]] = 1;
                    }else{
                        blackAttackingSquares[possibleRows[i]][possibleCols[i]] = 1;
                    }
                }
            }
        }
    }
}
