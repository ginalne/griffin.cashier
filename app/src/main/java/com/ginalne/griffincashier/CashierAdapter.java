package com.ginalne.griffincashier;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class CashierAdapter extends BaseAdapter {
    Context context;
    List<ProductHeader> products;
    CheckoutAdapter checkout;
    LayoutInflater inflter;

    public CashierAdapter(Context applicationContext, CheckoutAdapter checkout, List<ProductHeader> products) {
        this.context = applicationContext;
        this.checkout = checkout;
        this.products = products;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return products.size();
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
        view = inflter.inflate(R.layout.cashier_gridview, null);
        TextView text = view.findViewById(R.id.menu_name);
        TextView textIce = view.findViewById(R.id.hotPrice);
        TextView textHot = view.findViewById(R.id.icePrice);
        text.setText(products.get(i).name);
//        Log.i("PRODUCT", products.toString());
        if (products.get(i).product != null && products.get(i).product.length == 2){
            if (products.get(i).product[0] != null)
                textIce.setText(products.get(i).product[0].getPriceShort());
            if (products.get(i).product[1] != null)
                textHot.setText(products.get(i).product[1].getPriceShort());
        }

        ImageButton buttonIce = view.findViewById(R.id.iceButton);
        ImageButton buttonHot = view.findViewById(R.id.hotButton);
        buttonIce.setOnClickListener(v -> {
            checkout.addProduct(products.get(i).product[1]);
        });
        buttonHot.setOnClickListener(v -> {
            checkout.addProduct(products.get(i).product[0]);
        });
//        ImageView icon = (ImageView) view.findViewById(R.id.icon);
//        icon.setImageResource(products[i]);
        return view;
    }
}
