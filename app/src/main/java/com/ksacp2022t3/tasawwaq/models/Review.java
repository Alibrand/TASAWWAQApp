package com.ksacp2022t3.tasawwaq.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Review {
    String id;
    String name;
    String stuff_id;
    String comment;
    float rating;
    @ServerTimestamp
    Date created_at;

    public Review() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStuff_id() {
        return stuff_id;
    }

    public void setStuff_id(String stuff_id) {
        this.stuff_id = stuff_id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
