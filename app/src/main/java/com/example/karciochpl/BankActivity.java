package com.example.karciochpl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BankActivity extends AppCompatActivity {

    private TextView tvBalance, tvLoan;
    private double balance = 1000.0;    private double loan = 0.0;

    private static final String PREFS_NAME = "BankPrefs";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_LOAN = "loan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bank);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBalance = findViewById(R.id.Balance);
        tvLoan = findViewById(R.id.loan);

        loadFileData();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnGetLoan = findViewById(R.id.btnGetLoan);
        btnGetLoan.setOnClickListener(v -> {
            balance += 500;
            loan += 500;
            updateUI();
            saveFileData();
        });

        Button btnRepayLoan = findViewById(R.id.btnRepayLoan);
        btnRepayLoan.setOnClickListener(v -> {
            if (balance >= 100 && loan >= 100) {
                balance -= 100;
                loan -= 100;
                updateUI();
                saveFileData();
            }
        });
    }

    private void saveFileData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_BALANCE, (float) balance);
        editor.putFloat(KEY_LOAN, (float) loan);
        editor.apply();
    }

    private void loadFileData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Domyślne wartości: 1000 balans, 0 pożyczka
        balance = sharedPreferences.getFloat(KEY_BALANCE, 1000.0f);
        loan = sharedPreferences.getFloat(KEY_LOAN, 0.0f);
        updateUI();
    }

    private void updateUI() {
        tvBalance.setText("Balans: " + String.format("%.2f", balance) + " PLN");
        tvLoan.setText("Pożyczka: " + String.format("%.2f", loan) + " PLN");
    }
}
