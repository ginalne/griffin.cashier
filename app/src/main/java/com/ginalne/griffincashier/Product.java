package com.ginalne.griffincashier;

public class Product {
    public Integer id;
    public String name;
    public String name_19;
    public ProductPrice price;

    public Product(Integer id, String name, String name_19, ProductPrice price){
        this.id = id;
        this.name = name;
        this.price = price;
        this.name_19 = name_19;
    }

    public String getPriceShort(){
        return Integer.toString(price.price/1000) + "K";
    }
    public String getNameShort() {
        return name_19;
    }
}
