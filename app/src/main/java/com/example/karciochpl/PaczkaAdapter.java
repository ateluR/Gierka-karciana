package com.example.karciochpl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaczkaAdapter extends RecyclerView.Adapter<PaczkaAdapter.ViewHolder> {

    private final List<Paczka> listaPaczek;
    private final OnPaczkaClickListener listener;

    public interface OnPaczkaClickListener {
        void onPaczkaClick(Paczka paczka);
    }

    public PaczkaAdapter(List<Paczka> listaPaczek, OnPaczkaClickListener listener) {
        this.listaPaczek = listaPaczek;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paczka, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Paczka paczka = listaPaczek.get(position);
        holder.txtNazwa.setText(paczka.getNazwa());
        holder.txtCena.setText(String.format("%.2f PLN", paczka.getCena()));
        holder.imgPaczka.setImageResource(paczka.getObrazekResId());

        holder.itemView.setOnClickListener(v -> listener.onPaczkaClick(paczka));
    }

    @Override
    public int getItemCount() {
        return listaPaczek.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imgPaczka;
        public final TextView txtNazwa;
        public final TextView txtCena;

        public ViewHolder(View view) {
            super(view);
            imgPaczka = view.findViewById(R.id.imgPaczka);
            txtNazwa = view.findViewById(R.id.txtNazwaPaczki);
            txtCena = view.findViewById(R.id.txtCenaPaczki);
        }
    }
}
