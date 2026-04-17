package com.example.karciochpl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private TextView tvBalance, tvLoan;
    private static final String PREFS_NAME = "BankPrefs";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_LOAN = "loan";
    private static final String KEY_HISTORY = "history";
    private static final String KEY_FLOW = "flow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBalance = findViewById(R.id.Balance);
        tvLoan = findViewById(R.id.loan);

        loadFileData();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnInventory = findViewById(R.id.inventory);
        btnInventory.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPaczki);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Paczka> listaPaczek = new ArrayList<>();
        listaPaczek.add(new Paczka("2025 Topps Chrome Spongebob Squarepants 25th Anniversary Hobby Pack", 150.0, R.drawable.firstpacket));
        listaPaczek.add(new Paczka("2025-26 Topps Basketball Hobby Pack", 50.0, R.drawable.secondpacket));
        listaPaczek.add(new Paczka("2024-25 Topps Definitive Collection UEFA", 40000.0, R.drawable.thirdpacekt));

        PaczkaAdapter adapter = new PaczkaAdapter(listaPaczek, paczka -> {
            handlePackClick(paczka);
        });
        recyclerView.setAdapter(adapter);
    }

    private void handlePackClick(Paczka paczka) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float balance = prefs.getFloat(KEY_BALANCE, 1000.0f);
        float flow = prefs.getFloat(KEY_FLOW, 0.0f);

        if (balance >= paczka.getCena()) {
            // Odejmij pieniądze i zaktualizuj przepływ
            float newBalance = balance - (float) paczka.getCena();
            float newFlow = flow - (float) paczka.getCena();
            
            prefs.edit()
                .putFloat(KEY_BALANCE, newBalance)
                .putFloat(KEY_FLOW, newFlow)
                .apply();
                
            dodajDoHistorii("Zakup paczki: " + paczka.getNazwa(), -paczka.getCena());
            
            updateUI(newBalance, prefs.getFloat(KEY_LOAN, 0.0f));

            // Uruchom animację otwierania
            Intent intent = new Intent(GameActivity.this, OpeningActivity.class);
            intent.putExtra("packImage", paczka.getObrazekResId());
            intent.putExtra("packName", paczka.getNazwa());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Nie masz wystarczającej ilości pieniędzy!", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        loadFileData();
    }

    private void loadFileData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float balance = sharedPreferences.getFloat(KEY_BALANCE, 1000.0f);
        float loan = sharedPreferences.getFloat(KEY_LOAN, 0.0f);
        updateUI(balance, loan);
    }

    private void updateUI(float balance, float loan) {
        if (tvBalance != null) {
            tvBalance.setText("Balans: " + String.format("%.2f", balance) + " PLN");
        }
        if (tvLoan != null) {
            tvLoan.setText("Pożyczka: " + String.format("%.2f", loan) + " PLN");
        }
    }
}
