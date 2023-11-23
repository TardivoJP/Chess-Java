public class Bishop extends Piece {
    public Bishop(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WB");
        }else{
            setLabel("BB");
        }
    }

    public boolean isValidBishopMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference){
        //Check left direction moves
        if(toCol > fromCol){
            //Check left-down moves
            if(toRow > fromRow){
                //Check if it was truly a diagonal move
                if(toCol - fromCol != toRow - fromRow){
                    return false;
                }
                //Check if squares in the way of move are empty
                int rowIterator = fromRow+1;
                int colIterator = fromCol+1;
                while(rowIterator != toRow && colIterator != toCol){
                    if(boardStateReference[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }
                    rowIterator++;
                    colIterator++;
                }
                //Check if target square is empty or has an opposing piece
                if(boardStateReference[toRow][toCol].getPiece() == null || 
                boardStateReference[toRow][toCol].getPiece().getSide() != side){
                    return true;
                }else{
                    return false;
                }
            //Check left-up moves
            }else if(fromRow > toRow){
                //Check if it was truly a diagonal move
                if(toCol - fromCol != fromRow - toRow){
                    return false;
                }
                //Check if squares in the way of move are empty
                int rowIterator = fromRow-1;
                int colIterator = fromCol+1;
                while(rowIterator != toRow && colIterator != toCol){
                    if(boardStateReference[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }
                    rowIterator--;
                    colIterator++;
                }
                //Check if target square is empty or has an opposing piece
                if(boardStateReference[toRow][toCol].getPiece() == null || 
                boardStateReference[toRow][toCol].getPiece().getSide() != side){
                    return true;
                }else{
                    return false;
                }
            }
        //Check right direction moves
        }else if(fromCol > toCol){
            //Check right-down moves
            if(toRow > fromRow){
                //Check if it was truly a diagonal move
                if(fromCol - toCol != toRow - fromRow){
                    return false;
                }
                //Check if squares in the way of move are empty
                int rowIterator = fromRow+1;
                int colIterator = fromCol-1;
                while(rowIterator != toRow && colIterator != toCol){
                    if(boardStateReference[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }
                    rowIterator++;
                    colIterator--;
                }
                //Check if target square is empty or has an opposing piece
                if(boardStateReference[toRow][toCol].getPiece() == null || 
                boardStateReference[toRow][toCol].getPiece().getSide() != side){
                    return true;
                }else{
                    return false;
                }
            //Check right-up moves
            }else if(fromRow > toRow){
                //Check if it was truly a diagonal move
                if(fromCol - toCol != fromRow - toRow){
                    return false;
                }
                //Check if squares in the way of move are empty
                int rowIterator = fromRow-1;
                int colIterator = fromCol-1;
                while(rowIterator != toRow && colIterator != toCol){
                    if(boardStateReference[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }
                    rowIterator--;
                    colIterator--;
                }
                //Check if target square is empty or has an opposing piece
                if(boardStateReference[toRow][toCol].getPiece() == null || 
                boardStateReference[toRow][toCol].getPiece().getSide() != side){
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
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
