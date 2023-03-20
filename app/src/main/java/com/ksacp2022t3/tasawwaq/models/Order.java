package com.ksacp2022t3.tasawwaq.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Order {
    String id;
    String stuff_id;
    String stuff_name;
    String requester_uid;
    String requester_name;
    String owner_id;
    double price;
    @ServerTimestamp
    Date created_at;
    String status="Pending";


    public Order() {
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStuff_id() {
        return stuff_id;
    }

    public void setStuff_id(String stuff_id) {
        this.stuff_id = stuff_id;
    }

    public String getStuff_name() {
        return stuff_name;
    }

    public void setStuff_name(String stuff_name) {
        this.stuff_name = stuff_name;
    }

    public String getRequester_uid() {
        return requester_uid;
    }

    public void setRequester_uid(String requester_uid) {
        this.requester_uid = requester_uid;
    }

    public String getRequester_name() {
        return requester_name;
    }

    public void setRequester_name(String requester_name) {
        this.requester_name = requester_name;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
