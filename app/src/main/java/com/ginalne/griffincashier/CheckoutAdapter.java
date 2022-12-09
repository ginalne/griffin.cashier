package com.ginalne.griffincashier;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CheckoutAdapter extends BaseAdapter {
    Context context;
    List<ProductCheckout> lists;
    ArrayList<CheckoutRecord> records = new ArrayList<>();
    LayoutInflater inflter;
    TextView textTotal;
    Button undoButton;
    int total;

    public CheckoutAdapter(Context applicationContext, List<ProductCheckout> products, TextView textTotal, Button undoButton) {
        this.context = applicationContext;
        this.lists = products;
        inflter = (LayoutInflater.from(applicationContext));
        this.textTotal = textTotal;
        this.undoButton = undoButton;
        this.undoButton.setBackgroundColor(Color.GRAY);
        clearList();
    }

    public void clearAll(){
        records.clear();
        clearList();
        updateTotal();
    }
    public void clearList() {
        int i = 0;
        Collections.reverse(lists);
        for (ProductCheckout prod : lists) {
            addRecord(new CheckoutRecord(CheckoutAction.REMOVE, prod.product, prod.quantity, i == 0));
            i++;
        }
        lists.clear();
        updateTotal();
    }

    public void addProduct(Product product){
        addProduct(product, 1, true);
    }
    public void addProduct(Product product, Integer quantity, boolean record){
        for (ProductCheckout prod : lists){
            if (prod.product == product){
                prod.quantity += quantity;
                if (record)
                    addRecord(new CheckoutRecord(CheckoutAction.ADD, product, quantity));
                updateTotal();
                return;
            }
        }
        lists.add(new ProductCheckout(product, quantity));
        if (record)
            addRecord(new CheckoutRecord(CheckoutAction.ADD, product, 1));
        updateTotal();
    }

    public void removeProduct(Product product){
        removeProduct(product,1, true);
    }
    public void removeProduct(Product product, Integer quantity, boolean record){
        for (ProductCheckout prod : lists){
            if (prod.product == product){
                if (prod.quantity > quantity)
                    prod.quantity -= quantity;
                else
                    lists.remove(prod);
                if (record)
                    addRecord(new CheckoutRecord(CheckoutAction.REMOVE, product, quantity));
                updateTotal();
                return;
            }
        }
    }
    private void addRecord(CheckoutRecord fresh){
        if (records.size() == 0){
            undoButton.setBackgroundColor(Color.parseColor("#AA9911"));
        }
        records.add(fresh);
    }

    public void undo(){
        int size = records.size();
        if (size == 0)
            return;
        boolean stop;
        do{
            CheckoutRecord action = records.remove(records.size() - 1);
            stop = action.Undo(this);
            size--;
        }while(!stop);
        if (size == 0)
            undoButton.setBackgroundColor(Color.GRAY);
        notifyDataSetChanged();
    }

    private void updateTotal() {
        textTotal.setText("Rp. " + String.format("%,d", Long.parseLong(getTotal().toString())));
        notifyDataSetChanged();
    }

    public Integer getTotal() {
        Integer total = 0;
        for (ProductCheckout prod : lists){
            total += prod.getTotal();
        }
        return total;
    }


    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.checkout_listview, null);

        TextView text = (TextView) view.findViewById(R.id.name);
        TextView price = (TextView) view.findViewById(R.id.price);
        TextView qty = (TextView) view.findViewById(R.id.quantity);
        TextView total = (TextView) view.findViewById(R.id.total);

        text.setText(lists.get(i).product.name);
        price.setText(lists.get(i).product.getPriceShort());
        qty.setText(lists.get(i).getQuantity());
        total.setText(lists.get(i).getTotalShort());

//        ImageView icon = (ImageView) view.findViewById(R.id.icon);
//        icon.setImageResource(products[i]);
        return view;
    }
}
