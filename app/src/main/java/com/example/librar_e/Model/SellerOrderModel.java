package com.example.librar_e.Model;

public class SellerOrderModel {
    private String cap, city, date, houseNumber, nameAndSurname, phoneNumber, state, street, time, customerUid, code;
    private double totalPrice;

    public SellerOrderModel(){};

    public SellerOrderModel(String cap, String city, String date, String houseNumber, String nameAndSurname, String phoneNumber, String state, String street, String time, double totalPrice, String customerUid, String code) {
        this.cap = cap;
        this.city = city;
        this.date = date;
        this.houseNumber = houseNumber;
        this.nameAndSurname = nameAndSurname;
        this.phoneNumber = phoneNumber;
        this.state = state;
        this.street = street;
        this.time = time;
        this.totalPrice = totalPrice;
        this.customerUid = customerUid;
        this.code = code;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getNameAndSurname() {
        return nameAndSurname;
    }

    public void setNameAndSurname(String nameAndSurname) {
        this.nameAndSurname = nameAndSurname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerUid() { return customerUid; }

    public void setCustomerUid(String customerUid) { this.customerUid = customerUid; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }
}
