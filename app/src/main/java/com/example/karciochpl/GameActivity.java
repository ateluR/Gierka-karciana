package com.example.karciochpl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private TextView tvBalance, tvLoan;
    private static final String PREFS_NAME = "BankPrefs";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_LOAN = "loan";

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

        // Odśwież balans przy starcie
        loadFileData();

        // Przycisk wstecz
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPaczki);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Paczka> listaPaczek = new ArrayList<>();
        listaPaczek.add(new Paczka("2025 Topps Chrome Spongebob Squarepants 25th Anniversary Hobby Pack", "150 PLN", R.drawable.firstpacket));
        listaPaczek.add(new Paczka("2025-26 Topps Basketball Hobby Pack", "50 PLN", R.drawable.secondpacket));
        listaPaczek.add(new Paczka("2024-25 Topps Definitive Collection UEFA", "40000 PLN", R.drawable.thirdpacekt));

        PaczkaAdapter adapter = new PaczkaAdapter(listaPaczek);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Odśwież dane za każdym razem, gdy wracasz do tego ekranu
        loadFileData();
    }

    private void loadFileData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float balance = sharedPreferences.getFloat(KEY_BALANCE, 1000.0f);
        float loan = sharedPreferences.getFloat(KEY_LOAN, 0.0f);

        if (tvBalance != null) {
            tvBalance.setText("Balans: " + String.format("%.2f", balance) + " PLN");
        }
        if (tvLoan != null) {
            tvLoan.setText("Pożyczka: " + String.format("%.2f", loan) + " PLN");
        }
    }
}
