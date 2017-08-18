package com.example.na_at.examen_app_geo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.na_at.examen_app_geo.adapters.BancosAdapter;
import com.example.na_at.examen_app_geo.model.Bancos;

import java.util.ArrayList;

import static com.example.na_at.examen_app_geo.utils.Constants.REQUEST_IMAGE_CAPTURE;
/**
*Cesar
*Segura
*Granados
*/
public class Bancos_Activity extends AppCompatActivity {

    ArrayList<Bancos> bancosArrayList;
    private RecyclerView mRecyclerView;
    private BancosAdapter bancosAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bancos_);
        bancosArrayList = getIntent().getParcelableArrayListExtra("bancos");
        Log.d("BANCOS", bancosArrayList.get(0).getNombre());
        initRecycler();
    }

    private void initRecycler() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        bancosAdapter = new BancosAdapter(bancosArrayList, this);
        bancosAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(bancosAdapter);
    }
}
