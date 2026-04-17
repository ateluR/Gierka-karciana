package com.example.karciochpl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OpeningActivity extends AppCompatActivity {

    private ImageView imgPack, imgCard;
    private TextView tvCardName, tvCardValue;
    private LinearLayout cardContainer, buttonContainer;
    private Button btnSell, btnKeep;

    private List<Karta> wylosowaneKarty = new ArrayList<>();
    private List<Karta> pulaKart = new ArrayList<>();
    private int obecnaKartaIndex = 0;
    
    private static final String PREFS_NAME = "BankPrefs";
    private static final String KEY_BALANCE = "balance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        imgPack = findViewById(R.id.imgPackOpening);
        imgCard = findViewById(R.id.imgCard);
        tvCardName = findViewById(R.id.tvCardName);
        tvCardValue = findViewById(R.id.tvCardValue);
        cardContainer = findViewById(R.id.cardContainer);
        buttonContainer = findViewById(R.id.buttonContainer);
        btnSell = findViewById(R.id.btnSellCard);
        btnKeep = findViewById(R.id.btnKeepCard);

        int packImage = getIntent().getIntExtra("packImage", R.drawable.firstpacket);
        String packName = getIntent().getStringExtra("packName");
        imgPack.setImageResource(packImage);

        // 1. Inicjuj pulę kart na podstawie nazwy paczki
        inicjujPuleKart(packName);

        // 2. Generuj 5 losowych kart na podstawie rzadkości
        generujKarty();

        // Start animacji paczki
        new Handler().postDelayed(() -> {
            Animation shake = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            imgPack.startAnimation(shake);
            imgPack.setVisibility(View.GONE);
            pokazKarte();
        }, 2000);

        btnSell.setOnClickListener(v -> {
            dodajDoBalansu(wylosowaneKarty.get(obecnaKartaIndex).getWartosc());
            nastepnaKarta();
        });

        btnKeep.setOnClickListener(v -> {
            // Tu można dodać logikę zapisu do Inventory
            Toast.makeText(this, "Zachowano: " + wylosowaneKarty.get(obecnaKartaIndex).getNazwa(), Toast.LENGTH_SHORT).show();
            nastepnaKarta();
        });
    }

    private void inicjujPuleKart(String packName) {
        pulaKart.clear();
        
        if (packName == null) packName = "";

        if (packName.contains("Spongebob")) {
            // Pulą dla paczki Spongebob
            pulaKart.add(new Karta("Sandy cheeks", 2.0, R.drawable.sandcheeks, 90));
            pulaKart.add(new Karta("Squidward’s opinia", 3.0, R.drawable.squwiradreview, 88));
            pulaKart.add(new Karta("Zmęczony Spongebob Masteful Memes", 3.0, R.drawable.mastefulmemestired, 85));
            pulaKart.add(new Karta("Sandy miss appear", 200.0, R.drawable.missappear, 15));
            pulaKart.add(new Karta("SpongeBob sketchcard", 500.0, R.drawable.spongebobsketchcard1, 4));
            pulaKart.add(new Karta("Joe 1 na 50", 150.0, R.drawable.joe1of50, 20));
            pulaKart.add(new Karta("Szafirowy Patryk", 1300.0, R.drawable.sapphirepatrick, 2));
            pulaKart.add(new Karta("Autograf Spongebob i Patryka", 2000.0, R.drawable.spongeandpatrick, 1)); // Bardzo rzadka
        } else if (packName.contains("Basketball")) {
            // Pulą dla paczki Basketball
            pulaKart.add(new Karta("Colby Jones Podpis", 600.0, R.drawable.colbyjonessignature, 5));
            pulaKart.add(new Karta("Collin Murray-Boyles", 1.0, R.drawable.collinmurrayboyles, 97));
            pulaKart.add(new Karta("Cooper Flag Koszulka", 1100.0, R.drawable.logo, 1));
            pulaKart.add(new Karta("Jalen Green", 1.5, R.drawable.jalengreen, 94));
            pulaKart.add(new Karta("Keldon Johnson", 1.7, R.drawable.keldonjohnson, 93));
            pulaKart.add(new Karta("Marcus Sasser", 1.8, R.drawable.marcussasser, 92));
            pulaKart.add(new Karta("Mark Williams", 1.5, R.drawable.markwilliams, 94));
            pulaKart.add(new Karta("Myles Turner", 1.2, R.drawable.mylesturner, 97));
            pulaKart.add(new Karta("Pelle Larsson", 1.4, R.drawable.pellelarsson, 96));
            pulaKart.add(new Karta("Ryan Dunn", 3.5, R.drawable.ryandunn, 91));
            pulaKart.add(new Karta("Russell Westbrook", 5, R.drawable.russellwestbrook, 90));
            pulaKart.add(new Karta("Bradley Beal Podpis", 40, R.drawable.bradleybealsignature, 20));
            pulaKart.add(new Karta("Collin Murray-Boyles Podpis", 80, R.drawable.collinmurrayboylessignature, 15));


        } else if (packName.contains("UEFA")) {
            // Pulą dla paczki UEFA
            pulaKart.add(new Karta("Zlatan Ibrahimovic Podpis", 2500.0, R.drawable.zlatanibrahimovicsignature, 70));
            pulaKart.add(new Karta("Sergio Aguero Podpis", 800.0, R.drawable.sergiuaguerosiganture, 90));
            pulaKart.add(new Karta("Paul Nedved Podpis", 1800.0, R.drawable.paulnedvedsignature,  76));
            pulaKart.add(new Karta("Erling Haaland Podpis 1 na 1", 100000.0, R.drawable.erlinghaalandpodpis1na1, 5));
            pulaKart.add(new Karta("Heung-Min Son Podpis 1 na 10", 80000.0, R.drawable.geungminsonsingnatur1of10, 10));
            pulaKart.add(new Karta("Kenny Dalglish Podpis", 5000.0, R.drawable.kennydalglish, 30));
            pulaKart.add(new Karta("Fedrico Valverde Podpis", 1000.0, R.drawable.fedricovalverdesignature, 90));
            pulaKart.add(new Karta("Filippo Inzaghi Podpis", 200.0, R.drawable.filippoinzaghisignature, 95));
            pulaKart.add(new Karta("Gavi Podpis", 5000.0, R.drawable.gavisiganture, 30));
            pulaKart.add(new Karta("Lionel Messi Podpis 1 na 10", 200000.0, R.drawable.lionelmessipodpis, 3));
            pulaKart.add(new Karta("Marco van Basten Podpis 1 na 5", 10000.0, R.drawable.marcovanbasten, 15));
            pulaKart.add(new Karta("Miroslav Klose Podpis", 10000.0, R.drawable.miroslavklosesignature, 15));
            pulaKart.add(new Karta("Ronaldinho Podpis 1 na 5", 200000.0, R.drawable.ronaldinhopodpis1na5, 3));
            pulaKart.add(new Karta("Ronaldo Podpis 1 na 1", 300000.0, R.drawable.ronaldopodpis1na1, 1));
            pulaKart.add(new Karta("Samuel Eto Podpis", 4800.0, R.drawable.samueletosignature, 35));
            pulaKart.add(new Karta("Sergio Busquets Podpis", 15000.0, R.drawable.sergibusquetssignatures, 19));
            pulaKart.add(new Karta("Zlatan Ibrahimovic Podpis", 8000.0, R.drawable.zlatanibrahimovicsignature, 40));

        }
    }

    private void generujKarty() {
        if (pulaKart.isEmpty()) return;

        Random random = new Random();
        int sumaWag = 0;
        for (Karta k : pulaKart) {
            sumaWag += k.getRzadkosc();
        }

        for (int i = 0; i < 5; i++) {
            int los = random.nextInt(sumaWag);
            int aktualnaSuma = 0;
            
            for (Karta k : pulaKart) {
                aktualnaSuma += k.getRzadkosc();
                if (los < aktualnaSuma) {
                    wylosowaneKarty.add(k);
                    break;
                }
            }
        }
    }

    private void pokazKarte() {
        if (obecnaKartaIndex < wylosowaneKarty.size()) {
            Karta karta = wylosowaneKarty.get(obecnaKartaIndex);
            tvCardName.setText(karta.getNazwa());
            tvCardValue.setText("Wartość: " + String.format("%.2f", karta.getWartosc()) + " PLN");
            imgCard.setImageResource(karta.getObrazekResId());

            cardContainer.setVisibility(View.VISIBLE);
            buttonContainer.setVisibility(View.VISIBLE);
            
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            cardContainer.startAnimation(fadeIn);
        } else {
            finish();
        }
    }

    private void nastepnaKarta() {
        obecnaKartaIndex++;
        pokazKarte();
    }

    private void dodajDoBalansu(double kwota) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float obecnyBalans = prefs.getFloat(KEY_BALANCE, 1000.0f);
        prefs.edit().putFloat(KEY_BALANCE, obecnyBalans + (float) kwota).apply();
    }
}
