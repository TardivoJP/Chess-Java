public class Bishop extends Piece {
    public Bishop(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WB");
        }else{
            setLabel("BB");
        }
    }

    public boolean isValidBishopMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference) {
        //Check if target square has an opposing piece
        if(boardStateReference[toRow][toCol].getPiece() != null && 
        boardStateReference[toRow][toCol].getPiece().getSide() == side){
            return false;
        }
        
        //Check if it was truly a diagonal move
        if (Math.abs(toRow - fromRow) != Math.abs(toCol - fromCol)) {
            return false;
        }

        int rowIncrement;
        int colIncrement;

        //Check downwards moves
        if(toRow > fromRow){
            rowIncrement = 1;
        //Check upwards moves
        }else{
            rowIncrement = -1;
        }

        //Check left direction moves
        if(toCol > fromCol){
            colIncrement = 1;
        //Check right direction moves
        }else{
            colIncrement = -1;
        }

        //Iterators start at the piece location
        int rowIterator = fromRow;
        int colIterator = fromCol;
        //First increment is done so the piece square is not checked
        rowIterator += rowIncrement;
        colIterator += colIncrement;
    
        //Check if squares in the way of move are empty
        while(rowIterator != toRow && colIterator != toCol) {
            if (boardStateReference[rowIterator][colIterator].getPiece() != null) {
                return false;
            }

            rowIterator += rowIncrement;
            colIterator += colIncrement;
        }

        return true;
    }

    public void possibleBishopMovements(Piece piece, char side, int row, int col, Cell[][] boardStateReference, int[][] whiteAttackingSquares, int[][] blackAttackingSquares){
        int auxCol = col+1;

        for(int i=row+1; i<8; i++){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMove(piece, side, row, col, i, auxCol, boardStateReference)){
                    if(side == 'W'){
                        whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol++;
        }


        auxCol = col+1;

        for(int i=row-1; i>=0; i--){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMove(piece, side, row, col, i, auxCol, boardStateReference)){
                    if(side == 'W'){
                        whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol++;
        }

        auxCol = col-1;

        for(int i=row+1; i<8; i++){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMove(piece, side, row, col, i, auxCol, boardStateReference)){
                    if(side == 'W'){
                        whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol--;
        }

        auxCol = col-1;

        for(int i=row-1; i>=0; i--){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMove(piece, side, row, col, i, auxCol, boardStateReference)){
                    if(side == 'W'){
                        whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol--;
        }
    }
}
