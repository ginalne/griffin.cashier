package com.ginalne.griffincashier;

import static android.app.ProgressDialog.show;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Debug;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashierActivity extends AppCompatActivity implements PrintingCallback {
    GridView gridView;
    ListView listView;
    List<ProductHeader> products = new ArrayList<>();
    List<ProductCheckout> checkouts = new ArrayList<>();
    CheckoutAdapter checkoutAdapter;
    Printing printing;
    Bitmap logo;
    ImageButton btn_unpair_pair;
    Integer totalPay = 0,totalCheckout,totalChange;
    PayDialog lastDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier);
        gridView = findViewById(R.id.cashierGridView);
        Call<ApiResponse.ProductHeader> call = ApiClient.getRetrofitInstance().create(ApiInterface.class).productlist();
        Toast.makeText(this, "Total Header Call", Toast.LENGTH_SHORT).show();
        call.enqueue(new Callback<ApiResponse.ProductHeader>() {
            @Override
            public void onResponse(Call<ApiResponse.ProductHeader> call, Response<ApiResponse.ProductHeader> response) {
                ApiResponse.ProductHeader productHeader = response.body();
                products = productHeader.data;
                listView = findViewById(R.id.checkoutListView);
                TextView textTotal = findViewById(R.id.textTotal);
                Button buttonClear = findViewById(R.id.buttonClear);
                Button buttonUndo = findViewById(R.id.buttonUndo);
                Button buttonDone = findViewById(R.id.buttonDone);
                checkoutAdapter = new CheckoutAdapter(getApplicationContext(), checkouts, textTotal, buttonUndo);
                listView.setAdapter(checkoutAdapter);
                CashierAdapter cashierAdapter = new CashierAdapter(getApplicationContext(), checkoutAdapter, products);
                gridView.setAdapter(cashierAdapter);
                buttonClear.setOnClickListener( v -> {
                    checkoutAdapter.clearList();
                });

                buttonUndo.setOnClickListener( v -> {
                    checkoutAdapter.undo();
                });

                buttonDone.setOnClickListener( v -> {
                    showDialogPay();
                });
                initPrintSystem();
                Toast.makeText(CashierActivity.this, "Total Header : " + productHeader.data.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse.ProductHeader> call, Throwable t) {
                Log.e("ERROR API", t.getMessage());
                Toast.makeText(CashierActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void initPrintSystem() {
        btn_unpair_pair = findViewById(R.id.bluetoothIcon);

        if (this.printing != null)
            this.printing.setPrintingCallback(this);

        btn_unpair_pair.setOnClickListener(view -> {
            if (Printooth.INSTANCE.hasPairedPrinter())
                Printooth.INSTANCE.removeCurrentPrinter();
            else {
                startActivityForResult(new Intent(CashierActivity.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
                changePairAndUnpair();
            }
        });
        initPrinting();
        changePairAndUnpair();
    }

    public void showDialogPay(){
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        PayDialog newFragment = PayDialog.newInstance(checkoutAdapter.getTotal());
        newFragment.show(ft, "dialog");
    }

    public void doneDialogPay(int pay,PayDialog dialog) {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            totalPay = pay;
            lastDialog = dialog;
            print();
        } else {
            startActivityForResult(new Intent(CashierActivity.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
            changePairAndUnpair();
        }
    }
    public void donePrinting(boolean clear){
        lastDialog.dismissAllowingStateLoss();
        if (clear)
            checkoutAdapter.clearAll();
    }
    private void print() {
        totalCheckout = checkoutAdapter.getTotal();
        totalChange = totalPay - checkoutAdapter.getTotal();
        if (totalCheckout == 0 || totalChange < 0) {
            Toast.makeText(CashierActivity.this, "Paying not Enough!", Toast.LENGTH_LONG).show();
            return;
        }
        String filePath = "https://main.ginalne.my.id/logo/dimensilain";
        Toast.makeText(CashierActivity.this, "Printing...", Toast.LENGTH_SHORT).show();
        // Lo(ad Image
        if (logo == null)
            Picasso.get().load(filePath)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            logo = bitmap;
                            printStruct(logo);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Toast.makeText(CashierActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Toast.makeText(CashierActivity.this, "Load Printing...", Toast.LENGTH_SHORT).show();
                        }
                    });
        else
            printStruct(logo);
    }
    private void printStruct(Bitmap logo) {
        ApiInterface apiInterface = (ApiInterface)ApiClient.getRetrofitInstance().create(ApiInterface.class);
        ArrayMap<String, String> map = new ArrayMap();
        map.put("receipt_id", "1");
        map.put("paid", this.totalPay.toString());
        List<ProductCheckout> list = this.checkoutAdapter.lists;
        for (byte i = 0; i < list.size(); i++) {
            ProductCheckout productCheckout = this.checkoutAdapter.lists.get(i);
            map.put("product[" + i + "][product_id]", productCheckout.product.id.toString());
            map.put("product[" + i + "][price_id]", productCheckout.product.price.id.toString());
            map.put("product[" + i + "][quantity]", productCheckout.quantity.toString());
        }
        apiInterface.billAdd(map).enqueue(new Callback<ApiResponse.Bill>() {
            @Override
            public void onResponse(Call<ApiResponse.Bill> call, Response<ApiResponse.Bill> response) {
                ApiResponse.Bill bill = response.body();
                if (response.body() != null){
                    Log.i("PRINT", response.body().toString());
                    ArrayList<Printable> printable = new ArrayList<>();

                    printable.add(new ImagePrintable.Builder(logo)
                            .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                            .build());
                    printable.add(new TextPrintable.Builder()
                            .setText("Jl. Raya Anyer-Sirih")
                            .setLineSpacing(DefaultPrinter.Companion.getLINE_SPACING_60())
                            .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                            .setNewLinesAfter(1)
                            .build());

                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:ss", Locale.getDefault());
                    String formattedDate = df.format(c);

                    printable.add(new TextPrintable.Builder()
                            .setText(String.format("%-20s %11s", formattedDate, "####"))
                            .setNewLinesAfter(1)
                            .build()
                    );
                    printable.add(new TextPrintable.Builder()
                            .setText("-------------------------------")
                            .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                            .setNewLinesAfter(1)
                            .build()
                    );
                    for(ProductCheckout prod : checkoutAdapter.lists){
                        printable.add(prod.getPrintBuilder());
                    }
                    printable.add(new TextPrintable.Builder()
                            .setText(String.format("%31s", "-------------------"))
                            .setNewLinesAfter(1)
                            .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER()).build()
                    );
                    printable.add(new TextPrintable.Builder()
                            .setText(String.format("%-5s %10s\n", "Total", "Rp." + String.format("%,d", Long.parseLong(totalCheckout.toString()))))
                            .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_LARGE())
                            .setLineSpacing(DefaultPrinter.Companion.getLINE_SPACING_30())
                            .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER()).build()
                    );
                    printable.add(new TextPrintable.Builder()
                            .setText("(Harga sudah termasuk PPN 11%)")
                            .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                            .setNewLinesAfter(1)
                            .build()
                    );
                    printable.add(new TextPrintable.Builder()
                            .setText(String.format("%-20s %11s\n", "Tunai", "Rp." + String.format("%,d", Long.parseLong(totalPay.toString()))))
                            .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                            .build()
                    );
                    printable.add(new TextPrintable.Builder()
                            .setText(String.format("%-20s %11s\n", "Kembali", "Rp." + String.format("%,d", Long.parseLong(totalChange.toString()))))
                            .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                            .setNewLinesAfter(1).build()
                    );
                    printable.add(new TextPrintable.Builder()
                            .setText("Terimakasih sudah membeli produk Dimensi Lain, Selamat menikmati rasa uniknya.")
                            .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                            .setNewLinesAfter(3)
                            .build()
                    );
                    printing.print(printable);
                    donePrinting(true);
                } else {
                    try {
                        Log.e("PRINT", response.errorBody().string());
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        String entityId = jsonObject.getString("entity_id");
                        String errorMessage = jsonObject.getString("message");
                        String errorStatus = jsonObject.getString("status");
                        Log.e("PRINT", entityId);
                        Log.e("PRINT", errorMessage);
                        Log.e("PRINT", errorStatus);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    donePrinting(false);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse.Bill> call, Throwable t) {
                Log.e("PRINT", t.getMessage());
                Toast.makeText(CashierActivity.this, "Sending Bill Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePairAndUnpair() {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            btn_unpair_pair.setBackgroundColor(Color.parseColor("#77AAFF"));
            Toast.makeText(this, "Connected to Printer", Toast.LENGTH_SHORT).show();
        }
        else
            btn_unpair_pair.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void connectingWithPrinter() {
        Toast.makeText(this, "Connecting to Printer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionFailed(String s) {
        Toast.makeText(this, "Failed : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void disconnected() {
//        Toast.makeText(this, "Connection Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String s) {
        Toast.makeText(this, "Error : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessage(String s) {
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void printingOrderSentSuccessfully() {
//        Toast.makeText(this, "Order Sent to Printer", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            initPrinting();
        changePairAndUnpair();
    }

    private void initPrinting() {
        if (Printooth.INSTANCE.hasPairedPrinter())
            printing = Printooth.INSTANCE.printer();
        if (this.printing != null)
            this.printing.setPrintingCallback(this);
    }
}