package com.ginalne.griffincashier;

import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.TextPrintable;

public class ProductCheckout {
    Product product;
    Integer quantity;

    public ProductCheckout(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public String getQuantity() {
        return this.quantity.toString() +" x";
    }

    public String getTotalShort() {
        return "(" + (getTotal() / 1000) + "K)";
    }

    public Integer getTotal() {
        return quantity * product.price;
    }

    public Printable getPrintBuilder() {
        return new TextPrintable.Builder()
                .setText(
                        String.format(
                                "%2s %s %-19s %7s\n",
                                quantity.toString(),
                                "x",
                                product.getNameShort(),
                                String.format("%,d", Long.parseLong(String.valueOf(getTotal())))
                        )
                )
                .build();
    }
}
