package com.example.greeshma_prasad_project2.models;

import java.util.Map;

public class Category {
    private String name;
    private String image;


    private Map<String, Product> product;

    public Category() {
    }

    public Category(String name, String image, Map<String, Product> product) {
        this.name = name;
        this.image = image;
        this.product = product;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Product> getProduct() {
        return product;
    }

    public void setProduct(Map<String, Product> product) {
        this.product = product;
    }


}
