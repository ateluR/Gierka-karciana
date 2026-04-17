package com.example.karciochpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private String title;
    private double amount;
    private String date;

    public Transaction(String title, double amount) {
        this.title = title;
        this.amount = amount;
        this.date = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
    }

    public String getTitle() { return title; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
}
