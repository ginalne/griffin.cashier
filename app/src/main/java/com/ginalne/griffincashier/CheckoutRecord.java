package com.ginalne.griffincashier;

public class CheckoutRecord {
    private boolean stop;
    private int action;
    private Product product;
    private Integer quantity;

    public CheckoutRecord(int action, Product product, Integer quantity, boolean stop){
        this.stop = stop;
        this.action = action;
        this.product = product;
        this.quantity = quantity;
    }
    public CheckoutRecord(int action, Product product, Integer quantity){
        this.stop = true;
        this.action = action;
        this.product = product;
        this.quantity = quantity;
    }

    public boolean Undo(CheckoutAdapter adapter){
        switch (action){
            case CheckoutAction.ADD: // Attempt to remove
                adapter.removeProduct(product, quantity, false);
            break;
            case CheckoutAction.REMOVE: // Attempt to remove
                adapter.addProduct(product, quantity, false);
                break;
        }
        adapter.notifyDataSetChanged();
        return stop;
    }
}
