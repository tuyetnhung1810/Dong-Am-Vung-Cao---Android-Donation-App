package com.haui.dongamvungcao.models;

public class DonationDetail {
    private int id;
    private int campaignId;
    private String donorName;
    private int quantity;
    private String date;
    private String message;

    public DonationDetail(int id, int campaignId, String donorName, int quantity, String date, String message) {
        this.id = id;
        this.campaignId = campaignId;
        this.donorName = donorName;
        this.quantity = quantity;
        this.date = date;
        this.message = message;
    }

    public int getId() { return id; }
    public int getCampaignId() { return campaignId; }
    public String getDonorName() { return donorName; }
    public int getQuantity() { return quantity; }
    public String getDate() { return date; }
    public String getMessage() { return message; }
}