package com.example.karciochpl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Znajdowanie przycisku "Autorzy"
        Button btnGra = findViewById(R.id.btnGra);
// Znajdowanie przycisku "Bank"
        Button btnBank = findViewById(R.id.btnBank);

        // Co ma się stać po kliknięciu
        btnGra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przenosimy gracza do ekranu Autorów
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
        btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przenosimy gracza do ekranu Banku
                Intent intent = new Intent(MainActivity.this, BankActivity.class);
                startActivity(intent);
            }
        });
    }
}
