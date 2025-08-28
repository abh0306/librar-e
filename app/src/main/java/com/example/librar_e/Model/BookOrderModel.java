package com.example.librar_e.Model;

public class BookOrderModel {
    private String sellerUid, bookId, title;
    private double priceTotal, priceForSingle;
    private Integer quantity;

    public BookOrderModel(){}

    public BookOrderModel(String sellerUid, double priceTotal, double priceForSingle, Integer quantity, String bookId, String title) {
        this.sellerUid = sellerUid;
        this.priceTotal = priceTotal;
        this.priceForSingle = priceForSingle;
        this.quantity = quantity;
        this.bookId = bookId;
        this.title = title;
    }

    public String getSellerUid() {
        return sellerUid;
    }

    public void setSellerUid(String sellerUid) {
        this.sellerUid = sellerUid;
    }

    public double getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(double priceTotal) {
        this.priceTotal = priceTotal;
    }

    public double getPriceForSingle() {
        return priceForSingle;
    }

    public void setPriceForSingle(double priceForSingle) {
        this.priceForSingle = priceForSingle;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }
}
