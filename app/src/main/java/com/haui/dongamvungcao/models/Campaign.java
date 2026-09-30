package com.haui.dongamvungcao.models;

public class Campaign {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private int targetQty;
    private int currentQty;
    private String status; // Giá trị sẽ là "dang_dien_ra" hoặc "sap_dien_ra"




    // Hàm khởi tạo không tham số (Bắt buộc phải có để tạo đối tượng trống)
    public Campaign() {
    }

    // Hàm khởi tạo có đầy đủ tham số
    public Campaign(int id, String title, String description, String imageUrl, int targetQty, int currentQty, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.targetQty = targetQty;
        this.currentQty = currentQty;
        this.status = status;
    }

    // ==========================================
    // TRỌN BỘ PHƯƠNG THỨC GETTER / SETTER ĐỂ SỬA LỖI RED SYMBOL
    // ==========================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getTargetQty() {
        return targetQty;
    }

    public void setTargetQty(int targetQty) {
        this.targetQty = targetQty;
    }

    public int getCurrentQty() {
        return currentQty;
    }

    public void setCurrentQty(int currentQty) {
        this.currentQty = currentQty;
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}