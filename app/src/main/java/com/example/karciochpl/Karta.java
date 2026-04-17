package com.example.karciochpl;

import java.io.Serializable;

public class Karta implements Serializable {
    private String nazwa;
    private double wartosc;
    private int obrazekResId;
    private int rzadkosc; // 1-100, im mniejsza liczba tym rzadsza karta

    public Karta(String nazwa, double wartosc, int obrazekResId, int rzadkosc) {
        this.nazwa = nazwa;
        this.wartosc = wartosc;
        this.obrazekResId = obrazekResId;
        this.rzadkosc = rzadkosc;
    }

    public String getNazwa() { return nazwa; }
    public double getWartosc() { return wartosc; }
    public int getObrazekResId() { return obrazekResId; }
    public int getRzadkosc() { return rzadkosc; }
}
