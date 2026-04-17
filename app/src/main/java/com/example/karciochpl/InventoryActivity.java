package com.example.karciochpl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<Karta> inventoryList;
    private TextView tvBalance, tvLoan;

    private static final String PREFS_NAME = "BankPrefs";
    private static final String KEY_INVENTORY = "inventory";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_LOAN = "loan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.recyclerViewInventory);
        Button btnBack = findViewById(R.id.btnBackInventory);
        Button btnSellAll = findViewById(R.id.btnSellAll);
        tvBalance = findViewById(R.id.tvBalanceInventory);
        tvLoan = findViewById(R.id.tvLoanInventory);

        btnBack.setOnClickListener(v -> finish());
        btnSellAll.setOnClickListener(v -> sprzedajWszystko());

        // Wczytaj i wyświetl balans oraz pożyczkę
        odswiezNaglowek();

        // Wczytaj dane inventory
        wczytajInventory();

        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        }

        adapter = new InventoryAdapter(inventoryList, position -> sprzedajPojedynczaKarte(position));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void odswiezNaglowek() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float balans = prefs.getFloat(KEY_BALANCE, 1000.0f);
        float pozyczka = prefs.getFloat(KEY_LOAN, 0.0f);

        tvBalance.setText("Balans: " + String.format("%.2f", balans) + " PLN");
        tvLoan.setText("Pożyczka: " + String.format("%.2f", pozyczka) + " PLN");
    }

    private void wczytajInventory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_INVENTORY, null);
        Type type = new TypeToken<ArrayList<Karta>>() {}.getType();
        inventoryList = gson.fromJson(json, type);
    }

    private void sprzedajPojedynczaKarte(int position) {
        if (position >= 0 && position < inventoryList.size()) {
            Karta karta = inventoryList.get(position);
            double cena = karta.getWartosc();

            dodajDoBalansu(cena);
            inventoryList.remove(position);
            zapiszInventory();

            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, inventoryList.size());
            
            odswiezNaglowek();
            Toast.makeText(this, "Sprzedano: " + karta.getNazwa(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sprzedajWszystko() {
        if (inventoryList == null || inventoryList.isEmpty()) {
            Toast.makeText(this, "Inventory jest puste!", Toast.LENGTH_SHORT).show();
            return;
        }

        double suma = 0;
        for (Karta k : inventoryList) {
            suma += k.getWartosc();
        }

        dodajDoBalansu(suma);
        inventoryList.clear();
        zapiszInventory();

        adapter.notifyDataSetChanged();
        odswiezNaglowek();

        Toast.makeText(this, "Sprzedano wszystko za: " + String.format("%.2f", suma) + " PLN", Toast.LENGTH_LONG).show();
    }

    private void dodajDoBalansu(double kwota) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float obecnyBalans = prefs.getFloat(KEY_BALANCE, 1000.0f);
        prefs.edit().putFloat(KEY_BALANCE, obecnyBalans + (float) kwota).apply();
    }

    private void zapiszInventory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(inventoryList);
        prefs.edit().putString(KEY_INVENTORY, json).apply();
    }
}
