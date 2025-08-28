package com.example.librar_e.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String fullName;
    private String email;
    private String role;
    private String phone;
    private String image;
    private String street;
    private String houseNumber;
    private String city;
    private String cap;

    public User(){}

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public User(String fullName, String email, String role, String phone, String image, String street, String houseNumber, String city, String cap) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.image = image;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
        this.cap = cap;

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStreet() { return street; }

    public void setStreet(String street) { this.street = street; }

    public String getHouseNumber() { return houseNumber; }

    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getCap() { return cap; }

    public void setCap(String cap) { this.cap = cap; }
}
