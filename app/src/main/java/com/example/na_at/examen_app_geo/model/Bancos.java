package com.example.na_at.examen_app_geo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NA-AT on 25/04/2017.
 */

public class Bancos implements Parcelable {

    private String nombre;
    private String direccion;
    private double distancia;

    public Bancos(String nombre,
                  String direccion,
                  double distancia) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.distancia = distancia;
    }


    protected Bancos(Parcel in) {
        nombre = in.readString();
        direccion = in.readString();
        distancia = in.readDouble();
    }

    public static final Creator<Bancos> CREATOR = new Creator<Bancos>() {
        @Override
        public Bancos createFromParcel(Parcel in) {
            return new Bancos(in);
        }

        @Override
        public Bancos[] newArray(int size) {
            return new Bancos[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(direccion);
        parcel.writeDouble(distancia);
    }
}
