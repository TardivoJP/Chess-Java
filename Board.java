import java.util.Scanner;

public class Board {
    private Cell[][] cells;
    private int[][] whiteAttackingSquares;
    private int[][] blackAttackingSquares;
    private boolean castle;
    private boolean promotion;
    private int castleFromRow;
    private int castleFromCol;
    private int castleToRow;
    private int castleToCol;

    public Board() {
        cells = new Cell[8][8];
        initializeBoard();
        this.castle = false;
        this.promotion = false;
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j] = new Cell();
                whiteAttackingSquares[i][j] = 0;
                blackAttackingSquares[i][j] = 0;
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
        if (isValidMove(piece, piece.getSide(), fromRow, fromCol, toRow, toCol)) {
            toCell.setPiece(piece);
            fromCell.setPiece(null);

            if(castle){
                handleCastling();
            }

            if(promotion){
                handlePromotion(toCell, piece.getSide());
            }

            System.out.println("Move successful!");
        } else {
            System.out.println("Invalid move for the selected piece.");
        }
    }

    private boolean isValidMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol) {

        if(piece instanceof Pawn){
            Pawn pawn = (Pawn) piece;

            if (pawn.getSide() == 'W') {
                if(isValidWhitePawnMove(pawn, fromRow, fromCol, toRow, toCol)){
                    if(toRow == 0){
                        this.promotion = true;
                    }

                    return true;
                }else{
                    return false;
                }
            } else {
                if(isValidBlackPawnMove(pawn, fromRow, fromCol, toRow, toCol)){
                    if(toRow == 7){
                        this.promotion = true;
                    }

                    return true;
                }else{
                    return false;
                }
            }
        }

        if(piece instanceof Rook){
            if(fromRow != toRow && fromCol != toCol){
                return false;
            }

            return isValidRookMove(piece, side, fromRow, fromCol, toRow, toCol);
        }

        if(piece instanceof Knight){
            Knight knight = (Knight) piece;

            return isValidKnightMove(knight, side, fromRow, fromCol, toRow, toCol);
        }

        if(piece instanceof Bishop){
            return isValidBishopMove(piece, side, fromRow, fromCol, toRow, toCol);
        }

        if(piece instanceof Queen){
            if(isValidRookMove(piece, side, fromRow, fromCol, toRow, toCol) || isValidBishopMove(piece, side, fromRow, fromCol, toRow, toCol)){
                return true;
            }else{
                return false;
            }
        }

        if(piece instanceof King){
            King king = (King) piece;

            return isValidKingMove(king, side, fromRow, fromCol, toRow, toCol);
        }
        
        return false;
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
                    if(getCell(fromRow, toCol).getPiece() == null || !getCell(fromRow, toCol).getPiece().getLabel().equals("BP") || getCell(fromRow, toCol).getPiece().getSide() == 'W'){
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
                    if(getCell(fromRow, toCol).getPiece() == null || !getCell(fromRow, toCol).getPiece().getLabel().equals("WP") || getCell(fromRow, toCol).getPiece().getSide() == 'B'){
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

    private boolean isValidRookMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol){
        if(fromRow != toRow){
            if(fromRow > toRow){
                
                for(int i=fromRow-1; i>toRow; i--){
                    if(cells[i][fromCol].getPiece() != null){
                        return false;
                    }
                }

                if(cells[toRow][toCol].getPiece() == null){
                    if(piece instanceof Rook){
                        Rook rook = (Rook) piece;
                        if(!rook.isMoved()){
                            rook.setMoved(true);
                        }
                    }

                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        if(piece instanceof Rook){
                            Rook rook = (Rook) piece;
                            if(!rook.isMoved()){
                                rook.setMoved(true);
                            }
                        }

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
                    if(piece instanceof Rook){
                        Rook rook = (Rook) piece;
                        if(!rook.isMoved()){
                            rook.setMoved(true);
                        }
                    }

                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        if(piece instanceof Rook){
                            Rook rook = (Rook) piece;
                            if(!rook.isMoved()){
                                rook.setMoved(true);
                            }
                        }

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
                    if(piece instanceof Rook){
                        Rook rook = (Rook) piece;
                        if(!rook.isMoved()){
                            rook.setMoved(true);
                        }
                    }

                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        if(piece instanceof Rook){
                            Rook rook = (Rook) piece;
                            if(!rook.isMoved()){
                                rook.setMoved(true);
                            }
                        }

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
                    if(piece instanceof Rook){
                        Rook rook = (Rook) piece;
                        if(!rook.isMoved()){
                            rook.setMoved(true);
                        }
                    }

                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        if(piece instanceof Rook){
                            Rook rook = (Rook) piece;
                            if(!rook.isMoved()){
                                rook.setMoved(true);
                            }
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isValidKnightMove(Knight knight, char side, int fromRow, int fromCol, int toRow, int toCol){
        if(toRow - fromRow == 2 || fromRow - toRow == 2){
            if(toCol - fromCol == 1 || fromCol - toCol == 1){
                if(getCell(toRow, toCol).getPiece() == null){
                    return true;
                }else if(getCell(toRow, toCol).getPiece().getSide() == side){
                    return false;
                }else{
                    return true;
                }
            }else{
                return false;
            }
        }else if(toCol - fromCol == 2 || fromCol - toCol == 2){
            if(toRow - fromRow == 1 || fromRow - toRow == 1){
                if(getCell(toRow, toCol).getPiece() == null){
                    return true;
                }else if(getCell(toRow, toCol).getPiece().getSide() == side){
                    return false;
                }else{
                    return true;
                }
            }else{
                return false;
            }
        }

        return false;
    }

    private boolean isValidBishopMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol){
        if(toCol > fromCol){
            if(toRow > fromRow){


                if(toCol - fromCol != toRow - fromRow){
                    return false;
                }

                int rowIterator = fromRow+1;
                int colIterator = fromCol+1;
                
                while(rowIterator != toRow && colIterator != toCol){

                    if(cells[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }

                    rowIterator++;
                    colIterator++;
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        return true;
                    }
                }


            }else if(fromRow > toRow){

                if(toCol - fromCol != fromRow - toRow){
                    return false;
                }

                int rowIterator = fromRow-1;
                int colIterator = fromCol+1;
                
                while(rowIterator != toRow && colIterator != toCol){

                    if(cells[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }

                    rowIterator--;
                    colIterator++;
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        return true;
                    }
                }

            }
        }else if(fromCol > toCol){
            if(toRow > fromRow){


                if(fromCol - toCol != toRow - fromRow){
                    return false;
                }

                int rowIterator = fromRow+1;
                int colIterator = fromCol-1;
                
                while(rowIterator != toRow && colIterator != toCol){

                    if(cells[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }

                    rowIterator++;
                    colIterator--;
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        return true;
                    }
                }


            }else if(fromRow > toRow){

                if(fromCol - toCol != fromRow - toRow){
                    return false;
                }

                int rowIterator = fromRow-1;
                int colIterator = fromCol-1;
                
                while(rowIterator != toRow && colIterator != toCol){

                    if(cells[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }

                    rowIterator--;
                    colIterator--;
                }

                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(cells[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        return true;
                    }
                }

            }
        }

        return false;
    }

    private boolean isValidKingMove(King king, char side, int fromRow, int fromCol, int toRow, int toCol){
        if(!king.isMoved()){
            if(fromRow == toRow){
                if(toCol - fromCol == 2){
                    if(cells[fromRow][toCol].getPiece() == null && cells[fromRow][toCol-1].getPiece() == null){
                        if(cells[fromRow][toCol+1].getPiece() instanceof Rook){
                            Rook rightSideRook = (Rook) cells[fromRow][toCol+1].getPiece();
                            if(!rightSideRook.isMoved()){
                                king.setMoved(true);
                                this.castleFromCol = toCol + 1;
                                this.castleFromRow = fromRow;
                                this.castleToCol = toCol - 1;
                                this.castleToRow = fromRow;
                                this.castle = true;

                                return true;
                            }else{
                                return false;
                            }
                        }
                    }
                }else if(fromCol - toCol == 2){
                    if(cells[fromRow][toCol].getPiece() == null && cells[fromRow][toCol+1].getPiece() == null && cells[fromRow][toCol-1].getPiece() == null){
                        if(cells[fromRow][toCol-2].getPiece() instanceof Rook){
                            Rook leftSideRook = (Rook) cells[fromRow][toCol-2].getPiece();
                            if(!leftSideRook.isMoved()){
                                king.setMoved(true);
                                this.castleFromCol = toCol - 2;
                                this.castleFromRow = fromRow;
                                this.castleToCol = toCol + 1;
                                this.castleToRow = fromRow;
                                this.castle = true;

                                return true;
                            }else{
                                return false;
                            }
                        }
                    }
                }
            }
        }


        if(toCol - fromCol > 1 || fromCol - toCol > 1 || toRow - fromRow > 1 || fromRow - toRow > 1){
            return false;
        }else if(getCell(toRow, toCol).getPiece() == null){
            if(!king.isMoved()){
                king.setMoved(true);
            }

            return true;
        }else if(getCell(toRow, toCol).getPiece().getSide() == side){
            return false;
        }else{
            if(!king.isMoved()){
                king.setMoved(true);
            }

            return true;
        }
    }

    private void handleCastling(){
        Cell castleFromCell = getCell(this.castleFromRow, this.castleFromCol);
        Cell castleToCell = getCell(this.castleToRow, this.castleToCol);

        Piece castleRook = castleFromCell.getPiece();

        castleToCell.setPiece(castleRook);
        castleFromCell.setPiece(null);
        
        this.castle = false;
    }

    private void handlePromotion(Cell cell, char side){
        System.out.print((side == 'W') ? "White" : "Black");
        System.out.println(" Pawn was promoted!");

        int choice = 0;
        //Can't close this Scanner otherwise it conflicts with the one in the Main class
        Scanner s = new Scanner(System.in);
        while(choice < 1 || choice > 4){
            System.out.println("Input 1 for queen, 2 for rook, 3 for bishop and 4 for knight");
            choice = s.nextInt();
        }

        switch(choice){
            case 1:
                cell.setPiece(new Queen(side));
                break;
            case 2:
                cell.setPiece(new Rook(side));
                break;
            case 3:
                cell.setPiece(new Bishop(side));
                break;
            case 4:
                cell.setPiece(new Knight(side));
                break;
        }

        this.promotion = false;
    }

    private void createAttackingGrid(char side){
        
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
