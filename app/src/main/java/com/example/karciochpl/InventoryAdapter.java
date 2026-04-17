package com.example.karciochpl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<Karta> listaKart;
    private OnCardSellListener sellListener;

    public interface OnCardSellListener {
        void onSell(int position);
    }

    public InventoryAdapter(List<Karta> listaKart, OnCardSellListener sellListener) {
        this.listaKart = listaKart;
        this.sellListener = sellListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_karta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Karta karta = listaKart.get(position);
        holder.tvNazwa.setText(karta.getNazwa());
        holder.tvWartosc.setText("Wartość: " + String.format("%.2f", karta.getWartosc()) + " PLN");
        holder.imgKarta.setImageResource(karta.getObrazekResId());

        holder.btnSell.setOnClickListener(v -> {
            if (sellListener != null) {
                sellListener.onSell(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaKart != null ? listaKart.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgKarta;
        TextView tvNazwa, tvWartosc;
        Button btnSell;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgKarta = itemView.findViewById(R.id.imgKartaInventory);
            tvNazwa = itemView.findViewById(R.id.tvNazwaKartyInventory);
            tvWartosc = itemView.findViewById(R.id.tvWartoscKartyInventory);
            btnSell = itemView.findViewById(R.id.btnSellSingle);
        }
    }
}
