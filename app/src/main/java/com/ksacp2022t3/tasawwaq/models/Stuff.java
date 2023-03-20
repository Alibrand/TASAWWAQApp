package com.ksacp2022t3.tasawwaq.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Stuff {
    String id;
    String title;
    String description;
    String owner_id;
    String owner_name;
    String category_id;
    String category_name;
    String main_category;
    String image;
    double price;
    double rate=0;
    boolean request_needed=false;
    @ServerTimestamp
    Date created_at;
    String status="Available";
    Date sold_date;
    int reviews_count=0;

    public Stuff() {
    }

    public Date getSold_date() {
        return sold_date;
    }

    public void setSold_date(Date sold_date) {
        this.sold_date = sold_date;
    }

    public int getReviews_count() {
        return reviews_count;
    }

    public void setReviews_count(int reviews_count) {
        this.reviews_count = reviews_count;
    }

    public String getMain_category() {
        return main_category;
    }

    public void setMain_category(String main_category) {
        this.main_category = main_category;
    }

    public String getImage() {
        return image;
    }

    public double getRate() {
        return rate;
    }

    public boolean isRequest_needed() {
        return request_needed;
    }

    public void setRequest_needed(boolean request_needed) {
        this.request_needed = request_needed;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
