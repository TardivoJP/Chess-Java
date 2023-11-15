public class Board {
    private Cell[][] cells;

    public Board() {
        cells = new Cell[8][8];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j] = new Cell();
            }
        }

        //Pawns
        for (int i = 0; i < 8; i++) {
            cells[1][i].setPiece(new Pawn('B'));
            cells[6][i].setPiece(new Pawn('W'));
        }

        //Rooks
        cells[0][0].setPiece(new Rook('B'));
        cells[0][7].setPiece(new Rook('B'));
        cells[7][0].setPiece(new Rook('W'));
        cells[7][7].setPiece(new Rook('W'));

        //Knights
        cells[0][1].setPiece(new Knight('B'));
        cells[0][6].setPiece(new Knight('B'));
        cells[7][1].setPiece(new Knight('W'));
        cells[7][6].setPiece(new Knight('W'));

        //Bishops
        cells[0][2].setPiece(new Bishop('B'));
        cells[0][5].setPiece(new Bishop('B'));
        cells[7][2].setPiece(new Bishop('W'));
        cells[7][5].setPiece(new Bishop('W'));

        //Queens
        cells[0][3].setPiece(new Queen('B'));
        cells[7][3].setPiece(new Queen('W'));

        //Kings
        cells[0][4].setPiece(new King('B'));
        cells[7][4].setPiece(new King('W'));
    }

    public void move(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidCoordinate(fromRow, fromCol) || !isValidCoordinate(toRow, toCol) || (fromRow == toRow && fromCol == toCol)) {
            System.out.println("Invalid coordinates. Please provide valid coordinates.");
            return;
        }

        Cell fromCell = getCell(fromRow, fromCol);
        Cell toCell = getCell(toRow, toCol);

        if (fromCell.getPiece() == null) {
            System.out.println("There is no piece in the selected cell.");
            return;
        }

        Piece piece = fromCell.getPiece();

        // Assuming a simple rule where any move is valid for now
        // You can replace this with the specific rules for each piece
        if (isValidMove(piece, fromRow, fromCol, toRow, toCol)) {
            toCell.setPiece(piece);
            fromCell.setPiece(null);

            System.out.println("Move successful!");
        } else {
            System.out.println("Invalid move for the selected piece.");
        }
    }

    private boolean isValidMove(Piece piece, int fromRow, int fromCol, int toRow, int toCol) {

        if(piece instanceof Pawn){
            Pawn pawn = (Pawn) piece;

            if (pawn.getSide() == 'W') {
                return isValidWhitePawnMove(pawn, fromRow, fromCol, toRow, toCol);
            } else {
                return isValidBlackPawnMove(pawn, fromRow, fromCol, toRow, toCol);
            }

        }

        if(piece instanceof Rook){
            Rook rook = (Rook) piece;

            if(fromRow != toRow && fromCol != toCol){
                return false;
            }

            if (rook.getSide() == 'W') {
                return isValidWhiteRookMove(rook, fromRow, fromCol, toRow, toCol);
            } else {
                return isValidBlackRookMove(rook, fromRow, fromCol, toRow, toCol);
            }

        }

        
        // Implement specific rules for each piece type
        // For simplicity, assume any move is valid for now
        return true;
    }

    private boolean isValidWhitePawnMove(Pawn pawn, int fromRow, int fromCol, int toRow, int toCol){
        //Check for single move
        if(fromRow - toRow == 1){

            //Check forward move
            if(toCol - fromCol == 0){
                //Check if there's a piece in the way in forward move
                if(getCell(toRow, toCol).getPiece() == null){

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

                //Check if there's a piece to capture in diagonal move
                if(getCell(toRow, toCol).getPiece() == null){

                    //Check for En Passant
                    if(getCell(fromRow, toCol).getPiece() == null || !getCell(fromRow, toCol).getPiece().getLabel().equals("P") || getCell(fromRow, toCol).getPiece().getSide() == 'W'){
                        return false;
                    }else{
                        Pawn enemyPawn = (Pawn) getCell(fromRow, toCol).getPiece();
                        if(enemyPawn.isDoubleMoveLastTurn()){

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
                }else{
                    if(getCell(toRow, toCol).getPiece().getSide() == 'W'){
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
        }else if(fromRow - toRow == 2){
            if(!pawn.isMoved()){

                //Check if there's a piece in the way in double move
                if(getCell(toRow, toCol).getPiece() == null){
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

        return false;
    }

    private boolean isValidBlackPawnMove(Pawn pawn, int fromRow, int fromCol, int toRow, int toCol){
        //Check for single move
        if(toRow - fromRow == 1){

            //Check forward move
            if(toCol - fromCol == 0){
                //Check if there's a piece in the way in forward move
                if(getCell(toRow, toCol).getPiece() == null){

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

                //Check if there's a piece to capture in diagonal move
                if(getCell(toRow, toCol).getPiece() == null){

                    //Check for En Passant
                    if(getCell(fromRow, toCol).getPiece() == null || !getCell(fromRow, toCol).getPiece().getLabel().equals("P") || getCell(fromRow, toCol).getPiece().getSide() == 'B'){
                        return false;
                    }else{
                        Pawn enemyPawn = (Pawn) getCell(fromRow, toCol).getPiece();
                        if(enemyPawn.isDoubleMoveLastTurn()){

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
                }else{
                    if(getCell(toRow, toCol).getPiece().getSide() == 'B'){
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
        }else if(toRow - fromRow == 2){
            if(!pawn.isMoved()){

                //Check if there's a piece in the way in double move
                if(getCell(toRow, toCol).getPiece() == null){
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

        return false;
    }

    private boolean isValidWhiteRookMove(Rook rook, int fromRow, int fromCol, int toRow, int toCol){
        if(fromRow != toRow){
            if(fromRow > toRow){
                
                for(int i=fromRow-1; i>toRow; i--){
                    if(cells[i][fromCol].getPiece() != null){
                        return false;
                    }
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == 'W'){
                        return false;
                    }else{
                        return true;
                    }
                }

            }else if(toRow > fromRow){

                for(int i=fromRow+1; i<toRow; i++){
                    if(cells[i][fromCol].getPiece() != null){
                        return false;
                    }
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == 'W'){
                        return false;
                    }else{
                        return true;
                    }
                }

            }
        }else if(fromCol != toCol){
            if(fromCol > toCol){
                for(int i=fromCol-1; i>toCol; i--){
                    if(cells[fromRow][i].getPiece() != null){
                        return false;
                    }
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == 'W'){
                        return false;
                    }else{
                        return true;
                    }
                }

            }else if(toCol > fromCol){

                for(int i=fromCol+1; i<toCol; i++){
                    if(cells[fromRow][i].getPiece() != null){
                        return false;
                    }
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == 'W'){
                        return false;
                    }else{
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isValidBlackRookMove(Rook rook, int fromRow, int fromCol, int toRow, int toCol){
        if(fromRow != toRow){
            if(fromRow > toRow){
                
                for(int i=fromRow-1; i>toRow; i--){
                    if(cells[i][fromCol].getPiece() != null){
                        return false;
                    }
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == 'B'){
                        return false;
                    }else{
                        return true;
                    }
                }

            }else if(toRow > fromRow){

                for(int i=fromRow+1; i<toRow; i++){
                    if(cells[i][fromCol].getPiece() != null){
                        return false;
                    }
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == 'B'){
                        return false;
                    }else{
                        return true;
                    }
                }

            }
        }else if(fromCol != toCol){
            if(fromCol > toCol){
                for(int i=fromCol-1; i>toCol; i--){
                    if(cells[fromRow][i].getPiece() != null){
                        return false;
                    }
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == 'B'){
                        return false;
                    }else{
                        return true;
                    }
                }

            }else if(toCol > fromCol){

                for(int i=fromCol+1; i<toCol; i++){
                    if(cells[fromRow][i].getPiece() != null){
                        return false;
                    }
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == 'B'){
                        return false;
                    }else{
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void printCurrentBoard(){
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(cells[i][j].getPiece() == null){
                    System.out.print("X  ");
                }else{
                    System.out.print(cells[i][j].getPiece().getLabel()+" ");
                    
                }
            }
            System.out.println();
        }
    }

    public Cell getCell(int row, int col) {
        // Check if the provided coordinates are within the bounds of the board
        if (isValidCoordinate(row, col)) {
            return cells[row][col];
        } else {
            // Handle invalid coordinates (you can throw an exception or return null)
            throw new IllegalArgumentException("Invalid coordinates");
        }
    }

    private boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
