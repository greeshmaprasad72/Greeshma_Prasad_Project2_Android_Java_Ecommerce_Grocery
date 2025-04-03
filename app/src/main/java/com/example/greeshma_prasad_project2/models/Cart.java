package com.example.greeshma_prasad_project2.models;

public class Cart {
    private String productId;
    private String productName;
    private String productQuantity;
    private double productPrice;
    private String productImage;
    private String productDescription;
    private  int productCount;



    public Cart(String productId, String productName, double productPrice, String productQuantity, String productImage, String productDescription, int productCount) {
        this.productId = productId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.productDescription = productDescription;
        this.productCount=productCount;
    }

    public Cart() {
    }



    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
}
