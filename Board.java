import java.util.Scanner;

public class Board {
    private Cell[][] cells;
    private int[][] whiteAttackingSquares;
    private int[][] blackAttackingSquares;
    private int[][] potentialMovesForSelectedPiece;
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
    private enum Turn {WHITE, BLACK}
    private Turn currentTurn;

    public Board() {
        cells = new Cell[8][8];
        this.whiteAttackingSquares = new int[8][8];
        this.blackAttackingSquares = new int[8][8];
        this.potentialMovesForSelectedPiece = new int[8][8];
        initializeBoard();
        currentTurn = Turn.WHITE;
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
        //gameLoop();
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j] = new Cell();
                whiteAttackingSquares[i][j] = 0;
                blackAttackingSquares[i][j] = 0;
                potentialMovesForSelectedPiece[i][j] = 0;
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

    public Cell[][] getCurrentBoardState() {
        return cells;
    }

    public boolean sendMove(int fromRow, int fromCol, int toRow, int toCol){
        boolean validMove = false;

        validMove = move(fromRow, fromCol, toRow, toCol);

        if(validMove){
            if(currentTurn == Turn.WHITE){
                currentTurn = Turn.BLACK;
            }else{
                currentTurn = Turn.WHITE;
            }

            return true;
        }else{
            return false;
        }
    }

    public int[][] getPossibleMoves(int row, int col){
        Piece selectedPiece = this.cells[row][col].getPiece();

        int[][] whiteAttacksBackUp = this.whiteAttackingSquares;
        int[][] blackAttacksBackUp = this.blackAttackingSquares;

        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                this.potentialMovesForSelectedPiece[i][j] = 0;
            }
        }

        if(selectedPiece.getSide() == 'W'){
            if(this.currentTurn == Turn.WHITE){
                clearAttackingGrid('W');
                checkPossibleMovements(selectedPiece, selectedPiece.getSide(), row, col, this.cells);
                this.potentialMovesForSelectedPiece = this.whiteAttackingSquares;
            }
        }else{
            if(this.currentTurn == Turn.BLACK){
                clearAttackingGrid('B');
                checkPossibleMovements(selectedPiece, selectedPiece.getSide(), row, col, this.cells);
                this.potentialMovesForSelectedPiece = this.blackAttackingSquares;
            }
        }

        this.whiteAttackingSquares = whiteAttacksBackUp;
        this.blackAttackingSquares = blackAttacksBackUp;

        return this.potentialMovesForSelectedPiece;
    }

    private void gameLoop(){
        Scanner scanner = new Scanner(System.in);

        while(this.gameRunning){
            printCurrentBoard();

            if(currentTurn == Turn.WHITE){
                System.out.println("White to move.");
            }else{
                System.out.println("Black to move.");
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

            if(currentTurn == Turn.WHITE){
                currentTurn = Turn.BLACK;
            }else{
                currentTurn = Turn.WHITE;
            }
        }
        
        scanner.close();
    }

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidCoordinate(fromRow, fromCol) || !isValidCoordinate(toRow, toCol) || (fromRow == toRow && fromCol == toCol)) {
            System.out.println("Invalid coordinates. Please provide valid coordinates.");
            return false;
        }

        Cell fromCell = this.cells[fromRow][fromCol];
        Cell toCell = this.cells[toRow][toCol];

        if (fromCell.getPiece() == null) {
            System.out.println("There is no piece in the selected cell.");
            return false;
        }

        Piece piece = fromCell.getPiece();

        if(currentTurn == Turn.WHITE && piece.getSide() == 'B'){
            System.out.println("Invalid piece, this is white's turn.");
            return false;
        }else if(currentTurn == Turn.BLACK && piece.getSide() == 'W'){
            System.out.println("Invalid piece, this is black's turn.");
            return false;
        }

        int currentWhiteKingRow = this.whiteKingRow;
        int currentWhiteKingCol = this.whiteKingCol;
        int currentBlackKingRow = this.blackKingRow;
        int currentBlackKingCol = this.blackKingCol;

        if(isValidMove(piece, piece.getSide(), fromRow, fromCol, toRow, toCol, this.cells)){
            if(currentTurn == Turn.BLACK && this.blackKingChecked){
                if(!moveGetsOutofCheck(piece, piece.getSide(), fromRow, fromCol, toRow, toCol, currentBlackKingRow, currentBlackKingCol, this.cells)){
                    this.blackKingRow = currentBlackKingRow;
                    this.blackKingCol = currentBlackKingCol;

                    System.out.println("Invalid move for the selected piece. Remember, you are in check!");
                    return false;
                }else{
                    this.blackKingChecked = false;
                }
            }else if(currentTurn == Turn.WHITE && this.whiteKingChecked){
                if(!moveGetsOutofCheck(piece, piece.getSide(), fromRow, fromCol, toRow, toCol, currentWhiteKingRow, currentWhiteKingCol, this.cells)){
                    this.whiteKingRow = currentWhiteKingRow;
                    this.whiteKingCol = currentWhiteKingCol;

                    System.out.println("Invalid move for the selected piece. Remember, you are in check!");
                    return false;
                }else{
                    this.whiteKingChecked = false;
                }
            }

            toCell.setPiece(piece);
            fromCell.setPiece(null);

            if(enPassant){
                handleEnPassant(this.cells);
            }

            if(castle){
                handleCastling(this.cells);
            }

            if(promotion){
                handlePromotion(toCell, piece.getSide());
            }

            createAttackingGrid(piece.getSide(), this.cells);

            System.out.println("Move successful!");

            if(piece.getSide() == 'W'){
                if(this.whiteAttackingSquares[this.blackKingRow][this.blackKingCol] == 1){
                    this.blackKingChecked = true;
                    System.out.println("Black king checked!");

                    if(!anyMovesLeft('B', this.cells)){
                        this.blackKingMated = true;
                        System.out.println("Check mate!");
                    }
                }
            }else{
                if(this.blackAttackingSquares[this.whiteKingRow][this.whiteKingCol] == 1){
                    this.whiteKingChecked = true;
                    System.out.println("White king checked!");

                    if(!anyMovesLeft('W', this.cells)){
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
        }else{
            System.out.println("Invalid move for the selected piece.");
            return false;
        }
    }

    private boolean isValidMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference) {
        if(piece instanceof Pawn){
            Pawn pawn = (Pawn) piece;

            if(isValidPawnMove(pawn, side, fromRow, fromCol, toRow, toCol, boardStateReference)){
                if((pawn.getSide() == 'W' && toRow == 0) || (pawn.getSide() == 'B' && toRow == 7)){
                    this.promotion = true;
                }
                return true;
            }else{
                return false;
            }
        }

        if(piece instanceof Rook){
            if(fromRow != toRow && fromCol != toCol){
                return false;
            }

            return isValidRookMove(piece, side, fromRow, fromCol, toRow, toCol, boardStateReference);
        }

        if(piece instanceof Knight){
            Knight knight = (Knight) piece;

            return isValidKnightMove(knight, side, fromRow, fromCol, toRow, toCol, boardStateReference);
        }

        if(piece instanceof Bishop){
            return isValidBishopMove(piece, side, fromRow, fromCol, toRow, toCol, boardStateReference);
        }

        if(piece instanceof Queen){
            return isValidRookMove(piece, side, fromRow, fromCol, toRow, toCol, boardStateReference) || isValidBishopMove(piece, side, fromRow, fromCol, toRow, toCol, boardStateReference);
        }

        if(piece instanceof King){
            King king = (King) piece;

            return isValidKingMove(king, side, fromRow, fromCol, toRow, toCol, boardStateReference);
        }
        
        return false;
    }

    private boolean isValidPawnMove(Pawn pawn, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference){
        //Check for single move
        if((side == 'W' && fromRow - toRow == 1) || (side == 'B' && toRow - fromRow == 1)){
            //Check forward move
            if(toCol - fromCol == 0){
                //Check if there's a piece in the way in forward move
                if(boardStateReference[toRow][toCol].getPiece() == null){
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
                            this.enPassant = true;
                            if(side == 'W'){
                                this.enPassantRow = toRow + 1;
                            }else{
                                this.enPassantRow = toRow - 1;
                            }
                            this.enPassantCol = toCol;
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
            if(toCol - fromCol == 0){
                if(!pawn.isMoved()){
                    //Check if there's a piece in the way in double move
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

    private boolean isValidRookMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference){
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

    private boolean isValidKnightMove(Knight knight, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference){
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

    private boolean isValidBishopMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference){
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

    private boolean isValidKingMove(King king, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference){
        //Check for castling
        if(!king.isMoved()){
            //Check if the move is within the same row
            if(fromRow == toRow){
                //Check for King side castling
                if(toCol - fromCol == 2){
                    //Check King's color performing the castle move, in this case White
                    if(side == 'W'){
                        //Check if the squares in the way are empty and aren't being attacked by opposing pieces
                        if(boardStateReference[fromRow][toCol].getPiece() == null && boardStateReference[fromRow][toCol-1].getPiece() == null && this.blackAttackingSquares[fromRow][toCol] == 0 && this.blackAttackingSquares[fromRow][toCol-1] == 0){
                            //Check if the rightmost piece is actually a Rook
                            if(boardStateReference[fromRow][toCol+1].getPiece() instanceof Rook){
                                //Check if that rook hasn't moved yet
                                Rook rightSideRook = (Rook) boardStateReference[fromRow][toCol+1].getPiece();
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
                    //Check King's color performing the castle move, in this case Black
                    }else{
                        //Check if the squares in the way are empty and aren't being attacked by opposing pieces
                        if(boardStateReference[fromRow][toCol].getPiece() == null && boardStateReference[fromRow][toCol-1].getPiece() == null && this.whiteAttackingSquares[fromRow][toCol] == 0 && this.whiteAttackingSquares[fromRow][toCol-1] == 0){
                            //Check if the rightmost piece is actually a Rook
                            if(boardStateReference[fromRow][toCol+1].getPiece() instanceof Rook){
                                //Check if that rook hasn't moved yet
                                Rook rightSideRook = (Rook) boardStateReference[fromRow][toCol+1].getPiece();
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
                //Check for Queen side castling
                }else if(fromCol - toCol == 2){
                    //Check King's color performing the castle move, in this case White
                    if(side == 'W'){
                        //Check if the squares in the way are empty and aren't being attacked by opposing pieces, since this is Queen side, there's an additional square to check
                        if(boardStateReference[fromRow][toCol].getPiece() == null && boardStateReference[fromRow][toCol+1].getPiece() == null && boardStateReference[fromRow][toCol-1].getPiece() == null && this.blackAttackingSquares[fromRow][toCol] == 0 && this.blackAttackingSquares[fromRow][toCol+1] == 0 && this.blackAttackingSquares[fromRow][toCol-1] == 0){
                            //Check if the leftmost piece is actually a Rook
                            if(boardStateReference[fromRow][toCol-2].getPiece() instanceof Rook){
                                //Check if that rook hasn't moved yet
                                Rook leftSideRook = (Rook) boardStateReference[fromRow][toCol-2].getPiece();
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
                    //Check King's color performing the castle move, in this case Black
                    }else{
                        //Check if the squares in the way are empty and aren't being attacked by opposing pieces, since this is Queen side, there's an additional square to check
                        if(boardStateReference[fromRow][toCol].getPiece() == null && boardStateReference[fromRow][toCol+1].getPiece() == null && boardStateReference[fromRow][toCol-1].getPiece() == null && this.whiteAttackingSquares[fromRow][toCol] == 0 && this.whiteAttackingSquares[fromRow][toCol+1] == 0 && this.whiteAttackingSquares[fromRow][toCol-1] == 0){
                            //Check if the leftmost piece is actually a Rook
                            if(boardStateReference[fromRow][toCol-2].getPiece() instanceof Rook){
                                //Check if that rook hasn't moved yet
                                Rook leftSideRook = (Rook) boardStateReference[fromRow][toCol-2].getPiece();
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

        //Check for regular King moves
        //Check if King moved more than one square in any direction
        if(Math.abs(toCol - fromCol) > 1 || Math.abs(toRow - fromRow) > 1){
            return false;
        //Check if target square is empty
        }else if(boardStateReference[toRow][toCol].getPiece() == null){
            //Check King's color performing the move, in this case White
            if(side == 'W'){
                //Check if that square isn't attacked by an opposing piece
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
            //Check King's color performing the move, in this case Black
            }else{
                //Check if that square isn't attacked by an opposing piece
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
        //Check if square isn't occupied by friendly piece
        }else if(boardStateReference[toRow][toCol].getPiece().getSide() == side){
            return false;
        //Check edge case where King captures an opposing piece
        }else{
            int currentKingRow;
            int currentKingCol;

            if(side == 'W'){
                currentKingRow = this.whiteKingRow;
                currentKingCol = this.whiteKingCol;
            }else{
                currentKingRow = this.blackKingRow;
                currentKingCol = this.blackKingCol;
            }
            //If the capture is safe, then it's allowed and actually performed
            if(moveGetsOutofCheck(king, side, fromRow, fromCol, toRow, toCol, currentKingRow, currentKingCol, boardStateReference)){
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
            }else{
                return false;
            }
        }
    }

    private void handleEnPassant(Cell[][] boardStateReference){
        Cell capturedPawnCell = boardStateReference[this.enPassantRow][this.enPassantCol];
        capturedPawnCell.setPiece(null);
        this.enPassant = false;
    }

    private void handleCastling(Cell[][] boardStateReference){
        Cell castleFromCell = boardStateReference[this.castleFromRow][this.castleFromCol];
        Cell castleToCell = boardStateReference[this.castleToRow][this.castleToCol];
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

    private void clearAttackingGrid(char side){
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(side == 'W'){
                    this.whiteAttackingSquares[i][j] = 0;
                }else{
                    this.blackAttackingSquares[i][j] = 0;
                }
            }
        }
    }

    private void createAttackingGrid(char side, Cell[][] boardStateReference){
        clearAttackingGrid(side);
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(boardStateReference[i][j].getPiece() != null){
                    if(boardStateReference[i][j].getPiece().getSide() == side){
                        checkPossibleMovements(boardStateReference[i][j].getPiece(), side, i, j, boardStateReference);
                    }
                }
            }
        }
    }

    private void checkPossibleMovements(Piece piece, char side, int row, int col, Cell[][] boardStateReference){
        if(piece instanceof Pawn){
            Pawn pawn = (Pawn) piece;
            boolean currentEnPassantState = this.enPassant;
            boolean currentMovedState = pawn.isMoved();
            boolean currentDoubleMoveLastTurnState = pawn.isDoubleMoveLastTurn();

            possiblePawnMovements(pawn, side, row, col, boardStateReference);

            pawn.setMoved(currentMovedState);
            pawn.setDoubleMoveLastTurn(currentDoubleMoveLastTurnState);
            this.enPassant = currentEnPassantState;
            return;
        }

        if(piece instanceof Rook){
            Rook rook = (Rook) piece;
            boolean currentMovedState = rook.isMoved();

            possibleRookMovements(piece, side, row, col, boardStateReference);

            rook.setMoved(currentMovedState);
            return;
        }

        if(piece instanceof Knight){
            Knight knight = (Knight) piece;
            possibleKnightMovements(knight, side, row, col, boardStateReference);
            return;
        }

        if(piece instanceof Bishop){
            possibleBishopMovements(piece, side, row, col, boardStateReference);
            return;
        }

        if(piece instanceof Queen){
            possibleRookMovements(piece, side, row, col, boardStateReference);
            possibleBishopMovements(piece, side, row, col, boardStateReference);
            return;
        }

        if(piece instanceof King){
            King king = (King) piece;
            boolean currentCastleState = this.castle;
            boolean currentMovedState = king.isMoved();
            int currentWhiteKingRow = this.whiteKingRow;
            int currentWhiteKingCol = this.whiteKingCol;
            int currentBlackKingRow = this.blackKingRow;
            int currentBlackKingCol = this.blackKingCol;

            possibleKingMovements(king, side, row, col, (this.whiteKingChecked || this.blackKingChecked), boardStateReference);

            king.setMoved(currentMovedState);
            this.castle = currentCastleState;
            this.whiteKingRow = currentWhiteKingRow;
            this.whiteKingCol = currentWhiteKingCol;
            this.blackKingRow = currentBlackKingRow;
            this.blackKingCol = currentBlackKingCol;
            return;
        }
    }

    private void possiblePawnMovements(Pawn pawn, char side, int row, int col, Cell[][] boardStateReference){
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
            for(int j=start; j<possibleCols.length; j++){
                if(isValidCoordinate(possibleRows[i], possibleCols[j])){
                    if(isValidPawnMove(pawn, side, row, col, possibleRows[i], possibleCols[j], boardStateReference)){
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

    private void possibleRookMovements(Piece piece, char side, int row, int col, Cell[][] boardStateReference){
        for(int i=0; i<8; i++){
            if(i != row){
                if(isValidCoordinate(i, col)){
                    if(isValidRookMove(piece, side, row, col, i, col, boardStateReference)){
                        if(side == 'W'){
                            this.whiteAttackingSquares[i][col] = 1;
                        }else{
                            this.blackAttackingSquares[i][col] = 1;
                        }
                    }
                }
            }

            if(i != col){
                if(isValidCoordinate(row, i)){
                    if(isValidRookMove(piece, side, row, col, row, i, boardStateReference)){
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

    private void possibleKnightMovements(Knight knight, char side, int row, int col, Cell[][] boardStateReference){
        int[] possibleRows = {row + 2, row + 2, row - 2, row - 2, row + 1, row - 1, row + 1, row - 1};
        int[] possibleCols = {col + 1, col - 1, col + 1, col - 1, col + 2, col + 2, col - 2, col - 2};

        for(int i=0; i<possibleRows.length; i++){
            for(int j=0; j<possibleCols.length; j++){
                if(isValidCoordinate(possibleRows[i], possibleCols[j])){
                    if(isValidKnightMove(knight, side, row, col, possibleRows[i], possibleCols[j], boardStateReference)){
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

    private void possibleBishopMovements(Piece piece, char side, int row, int col, Cell[][] boardStateReference){
        int auxCol = col+1;

        for(int i=row+1; i<8; i++){
            if(isValidCoordinate(i, auxCol)){
                if(isValidBishopMove(piece, side, row, col, i, auxCol, boardStateReference)){
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
                if(isValidBishopMove(piece, side, row, col, i, auxCol, boardStateReference)){
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
                if(isValidBishopMove(piece, side, row, col, i, auxCol, boardStateReference)){
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
                if(isValidBishopMove(piece, side, row, col, i, auxCol, boardStateReference)){
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

    private void possibleKingMovements(King king, char side, int row, int col, boolean check, Cell[][] boardStateReference){
        int[] possibleRows = {row + 1, row + 1, row + 1, row - 1, row - 1, row - 1, row, row};
        int[] possibleCols = {col, col + 1, col - 1, col, col + 1, col - 1, col + 1, col - 1};

        if(!check){
            if(king.isMoved() == false){
                int delta = -2;
                for(int i=0; i<2; i++){
                    if(isValidKingMove(king, side, row, col, row, col+delta, boardStateReference)){
                        if(side == 'W'){
                            this.whiteAttackingSquares[row][col+delta] = 1;
                        }else{
                            this.blackAttackingSquares[row][col+delta] = 1;
                        }
                    }
                    delta += 4;
                }
            }
        }

        for(int i=0; i<possibleRows.length; i++){
            for(int j=0; j<possibleCols.length; j++){
                if(isValidCoordinate(possibleRows[i], possibleCols[j])){
                    if(isValidKingMove(king, side, row, col, possibleRows[i], possibleCols[j], boardStateReference)){
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

    private boolean anyMovesLeft(char side, Cell[][] boardStateReference){
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
                if(boardStateReference[i][j].getPiece() != null){
                    if(boardStateReference[i][j].getPiece().getSide() == side){
                        checkPossibleMovements(boardStateReference[i][j].getPiece(), side, i, j, boardStateReference);

                        for(int k=0; k<8; k++){
                            for(int l=0; l<8; l++){

                                if(side == 'W'){
                                    if(this.whiteAttackingSquares[k][l] == 1){

                                        Cell[][] copyState = copyBoardState(boardStateReference);

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
                                                        checkPossibleMovements(copyState[m][n].getPiece(), 'B', m, n, copyState);
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
                                                        checkPossibleMovements(copyState[m][n].getPiece(), 'W', m, n, copyState);
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

    private boolean moveGetsOutofCheck(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, int previousKingRow, int previousKingCol, Cell[][] boardStateReference){
        boolean result = false;

        int[][] whiteAttacksBackUp = this.whiteAttackingSquares;
        int[][] blackAttacksBackUp = this.blackAttackingSquares;

        Cell[][] copyState = copyBoardState(boardStateReference);

        Cell possibleFromCell = copyState[fromRow][fromCol];
        Cell possibleToCell = copyState[toRow][toCol];

        possibleToCell.setPiece(piece);
        possibleFromCell.setPiece(null);

        if(previousKingRow == fromRow && previousKingCol == fromCol){
            previousKingRow = toRow;
            previousKingCol = toCol;
        }

        if(side == 'W'){
            createAttackingGrid('B', copyState);
            if(this.blackAttackingSquares[previousKingRow][previousKingCol] == 0){
                result = true;
            }

        }else{
            createAttackingGrid('W', copyState);
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
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                copyState[i][j] = new Cell();
                if(originalBoard[i][j].getPiece() != null){
                    Piece originalPiece = originalBoard[i][j].getPiece();
                    char originalSide = originalPiece.getSide();
                    Piece copiedPiece;

                    // Check the type of the piece and create a new instance accordingly
                    if(originalPiece instanceof Pawn){
                        copiedPiece = new Pawn(originalSide);
                        ((Pawn)copiedPiece).setMoved(((Pawn)originalPiece).isMoved());
                        ((Pawn)copiedPiece).setDoubleMoveLastTurn(((Pawn) originalPiece).isDoubleMoveLastTurn());
                    }else if(originalPiece instanceof King){
                        copiedPiece = new King(originalSide);
                        ((King)copiedPiece).setMoved(((King)originalPiece).isMoved());
                    }else if(originalPiece instanceof Rook){
                        copiedPiece = new Rook(originalSide);
                        ((Rook)copiedPiece).setMoved(((Rook)originalPiece).isMoved());
                    }else if(originalPiece instanceof Bishop){
                        copiedPiece = new Bishop(originalSide);
                    }else if(originalPiece instanceof Knight){
                        copiedPiece = new Knight(originalSide);
                    }else if(originalPiece instanceof Queen){
                        copiedPiece = new Queen(originalSide);
                    }else{
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

    private boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}