import java.util.Scanner;

public class Board {
    private Cell[][] cells;
    private int[][] whiteAttackingSquares;
    private int[][] blackAttackingSquares;
    private boolean turn;
    private boolean enPassant;
    private int enPassantRow;
    private int enPassantCol;
    private boolean castle;
    private boolean promotion;
    private int castleFromRow;
    private int castleFromCol;
    private int castleToRow;
    private int castleToCol;
    private boolean whiteKingChecked;
    private boolean whiteKingMated;
    private int whiteKingRow;
    private int whiteKingCol;
    private boolean blackKingChecked;
    private boolean blackKingMated;
    private int blackKingRow;
    private int blackKingCol;
    private boolean gameRunning;

    public Board() {
        cells = new Cell[8][8];
        this.whiteAttackingSquares = new int[8][8];
        this.blackAttackingSquares = new int[8][8];
        initializeBoard();
        this.turn = false;
        this.castle = false;
        this.promotion = false;
        this.whiteKingChecked = false;
        this.whiteKingMated = false;
        this.whiteKingRow = 7;
        this.whiteKingCol = 4;
        this.blackKingChecked = false;
        this.blackKingMated = false;
        this.blackKingRow = 0;
        this.blackKingCol = 4;
        this.gameRunning = true;
        this.enPassant = false;
        gameLoop();
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

        //cells[4][6].setPiece(new Queen('W'));
        //cells[1][5].setPiece(null);
        //cells[1][6].setPiece(null);
    }

    private void gameLoop(){
        Scanner scanner = new Scanner(System.in);

        while(this.gameRunning){
            printCurrentBoard();

            if(this.turn){
                System.out.println("Black to move.");
            }else{
                System.out.println("White to move.");
            }

            boolean validMove = false;

            while(!validMove){
                /* System.out.print("Enter the next move: ");
                String nextMove = scanner.nextLine();

                String[] fromAndTo = nextMove.split("->");
                String[] fromValues = fromAndTo[0].split(",");
                String[] toValues = fromAndTo[1].split(",");

                validMove = move(Integer.parseInt(fromValues[0]), Integer.parseInt(fromValues[1]), Integer.parseInt(toValues[0]), Integer.parseInt(toValues[1])); */

                System.out.print("Enter the row to move from: ");
                int fromRow = scanner.nextInt();

                System.out.print("Enter the column to move from: ");
                int fromCol = scanner.nextInt();

                System.out.print("Enter the row to move to: ");
                int toRow = scanner.nextInt();

                System.out.print("Enter the column to move to: ");
                int toCol = scanner.nextInt();

                validMove = move(fromRow, fromCol, toRow, toCol);
            }

            this.turn = !this.turn;
        }
        
        scanner.close();
    }

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidCoordinate(fromRow, fromCol) || !isValidCoordinate(toRow, toCol) || (fromRow == toRow && fromCol == toCol)) {
            System.out.println("Invalid coordinates. Please provide valid coordinates.");
            return false;
        }

        Cell fromCell = getCell(fromRow, fromCol);
        Cell toCell = getCell(toRow, toCol);

        if (fromCell.getPiece() == null) {
            System.out.println("There is no piece in the selected cell.");
            return false;
        }

        Piece piece = fromCell.getPiece();

        if(!this.turn && piece.getSide() == 'B'){
            System.out.println("Invalid piece, this is white's turn.");
            return false;
        }else if(this.turn && piece.getSide() == 'W'){
            System.out.println("Invalid piece, this is black's turn.");
            return false;
        }

        int currentWhiteKingRow = this.whiteKingRow;
        int currentWhiteKingCol = this.whiteKingCol;
        int currentBlackKingRow = this.blackKingRow;
        int currentBlackKingCol = this.blackKingCol;

        if (isValidMove(piece, piece.getSide(), fromRow, fromCol, toRow, toCol)) {
            if(this.turn && this.blackKingChecked){
                if(!moveGetsOutofCheck(piece, piece.getSide(), fromRow, fromCol, toRow, toCol, currentBlackKingRow, currentBlackKingCol)){
                    this.blackKingRow = currentBlackKingRow;
                    this.blackKingCol = currentBlackKingCol;

                    if(castle){
                        this.castle = false;
                    }

                    System.out.println("Invalid move for the selected piece. Remember, you are in check!");
                    return false;
                }else{
                    this.blackKingChecked = false;
                }
            }else if(!this.turn && this.whiteKingChecked){
                if(!moveGetsOutofCheck(piece, piece.getSide(), fromRow, fromCol, toRow, toCol, currentWhiteKingRow, currentWhiteKingCol)){
                    this.whiteKingRow = currentWhiteKingRow;
                    this.whiteKingCol = currentWhiteKingCol;

                    if(castle){
                        this.castle = false;
                    }

                    System.out.println("Invalid move for the selected piece. Remember, you are in check!");
                    return false;
                }else{
                    this.whiteKingChecked = false;
                }
            }

            toCell.setPiece(piece);
            fromCell.setPiece(null);

            if(enPassant){
                handleEnPassant();
            }

            if(castle){
                handleCastling();
            }

            if(promotion){
                handlePromotion(toCell, piece.getSide());
            }

            createAttackingGrid(piece.getSide());

            System.out.println("Move successful!");

            if(piece.getSide() == 'W'){
                if(this.whiteAttackingSquares[this.blackKingRow][this.blackKingCol] == 1){
                    this.blackKingChecked = true;
                    System.out.println("Black king checked!");

                    if(!anyMovesLeft('B')){
                        this.blackKingMated = true;
                        System.out.println("Check mate!");
                    }
                }
            }else{
                if(this.blackAttackingSquares[this.whiteKingRow][this.whiteKingCol] == 1){
                    this.whiteKingChecked = true;
                    System.out.println("White king checked!");

                    if(!anyMovesLeft('W')){
                        this.whiteKingMated = true;
                        System.out.println("Check mate!");
                    }
                }
            }

            if(this.whiteKingMated){
                System.out.println("Black wins!");
                this.gameRunning = false;
            }else if(this.blackKingMated){
                System.out.println("White wins!");
                this.gameRunning = false;
            }

            return true;

        } else {
            System.out.println("Invalid move for the selected piece.");
            return false;
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

                            this.enPassant = true;
                            this.enPassantRow = toRow + 1;
                            this.enPassantCol = toCol;

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

                            this.enPassant = true;
                            this.enPassantRow = toRow - 1;
                            this.enPassantCol = toCol;

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
                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else if(cells[toRow][toCol].getPiece().getSide() == side){
                    return false;
                }else{
                    return true;
                }
            }else{
                return false;
            }
        }else if(toCol - fromCol == 2 || fromCol - toCol == 2){
            if(toRow - fromRow == 1 || fromRow - toRow == 1){
                if(cells[toRow][toCol].getPiece() == null){
                    return true;
                }else if(cells[toRow][toCol].getPiece().getSide() == side){
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
                    if(side == 'W'){
                        if(cells[fromRow][toCol].getPiece() == null && cells[fromRow][toCol-1].getPiece() == null && this.blackAttackingSquares[fromRow][toCol] == 0 && this.blackAttackingSquares[fromRow][toCol-1] == 0){
                            if(cells[fromRow][toCol+1].getPiece() instanceof Rook){
                                Rook rightSideRook = (Rook) cells[fromRow][toCol+1].getPiece();
                                if(!rightSideRook.isMoved()){
                                    king.setMoved(true);
                                    this.castleFromCol = toCol + 1;
                                    this.castleFromRow = fromRow;
                                    this.castleToCol = toCol - 1;
                                    this.castleToRow = fromRow;
                                    this.castle = true;
                                    this.whiteKingRow = toRow;
                                    this.whiteKingCol = toCol;
                                    
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    }else{
                        if(cells[fromRow][toCol].getPiece() == null && cells[fromRow][toCol-1].getPiece() == null && this.whiteAttackingSquares[fromRow][toCol] == 0 && this.whiteAttackingSquares[fromRow][toCol-1] == 0){
                            if(cells[fromRow][toCol+1].getPiece() instanceof Rook){
                                Rook rightSideRook = (Rook) cells[fromRow][toCol+1].getPiece();
                                if(!rightSideRook.isMoved()){
                                    king.setMoved(true);
                                    this.castleFromCol = toCol + 1;
                                    this.castleFromRow = fromRow;
                                    this.castleToCol = toCol - 1;
                                    this.castleToRow = fromRow;
                                    this.castle = true;
                                    this.blackKingRow = toRow;
                                    this.blackKingCol = toCol;
    
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    }
                }else if(fromCol - toCol == 2){
                    if(side == 'W'){
                        if(cells[fromRow][toCol].getPiece() == null && cells[fromRow][toCol+1].getPiece() == null && cells[fromRow][toCol-1].getPiece() == null && this.blackAttackingSquares[fromRow][toCol] == 0 && this.blackAttackingSquares[fromRow][toCol+1] == 0 && this.blackAttackingSquares[fromRow][toCol-1] == 0){
                            if(cells[fromRow][toCol-2].getPiece() instanceof Rook){
                                Rook leftSideRook = (Rook) cells[fromRow][toCol-2].getPiece();
                                if(!leftSideRook.isMoved()){
                                    king.setMoved(true);
                                    this.castleFromCol = toCol - 2;
                                    this.castleFromRow = fromRow;
                                    this.castleToCol = toCol + 1;
                                    this.castleToRow = fromRow;
                                    this.castle = true;
                                    this.whiteKingRow = toRow;
                                    this.whiteKingCol = toCol;
    
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    }else{
                        if(cells[fromRow][toCol].getPiece() == null && cells[fromRow][toCol+1].getPiece() == null && cells[fromRow][toCol-1].getPiece() == null && this.whiteAttackingSquares[fromRow][toCol] == 0 && this.whiteAttackingSquares[fromRow][toCol+1] == 0 && this.whiteAttackingSquares[fromRow][toCol-1] == 0){
                            if(cells[fromRow][toCol-2].getPiece() instanceof Rook){
                                Rook leftSideRook = (Rook) cells[fromRow][toCol-2].getPiece();
                                if(!leftSideRook.isMoved()){
                                    king.setMoved(true);
                                    this.castleFromCol = toCol - 2;
                                    this.castleFromRow = fromRow;
                                    this.castleToCol = toCol + 1;
                                    this.castleToRow = fromRow;
                                    this.castle = true;
                                    this.blackKingRow = toRow;
                                    this.blackKingCol = toCol;
    
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


        if(toCol - fromCol > 1 || fromCol - toCol > 1 || toRow - fromRow > 1 || fromRow - toRow > 1){
            return false;
        }else if(getCell(toRow, toCol).getPiece() == null){
            if(side == 'W'){
                if(this.blackAttackingSquares[toRow][toCol] == 0){
                    if(!king.isMoved()){
                        king.setMoved(true);
                    }
                    this.whiteKingRow = toRow;
                    this.whiteKingCol = toCol;
                    return true;
                }else{
                    return false;
                }
            }else{
                if(this.whiteAttackingSquares[toRow][toCol] == 0){
                    if(!king.isMoved()){
                        king.setMoved(true);
                    }
                    this.blackKingRow = toRow;
                    this.blackKingCol = toCol;
                    return true;
                }else{
                    return false;
                }
            }
        }else if(getCell(toRow, toCol).getPiece().getSide() == side){
            return false;
        }else{
            if(!king.isMoved()){
                king.setMoved(true);
            }

            if(side == 'W'){
                this.whiteKingRow = toRow;
                this.whiteKingCol = toCol;
            }else{
                this.blackKingRow = toRow;
                this.blackKingCol = toCol;
            }

            return true;
        }
    }

    private void handleEnPassant(){
        Cell capturedPawnCell = getCell(this.enPassantRow, this.enPassantCol);
        capturedPawnCell.setPiece(null);
        this.enPassant = false;
    }

    private void handleCastling(){
        Cell castleFromCell = getCell(this.castleFromRow, this.castleFromCol);
        Cell castleToCell = getCell(this.castleToRow, this.castleToCol);

        Piece castleRook = castleFromCell.getPiece();

        castleToCell.setPiece(castleRook);
        castleFromCell.setPiece(null);
        
        this.castle = false;
    }

    private void testPossibleCastling(Cell[][] possibleState){
        Cell castleFromCell = possibleState[this.castleFromRow][this.castleFromCol];
        Cell castleToCell = possibleState[this.castleToRow][this.castleToCol];

        Piece castleRook = castleFromCell.getPiece();

        castleToCell.setPiece(castleRook);
        castleFromCell.setPiece(null);
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

    private void clearAttackingGrid(char side){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(side == 'W'){
                    this.whiteAttackingSquares[i][j] = 0;
                }else{
                    this.blackAttackingSquares[i][j] = 0;
                }
            }
        }
    }

    private void createAttackingGrid(char side){
        clearAttackingGrid(side);

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(cells[i][j].getPiece() != null){
                    if(cells[i][j].getPiece().getSide() == side){
                        checkPossibleMovements(cells[i][j].getPiece(), side, i, j);
                    }
                }
            }
        }
    }

    private void checkPossibleMovements(Piece piece, char side, int row, int col){
        if(piece instanceof Pawn){
            Pawn pawn = (Pawn) piece;
            boolean currentMovedState = pawn.isMoved();
            boolean currentDoubleMoveLastTurnState = pawn.isDoubleMoveLastTurn();

            possiblePawnMovements(pawn, side, row, col);

            pawn.setMoved(currentMovedState);
            pawn.setDoubleMoveLastTurn(currentDoubleMoveLastTurnState);
            return;
        }

        if(piece instanceof Rook){
            Rook rook = (Rook) piece;
            boolean currentMovedState = rook.isMoved();

            possibleRookMovements(piece, side, row, col);

            rook.setMoved(currentMovedState);
            return;
        }

        if(piece instanceof Knight){
            Knight knight = (Knight) piece;
            possibleKnightMovements(knight, side, row, col);
            return;
        }

        if(piece instanceof Bishop){
            possibleBishopMovements(piece, side, row, col);
            return;
        }

        if(piece instanceof Queen){
            possibleRookMovements(piece, side, row, col);
            possibleBishopMovements(piece, side, row, col);
            return;
        }

        if(piece instanceof King){
            King king = (King) piece;
            boolean currentMovedState = king.isMoved();

            possibleKingMovements(king, side, row, col);

            king.setMoved(currentMovedState);
            return;
        }
    }

    private void possiblePawnMovements(Pawn pawn, char side, int row, int col){
        if(side == 'W'){
            int[] possibleWhiteRows = {row - 2, row - 1, row - 1, row - 1};
            int[] possibleWhiteCols = {col, col, col - 1, col + 1};

            if(!pawn.isMoved()){
                if(isValidCoordinate(possibleWhiteRows[0], possibleWhiteCols[0])){
                    if(isValidWhitePawnMove(pawn, row, col, possibleWhiteRows[0], possibleWhiteCols[0])){
                        this.whiteAttackingSquares[possibleWhiteRows[0]][possibleWhiteCols[0]] = 1;
                    }
                }
            }

            for(int i=1; i<possibleWhiteRows.length; i++){
                for(int j=1; j<possibleWhiteCols.length; j++){
                    if(isValidCoordinate(possibleWhiteRows[i], possibleWhiteCols[j])){
                        if(isValidWhitePawnMove(pawn, row, col, possibleWhiteRows[i], possibleWhiteCols[j])){
                            this.whiteAttackingSquares[possibleWhiteRows[i]][possibleWhiteCols[j]] = 1;
                        }
                    }
                }
            }
        }else{
            int[] possibleBlackRows = {row + 2, row + 1, row + 1, row + 1};
            int[] possibleBlackCols = {col, col, col - 1, col + 1};

            if(!pawn.isMoved()){
                if(isValidCoordinate(possibleBlackRows[0], possibleBlackCols[0])){
                    if(isValidBlackPawnMove(pawn, row, col, possibleBlackRows[0], possibleBlackCols[0])){
                        this.blackAttackingSquares[possibleBlackRows[0]][possibleBlackCols[0]] = 1;
                    }
                }
            }

            for(int i=1; i<possibleBlackRows.length; i++){
                for(int j=1; j<possibleBlackCols.length; j++){
                    if(isValidCoordinate(possibleBlackRows[i], possibleBlackCols[j])){
                        if(isValidBlackPawnMove(pawn, row, col, possibleBlackRows[i], possibleBlackCols[j])){
                            this.blackAttackingSquares[possibleBlackRows[i]][possibleBlackCols[j]] = 1;
                        }
                    }
                }
            }
        }
    }

    private void possibleRookMovements(Piece piece, char side, int row, int col){
        for(int i=0; i<8; i++){
            if(i != row){
                if(isValidCoordinate(i, col)){
                    if(isValidRookMove(piece, side, row, col, i, col)){
                        if(side == 'W'){
                            this.whiteAttackingSquares[i][col] = 1;
                        }else{
                            this.blackAttackingSquares[i][col] = 1;
                        }
                    }
                }
            }
        }

        for(int i=0; i<8; i++){
            if(i != col){
                if(isValidCoordinate(row, i)){
                    if(isValidRookMove(piece, side, row, col, row, i)){
                        if(side == 'W'){
                            this.whiteAttackingSquares[row][i] = 1;
                        }else{
                            this.blackAttackingSquares[row][i] = 1;
                        }
                    }
                }
            }
        } 
    }

    private void possibleKnightMovements(Knight knight, char side, int row, int col){
        int[] possibleRows = {row + 2, row + 2, row - 2, row - 2, row + 1, row - 1, row + 1, row - 1};
        int[] possibleCols = {col + 1, col - 1, col + 1, col - 1, col + 2, col + 2, col - 2, col - 2};

        for(int i=0; i<possibleRows.length; i++){
            for(int j=0; j<possibleCols.length; j++){
                if(isValidCoordinate(possibleRows[i], possibleCols[j])){
                    if(isValidKnightMove(knight, side, row, col, possibleRows[i], possibleCols[j])){
                        if(side == 'W'){
                            this.whiteAttackingSquares[possibleRows[i]][possibleCols[j]] = 1;
                        }else{
                            this.blackAttackingSquares[possibleRows[i]][possibleCols[j]] = 1;
                        }
                    }
                }
            }
        }
    }

    private void possibleBishopMovements(Piece piece, char side, int row, int col){
        int auxCol = col+1;

        for(int i=row+1; i<8; i++){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMove(piece, side, row, col, i, auxCol)){
                    if(side == 'W'){
                        this.whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        this.blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol++;
        }


        auxCol = col+1;

        for(int i=row-1; i>=0; i--){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMove(piece, side, row, col, i, auxCol)){
                    if(side == 'W'){
                        this.whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        this.blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol++;
        }

        auxCol = col-1;

        for(int i=row+1; i<8; i++){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMove(piece, side, row, col, i, auxCol)){
                    if(side == 'W'){
                        this.whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        this.blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol--;
        }

        auxCol = col-1;

        for(int i=row-1; i>=0; i--){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMove(piece, side, row, col, i, auxCol)){
                    if(side == 'W'){
                        this.whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        this.blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol--;
        }
    }

    private void possibleKingMovements(King king, char side, int row, int col){
        int[] possibleRows = {row + 1, row + 1, row + 1, row - 1, row - 1, row - 1, row, row};
        int[] possibleCols = {col, col + 1, col - 1, col, col + 1, col - 1, col + 1, col - 1};

        for(int i=0; i<possibleRows.length; i++){
            for(int j=0; j<possibleCols.length; j++){
                if(isValidCoordinate(possibleRows[i], possibleCols[j])){
                    if(isValidKingMove(king, side, row, col, possibleRows[i], possibleCols[j])){
                        if(side == 'W'){
                            this.whiteAttackingSquares[possibleRows[i]][possibleCols[j]] = 1;
                        }else{
                            this.blackAttackingSquares[possibleRows[i]][possibleCols[j]] = 1;
                        }
                    }
                }
            }
        }
    }

    private boolean anyMovesLeft(char side){
        int[][] opposingAttacks;
        int[][] currentSideAttacks;

        if(side == 'W'){
            opposingAttacks = this.blackAttackingSquares;
            currentSideAttacks = this.whiteAttackingSquares;
        }else{
            opposingAttacks = this.whiteAttackingSquares;
            currentSideAttacks = this.blackAttackingSquares;
        }

        clearAttackingGrid('W');
        clearAttackingGrid('B');

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(cells[i][j].getPiece() != null){
                    if(cells[i][j].getPiece().getSide() == side){
                        checkPossibleMovements(cells[i][j].getPiece(), side, i, j);

                        for(int k=0; k<8; k++){
                            for(int l=0; l<8; l++){

                                if(side == 'W'){
                                    if(this.whiteAttackingSquares[k][l] == 1){

                                        Cell[][] copyState = copyBoardState(cells);

                                        Cell possibleFromCell = copyState[i][j];
                                        Cell possibleToCell = copyState[k][l];

                                        Piece currentPiece = possibleFromCell.getPiece();

                                        possibleToCell.setPiece(currentPiece);
                                        possibleFromCell.setPiece(null);

                                        clearAttackingGrid('B');
                                        for(int m=0; m<8; m++){
                                            for(int n=0; n<8; n++){
                                                if(copyState[m][n].getPiece() != null){
                                                    if(copyState[m][n].getPiece().getSide() == 'B'){
                                                        checkPossibleMovementsCopy(copyState[m][n].getPiece(), 'B', m, n, copyState);
                                                    }
                                                }
                                            }
                                        }

                                        if(this.blackAttackingSquares[this.whiteKingRow][this.whiteKingCol] == 0){
                                            this.whiteAttackingSquares = currentSideAttacks;
                                            this.blackAttackingSquares = opposingAttacks;
                                            return true;
                                        }

                                    }
                                }else{
                                    if(this.blackAttackingSquares[k][l] == 1){
                                        
                                        Cell[][] copyState = copyBoardState(cells);

                                        Cell possibleFromCell = copyState[i][j];
                                        Cell possibleToCell = copyState[k][l];

                                        Piece currentPiece = possibleFromCell.getPiece();

                                        possibleToCell.setPiece(currentPiece);
                                        possibleFromCell.setPiece(null);

                                        clearAttackingGrid('W');
                                        for(int m=0; m<8; m++){
                                            for(int n=0; n<8; n++){
                                                if(copyState[m][n].getPiece() != null){
                                                    if(copyState[m][n].getPiece().getSide() == 'W'){
                                                        checkPossibleMovementsCopy(copyState[m][n].getPiece(), 'W', m, n, copyState);
                                                    }
                                                }
                                            }
                                        }

                                        if(this.whiteAttackingSquares[this.blackKingRow][this.blackKingCol] == 0){
                                            this.whiteAttackingSquares = opposingAttacks;
                                            this.blackAttackingSquares = currentSideAttacks;
                                            return true;
                                        }

                                    }
                                }
                            }    
                        }

                        if(side == 'W'){
                            clearAttackingGrid('W');
                        }else{
                            clearAttackingGrid('B');
                        }

                    }
                }
            }
        }

        if(side == 'W'){
            this.whiteAttackingSquares = currentSideAttacks;
            this.blackAttackingSquares = opposingAttacks;
        }else{
            this.whiteAttackingSquares = opposingAttacks;
            this.blackAttackingSquares = currentSideAttacks;
        }

        return false;
    }

    private boolean moveGetsOutofCheck(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, int previousKingRow, int previousKingCol){
        boolean result = false;

        int[][] whiteAttacksBackUp = this.whiteAttackingSquares;
        int[][] blackAttacksBackUp = this.blackAttackingSquares;

        Cell[][] copyState = copyBoardState(cells);

        Cell possibleFromCell = copyState[fromRow][fromCol];
        Cell possibleToCell = copyState[toRow][toCol];

        possibleToCell.setPiece(piece);
        possibleFromCell.setPiece(null);

        if(castle){
            testPossibleCastling(copyState);
        }

        if(side == 'W'){
            clearAttackingGrid('B');
            for(int i=0; i<8; i++){
                for(int j=0; j<8; j++){
                    if(copyState[i][j].getPiece() != null){
                        if(copyState[i][j].getPiece().getSide() == 'B'){
                            checkPossibleMovementsCopy(copyState[i][j].getPiece(), 'B', i, j, copyState);
                        }
                    }
                }
            }

            if(this.blackAttackingSquares[previousKingRow][previousKingCol] == 0){
                result = true;
            }

        }else{
            clearAttackingGrid('W');
            for(int i=0; i<8; i++){
                for(int j=0; j<8; j++){
                    if(copyState[i][j].getPiece() != null){
                        if(copyState[i][j].getPiece().getSide() == 'W'){
                            checkPossibleMovementsCopy(copyState[i][j].getPiece(), 'W', i, j, copyState);
                        }
                    }
                }
            }

            if(this.whiteAttackingSquares[previousKingRow][previousKingCol] == 0){
                result = true;
            }
        }

        this.whiteAttackingSquares = whiteAttacksBackUp;
        this.blackAttackingSquares = blackAttacksBackUp;

        return result;
    }

    private Cell[][] copyBoardState(Cell[][] originalBoard) {
        Cell[][] copyState = new Cell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copyState[i][j] = new Cell();
                if (originalBoard[i][j].getPiece() != null) {
                    Piece originalPiece = originalBoard[i][j].getPiece();
                    char originalSide = originalPiece.getSide();
                    Piece copiedPiece;

                    // Check the type of the piece and create a new instance accordingly
                    if (originalPiece instanceof Pawn) {
                        copiedPiece = new Pawn(originalSide);
                        ((Pawn) copiedPiece).setMoved(((Pawn) originalPiece).isMoved());
                        ((Pawn) copiedPiece).setDoubleMoveLastTurn(((Pawn) originalPiece).isDoubleMoveLastTurn());
                    } else if (originalPiece instanceof King) {
                        copiedPiece = new King(originalSide);
                        ((King) copiedPiece).setMoved(((King) originalPiece).isMoved());
                    } else if (originalPiece instanceof Rook) {
                        copiedPiece = new Rook(originalSide);
                        ((Rook) copiedPiece).setMoved(((Rook) originalPiece).isMoved());
                    } else if (originalPiece instanceof Bishop){
                        copiedPiece = new Bishop(originalSide);
                    } else if (originalPiece instanceof Knight){
                        copiedPiece = new Knight(originalSide);
                    } else if (originalPiece instanceof Queen){
                        copiedPiece = new Queen(originalSide);
                    } else{
                        copiedPiece = new Piece();
                    }
    
                    // Copy common attributes
                    copiedPiece.setLabel(originalPiece.getLabel());
                    copiedPiece.setSide(originalPiece.getSide());
    
                    copyState[i][j].setPiece(copiedPiece);
                }
            }
        }
        return copyState;
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

    public void printCurrentAtackGrid(char side){
        System.out.println("========================================");
        //createAttackingGrid(side);

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(side == 'W'){
                    System.out.print(this.whiteAttackingSquares[i][j]+" ");
                }else{
                    System.out.print(this.blackAttackingSquares[i][j]+" ");
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

    //THE MESSED UP UNDERWORLD OF NEARLY IDENTICAL FUNCTIONS BECAUSE OF COPY AND REFERENCE SHENANIGANS
    
    private void checkPossibleMovementsCopy(Piece piece, char side, int row, int col, Cell[][] copyReference){
        if(piece instanceof Pawn){
            Pawn pawn = (Pawn) piece;
            boolean currentMovedState = pawn.isMoved();
            boolean currentDoubleMoveLastTurnState = pawn.isDoubleMoveLastTurn();

            possiblePawnMovementsCopy(pawn, side, row, col, copyReference);

            pawn.setMoved(currentMovedState);
            pawn.setDoubleMoveLastTurn(currentDoubleMoveLastTurnState);
            return;
        }

        if(piece instanceof Rook){
            Rook rook = (Rook) piece;
            boolean currentMovedState = rook.isMoved();

            possibleRookMovementsCopy(piece, side, row, col, copyReference);

            rook.setMoved(currentMovedState);
            return;
        }

        if(piece instanceof Knight){
            Knight knight = (Knight) piece;
            possibleKnightMovementsCopy(knight, side, row, col, copyReference);
            return;
        }

        if(piece instanceof Bishop){
            possibleBishopMovementsCopy(piece, side, row, col, copyReference);
            return;
        }

        if(piece instanceof Queen){
            possibleRookMovementsCopy(piece, side, row, col, copyReference);
            possibleBishopMovementsCopy(piece, side, row, col, copyReference);
            return;
        }

        if(piece instanceof King){
            King king = (King) piece;
            boolean currentMovedState = king.isMoved();

            possibleKingMovementsCopy(king, side, row, col, copyReference);

            king.setMoved(currentMovedState);
            return;
        }
    }

    private void possiblePawnMovementsCopy(Pawn pawn, char side, int row, int col, Cell[][] copyReference){
        if(side == 'W'){
            int[] possibleWhiteRows = {row - 2, row - 1, row - 1, row - 1};
            int[] possibleWhiteCols = {col, col, col - 1, col + 1};

            if(!pawn.isMoved()){
                if(isValidCoordinate(possibleWhiteRows[0], possibleWhiteCols[0])){
                    if(isValidWhitePawnMoveCopy(pawn, row, col, possibleWhiteRows[0], possibleWhiteCols[0], copyReference)){
                        this.whiteAttackingSquares[possibleWhiteRows[0]][possibleWhiteCols[0]] = 1;
                    }
                }
            }

            for(int i=1; i<possibleWhiteRows.length; i++){
                for(int j=1; j<possibleWhiteCols.length; j++){
                    if(isValidCoordinate(possibleWhiteRows[i], possibleWhiteCols[j])){
                        if(isValidWhitePawnMoveCopy(pawn, row, col, possibleWhiteRows[i], possibleWhiteCols[j], copyReference)){
                            this.whiteAttackingSquares[possibleWhiteRows[i]][possibleWhiteCols[j]] = 1;
                        }
                    }
                }
            }
        }else{
            int[] possibleBlackRows = {row + 2, row + 1, row + 1, row + 1};
            int[] possibleBlackCols = {col, col, col - 1, col + 1};

            if(!pawn.isMoved()){
                if(isValidCoordinate(possibleBlackRows[0], possibleBlackCols[0])){
                    if(isValidBlackPawnMoveCopy(pawn, row, col, possibleBlackRows[0], possibleBlackCols[0], copyReference)){
                        this.blackAttackingSquares[possibleBlackRows[0]][possibleBlackCols[0]] = 1;
                    }
                }
            }

            for(int i=1; i<possibleBlackRows.length; i++){
                for(int j=1; j<possibleBlackCols.length; j++){
                    if(isValidCoordinate(possibleBlackRows[i], possibleBlackCols[j])){
                        if(isValidBlackPawnMoveCopy(pawn, row, col, possibleBlackRows[i], possibleBlackCols[j], copyReference)){
                            this.blackAttackingSquares[possibleBlackRows[i]][possibleBlackCols[j]] = 1;
                        }
                    }
                }
            }
        }
    }
    
    private boolean isValidWhitePawnMoveCopy(Pawn pawn, int fromRow, int fromCol, int toRow, int toCol, Cell[][] copyReference){
        //Check for single move
        if(fromRow - toRow == 1){

            //Check forward move
            if(toCol - fromCol == 0){
                //Check if there's a piece in the way in forward move
                if(copyReference[toRow][toCol].getPiece() == null){

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
                if(copyReference[toRow][toCol].getPiece() == null){

                    //Check for En Passant
                    if(copyReference[fromRow][toCol].getPiece() == null || !copyReference[fromRow][toCol].getPiece().getLabel().equals("BP") || copyReference[fromRow][toCol].getPiece().getSide() == 'W'){
                        return false;
                    }else{
                        Pawn enemyPawn = (Pawn) copyReference[fromRow][toCol].getPiece();
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
                    if(copyReference[toRow][toCol].getPiece().getSide() == 'W'){
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
                if(copyReference[toRow][toCol].getPiece() == null){
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

    private boolean isValidBlackPawnMoveCopy(Pawn pawn, int fromRow, int fromCol, int toRow, int toCol, Cell[][] copyReference){
        //Check for single move
        if(toRow - fromRow == 1){

            //Check forward move
            if(toCol - fromCol == 0){
                //Check if there's a piece in the way in forward move
                if(copyReference[toRow][toCol].getPiece() == null){

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
                if(copyReference[toRow][toCol].getPiece() == null){

                    //Check for En Passant
                    if(copyReference[fromRow][toCol].getPiece() == null || !copyReference[fromRow][toCol].getPiece().getLabel().equals("WP") || copyReference[fromRow][toCol].getPiece().getSide() == 'B'){
                        return false;
                    }else{
                        Pawn enemyPawn = (Pawn) copyReference[fromRow][toCol].getPiece();
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
                    if(copyReference[toRow][toCol].getPiece().getSide() == 'B'){
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
                if(copyReference[toRow][toCol].getPiece() == null){
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

    private void possibleRookMovementsCopy(Piece piece, char side, int row, int col, Cell[][] copyReference){
        for(int i=0; i<8; i++){
            if(i != row){
                if(isValidCoordinate(i, col)){
                    if(isValidRookMoveCopy(piece, side, row, col, i, col, copyReference)){
                        if(side == 'W'){
                            this.whiteAttackingSquares[i][col] = 1;
                        }else{
                            this.blackAttackingSquares[i][col] = 1;
                        }
                    }
                }
            }
        }

        for(int i=0; i<8; i++){
            if(i != col){
                if(isValidCoordinate(row, i)){
                    if(isValidRookMoveCopy(piece, side, row, col, row, i, copyReference)){
                        if(side == 'W'){
                            this.whiteAttackingSquares[row][i] = 1;
                        }else{
                            this.blackAttackingSquares[row][i] = 1;
                        }
                    }
                }
            }
        } 
    }

    private boolean isValidRookMoveCopy(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] copyReference){
        if(fromRow != toRow){
            if(fromRow > toRow){
                
                for(int i=fromRow-1; i>toRow; i--){
                    if(copyReference[i][fromCol].getPiece() != null){
                        return false;
                    }
                }

                if(copyReference[toRow][toCol].getPiece() == null){
                    if(piece instanceof Rook){
                        Rook rook = (Rook) piece;
                        if(!rook.isMoved()){
                            rook.setMoved(true);
                        }
                    }

                    return true;
                }else{
                    if(copyReference[toRow][toCol].getPiece().getSide() == side){
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
                    if(copyReference[i][fromCol].getPiece() != null){
                        return false;
                    }
                }

                if(copyReference[toRow][toCol].getPiece() == null){
                    if(piece instanceof Rook){
                        Rook rook = (Rook) piece;
                        if(!rook.isMoved()){
                            rook.setMoved(true);
                        }
                    }

                    return true;
                }else{
                    if(copyReference[toRow][toCol].getPiece().getSide() == side){
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
                    if(copyReference[fromRow][i].getPiece() != null){
                        return false;
                    }
                }

                if(copyReference[toRow][toCol].getPiece() == null){
                    if(piece instanceof Rook){
                        Rook rook = (Rook) piece;
                        if(!rook.isMoved()){
                            rook.setMoved(true);
                        }
                    }

                    return true;
                }else{
                    if(copyReference[toRow][toCol].getPiece().getSide() == side){
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
                    if(copyReference[fromRow][i].getPiece() != null){
                        return false;
                    }
                }

                if(copyReference[toRow][toCol].getPiece() == null){
                    if(piece instanceof Rook){
                        Rook rook = (Rook) piece;
                        if(!rook.isMoved()){
                            rook.setMoved(true);
                        }
                    }

                    return true;
                }else{
                    if(copyReference[toRow][toCol].getPiece().getSide() == side){
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

    private void possibleKnightMovementsCopy(Knight knight, char side, int row, int col, Cell[][] copyReference){
        int[] possibleRows = {row + 2, row + 2, row - 2, row - 2, row + 1, row - 1, row + 1, row - 1};
        int[] possibleCols = {col + 1, col - 1, col + 1, col - 1, col + 2, col + 2, col - 2, col - 2};

        for(int i=0; i<possibleRows.length; i++){
            for(int j=0; j<possibleCols.length; j++){
                if(isValidCoordinate(possibleRows[i], possibleCols[j])){
                    if(isValidKnightMoveCopy(knight, side, row, col, possibleRows[i], possibleCols[j], copyReference)){
                        if(side == 'W'){
                            this.whiteAttackingSquares[possibleRows[i]][possibleCols[j]] = 1;
                        }else{
                            this.blackAttackingSquares[possibleRows[i]][possibleCols[j]] = 1;
                        }
                    }
                }
            }
        }
    }

    private boolean isValidKnightMoveCopy(Knight knight, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] copyReference){
        if(toRow - fromRow == 2 || fromRow - toRow == 2){
            if(toCol - fromCol == 1 || fromCol - toCol == 1){
                if(copyReference[toRow][toCol].getPiece() == null){
                    return true;
                }else if(copyReference[toRow][toCol].getPiece().getSide() == side){
                    return false;
                }else{
                    return true;
                }
            }else{
                return false;
            }
        }else if(toCol - fromCol == 2 || fromCol - toCol == 2){
            if(toRow - fromRow == 1 || fromRow - toRow == 1){
                if(copyReference[toRow][toCol].getPiece() == null){
                    return true;
                }else if(copyReference[toRow][toCol].getPiece().getSide() == side){
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

    private void possibleBishopMovementsCopy(Piece piece, char side, int row, int col, Cell[][] copyReference){
        int auxCol = col+1;

        for(int i=row+1; i<8; i++){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMoveCopy(piece, side, row, col, i, auxCol, copyReference)){
                    if(side == 'W'){
                        this.whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        this.blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol++;
        }

        auxCol = col+1;
        for(int i=row-1; i>=0; i--){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMoveCopy(piece, side, row, col, i, auxCol, copyReference)){
                    if(side == 'W'){
                        this.whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        this.blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol++;
        }

        auxCol = col-1;
        for(int i=row+1; i<8; i++){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMoveCopy(piece, side, row, col, i, auxCol, copyReference)){
                    if(side == 'W'){
                        this.whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        this.blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol--;
        }

        auxCol = col-1;
        for(int i=row-1; i>=0; i--){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMoveCopy(piece, side, row, col, i, auxCol, copyReference)){
                    if(side == 'W'){
                        this.whiteAttackingSquares[i][auxCol] = 1;
                    }else{
                        this.blackAttackingSquares[i][auxCol] = 1;
                    }
                }
            }
            auxCol--;
        }
    }

    private boolean isValidBishopMoveCopy(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] copyReference){
        if(toCol > fromCol){
            if(toRow > fromRow){
                if(toCol - fromCol != toRow - fromRow){
                    return false;
                }

                int rowIterator = fromRow+1;
                int colIterator = fromCol+1;
                
                while(rowIterator != toRow && colIterator != toCol){

                    if(copyReference[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }

                    rowIterator++;
                    colIterator++;
                }

                if(copyReference[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(copyReference[toRow][toCol].getPiece().getSide() == side){
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

                    if(copyReference[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }

                    rowIterator--;
                    colIterator++;
                }

                if(copyReference[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(copyReference[toRow][toCol].getPiece().getSide() == side){
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

                    if(copyReference[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }

                    rowIterator++;
                    colIterator--;
                }

                if(copyReference[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(copyReference[toRow][toCol].getPiece().getSide() == side){
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

                    if(copyReference[rowIterator][colIterator].getPiece() != null){
                        return false;
                    }

                    rowIterator--;
                    colIterator--;
                }

                if(copyReference[toRow][toCol].getPiece() == null){
                    return true;
                }else{
                    if(copyReference[toRow][toCol].getPiece().getSide() == side){
                        return false;
                    }else{
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void possibleKingMovementsCopy(King king, char side, int row, int col, Cell[][] copyReference){
        int[] possibleRows = {row + 1, row + 1, row + 1, row - 1, row - 1, row - 1, row, row};
        int[] possibleCols = {col, col + 1, col - 1, col, col + 1, col - 1, col + 1, col - 1};

        for(int i=0; i<possibleRows.length; i++){
            for(int j=0; j<possibleCols.length; j++){
                if(isValidCoordinate(possibleRows[i], possibleCols[j])){
                    if(isValidKingMoveCopy(king, side, row, col, possibleRows[i], possibleCols[j], copyReference)){
                        if(side == 'W'){
                            this.whiteAttackingSquares[possibleRows[i]][possibleCols[j]] = 1;
                        }else{
                            this.blackAttackingSquares[possibleRows[i]][possibleCols[j]] = 1;
                        }
                    }
                }
            }
        }
    }

    private boolean isValidKingMoveCopy(King king, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] copyReference){
        if(!king.isMoved()){
            if(fromRow == toRow){
                if(toCol - fromCol == 2){
                    if(side == 'W'){
                        if(copyReference[fromRow][toCol].getPiece() == null && copyReference[fromRow][toCol-1].getPiece() == null && this.blackAttackingSquares[fromRow][toCol] == 0 && this.blackAttackingSquares[fromRow][toCol-1] == 0){
                            if(copyReference[fromRow][toCol+1].getPiece() instanceof Rook){
                                Rook rightSideRook = (Rook) copyReference[fromRow][toCol+1].getPiece();
                                if(!rightSideRook.isMoved()){
                                    king.setMoved(true);
                                    this.castleFromCol = toCol + 1;
                                    this.castleFromRow = fromRow;
                                    this.castleToCol = toCol - 1;
                                    this.castleToRow = fromRow;
                                    this.castle = true;
                                    this.whiteKingRow = toRow;
                                    this.whiteKingCol = toCol;
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    }else{
                        if(copyReference[fromRow][toCol].getPiece() == null && copyReference[fromRow][toCol-1].getPiece() == null && this.whiteAttackingSquares[fromRow][toCol] == 0 && this.whiteAttackingSquares[fromRow][toCol-1] == 0){
                            if(copyReference[fromRow][toCol+1].getPiece() instanceof Rook){
                                Rook rightSideRook = (Rook) copyReference[fromRow][toCol+1].getPiece();
                                if(!rightSideRook.isMoved()){
                                    king.setMoved(true);
                                    this.castleFromCol = toCol + 1;
                                    this.castleFromRow = fromRow;
                                    this.castleToCol = toCol - 1;
                                    this.castleToRow = fromRow;
                                    this.castle = true;
                                    this.blackKingRow = toRow;
                                    this.blackKingCol = toCol;
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    }
                }else if(fromCol - toCol == 2){
                    if(side == 'W'){
                        if(copyReference[fromRow][toCol].getPiece() == null && copyReference[fromRow][toCol+1].getPiece() == null && copyReference[fromRow][toCol-1].getPiece() == null && this.blackAttackingSquares[fromRow][toCol] == 0 && this.blackAttackingSquares[fromRow][toCol+1] == 0 && this.blackAttackingSquares[fromRow][toCol-1] == 0){
                            if(copyReference[fromRow][toCol-2].getPiece() instanceof Rook){
                                Rook leftSideRook = (Rook) copyReference[fromRow][toCol-2].getPiece();
                                if(!leftSideRook.isMoved()){
                                    king.setMoved(true);
                                    this.castleFromCol = toCol - 2;
                                    this.castleFromRow = fromRow;
                                    this.castleToCol = toCol + 1;
                                    this.castleToRow = fromRow;
                                    this.castle = true;
                                    this.whiteKingRow = toRow;
                                    this.whiteKingCol = toCol;
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                    }else{
                        if(copyReference[fromRow][toCol].getPiece() == null && copyReference[fromRow][toCol+1].getPiece() == null && copyReference[fromRow][toCol-1].getPiece() == null && this.whiteAttackingSquares[fromRow][toCol] == 0 && this.whiteAttackingSquares[fromRow][toCol+1] == 0 && this.whiteAttackingSquares[fromRow][toCol-1] == 0){
                            if(copyReference[fromRow][toCol-2].getPiece() instanceof Rook){
                                Rook leftSideRook = (Rook) copyReference[fromRow][toCol-2].getPiece();
                                if(!leftSideRook.isMoved()){
                                    king.setMoved(true);
                                    this.castleFromCol = toCol - 2;
                                    this.castleFromRow = fromRow;
                                    this.castleToCol = toCol + 1;
                                    this.castleToRow = fromRow;
                                    this.castle = true;
                                    this.blackKingRow = toRow;
                                    this.blackKingCol = toCol;
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

        if(toCol - fromCol > 1 || fromCol - toCol > 1 || toRow - fromRow > 1 || fromRow - toRow > 1){
            return false;
        }else if(copyReference[toRow][toCol].getPiece() == null){
            if(side == 'W'){
                if(this.blackAttackingSquares[toRow][toCol] == 0){
                    if(!king.isMoved()){
                        king.setMoved(true);
                    }
                    this.whiteKingRow = toRow;
                    this.whiteKingCol = toCol;
                    return true;
                }else{
                    return false;
                }
            }else{
                if(this.whiteAttackingSquares[toRow][toCol] == 0){
                    if(!king.isMoved()){
                        king.setMoved(true);
                    }
                    this.blackKingRow = toRow;
                    this.blackKingCol = toCol;
                    return true;
                }else{
                    return false;
                }
            }
        }else if(copyReference[toRow][toCol].getPiece().getSide() == side){
            return false;
        }else{
            if(!king.isMoved()){
                king.setMoved(true);
            }

            if(side == 'W'){
                this.whiteKingRow = toRow;
                this.whiteKingCol = toCol;
            }else{
                this.blackKingRow = toRow;
                this.blackKingCol = toCol;
            }

            return true;
        }
    }

}
