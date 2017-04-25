package com.example.na_at.examen_app_geo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.na_at.examen_app_geo.model.Bancos;
import com.example.na_at.examen_app_geo.utils.Constants;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import static com.example.na_at.examen_app_geo.utils.Constants.REQUEST_IMAGE_CAPTURE;

public class Detalle_Banco extends AppCompatActivity {
    private ImageView mImageView;
    private FloatingActionButton camara;
    TextView nombre, direccion, distancia;
    private Bancos bancos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle__banco);
        bancos = getIntent().getParcelableExtra("detalle");
        initElements();
    }

    private void initElements() {
        nombre = (TextView)findViewById(R.id.nombreDetalleID);
        nombre.setText(bancos.getNombre());
        direccion = (TextView)findViewById(R.id.direccionDetalleID);
        direccion.setText(bancos.getDireccion());
        distancia = (TextView)findViewById(R.id.distanciaDetalleID);
        distancia.setText(String.valueOf(bancos.getDistancia()));
        camara = (FloatingActionButton) findViewById(R.id.cameraID);
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        mImageView = (ImageView)findViewById(R.id.image_paralax);
    }
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("CAMARA", "foto ok");
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }
}
