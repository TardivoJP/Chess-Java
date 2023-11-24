import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessBoard extends JFrame {
    private JPanel mainPanel;
    private JPanel[][] cellPanels;
    private Cell[][] boardState;
    private boolean validFromClick = false;
    private int fromRow, fromCol;
    private JPanel lastClickedCell;
    private int[][] potentialMovesForPiece;
    private boolean promotionHandled = true;

    private static Board board = new Board();

    public ChessBoard(){
        setTitle("Chess Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(9, 9));

        cellPanels = new JPanel[8][8];

        for(int i=8; i>0; i--){
            mainPanel.add(new JLabel(Integer.toString(i), SwingConstants.CENTER));
            for(int j=0; j<8; j++){
                JPanel cellPanel = new JPanel();
                cellPanel.setBackground((i + j) % 2 == 0 ? Color.decode("#eeeed2") : Color.decode("#769656"));
                mainPanel.add(cellPanel);

                cellPanels[i - 1][j] = cellPanel;

                int row = i - 1;
                int col = j;
                cellPanel.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent e){
                        System.out.println("Clicked on cell: " + (row + 1) + ", " + (char) ('A' + col));
                        handleCellClick(row, col);   
                    }
                });
            }
        }

        mainPanel.add(new JLabel(""));

        for(char c='A'; c<='H'; c++){
            mainPanel.add(new JLabel(Character.toString(c), SwingConstants.CENTER));
        }

        add(mainPanel);
        setVisible(true);
        updateBoard();
    }

    private void handleCellClick(int row, int col){
        if(!promotionHandled){
            JOptionPane.showMessageDialog(this, "Please choose a piece to promote to!", "Error", JOptionPane.ERROR_MESSAGE);
        }else{
            //Convert ChessBoard row to Board row
            int logicRow = 7 - row;

            if(!validFromClick){
                //If it's the first click, check if the cell has a piece
                JLabel cellLabel = (JLabel) cellPanels[row][col].getComponent(0);
                if(cellLabel.getIcon() != null){
                    validFromClick = true;

                    fromRow = logicRow;
                    fromCol = col;

                    potentialMovesForPiece = board.getPossibleMoves(fromRow, fromCol);
                    highlightPossibleMoves();
                }

                //Set the border for the last clicked cell to null to remove the previous border
                if(lastClickedCell != null){
                    lastClickedCell.setBorder(null);
                }

                //Set the blue border for the current cell
                cellPanels[row][col].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

                //Update the last clicked cell reference
                lastClickedCell = cellPanels[row][col];
                
            }else{
                //If it's the second click, call the sendMove method from the Board class
                boolean isValidMove = board.sendMove(fromRow, fromCol, logicRow, col);

                if(isValidMove){
                    updateBoard();

                    if(board.isPromotion()){
                        promotionHandled = false;
                        openPromotionWindow(logicRow, col);
                    }

                }else{
                    JOptionPane.showMessageDialog(this, "Invalid move", "Error", JOptionPane.ERROR_MESSAGE);
                }

                //Reset the variables for the next move
                validFromClick = false;
                fromRow = fromCol = -1;

                //Set the border for the last clicked cell to null to remove the previous border
                if(lastClickedCell != null){
                    lastClickedCell.setBorder(null);
                }

                removePossibleMovesHighlights();
            }
        }
    }

    //Method to highlight cells with green border based on possible moves
    private void highlightPossibleMoves() {
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                //Convert ChessBoard row to Board row
                int logicRow = 7 - i;

                //Check if the cell is a valid move
                if (potentialMovesForPiece[logicRow][j] == 1) {
                    cellPanels[i][j].setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                }
            }
        }
    }

    //Method to remove the green borders from the highlighted cells
    private void removePossibleMovesHighlights() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cellPanels[i][j].setBorder(null);
            }
        }
    }

    //Method to open a window with promotion choices
    private void openPromotionWindow(int toRow, int toCol) {
        // Create a new JFrame for the promotion window
        JFrame promotionFrame = new JFrame("Pawn Promotion");
        promotionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        promotionFrame.setSize(300, 150);
        promotionFrame.setLayout(new GridLayout(1, 4));
        
        String[] whitePiecelabels = {"WQ", "WR", "WB", "WN"};
        String[] blackPiecelabels = {"BQ", "BR", "BB", "BN"};

        //Create buttons for each promotion choice
        for (int i = 1; i <= 4; i++) {
            int choice = i;

            JButton promotionButton  = new JButton();

            String imagePath;
            if(board.getCurrentTurn() == Turn.BLACK){
                imagePath = "images/" + whitePiecelabels[i-1] + ".png";
            }else{
                imagePath = "images/" + blackPiecelabels[i-1] + ".png";
            }

            ImageIcon icon = new ImageIcon(imagePath);
            icon = new ImageIcon(icon.getImage().getScaledInstance(cellPanels[0][0].getWidth(), cellPanels[0][0].getHeight(), Image.SCALE_SMOOTH));
            promotionButton.setIcon(icon);

            //Add ActionListener to handle button click
            promotionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //Call the sendPromotionSignal method from the Board class
                    board.sendPromotionSignal(toRow, toCol, choice);

                    //Update the board after promotion
                    updateBoard();

                    //Update promotion handled flag to continue the game
                    promotionHandled = true;

                    //Close the promotion window
                    ((JButton) e.getSource()).getRootPane().getParent().setVisible(false);
                }
            });

            promotionFrame.add(promotionButton);
        }

        //Set the frame to be visible
        promotionFrame.setVisible(true);
    }

    //Method to check for game over and display the result
    private void checkGameOver() {
        if (!board.isGameRunning()) {
            String winner;
            String imagePath;

            if (board.isWhiteKingMated()) {
                winner = "Black";
                imagePath = "images/BQ.png";
            } else if (board.isBlackKingMated()) {
                winner = "White";
                imagePath = "images/WQ.png";
            } else {
                //The game is not over
                return;
            }

            //Create a new JFrame to display the result
            JFrame resultFrame = new JFrame("Game Over");
            resultFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            resultFrame.setSize(300, 200);

            JPanel panel = new JPanel(new BorderLayout());

            //Add text message
            JLabel messageLabel = new JLabel(winner + " side wins!");
            messageLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(messageLabel, BorderLayout.CENTER);

            //Add images of the respective queen and king
            ImageIcon queenIcon = new ImageIcon(imagePath);
            queenIcon = new ImageIcon(queenIcon.getImage().getScaledInstance(cellPanels[0][0].getWidth(), cellPanels[0][0].getHeight(), Image.SCALE_SMOOTH));
            ImageIcon kingIcon = new ImageIcon(imagePath.replace("Q", "K"));
            kingIcon = new ImageIcon(kingIcon.getImage().getScaledInstance(cellPanels[0][0].getWidth(), cellPanels[0][0].getHeight(), Image.SCALE_SMOOTH));

            JLabel queenLabel = new JLabel(queenIcon);
            JLabel kingLabel = new JLabel(kingIcon);

            panel.add(queenLabel, BorderLayout.WEST);
            panel.add(kingLabel, BorderLayout.EAST);

            //Add the panel to the frame
            resultFrame.add(panel);

            //Set the frame to be visible
            resultFrame.setVisible(true);
        }
    }

    public void updateBoard(){
        boardState = board.getCurrentBoardState();

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                //Convert ChessBoard row to Board row
                int logicRow = 7 - i;

                Piece piece = boardState[logicRow][j].getPiece();

                //Create a label with the appropriate image
                JLabel label = new JLabel();
                if (piece != null) {
                    String pieceLabel = piece.getLabel();
                    ImageIcon icon = new ImageIcon("images/" + pieceLabel + ".png");
                    //Set the size of the ImageIcon to match the cell size
                    icon = new ImageIcon(icon.getImage().getScaledInstance(cellPanels[i][j].getWidth(), cellPanels[i][j].getHeight(), Image.SCALE_SMOOTH));
                    label.setIcon(icon);
                }

                cellPanels[i][j].removeAll();
                cellPanels[i][j].add(label);
            }
        }
        revalidate();
        repaint();

        //Check for game over and display the result
        checkGameOver();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessBoard());
    }
}
