package com.company;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import com.toedter.calendar.JDateChooser;

public class Admin {
    private JLabel timerLabel;
    private JPanel adminPanel;
    private JTable itemTable;
    private JTextField itemName;
    private JTextField itemPrice;
    private JButton addButton;
    private JButton closeButton;
    private JButton uploadImageButton;
    private JButton deleteButton;
    private JButton approveBidButton; // New button for approving bids
    private JButton logoutButton;
    private JLabel imageLabel;
    private String imagePath;
    private JDateChooser startDateChooser;
    private JSpinner startTimeSpinner;
    private JFrame adminFrame;
    private JComboBox<String> filterComboBox;

    public Admin() {
        adminPanel = new JPanel();
        adminPanel.setBackground(new Color(60, 63, 65));

        itemName = new JTextField(20);
        itemPrice = new JTextField(20);

        timerLabel = new JLabel("Admin Panel", SwingConstants.CENTER);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        itemTable = new JTable();
        itemTable.setBackground(new Color(230, 230, 230));
        itemTable.setGridColor(Color.BLACK);
        itemTable.setRowHeight(25);

        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm:ss");
        startTimeSpinner.setEditor(timeEditor);
        startTimeSpinner.setValue(new Date());

        addButton = new JButton("Add Item");
        styleButton(addButton);

        closeButton = new JButton("Close");
        styleButton(closeButton);

        deleteButton = new JButton("Delete Item");
        styleButton(deleteButton);

        approveBidButton = new JButton("Approve Bid"); // New button for approving bids
        styleButton(approveBidButton);

        logoutButton = new JButton("Logout");
        styleButton(logoutButton);

        uploadImageButton = new JButton("Upload Image");
        styleButton(uploadImageButton);

        startDateChooser = new JDateChooser();

        imageLabel = new JLabel("No image uploaded", SwingConstants.CENTER);
        imageLabel.setForeground(Color.WHITE);

        adminPanel.setLayout(new BorderLayout());
        JPanel northPanel = new JPanel();
        northPanel.setBackground(new Color(40, 45, 50));
        northPanel.add(timerLabel);
        adminPanel.add(northPanel, BorderLayout.NORTH);

        // Center panel with input fields and image upload
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 2, 10, 10));
        centerPanel.setBackground(new Color(60, 63, 65));
        centerPanel.add(createStyledLabel("Item Name:"));
        centerPanel.add(itemName);
        centerPanel.add(createStyledLabel("Item Price:"));
        centerPanel.add(itemPrice);
        centerPanel.add(createStyledLabel("Start Time:"));
        centerPanel.add(startTimeSpinner);
        centerPanel.add(createStyledLabel("Start Date:"));
        centerPanel.add(startDateChooser);
        centerPanel.add(uploadImageButton);
        centerPanel.add(imageLabel);
        centerPanel.add(addButton);

        filterComboBox = new JComboBox<>(new String[]{"All", "Active", "Inactive"});
        centerPanel.add(createStyledLabel("Filter Items:"));
        centerPanel.add(filterComboBox);

        // Add center panel to a new panel with a horizontal layout
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(itemTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Adding both input and table panels to admin panel
        adminPanel.add(inputPanel, BorderLayout.WEST);
        adminPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(new Color(40, 45, 50));
        southPanel.add(deleteButton);
        southPanel.add(approveBidButton); // Add approve bid button to the panel
        southPanel.add(logoutButton);
        southPanel.add(closeButton);
        adminPanel.add(southPanel, BorderLayout.SOUTH);

        adminFrame = new JFrame("Admin Panel");
        adminFrame.setContentPane(adminPanel);
        adminFrame.setSize(800, 400); // Increased width for better layout
        adminFrame.setLocationRelativeTo(null);
        adminFrame.setVisible(true);

        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadImage();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (itemName.getText().isEmpty() || itemPrice.getText().isEmpty() || imagePath == null) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields and upload an image.");
                } else {
                    addItem(itemName.getText(), itemPrice.getText(), startTimeSpinner.getValue(), startDateChooser.getDate());
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow != -1) {
                    String itemId = itemTable.getValueAt(selectedRow, 0).toString();
                    deleteItem(itemId);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an item to delete.");
                }
            }
        });

        approveBidButton.addActionListener(new ActionListener() { // Approve bid action
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow != -1) {
                    String itemId = itemTable.getValueAt(selectedRow, 0).toString();
                    approveBid(itemId); // Approve highest bid for this item
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an item to approve the bid.");
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminFrame.dispose();
                new MainScreen();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminFrame.dispose();
            }
        });

        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFilter = (String) filterComboBox.getSelectedItem();
                filterTableData(selectedFilter);
            }
        });

        tableData();
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255));
        button.setFocusPainted(false);
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePath = selectedFile.getAbsolutePath();
            imageLabel.setText("Image Uploaded: " + selectedFile.getName());
        }
    }

    private void addItem(String name, String price, Object startTime, java.util.Date startDate) {
        String sql = "INSERT INTO auction_items (ITEM_NAME, ITEM_PRICE, ITEM_IMAGE, START_TIME, START_DATE) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern", "root", "1234");
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, price);
            statement.setString(3, imagePath);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(startDate);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String formattedTime = timeFormat.format((Date) startTime);
            String formattedStartTime = formattedDate + " " + formattedTime;

            statement.setTimestamp(4, Timestamp.valueOf(formattedStartTime));
            statement.setDate(5, new java.sql.Date(startDate.getTime()));
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item added successfully");
            clearFields();
            tableData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding item: " + ex.getMessage());
        }
    }

    private void deleteItem(String itemId) {
        String sql = "DELETE FROM auction_items WHERE ITEM_ID = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern", "root", "1234");
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, itemId);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item deleted successfully");
            tableData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error deleting item: " + ex.getMessage());
        }
    }

    private void approveBid(String itemId) {
        // Logic to approve the highest bid for the item
        String sql = "UPDATE auction_items SET IS_SOLD = 1 WHERE ITEM_ID = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern", "root", "1234");
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, itemId);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Bid approved successfully for item: " + itemId);
            tableData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error approving bid: " + ex.getMessage());
        }
    }

    private void tableData() {
        // Logic to load and display data in the itemTable
        String[] columnNames = {"Item ID", "Item Name", "Item Price", "Start Time", "Start Date", "Image", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String sql = "SELECT * FROM auction_items";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern", "root", "1234");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getString("ITEM_ID"));
                row.add(resultSet.getString("ITEM_NAME"));
                row.add(resultSet.getDouble("ITEM_PRICE"));
                row.add(resultSet.getTimestamp("START_TIME"));
                row.add(resultSet.getDate("START_DATE"));
                row.add(resultSet.getString("ITEM_IMAGE"));
                row.add(resultSet.getBoolean("IS_SOLD") ? "Sold" : "Available");
                model.addRow(row);
            }
            itemTable.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading item data: " + ex.getMessage());
        }
    }

    private void filterTableData(String filter) {
        // Logic to filter the itemTable based on the selected filter
        String sql = "SELECT * FROM auction_items";
        if ("Active".equals(filter)) {
            sql += " WHERE IS_SOLD = 0";
        } else if ("Inactive".equals(filter)) {
            sql += " WHERE IS_SOLD = 1";
        }

        DefaultTableModel model = new DefaultTableModel(new String[]{"Item ID", "Item Name", "Item Price", "Start Time", "Start Date", "Image", "Status"}, 0);
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern", "root", "1234");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getString("ITEM_ID"));
                row.add(resultSet.getString("ITEM_NAME"));
                row.add(resultSet.getDouble("ITEM_PRICE"));
                row.add(resultSet.getTimestamp("START_TIME"));
                row.add(resultSet.getDate("START_DATE"));
                row.add(resultSet.getString("ITEM_IMAGE"));
                row.add(resultSet.getBoolean("IS_SOLD") ? "Sold" : "Available");
                model.addRow(row);
            }
            itemTable.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error filtering item data: " + ex.getMessage());
        }
    }

    private void clearFields() {
        itemName.setText("");
        itemPrice.setText("");
        imagePath = null;
        imageLabel.setText("No image uploaded");
        startTimeSpinner.setValue(new Date());
        startDateChooser.setDate(null);
    }

    public static void main(String[] args) {
        new Admin();
    }
}