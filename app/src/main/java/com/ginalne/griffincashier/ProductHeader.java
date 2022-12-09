package com.ginalne.griffincashier;

public class ProductHeader {
    public Integer id;
    public String name;
    public Product[] products;
    public ProductHeader(String name, Product[] products){
        this.name = name;
        this.products = products;
    }
    public String toString(){
        return "null";
    }
}
