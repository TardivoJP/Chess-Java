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
    
    public boolean isValidKingMove(King king, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference,
    int[][] whiteAttackingSquares, int[][] blackAttackingSquares, Board board){
        //Check for castling
        if(!king.isMoved()){
            //Check if the move is within the same row
            if(fromRow == toRow){
                //Check for King side castling
                if(toCol - fromCol == 2){
                    //Check King's color performing the castle move, in this case White
                    if(side == 'W'){
                        //Check if the squares in the way are empty and aren't being attacked by opposing pieces
                        if(boardStateReference[fromRow][toCol].getPiece() == null && boardStateReference[fromRow][toCol-1].getPiece() == null && blackAttackingSquares[fromRow][toCol] == 0 && blackAttackingSquares[fromRow][toCol-1] == 0){
                            //Check if the rightmost piece is actually a Rook
                            if(boardStateReference[fromRow][toCol+1].getPiece() instanceof Rook){
                                //Check if that rook hasn't moved yet
                                Rook rightSideRook = (Rook) boardStateReference[fromRow][toCol+1].getPiece();
                                if(!rightSideRook.isMoved()){
                                    king.setMoved(true);

                                    board.setCastleFromCol(toCol + 1);
                                    board.setCastleFromRow(fromRow);
                                    board.setCastleToCol(toCol - 1);
                                    board.setCastleToRow(fromRow);
                                    board.setCastle(true);
                                    board.setWhiteKingRow(toRow);
                                    board.setWhiteKingCol(toCol);
                                    
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    //Check King's color performing the castle move, in this case Black
                    }else{
                        //Check if the squares in the way are empty and aren't being attacked by opposing pieces
                        if(boardStateReference[fromRow][toCol].getPiece() == null && boardStateReference[fromRow][toCol-1].getPiece() == null && whiteAttackingSquares[fromRow][toCol] == 0 && whiteAttackingSquares[fromRow][toCol-1] == 0){
                            //Check if the rightmost piece is actually a Rook
                            if(boardStateReference[fromRow][toCol+1].getPiece() instanceof Rook){
                                //Check if that rook hasn't moved yet
                                Rook rightSideRook = (Rook) boardStateReference[fromRow][toCol+1].getPiece();
                                if(!rightSideRook.isMoved()){
                                    king.setMoved(true);

                                    board.setCastleFromCol(toCol + 1);
                                    board.setCastleFromRow(fromRow);
                                    board.setCastleToCol(toCol - 1);
                                    board.setCastleToRow(fromRow);
                                    board.setCastle(true);
                                    board.setBlackKingRow(toRow);
                                    board.setBlackKingCol(toCol);
    
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    }
                //Check for Queen side castling
                }else if(fromCol - toCol == 2){
                    //Check King's color performing the castle move, in this case White
                    if(side == 'W'){
                        //Check if the squares in the way are empty and aren't being attacked by opposing pieces, since this is Queen side, there's an additional square to check
                        if(boardStateReference[fromRow][toCol].getPiece() == null && boardStateReference[fromRow][toCol+1].getPiece() == null && boardStateReference[fromRow][toCol-1].getPiece() == null && blackAttackingSquares[fromRow][toCol] == 0 && blackAttackingSquares[fromRow][toCol+1] == 0 && blackAttackingSquares[fromRow][toCol-1] == 0){
                            //Check if the leftmost piece is actually a Rook
                            if(boardStateReference[fromRow][toCol-2].getPiece() instanceof Rook){
                                //Check if that rook hasn't moved yet
                                Rook leftSideRook = (Rook) boardStateReference[fromRow][toCol-2].getPiece();
                                if(!leftSideRook.isMoved()){
                                    king.setMoved(true);

                                    board.setCastleFromCol(toCol - 2);
                                    board.setCastleFromRow(fromRow);
                                    board.setCastleToCol(toCol + 1);
                                    board.setCastleToRow(fromRow);
                                    board.setCastle(true);
                                    board.setWhiteKingRow(toRow);
                                    board.setWhiteKingCol(toCol);
    
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    //Check King's color performing the castle move, in this case Black
                    }else{
                        //Check if the squares in the way are empty and aren't being attacked by opposing pieces, since this is Queen side, there's an additional square to check
                        if(boardStateReference[fromRow][toCol].getPiece() == null && boardStateReference[fromRow][toCol+1].getPiece() == null && boardStateReference[fromRow][toCol-1].getPiece() == null && whiteAttackingSquares[fromRow][toCol] == 0 && whiteAttackingSquares[fromRow][toCol+1] == 0 && whiteAttackingSquares[fromRow][toCol-1] == 0){
                            //Check if the leftmost piece is actually a Rook
                            if(boardStateReference[fromRow][toCol-2].getPiece() instanceof Rook){
                                //Check if that rook hasn't moved yet
                                Rook leftSideRook = (Rook) boardStateReference[fromRow][toCol-2].getPiece();
                                if(!leftSideRook.isMoved()){
                                    king.setMoved(true);

                                    board.setCastleFromCol(toCol - 2);
                                    board.setCastleFromRow(fromRow);
                                    board.setCastleToCol(toCol + 1);
                                    board.setCastleToRow(fromRow);
                                    board.setCastle(true);
                                    board.setBlackKingRow(toRow);
                                    board.setBlackKingCol(toCol);
    
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        //Check for regular King moves
        //Check if King moved more than one square in any direction
        if(Math.abs(toCol - fromCol) > 1 || Math.abs(toRow - fromRow) > 1){
            return false;
        //Check if target square is empty
        }else if(boardStateReference[toRow][toCol].getPiece() == null){
            //Check King's color performing the move, in this case White
            if(side == 'W'){
                //Check if that square isn't attacked by an opposing piece
                if(blackAttackingSquares[toRow][toCol] == 0){
                    if(!king.isMoved()){
                        king.setMoved(true);
                    }
                    board.setWhiteKingRow(toRow);
                    board.setWhiteKingCol(toCol);
                    return true;
                }else{
                    return false;
                }
            //Check King's color performing the move, in this case Black
            }else{
                //Check if that square isn't attacked by an opposing piece
                if(whiteAttackingSquares[toRow][toCol] == 0){
                    if(!king.isMoved()){
                        king.setMoved(true);
                    }
                    board.setBlackKingRow(toRow);
                    board.setBlackKingCol(toCol);
                    return true;
                }else{
                    return false;
                }
            }
        //Check if square isn't occupied by friendly piece
        }else if(boardStateReference[toRow][toCol].getPiece().getSide() == side){
            return false;
        //Check edge case where King captures an opposing piece
        }else{
            int currentKingRow;
            int currentKingCol;

            if(side == 'W'){
                currentKingRow = board.getWhiteKingRow();
                currentKingCol = board.getWhiteKingCol();
            }else{
                currentKingRow = board.getBlackKingRow();
                currentKingCol = board.getBlackKingCol();
            }
            //If the capture is safe, then it's allowed and actually performed
            if(board.moveGetsOutofCheck(king, side, fromRow, fromCol, toRow, toCol, currentKingRow, currentKingCol, boardStateReference)){
                if(!king.isMoved()){
                    king.setMoved(true);
                }
                if(side == 'W'){
                    board.setWhiteKingRow(toRow);
                    board.setWhiteKingCol(toCol);
                }else{
                    board.setBlackKingRow(toRow);
                    board.setBlackKingCol(toCol);
                }
    
                return true;
            }else{
                return false;
            }
        }
    }

    public void possibleKingMovements(King king, char side, int row, int col, boolean check, Cell[][] boardStateReference,
    int[][] whiteAttackingSquares, int[][] blackAttackingSquares, Board board){
        int[] possibleRows = {row + 1, row + 1, row + 1, row - 1, row - 1, row - 1, row, row};
        int[] possibleCols = {col, col + 1, col - 1, col, col + 1, col - 1, col + 1, col - 1};

        if(!check){
            if(king.isMoved() == false){
                int delta = -2;
                for(int i=0; i<2; i++){
                    if(king.isValidKingMove(king, side, row, col, row, col+delta, boardStateReference, whiteAttackingSquares, blackAttackingSquares, board)){
                        if(side == 'W'){
                            whiteAttackingSquares[row][col+delta] = 1;
                        }else{
                            blackAttackingSquares[row][col+delta] = 1;
                        }
                    }
                    delta += 4;
                }
            }
        }

        for(int i=0; i<possibleRows.length; i++){
            if(isValidCoordinate(possibleRows[i], possibleCols[i])){
                if(king.isValidKingMove(king, side, row, col, possibleRows[i], possibleCols[i], boardStateReference, whiteAttackingSquares, blackAttackingSquares, board)){
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