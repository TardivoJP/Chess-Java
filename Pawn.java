public class Pawn extends Piece {
    private boolean moved;
    private boolean doubleMoveLastTurn;

    public Pawn(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WP");
        }else{
            setLabel("BP");
        }

        this.moved = false;
        this.doubleMoveLastTurn = false;
    }

    public boolean isMoved() {
        return moved;
    }

    public boolean isDoubleMoveLastTurn() {
        return doubleMoveLastTurn;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public void setDoubleMoveLastTurn(boolean doubleMoveLastTurn) {
        this.doubleMoveLastTurn = doubleMoveLastTurn;
    }

    public boolean isValidPawnMove(Pawn pawn, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference,
    PawnPossibilityState currentPawnPossibilityState, Board board){
        //Check for single move
        if((side == 'W' && fromRow - toRow == 1) || (side == 'B' && toRow - fromRow == 1)){
            //Check forward move
            if(toCol - fromCol == 0){
                //Check if there's a piece in the way in forward move
                if(boardStateReference[toRow][toCol].getPiece() == null){
                    //Override that's only used for filling in attacking grid,
                    //since pawns can't capture going forwards this returns false
                    if(currentPawnPossibilityState == PawnPossibilityState.POSSIBLE){
                        return false;
                    }

                    if(pawn.isDoubleMoveLastTurn() == true){
                        pawn.setDoubleMoveLastTurn(false);
                    }
                    if(!pawn.isMoved()){
                        pawn.setMoved(true);
                    }
                    return true;
                }else{
                    return false;
                }
            }
            //Check diagonal move
            if(toCol - fromCol == 1 || fromCol - toCol == 1){
                //Override that's only used for filling in attacking grid
                if(currentPawnPossibilityState == PawnPossibilityState.POSSIBLE){
                    return true;
                }

                //Check if there's a piece to capture in diagonal move
                if(boardStateReference[toRow][toCol].getPiece() == null){
                    //Check for En Passant
                    if(boardStateReference[fromRow][toCol].getPiece() == null ||
                    !(boardStateReference[fromRow][toCol].getPiece() instanceof Pawn) ||
                    (side == 'W' && boardStateReference[fromRow][toCol].getPiece().getSide() == 'W') ||
                    (side == 'B' && boardStateReference[fromRow][toCol].getPiece().getSide() == 'B')){
                        return false;
                    }else{
                        Pawn enemyPawn = (Pawn) boardStateReference[fromRow][toCol].getPiece();
                        if(enemyPawn.isDoubleMoveLastTurn()){
                            if(pawn.isDoubleMoveLastTurn() == true){
                                pawn.setDoubleMoveLastTurn(false);
                            }
                            if(!pawn.isMoved()){
                                pawn.setMoved(true);
                            }
                            board.setEnPassant(true);
                            if(side == 'W'){
                                board.setEnPassantRow(toRow + 1);
                            }else{
                                board.setEnPassantRow(toRow - 1);
                            }
                            board.setEnPassantCol(toCol);
                            return true;
                        }else{
                            return false;
                        }
                    }
                }else{
                    if(boardStateReference[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        if(pawn.isDoubleMoveLastTurn() == true){
                            pawn.setDoubleMoveLastTurn(false);
                        }
                        if(!pawn.isMoved()){
                            pawn.setMoved(true);
                        }
                        return true;
                    }
                }
            }
        //Check for double move    
        }else if((side == 'W' && fromRow - toRow == 2) || (side == 'B' && toRow - fromRow == 2)){
            //Override that's only used for filling in attacking grid,
            //since pawns can't capture going forwards this returns false
            if(currentPawnPossibilityState == PawnPossibilityState.POSSIBLE){
                return false;
            }

            if(toCol - fromCol == 0){
                if(!pawn.isMoved()){
                    //Check if there's a piece in the way in double move
                    if((side == 'W' && boardStateReference[fromRow - 1][toCol].getPiece() != null) || (side == 'B' && boardStateReference[fromRow + 1][toCol].getPiece() != null)){
                        return false;
                    }

                    if(boardStateReference[toRow][toCol].getPiece() == null){
                        pawn.setMoved(true);
                        pawn.setDoubleMoveLastTurn(true);
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
        return false;
    }

    public void possiblePawnMovements(Pawn pawn, char side, int row, int col, Cell[][] boardStateReference,
    PawnPossibilityState currentPawnPossibilityState, Board board, int[][] whiteAttackingSquares, int[][] blackAttackingSquares){
        int delta;
        if(side == 'W'){
            delta = -1;
        }else{
            delta = 1;
        }

        int[] possibleRows = {row + (delta * 2), row + delta, row + delta, row + delta};
        int[] possibleCols = {col, col, col - 1, col + 1};

        int start;
        if(!pawn.isMoved()){
            start = 0;
        }else{
            start = 1;
        }

        for(int i=start; i<possibleRows.length; i++){
            if(isValidCoordinate(possibleRows[i], possibleCols[i])){
                if(isValidPawnMove(pawn, side, row, col, possibleRows[i], possibleCols[i], boardStateReference, currentPawnPossibilityState, board)){
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
