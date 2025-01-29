package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainScreen {
    private JButton ADMINButton;
    private JButton CUSTOMERButton;
    private JPanel mainPanel;
    private JFrame mainFrame;

    public MainScreen() {
        mainFrame = new JFrame("Main Screen");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);

        // Set background color
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(60, 63, 65));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Admin button styling
        ADMINButton = new JButton("Admin");
        ADMINButton.setPreferredSize(new Dimension(150, 40));
        ADMINButton.setFont(new Font("Arial", Font.BOLD, 18));
        ADMINButton.setForeground(Color.WHITE);
        ADMINButton.setBackground(new Color(0, 123, 255));
        ADMINButton.setFocusPainted(false); // Remove focus border
        mainPanel.add(ADMINButton, gbc);

        // Customer button styling
        gbc.gridy++;
        CUSTOMERButton = new JButton("Customer");
        CUSTOMERButton.setPreferredSize(new Dimension(150, 40));
        CUSTOMERButton.setFont(new Font("Arial", Font.BOLD, 18));
        CUSTOMERButton.setForeground(Color.WHITE);
        CUSTOMERButton.setBackground(new Color(0, 123, 255));
        CUSTOMERButton.setFocusPainted(false); // Remove focus border
        mainPanel.add(CUSTOMERButton, gbc);

        // Set a border with title and custom color
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2), 
                "Choose Option", 
                javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 20), 
                Color.WHITE));

        // Frame background color
        mainFrame.getContentPane().setBackground(new Color(44, 62, 80));
        mainFrame.add(mainPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        // Admin button action
        ADMINButton.addActionListener(e -> new AdminLogin()); // Open Admin login screen

        // Customer button action
        CUSTOMERButton.addActionListener(e -> new CustomerLogin()); // Open Customer login screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreen());
    }
}
