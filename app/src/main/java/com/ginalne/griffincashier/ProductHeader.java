package com.ginalne.griffincashier;

public class ProductHeader {
    public Integer id;
    public String name;
    public Product[] product;
    public ProductHeader(String name, Product[] products){
        this.name = name;
        this.product = products;
    }
    public String toString(){
        return id + " : " + name + " ("+(product == null ? 0 : product.length)+" Product)";
    }
}
