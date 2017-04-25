package com.example.na_at.examen_app_geo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.na_at.examen_app_geo.Bancos_Activity;
import com.example.na_at.examen_app_geo.Detalle_Banco;
import com.example.na_at.examen_app_geo.R;
import com.example.na_at.examen_app_geo.model.Bancos;
import com.example.na_at.examen_app_geo.utils.Constants;

import java.util.ArrayList;

/**
 * Created by NA-AT on 25/04/2017.
 */

public class BancosAdapter extends RecyclerView.Adapter<BancosAdapter.BancosViewHolder> {

    ArrayList<Bancos> bancosArrayList;
    Activity a;
    private Bancos bancos;

    public BancosAdapter(ArrayList<Bancos> bancosArrayList, Activity a) {
        this.bancosArrayList = bancosArrayList;
        this.a = a;
    }

    @Override
    public BancosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_banco, parent, false);
        return new BancosViewHolder(v);

    }

    @Override
    public void onBindViewHolder(BancosViewHolder holder, int position) {
        bancos = bancosArrayList.get(position);
        holder.nombre.setText(bancos.getNombre());
        holder.direccion.setText(bancos.getDireccion());
        holder.distancia.setText(String.valueOf(bancos.getDistancia()));
        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(a.getApplicationContext(), Detalle_Banco.class);
                inten.putExtra("detalle", bancos);
                a.startActivity(inten);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bancosArrayList.size();
    }

    public static class BancosViewHolder extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView direccion;
        TextView distancia;
        ImageView next;

        public BancosViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.nombreId);
            direccion = (TextView) itemView.findViewById(R.id.direccionId);
            distancia = (TextView) itemView.findViewById(R.id.distanciaID);
            next = (ImageView) itemView.findViewById(R.id.nextID);
        }
    }
}
