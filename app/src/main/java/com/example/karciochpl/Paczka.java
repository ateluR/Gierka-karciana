package com.example.karciochpl;

public class Paczka {
    private String nazwa;
    private String cena;
    private int obrazekResId;

    public Paczka(String nazwa, String cena, int obrazekResId) {
        this.nazwa = nazwa;
        this.cena = cena;
        this.obrazekResId = obrazekResId;
    }

    public String getNazwa() {
        return nazwa;
    }

    public String getCena() {
        return cena;
    }

    public int getObrazekResId() {
        return obrazekResId;
    }
}
