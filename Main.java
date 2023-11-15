import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Board board = new Board();
        /* board.getCell(3, 2).setPiece(new Pawn('B'));
        board.getCell(4, 3).setPiece(new Pawn('W'));
        board.getCell(7, 1).setPiece(new Rook('W')); */

        board.getCell(1, 2).setPiece(new Pawn('W'));

        board.printCurrentBoard();

        Scanner scanner = new Scanner(System.in);
        
        for(int i=0;i<2;i++){
            // Asking the user to move the piece
            System.out.print("Enter the row to move from: ");
            int fromRow = scanner.nextInt();

            System.out.print("Enter the column to move from: ");
            int fromCol = scanner.nextInt();

            System.out.print("Enter the row to move to: ");
            int toRow = scanner.nextInt();

            System.out.print("Enter the column to move to: ");
            int toCol = scanner.nextInt();

            board.move(fromRow, fromCol, toRow, toCol);

            board.printCurrentBoard();
        }

        scanner.close();
    }
}