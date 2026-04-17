package com.example.karciochpl;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BankActivity extends AppCompatActivity {

    private TextView tvBalance, tvLoan;
    private double balance = 1000.0;
    private double loan = 0.0;

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
        btnGetLoan.setOnClickListener(v -> showLoanDialog());

        Button btnRepayLoan = findViewById(R.id.btnRepayLoan);
        btnRepayLoan.setOnClickListener(v -> showRepayDialog());
    }

    private void showLoanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Weź kredyt");
        builder.setMessage("Wpisz kwotę, którą chcesz otrzymać (Doliczymy 10% do długu):");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Pożycz", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                try {
                    double amount = Double.parseDouble(value);
                    if (amount > 0) {
                        double amountWithInterest = amount * 1.10;
                        balance += amount;
                        loan += amountWithInterest;
                        updateUI();
                        saveFileData();
                        Toast.makeText(this, "Otrzymano " + amount + " PLN. Do spłaty dodano " + String.format("%.2f", amountWithInterest), Toast.LENGTH_LONG).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Nieprawidłowa kwota", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showRepayDialog() {
        if (loan <= 0) {
            Toast.makeText(this, "Nie masz żadnej pożyczki do spłacenia", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Spłać pożyczkę");
        builder.setMessage("Ile długu chcesz spłacić?");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Spłać", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                try {
                    double amountToRepay = Double.parseDouble(value);
                    if (amountToRepay > 0) {
                        if (amountToRepay > loan) {
                            amountToRepay = loan;
                        }

                        if (balance >= amountToRepay) {
                            balance -= amountToRepay;
                            loan -= amountToRepay;
                            updateUI();
                            saveFileData();
                            Toast.makeText(this, "Spłacono " + String.format("%.2f", amountToRepay) + " PLN długu", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Nie masz wystarczających środków na balansie!", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Nieprawidłowa kwota", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.cancel());

        builder.show();
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
        balance = sharedPreferences.getFloat(KEY_BALANCE, 1000.0f);
        loan = sharedPreferences.getFloat(KEY_LOAN, 0.0f);
        updateUI();
    }

    private void updateUI() {
        tvBalance.setText("Balans: " + String.format("%.2f", balance) + " PLN");
        tvLoan.setText("Pożyczka: " + String.format("%.2f", loan) + " PLN");
    }
}
