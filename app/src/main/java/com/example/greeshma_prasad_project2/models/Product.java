package com.example.greeshma_prasad_project2.models;

import java.util.List;

public class Product {
    private String id;



    private String name;
    private String description;
    private double price;
    private List<String> images;

    public Product(String id, String name, String description, double price, List<String> images, String quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.images = images;
        this.quantity = quantity;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    private String quantity;


    public Product() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


}
