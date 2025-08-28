package com.example.librar_e.Model;

public class CartModel {
    private String bookID, bookName, image, bookSellerUid;
    private double totalprice, singleprice;
    private Integer quantity;

    public CartModel() {
    }

    public CartModel(String bookID, double singleprice, double totalprice, Integer quantity, String bookName, String image, String bookselleruid) {
        this.bookID = bookID;
        this.singleprice = singleprice;
        this.totalprice = totalprice;
        this.quantity = quantity;
        this.bookName = bookName;
        this.image = image;
        this.bookSellerUid = bookselleruid;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public double getSingleprice() {
        return singleprice;
    }

    public void setSingleprice(double singleprice) {
        this.singleprice = singleprice;
    }

    public Double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(Double totalprice) {
        this.totalprice = totalprice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBookSellerUid() { return bookSellerUid; }

    public void setBookSellerUid(String bookSellerUid) { this.bookSellerUid = bookSellerUid; }
}


