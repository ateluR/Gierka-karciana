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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BankActivity extends AppCompatActivity {

    private TextView tvBalance, tvLoan, tvTurnover;
    private double balance = 1000.0;
    private double loan = 0.0;
    private double turnover = 0.0;
    
    private RecyclerView recyclerViewHistory;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    private static final String PREFS_NAME = "BankPrefs";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_LOAN = "loan";
    private static final String KEY_FLOW = "flow"; // Używamy tego samego klucza dla kompatybilności
    private static final String KEY_HISTORY = "history";

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
        tvTurnover = findViewById(R.id.amountValue);
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);

        loadFileData();

        if (transactionList == null) transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistory.setAdapter(transactionAdapter);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnGetLoan = findViewById(R.id.btnGetLoan);
        btnGetLoan.setOnClickListener(v -> showLoanDialog());

        Button btnRepayLoan = findViewById(R.id.btnRepayLoan);
        btnRepayLoan.setOnClickListener(v -> showRepayDialog());
    }

    private void dodajTransakcje(String tytul, double kwota) {
        Transaction t = new Transaction(tytul, kwota);
        transactionList.add(0, t);
        if (transactionList.size() > 50) transactionList.remove(transactionList.size() - 1);
        transactionAdapter.notifyDataSetChanged();
        
        // Każda transakcja (dodatnia czy ujemna) zwiększa obrót o swoją wartość bezwzględną
        turnover += Math.abs(kwota);
        updateUI();
        saveFileData();
    }

    private void showLoanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Weź kredyt");
        builder.setMessage("Wpisz kwotę, którą chcesz otrzymać:");

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
                        
                        dodajTransakcje("Kredyt (Otrzymano)", amount);
                        Toast.makeText(this, "Otrzymano " + amount + " PLN", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Nie masz pożyczki", Toast.LENGTH_SHORT).show();
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
                        if (amountToRepay > loan) amountToRepay = loan;

                        if (balance >= amountToRepay) {
                            balance -= amountToRepay;
                            loan -= amountToRepay;
                            
                            dodajTransakcje("Spłata kredytu", -amountToRepay);
                            Toast.makeText(this, "Spłacono " + amountToRepay + " PLN", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Brak środków", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Błąd kwoty", Toast.LENGTH_SHORT).show();
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
        editor.putFloat(KEY_FLOW, (float) turnover); // Zapisujemy obrót pod tym samym kluczem
        
        Gson gson = new Gson();
        editor.putString(KEY_HISTORY, gson.toJson(transactionList));
        editor.apply();
    }

    private void loadFileData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        balance = sharedPreferences.getFloat(KEY_BALANCE, 1000.0f);
        loan = sharedPreferences.getFloat(KEY_LOAN, 0.0f);
        turnover = sharedPreferences.getFloat(KEY_FLOW, 0.0f);
        
        Gson gson = new Gson();
        String jsonHistory = sharedPreferences.getString(KEY_HISTORY, null);
        Type type = new TypeToken<ArrayList<Transaction>>() {}.getType();
        transactionList = gson.fromJson(jsonHistory, type);

        updateUI();
    }

    private void updateUI() {
        tvBalance.setText("Balans: " + String.format("%.2f", balance) + " PLN");
        tvLoan.setText("Pożyczka: " + String.format("%.2f", loan) + " PLN");
        tvTurnover.setText(String.format("%.2f", turnover) + " PLN");
        tvTurnover.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
    }
}
