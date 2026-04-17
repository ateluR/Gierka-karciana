package com.example.karciochpl;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        // Przycisk wstecz
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Zamyka bieżącą aktywność i wraca do poprzedniej
            }
        });

        // 1. Znajdź RecyclerView w activity_shop.xml
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPaczki);

        // 2. Ustaw LayoutManager (lista pionowa)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Przygotuj dane (przykładowe paczki)
        List<Paczka> listaPaczek = new ArrayList<>();
        listaPaczek.add(new Paczka("2025 Topps Chrome Spongebob Squarepants 25th Anniversary Hobby Pack", "150 PLN", R.drawable.firstpacket));
        listaPaczek.add(new Paczka("2025-26 Topps Basketball Hobby Pack", "50 PLN", R.drawable.secondpacket));
        listaPaczek.add(new Paczka("2024-25 Topps Definitive Collection UEFA", "40000 PLN", R.drawable.thirdpacekt));

        // 4. Podepnij adapter
        PaczkaAdapter adapter = new PaczkaAdapter(listaPaczek);
        recyclerView.setAdapter(adapter);
    }
}
