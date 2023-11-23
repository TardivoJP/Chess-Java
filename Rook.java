public class Rook extends Piece {
    private boolean moved;

    public Rook(char side){
        setSide(side);
        if(side == 'W'){
            setLabel("WR");
        }else{
            setLabel("BR");
        }

        this.moved = false;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean isValidRookMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference){
        //Check if target square is empty or has a friendly piece
        if(boardStateReference[toRow][toCol].getPiece() != null &&
        boardStateReference[toRow][toCol].getPiece().getSide() == side){
            return false;
        }

        //Rook move is only valid if it was performed either on its row or column
        //If it's both then it's invalid
        if(fromRow != toRow && fromCol != toCol){
            return false;
        }

        //Set looping parameters based on row or column move
        int delta;
        int start;
        int end;

        //Check for column move
        if(fromRow == toRow){
            //Check upwards move
            if(fromCol > toCol){
                delta = -1;
            //Check downwards move
            }else{
                delta = 1;
            }
            start = fromCol + delta;
            end = toCol;
        //Check for row move
        }else{
            //Check leftwards move
            if(fromRow > toRow){
                delta = -1;
            //Check rightwards move
            }else{
                delta = 1;
            }
            start = fromRow + delta;
            end = toRow;
        }

        //Check if squares in the way of move are empty
        for(int i = start; i != end; i += delta){
            if((fromCol != toCol && boardStateReference[fromRow][i].getPiece() != null) ||
            (fromRow != toRow && boardStateReference[i][fromCol].getPiece() != null)) {
                return false;
            }
        }

        //Check if it's a Rook to change its moved boolean which affects castling
        //This check is done because the Queen also uses this function
        if(piece instanceof Rook){
            Rook rook = (Rook) piece;
            if(!rook.isMoved()){
                rook.setMoved(true);
            }
        }
        return true;
    }

    public void possibleRookMovements(Piece piece, char side, int row, int col, Cell[][] boardStateReference, int[][] whiteAttackingSquares, int[][] blackAttackingSquares){
        for(int i=0; i<8; i++){
            if(i != row){
                if(isValidCoordinate(i, col)){
                    if(isValidRookMove(piece, side, row, col, i, col, boardStateReference)){
                        if(side == 'W'){
                            whiteAttackingSquares[i][col] = 1;
                        }else{
                            blackAttackingSquares[i][col] = 1;
                        }
                    }
                }
            }

            if(i != col){
                if(isValidCoordinate(row, i)){
                    if(isValidRookMove(piece, side, row, col, row, i, boardStateReference)){
                        if(side == 'W'){
                            whiteAttackingSquares[row][i] = 1;
                        }else{
                            blackAttackingSquares[row][i] = 1;
                        }
                    }
                }
            }
        }
    }
}
