package com.example.deliveryRoute.model;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class RouteNode {
    private String address;
    private String type;
    private int id;

    public RouteNode(String address, String type, int id) {
        this.address = address;
        this.type = type;
        this.id = id;
    }

    public RouteNode(){
    }

    public RouteNode(String address, String type) {
        this.address = address;
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

}
