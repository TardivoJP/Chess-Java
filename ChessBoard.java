import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessBoard extends JFrame {
    private JPanel mainPanel;
    private JPanel[][] cellPanels;
    private Cell[][] boardState;
    private boolean validFromClick = false;
    private int fromRow, fromCol;

    public static Board board = new Board();

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
                cellPanel.setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GREEN);
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
        int logicRow = 7 - row;

        if(!validFromClick){
            //If it's the first click, check if the cell has a piece
            JLabel cellLabel = (JLabel) cellPanels[row][col].getComponent(0);
            if(!cellLabel.getText().equals("X")) {
                validFromClick = true;
                fromRow = logicRow;
                fromCol = col;
            }
        }else{
            //If it's the second click, call the sendMove method from the Board class
            boolean isValidMove = board.sendMove(fromRow, fromCol, logicRow, col);

            if(isValidMove){
                updateBoard();
            }else{
                JOptionPane.showMessageDialog(this, "Invalid move", "Error", JOptionPane.ERROR_MESSAGE);
            }

            //Reset the variables for the next move
            validFromClick = false;
            fromRow = fromCol = -1;
        }
    }

    public void updateBoard(){
        boardState = board.getCurrentBoardState();

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                int logicRow = 7 - i;

                String pieceLabel = boardState[logicRow][j].getPiece() == null ? "X" : boardState[logicRow][j].getPiece().getLabel();

                JLabel label = new JLabel(pieceLabel, SwingConstants.CENTER);
                cellPanels[i][j].removeAll();
                cellPanels[i][j].add(label);
            }
        }

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessBoard());
    }
}
