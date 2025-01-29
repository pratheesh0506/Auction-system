package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Customer {
    private JPanel customerPanel;
    private JTable auctionTable;
    private JTextField bidderNameField;
    private JTextField bidAmountField;
    private JTextArea receiptArea; // Area to display the receipt
    private JButton placeBidButton;
    private JButton closeButton;
    private JButton logoutButton;
    private JComboBox<String> filterComboBox;
    private JFrame customerFrame;

    private static Customer instance; // To hold the current instance of Customer

    public Customer() {
        // Assign the instance to the current Customer
        instance = this;

        customerPanel = new JPanel();
        customerPanel.setBackground(new Color(60, 63, 65));
        customerPanel.setLayout(new BorderLayout()); // Set layout

        auctionTable = new JTable();
        auctionTable.setBackground(new Color(230, 230, 230));
        auctionTable.setGridColor(Color.BLACK);
        auctionTable.setRowHeight(25);

        bidderNameField = new JTextField(20);
        bidAmountField = new JTextField(10);
        placeBidButton = new JButton("Place Bid");
        closeButton = new JButton("Close");
        logoutButton = new JButton("Logout");

        // Adding filter combo box
        filterComboBox = new JComboBox<>(new String[]{"All", "Active", "Inactive"});
        filterComboBox.addActionListener(e -> loadAuctionItems());

        styleButton(placeBidButton);
        styleButton(closeButton);
        styleButton(logoutButton);

        JPanel northPanel = new JPanel();
        northPanel.setBackground(new Color(40, 45, 50));
        northPanel.add(new JLabel("Customer Panel", SwingConstants.CENTER));
        customerPanel.add(northPanel, BorderLayout.NORTH);

        // Scroll pane for the auction table
        JScrollPane scrollPane = new JScrollPane(auctionTable);
        customerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(40, 45, 50));

        JLabel bidderLabel = new JLabel("Bidder Name:");
        bidderLabel.setForeground(Color.WHITE);
        JLabel bidAmountLabel = new JLabel("Bid Amount:");
        bidAmountLabel.setForeground(Color.WHITE);

        southPanel.add(bidderLabel);
        southPanel.add(bidderNameField);
        southPanel.add(bidAmountLabel);
        southPanel.add(bidAmountField);
        southPanel.add(placeBidButton);
        southPanel.add(closeButton);
        southPanel.add(logoutButton);
        southPanel.add(filterComboBox); // Adding filter combo box to south panel
        customerPanel.add(southPanel, BorderLayout.SOUTH);

        // Create a panel for the receipt area
        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BorderLayout()); // Use BorderLayout for receipt area

        // Adding receipt display area
        receiptArea = new JTextArea(10, 30);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane receiptScrollPane = new JScrollPane(receiptArea);
        receiptPanel.add(receiptScrollPane, BorderLayout.CENTER); // Center the receipt scroll pane

        // Add the receipt panel to the right side of the customerPanel
        customerPanel.add(receiptPanel, BorderLayout.EAST); // Display on the right side

        customerFrame = new JFrame("Customer Panel");
        customerFrame.setContentPane(customerPanel);
        customerFrame.setSize(800, 400); // Increased width to accommodate receipt area
        customerFrame.setLocationRelativeTo(null);
        customerFrame.setVisible(true);

        closeButton.addActionListener(e -> customerFrame.dispose());
        logoutButton.addActionListener(e -> logout());
        placeBidButton.addActionListener(e -> placeBid());

        loadAuctionItems(); // Load items initially
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255));
        button.setFocusPainted(false);
    }

    private void loadAuctionItems() {
        String filter = (String) filterComboBox.getSelectedItem();
        String sql = "SELECT * FROM auction_items";

        // Add filtering logic
        if ("Active".equals(filter)) {
            sql += " WHERE NOW() < START_DATE"; // Active items
        } else if ("Inactive".equals(filter)) {
            sql += " WHERE NOW() >= START_DATE"; // Inactive items
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern", "root", "1234");
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            // Create a model to hold the data
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("ITEM_ID");
            tableModel.addColumn("ITEM_NAME");
            tableModel.addColumn("ITEM_PRICE");
            tableModel.addColumn("ITEM_IMAGE");
            tableModel.addColumn("START_TIME");
            tableModel.addColumn("START_DATE");

            while (rs.next()) {
                int itemId = rs.getInt("ITEM_ID");
                String itemName = rs.getString("ITEM_NAME");
                double itemPrice = rs.getDouble("ITEM_PRICE");
                String itemImage = rs.getString("ITEM_IMAGE");
                Time startTime = rs.getTime("START_TIME");
                Date startDate = rs.getDate("START_DATE");

                // Format the start time
                String formattedStartTime = "";
                if (startTime != null) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    formattedStartTime = timeFormat.format(startTime);
                }

                // Add the row to the table model
                tableModel.addRow(new Object[]{itemId, itemName, itemPrice, itemImage, formattedStartTime, startDate});
            }

            auctionTable.setModel(tableModel); // Set the model to the auction table
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading auction items: " + ex.getMessage());
        }
    }

    private void placeBid() {
        String bidderName = bidderNameField.getText();
        String bidAmountStr = bidAmountField.getText();
        int selectedRow = auctionTable.getSelectedRow();

        // Validate inputs
        if (selectedRow == -1 || bidderName.isEmpty() || bidAmountStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select an item, enter your name, and enter a bid amount.");
            return;
        }

        // Validate bid amount
        double bidAmount;
        try {
            bidAmount = Double.parseDouble(bidAmountStr);
            if (bidAmount <= 0) {
                throw new NumberFormatException("Bid amount must be positive.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid bid amount. Please enter a positive number.");
            return;
        }

        // Retrieve the start time of the selected auction item
        String startDateStr = auctionTable.getValueAt(selectedRow, 5).toString(); // START_DATE
        String startTimeStr = auctionTable.getValueAt(selectedRow, 4).toString(); // START_TIME

        // Combine and validate start date and time format
        String fullStartTime = startDateStr + " " + startTimeStr;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Timestamp startTime = null;
        try {
            startTime = new Timestamp(sdf.parse(fullStartTime).getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid start time format. Expected format: yyyy-MM-dd HH:mm");
            return;
        }

        // Check if the current time is before the start time
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (currentTime.before(startTime)) {
            JOptionPane.showMessageDialog(null, "Bidding has not yet opened for this item.");
            return;
        }

        // Retrieve item details
        String itemName = auctionTable.getValueAt(selectedRow, 1).toString(); // ITEM_NAME
        int itemId = (int) auctionTable.getValueAt(selectedRow, 0); // ITEM_ID

        // Check if the new bid is higher than the current highest bid
        String checkBidSql = "SELECT MAX(bid_amount) AS highest_bid FROM bids WHERE item_id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern", "root", "1234");
             PreparedStatement checkBidStmt = connection.prepareStatement(checkBidSql)) {

            checkBidStmt.setInt(1, itemId);
            ResultSet rs = checkBidStmt.executeQuery();
            if (rs.next()) {
                double highestBid = rs.getDouble("highest_bid");

                if (bidAmount <= highestBid) {
                    JOptionPane.showMessageDialog(null, "Your bid must be higher than the current highest bid of " + highestBid);
                    return;
                }
            }

            // If the bid is valid, insert it into the database
            String insertBidSql = "INSERT INTO bids (item_id, bidder_name, bid_amount) VALUES (?, ?, ?)";
            try (PreparedStatement insertBidStmt = connection.prepareStatement(insertBidSql)) {
                insertBidStmt.setInt(1, itemId);
                insertBidStmt.setString(2, bidderName);
                insertBidStmt.setDouble(3, bidAmount);
                insertBidStmt.executeUpdate();

                // Display the receipt in the receiptArea
                receiptArea.setText("Receipt for Bid:\n");
                receiptArea.append("Item: " + itemName + "\n");
                receiptArea.append("Bidder: " + bidderName + "\n");
                receiptArea.append("Bid Amount: $" + bidAmount + "\n");

                JOptionPane.showMessageDialog(null, "Bid placed successfully!");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error placing bid: " + ex.getMessage());
        }
    }

    private void logout() {
        customerFrame.dispose();
        new MainScreen();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Customer::new);
    }
}