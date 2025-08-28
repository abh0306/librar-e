package com.example.librar_e.Model;

public class Books {
    private String Title, Author, Bookid, Category, Date, Description, ISBN, Image, Language, PH, Time, Year, SellerUid;
    private double Price;

    public Books(String title, String author, String bookid, String category, String date, String description, String ISBN, String image, String language, String PH, double price, String time, String year, String sellerUid) {
        this.Title = title;
        this.Author = author;
        this.Bookid = bookid;
        this.Category = category;
        this.Date = date;
        this.Description = description;
        this.ISBN = ISBN;
        this.Image = image;
        this.Language = language;
        this.PH = PH;
        this.Price = price;
        this.Time = time;
        this.Year = year;
        this.SellerUid = sellerUid;
    }
    public Books() {}

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getBookid() {
        return Bookid;
    }

    public void setBookid(String bookid) {
        Bookid = bookid;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getPH() {
        return PH;
    }

    public void setPH(String PH) {
        this.PH = PH;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getSellerUid() { return SellerUid; }

    public void setSellerUid(String sellerUid) { SellerUid = sellerUid; }
}
