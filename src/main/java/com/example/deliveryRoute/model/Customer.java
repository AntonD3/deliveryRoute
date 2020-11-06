package com.example.deliveryRoute.model;

public class Customer {
    private String address;
    private String storageId;

    public Customer() {
    }

    public Customer(String address, String storageId) {
        this.address = address;
        this.storageId = storageId;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
