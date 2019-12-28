package com.ma.vodhmsdemo.huawei.data.model;

public class ProductModel {
    private String id;
    private String name;
    private String price;
    private boolean paidForIt;

    public ProductModel(String id, String name, String price, boolean paidForIt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.paidForIt = paidForIt;
    }
    public ProductModel(){
        this("", "", "", false);
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public boolean isPaidForIt() {
        return paidForIt;
    }

    public void setPaidForIt(boolean paidForIt) {
        this.paidForIt = paidForIt;
    }
}
