import java.util.ArrayList;
import java.util.Random;

public class Board {
    private Cell[][] cells;
    private int[][] whiteAttackingSquares;
    private int[][] blackAttackingSquares;
    private int[][] potentialMovesForSelectedPiece;
    private boolean enPassant;
    private int enPassantRow;
    private int enPassantCol;
    private PawnPossibilityState currentPawnPossibilityState;
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
    private Turn currentTurn;
    private AISide AIOpponentSide;
    private boolean AIMated;
    private ArrayList<PossibleMove> possibleAIMoves;

    public Board() {
        cells = new Cell[8][8];
        this.whiteAttackingSquares = new int[8][8];
        this.blackAttackingSquares = new int[8][8];
        this.potentialMovesForSelectedPiece = new int[8][8];
        initializeBoard();
        this.currentTurn = Turn.WHITE;
        this.currentPawnPossibilityState = PawnPossibilityState.MOVING;
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
        this.possibleAIMoves = new ArrayList<>();
        this.AIOpponentSide = AISide.BLACK;
        this.AIMated = false;
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

    public boolean isGameRunning() {
        return gameRunning;
    }

    public boolean isWhiteKingMated() {
        return whiteKingMated;
    }

    public boolean isBlackKingMated() {
        return blackKingMated;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public Turn getCurrentTurn() {
        return this.currentTurn;
    }

    public void setEnPassant(boolean enPassant) {
        this.enPassant = enPassant;
    }

    public void setEnPassantRow(int enPassantRow) {
        this.enPassantRow = enPassantRow;
    }

    public void setEnPassantCol(int enPassantCol) {
        this.enPassantCol = enPassantCol;
    }

    public void setCastle(boolean castle) {
        this.castle = castle;
    }

    public void setCastleFromRow(int castleFromRow) {
        this.castleFromRow = castleFromRow;
    }

    public void setCastleFromCol(int castleFromCol) {
        this.castleFromCol = castleFromCol;
    }

    public void setCastleToRow(int castleToRow) {
        this.castleToRow = castleToRow;
    }

    public void setCastleToCol(int castleToCol) {
        this.castleToCol = castleToCol;
    }

    public int getWhiteKingRow() {
        return whiteKingRow;
    }

    public void setWhiteKingRow(int whiteKingRow) {
        this.whiteKingRow = whiteKingRow;
    }

    public int getWhiteKingCol() {
        return whiteKingCol;
    }

    public void setWhiteKingCol(int whiteKingCol) {
        this.whiteKingCol = whiteKingCol;
    }

    public int getBlackKingRow() {
        return blackKingRow;
    }

    public void setBlackKingRow(int blackKingRow) {
        this.blackKingRow = blackKingRow;
    }
    
    public int getBlackKingCol() {
        return blackKingCol;
    }

    public void setBlackKingCol(int blackKingCol) {
        this.blackKingCol = blackKingCol;
    }

    public void setAIOpponentSide(AISide AIOpponentSide) {
        this.AIOpponentSide = AIOpponentSide;
    }

    public boolean getAIMated() {
        return this.AIMated;
    }

    public void calculateAIMove(){
        this.possibleAIMoves.clear();

        Cell[][] boardStateReference = copyBoardState(this.cells);

        char side;

        if(this.AIOpponentSide == AISide.WHITE){
            side = 'W';
        }else{
            side = 'B';
        }

        int[][] opposingAttacks;
        int[][] currentSideAttacks;

        if(side == 'W'){
            opposingAttacks = copyAttackState(this.blackAttackingSquares);
            currentSideAttacks = copyAttackState(this.whiteAttackingSquares);
        }else{
            opposingAttacks = copyAttackState(this.whiteAttackingSquares);
            currentSideAttacks = copyAttackState(this.blackAttackingSquares);
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
                                        this.possibleAIMoves.add(new PossibleMove(i, j, k, l));
                                    }
                                }else{
                                    if(this.blackAttackingSquares[k][l] == 1){
                                        this.possibleAIMoves.add(new PossibleMove(i, j, k, l));
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

        int upperBound = this.possibleAIMoves.size();

        /* boolean checked = false;
        int AIKingRow;
        int AIKingCol;

        if(this.AIOpponentSide == AISide.WHITE){
            if(this.whiteKingChecked == true){
                checked = true;
                AIKingRow = this.whiteKingRow;
                AIKingCol = this.whiteKingCol;
            }
        }else{
            if(this.blackKingChecked == true){
                checked = true;
                AIKingRow = this.blackKingRow;
                AIKingCol = this.blackKingCol;
            }
        }

        boolean mate = false;
        Piece AIKing = this.cells[AIKingRow][AIKingCol].getPiece();
        checkIfAnyKingAttacked(AIKing, side, boardStateReference); */
        
        boolean validMove = false;
        
        while(!validMove){
            Random random = new Random();
            int randomIndex = random.nextInt(upperBound);
    
            int fromRow = this.possibleAIMoves.get(randomIndex).getFromRow();
            int fromCol = this.possibleAIMoves.get(randomIndex).getFromCol();
            int toRow = this.possibleAIMoves.get(randomIndex).getToRow();
            int toCol = this.possibleAIMoves.get(randomIndex).getToCol();
    
            validMove = move(fromRow, fromCol, toRow, toCol);
        }

        if(currentTurn == Turn.WHITE){
            currentTurn = Turn.BLACK;
        }else{
            currentTurn = Turn.WHITE;
        }

    }


    public boolean sendMove(int fromRow, int fromCol, int toRow, int toCol){
        boolean validMove = false;

        validMove = move(fromRow, fromCol, toRow, toCol);

        if(validMove){
            if(currentTurn == Turn.WHITE){
                //printCurrentAtackGrid('W');
                currentTurn = Turn.BLACK;
            }else{
                //printCurrentAtackGrid('B');
                currentTurn = Turn.WHITE;
            }

            return true;
        }else{
            return false;
        }
    }

    public void sendPromotionSignal(int toRow, int toCol, int choice){
        Cell toCell = this.cells[toRow][toCol];
        Piece piece = toCell.getPiece();

        handlePromotion(toCell, piece.getSide(), choice);

        this.currentPawnPossibilityState = PawnPossibilityState.POSSIBLE;
        createAttackingGrid(piece.getSide(), this.cells);
        this.currentPawnPossibilityState = PawnPossibilityState.MOVING;

        checkIfAnyKingAttacked(piece, piece.getSide(), cells);
    }

    public int[][] getPossibleMoves(int row, int col){
        Piece selectedPiece = this.cells[row][col].getPiece();

        int[][] whiteAttacksBackUp = copyAttackState(this.whiteAttackingSquares);
        int[][] blackAttacksBackUp = copyAttackState(this.blackAttackingSquares);

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

            this.currentPawnPossibilityState = PawnPossibilityState.POSSIBLE;
            createAttackingGrid(piece.getSide(), this.cells);
            this.currentPawnPossibilityState = PawnPossibilityState.MOVING;

            System.out.println("Move successful!");

            checkIfAnyKingAttacked(piece, piece.getSide(), cells);

            return true;
        }else{
            System.out.println("Invalid move for the selected piece.");
            return false;
        }
    }

    private void checkIfAnyKingAttacked(Piece piece, char side, Cell[][] boardStateReference){
        if(side == 'W'){
            if(this.whiteAttackingSquares[this.blackKingRow][this.blackKingCol] == 1){
                this.blackKingChecked = true;
                System.out.println("Black king checked!");

                if(!anyMovesLeft('B', boardStateReference)){
                    this.blackKingMated = true;
                    System.out.println("Check mate!");
                    if(this.AIOpponentSide == AISide.BLACK){
                        this.AIMated = true;
                    }
                }
            }
        }else{
            if(this.blackAttackingSquares[this.whiteKingRow][this.whiteKingCol] == 1){
                this.whiteKingChecked = true;
                System.out.println("White king checked!");

                if(!anyMovesLeft('W', boardStateReference)){
                    this.whiteKingMated = true;
                    System.out.println("Check mate!");
                    if(this.AIOpponentSide == AISide.WHITE){
                        this.AIMated = true;
                    }
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
    }

    private boolean isValidMove(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, Cell[][] boardStateReference) {
        if(piece instanceof Pawn){
            Pawn pawn = (Pawn) piece;

            if(pawn.isValidPawnMove(pawn, side, fromRow, fromCol, toRow, toCol, boardStateReference, this.currentPawnPossibilityState, this)){
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
            Rook aux = new Rook(side);
            aux.isValidRookMove(piece, side, fromRow, fromCol, toRow, toCol, boardStateReference);

            return aux.isValidRookMove(piece, side, fromRow, fromCol, toRow, toCol, boardStateReference);
        }

        if(piece instanceof Knight){
            Knight knight = (Knight) piece;

            return knight.isValidKnightMove(knight, side, fromRow, fromCol, toRow, toCol, boardStateReference);
        }

        if(piece instanceof Bishop){
            Bishop bishop = (Bishop) piece;

            return bishop.isValidBishopMove(piece, side, fromRow, fromCol, toRow, toCol, boardStateReference);
        }

        if(piece instanceof Queen){
            Rook auxRook = new Rook(side);
            Bishop auxBishop = new Bishop(side);

            return auxRook.isValidRookMove(piece, side, fromRow, fromCol, toRow, toCol, boardStateReference) || 
            auxBishop.isValidBishopMove(piece, side, fromRow, fromCol, toRow, toCol, boardStateReference);
        }

        if(piece instanceof King){
            King king = (King) piece;

            return king.isValidKingMove(king, side, fromRow, fromCol, toRow, toCol, boardStateReference, this.whiteAttackingSquares, this.blackAttackingSquares, this);
        }
        
        return false;
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

    private void handlePromotion(Cell cell, char side, int choice){
        System.out.print((side == 'W') ? "White" : "Black");
        System.out.print(" Pawn was promoted ");

        switch(choice){
            case 1:
                cell.setPiece(new Queen(side));
                System.out.println(" Pawn was promoted to Queen!");
                break;
            case 2:
                cell.setPiece(new Rook(side));
                System.out.println(" Pawn was promoted to Rook!");
                break;
            case 3:
                cell.setPiece(new Bishop(side));
                System.out.println(" Pawn was promoted to Bishop!");
                break;
            case 4:
                cell.setPiece(new Knight(side));
                System.out.println(" Pawn was promoted to Knight!");
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

            pawn.possiblePawnMovements(pawn, side, row, col, boardStateReference, this.currentPawnPossibilityState, this, this.whiteAttackingSquares, this.blackAttackingSquares);

            pawn.setMoved(currentMovedState);
            pawn.setDoubleMoveLastTurn(currentDoubleMoveLastTurnState);
            this.enPassant = currentEnPassantState;
            return;
        }

        if(piece instanceof Rook){
            Rook rook = (Rook) piece;
            boolean currentMovedState = rook.isMoved();

            rook.possibleRookMovements(piece, side, row, col, boardStateReference, this.whiteAttackingSquares, this.blackAttackingSquares);

            rook.setMoved(currentMovedState);
            return;
        }

        if(piece instanceof Knight){
            Knight knight = (Knight) piece;
            knight.possibleKnightMovements(knight, side, row, col, boardStateReference, this.whiteAttackingSquares, this.blackAttackingSquares);
            return;
        }

        if(piece instanceof Bishop){
            Bishop bishop = (Bishop) piece;

            bishop.possibleBishopMovements(piece, side, row, col, boardStateReference, this.whiteAttackingSquares, this.blackAttackingSquares);
            return;
        }

        if(piece instanceof Queen){
            Rook auxRook = new Rook(side);
            Bishop auxBishop = new Bishop(side);

            auxRook.possibleRookMovements(piece, side, row, col, boardStateReference, this.whiteAttackingSquares, this.blackAttackingSquares);
            auxBishop.possibleBishopMovements(piece, side, row, col, boardStateReference, this.whiteAttackingSquares, this.blackAttackingSquares);
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

            king.possibleKingMovements(king, side, row, col, (this.whiteKingChecked || this.blackKingChecked), 
            boardStateReference, this.whiteAttackingSquares, this.blackAttackingSquares, this);

            king.setMoved(currentMovedState);
            this.castle = currentCastleState;
            this.whiteKingRow = currentWhiteKingRow;
            this.whiteKingCol = currentWhiteKingCol;
            this.blackKingRow = currentBlackKingRow;
            this.blackKingCol = currentBlackKingCol;
            return;
        }
    }

    private boolean anyMovesLeft(char side, Cell[][] boardStateReference){
        int[][] opposingAttacks;
        int[][] currentSideAttacks;

        int currentKingRow;
        int currentKingCol;

        if(side == 'W'){
            opposingAttacks = copyAttackState(this.blackAttackingSquares);
            currentSideAttacks = copyAttackState(this.whiteAttackingSquares);
            currentKingRow = this.whiteKingRow;
            currentKingCol = this.whiteKingCol;
        }else{
            opposingAttacks = copyAttackState(this.whiteAttackingSquares);
            currentSideAttacks = copyAttackState(this.blackAttackingSquares);
            currentKingRow = this.blackKingRow;
            currentKingCol = this.blackKingCol;
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

                                        if(boardStateReference[i][j].getPiece() instanceof King){
                                            currentKingRow = k;
                                            currentKingCol = l;
                                        }

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



                                        if(this.blackAttackingSquares[currentKingRow][currentKingCol] == 0){
                                            this.whiteAttackingSquares = currentSideAttacks;
                                            this.blackAttackingSquares = opposingAttacks;
                                            return true;
                                        }

                                        currentKingRow = this.whiteKingRow;
                                        currentKingCol = this.whiteKingCol;
                                    }
                                }else{
                                    if(this.blackAttackingSquares[k][l] == 1){
                                        
                                        Cell[][] copyState = copyBoardState(cells);

                                        Cell possibleFromCell = copyState[i][j];
                                        Cell possibleToCell = copyState[k][l];

                                        Piece currentPiece = possibleFromCell.getPiece();

                                        possibleToCell.setPiece(currentPiece);
                                        possibleFromCell.setPiece(null);

                                        if(boardStateReference[i][j].getPiece() instanceof King){
                                            currentKingRow = k;
                                            currentKingCol = l;
                                        }

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

                                        if(this.whiteAttackingSquares[currentKingRow][currentKingCol] == 0){
                                            this.whiteAttackingSquares = opposingAttacks;
                                            this.blackAttackingSquares = currentSideAttacks;
                                            return true;
                                        }

                                        currentKingRow = this.blackKingRow;
                                        currentKingCol = this.blackKingCol;
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

    public boolean moveGetsOutofCheck(Piece piece, char side, int fromRow, int fromCol, int toRow, int toCol, int previousKingRow, int previousKingCol, Cell[][] boardStateReference){
        boolean result = false;

        int[][] whiteAttacksBackUp = copyAttackState(this.whiteAttackingSquares);
        int[][] blackAttacksBackUp = copyAttackState(this.blackAttackingSquares);

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

    private int[][] copyAttackState(int[][] originalAttackGrid){
        int[][] copyAttackState = new int[8][8];
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                copyAttackState[i][j] = originalAttackGrid[i][j];
            }
        }
        return copyAttackState;
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