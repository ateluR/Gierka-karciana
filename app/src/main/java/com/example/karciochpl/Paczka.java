package com.example.karciochpl;

public class Paczka {
    private String nazwa;
    private double cena;
    private int obrazekResId;

    public Paczka(String nazwa, double cena, int obrazekResId) {
        this.nazwa = nazwa;
        this.cena = cena;
        this.obrazekResId = obrazekResId;
    }

    public String getNazwa() {
        return nazwa;
    }

    public double getCena() {
        return cena;
    }

    public int getObrazekResId() {
        return obrazekResId;
    }
}
