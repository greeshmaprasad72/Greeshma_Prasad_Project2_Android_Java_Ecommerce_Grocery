package com.example.greeshma_prasad_project2.models;

public class Checkout {
    private String firstname;
    private String lastname;
    private String address;
    private String email;
    private String zipcode;
    private String city;
    private String country;

    public Checkout() {
    }

    public Checkout(String firstname, String lastname, String address, String email, String zipcode, String city, String country) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.email = email;
        this.zipcode = zipcode;
        this.city = city;
        this.country = country;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
