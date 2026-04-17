package com.example.karciochpl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private List<Karta> filteredList;
    private TextView tvBalance, tvLoan;
    private SearchView searchView;

    private static final String PREFS_NAME = "BankPrefs";
    private static final String KEY_INVENTORY = "inventory";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_LOAN = "loan";
    private static final String KEY_HISTORY = "history";
    private static final String KEY_FLOW = "flow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.recyclerViewInventory);
        Button btnBack = findViewById(R.id.btnBackInventory);
        Button btnSellAll = findViewById(R.id.btnSellAll);
        tvBalance = findViewById(R.id.tvBalanceInventory);
        tvLoan = findViewById(R.id.tvLoanInventory);
        searchView = findViewById(R.id.searchViewInventory);

        btnBack.setOnClickListener(v -> finish());
        btnSellAll.setOnClickListener(v -> sprzedajWszystko());

        odswiezNaglowek();
        wczytajInventory();

        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        }
        
        filteredList = new ArrayList<>(inventoryList);

        adapter = new InventoryAdapter(filteredList, position -> sprzedajPojedynczaKarte(position));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(inventoryList);
        } else {
            text = text.toLowerCase();
            for (Karta item : inventoryList) {
                if (item.getNazwa().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
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
        if (position >= 0 && position < filteredList.size()) {
            Karta karta = filteredList.get(position);
            double cena = karta.getWartosc();

            dodajDoBalansuIFlow(cena);
            dodajDoHistorii("Sprzedaż: " + karta.getNazwa(), cena);

            inventoryList.remove(karta);
            filteredList.remove(position);

            zapiszInventory();
            adapter.notifyDataSetChanged();
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

        dodajDoBalansuIFlow(suma);
        dodajDoHistorii("Sprzedaż wszystkich kart", suma);

        inventoryList.clear();
        filteredList.clear();
        zapiszInventory();

        adapter.notifyDataSetChanged();
        odswiezNaglowek();

        Toast.makeText(this, "Sprzedano wszystko za: " + String.format("%.2f", suma) + " PLN", Toast.LENGTH_LONG).show();
    }

    private void dodajDoBalansuIFlow(double kwota) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float obecnyBalans = prefs.getFloat(KEY_BALANCE, 1000.0f);
        float obecnyFlow = prefs.getFloat(KEY_FLOW, 0.0f);
        
        prefs.edit()
            .putFloat(KEY_BALANCE, obecnyBalans + (float) kwota)
            .putFloat(KEY_FLOW, obecnyFlow + (float) kwota)
            .apply();
    }

    private void dodajDoHistorii(String tytul, double kwota) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        
        String json = prefs.getString(KEY_HISTORY, null);
        Type type = new TypeToken<ArrayList<Transaction>>() {}.getType();
        List<Transaction> history = gson.fromJson(json, type);
        
        if (history == null) history = new ArrayList<>();
        
        history.add(0, new Transaction(tytul, kwota));
        if (history.size() > 50) history.remove(history.size() - 1);
        
        prefs.edit().putString(KEY_HISTORY, gson.toJson(history)).apply();
    }

    private void zapiszInventory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(inventoryList);
        prefs.edit().putString(KEY_INVENTORY, json).apply();
    }
}
