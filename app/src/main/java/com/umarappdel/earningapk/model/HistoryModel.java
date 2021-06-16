package com.umarappdel.earningapk.model;

public class HistoryModel {

    String id;
    String phone;
    String status;
    String image;

    public HistoryModel() {
    }

    public HistoryModel(String id, String phone, String status, String image) {
        this.id = id;
        this.phone = phone;
        this.status = status;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
