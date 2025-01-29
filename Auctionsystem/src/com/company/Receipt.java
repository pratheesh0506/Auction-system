package com.company;

public class Receipt {
    private String itemName;
    private String bidderName;
    private double bidAmount;

    // Constructor
    public Receipt(String itemName, String bidderName, double bidAmount) {
        this.itemName = itemName;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
    }

    // Getters and Setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    // Optional: A method to display receipt details
    @Override
    public String toString() {
        return "===== Auction Receipt =====\n" +
               "Item Name: " + itemName + "\n" +
               "Bidder Name: " + bidderName + "\n" +
               "Bid Amount: " + bidAmount + "\n" +
               "==========================\n";
    }
}
