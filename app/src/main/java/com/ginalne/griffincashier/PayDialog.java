package com.ginalne.griffincashier;

import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PayDialog extends DialogFragment {
    int totalCost;
    int totalPay;
    TextView textPaid, textValue, textCost, textChange;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static PayDialog newInstance(int total) {
        PayDialog f = new PayDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("total", total);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totalCost = getArguments().getInt("total");

        // Pick a style based on the num.
//        int style = DialogFragment.STYLE_NORMAL, theme = 0;
//        switch ((totalCost -1)%6) {
//            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
//            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
//            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
//            case 4: style = DialogFragment.STYLE_NORMAL; break;
//            case 5: style = DialogFragment.STYLE_NORMAL; break;
//            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
//            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
//            case 8: style = DialogFragment.STYLE_NORMAL; break;
//        }
//        switch ((totalCost -1)%6) {
//            case 4: theme = android.R.style.Theme_Holo; break;
//            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
//            case 6: theme = android.R.style.Theme_Holo_Light; break;
//            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
//            case 8: theme = android.R.style.Theme_Holo_Light; break;
//        }
//        setStyle(style, theme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_dialog, container, false);
        textPaid = (TextView) v.findViewById(R.id.textPaid);
        textValue = (TextView) v.findViewById(R.id.textValue);
        textChange = (TextView) v.findViewById(R.id.textChange);
        textCost = (TextView) v.findViewById(R.id.textBill);
        this.refreshPay();
        ((Button)v.findViewById(R.id.numpad00)).setOnClickListener(view -> {
            totalPay = totalPay * 10;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad01)).setOnClickListener(view -> {
            totalPay = totalPay * 10 + 1;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad02)).setOnClickListener(view -> {
            totalPay = totalPay * 10 + 2;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad03)).setOnClickListener(view -> {
            totalPay = totalPay * 10 + 3;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad04)).setOnClickListener(view -> {
            totalPay = totalPay * 10 + 4;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad05)).setOnClickListener(view -> {
            totalPay = totalPay * 10 + 5;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad06)).setOnClickListener(view -> {
            totalPay = totalPay * 10 + 6;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad07)).setOnClickListener(view -> {
            totalPay = totalPay * 10 + 7;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad08)).setOnClickListener(view -> {
            totalPay = totalPay * 10 + 8;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad09)).setOnClickListener(view -> {
            totalPay = totalPay * 10 + 9;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpad100)).setOnClickListener(view -> {
            totalPay = totalPay * 100;
            refreshPay();
        });
        ((Button)v.findViewById(R.id.numpadDelete)).setOnClickListener(view -> {
            totalPay = (totalPay - totalPay % 10)/10;
            refreshPay();
        });
        ProgressBar pBar = v.findViewById(R.id.numpadLoading);
        LinearLayout nButton = v.findViewById(R.id.numpadButton);
        ((Button)v.findViewById(R.id.numpadPay)).setOnClickListener(view -> {
            CashierActivity activity = (CashierActivity) getActivity();
            if (totalCost == 0) {
                Toast.makeText(activity.getBaseContext(), "No Item Added!", Toast.LENGTH_LONG).show();
                return;
            }
            if ((totalPay - totalCost) < 0) {
                Toast.makeText(activity.getBaseContext(), "Paying not Enough!", Toast.LENGTH_LONG).show();
                return;
            }
            pBar.setVisibility(View.VISIBLE);
            nButton.setVisibility(View.GONE);
            activity.doneDialogPay(totalPay, this);
        });

        return v;
    }

    public void refreshPay(){
        textPaid.setText("Rp. " + String.format("%,d", Long.parseLong(String.valueOf(totalPay))));
        textValue.setText("Rp. " + String.format("%,d", Long.parseLong(String.valueOf(totalPay))));
        textCost.setText("Rp. " + String.format("%,d", Long.parseLong(String.valueOf(totalCost))));
        textChange.setText("Rp. " + String.format("%,d", Long.parseLong(String.valueOf(totalPay - totalCost))));
        if (totalPay - totalCost < 0)
            textChange.setTextColor(Color.RED);
        else
            textChange.setTextColor(Color.BLACK);
    }

}