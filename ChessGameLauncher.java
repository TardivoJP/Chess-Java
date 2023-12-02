import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGameLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                openLauncherWindow();
            }
        });
    }

    public static void openLauncherWindow() {
        JFrame launcherFrame = new JFrame("Chess Game Launcher");
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launcherFrame.setSize(800, 800);

        //Create a panel with a GridBagLayout for centering
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        //Load and scale hero image to fit the window's proportions
        ImageIcon heroImageIcon = new ImageIcon("images/hero_image.png"); // Replace with the path to your hero image
        heroImageIcon = new ImageIcon(heroImageIcon.getImage().getScaledInstance(650, 250, Image.SCALE_SMOOTH));

        //Add scaled hero image at the top, centered
        JLabel heroLabel = new JLabel(heroImageIcon);
        centeringPanel.add(heroLabel, gbc);

        //Add "Human vs Human" button below the hero image
        JButton humanVsHumanButton = new JButton("Human vs Human");
        humanVsHumanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Close the launcher window
                launcherFrame.dispose();

                //Start the ChessBoard class
                startChessBoard();
            }
        });

        //Set preferred size for the button and add it to the panel
        humanVsHumanButton.setPreferredSize(new Dimension(150, 50));
        gbc.gridy = 1;
        centeringPanel.add(humanVsHumanButton, gbc);

        //Add "Human vs AI" button below the "Human vs Human" button
        JButton humanVsWhiteAIButton = new JButton("Human vs White AI");
        humanVsWhiteAIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Close the launcher window
                launcherFrame.dispose();

                //Start the ChessBoard class
                startChessBoard();
            }
        });

        //Set preferred size for the button and add it to the panel
        humanVsWhiteAIButton.setPreferredSize(new Dimension(150, 50));
        gbc.gridy = 2;
        centeringPanel.add(humanVsWhiteAIButton, gbc);

        //Add "Human vs AI" button below the "Human vs Human" button
        JButton humanVsBlackAIButton = new JButton("Human vs Black AI");
        humanVsBlackAIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Close the launcher window
                launcherFrame.dispose();

                //Start the ChessBoard class
                startChessBoard();
            }
        });

        //Set preferred size for the button and add it to the panel
        humanVsBlackAIButton.setPreferredSize(new Dimension(150, 50));
        gbc.gridy = 3;
        centeringPanel.add(humanVsBlackAIButton, gbc);

        //Add the centering panel to the frame
        launcherFrame.add(centeringPanel);

        //Center the frame on the screen
        launcherFrame.setLocationRelativeTo(null);

        //Set the frame to be visible
        launcherFrame.setVisible(true);
    }

    private static void startChessBoard() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create an instance of ChessBoard class
                ChessBoard chessBoard = new ChessBoard();
                chessBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                chessBoard.setSize(800, 800);
                chessBoard.setVisible(true);
            }
        });
    }
}
