package com.example.aap.modelDominio;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


public class Percurso implements Serializable {

    private double latUltima;
    private double lngUltima;
    private double latDestino;
    private double lngDestino;
    private boolean finalizado;
    private String horaInicio;
    private String horaFinal;
    private String emailUsuario;
    private String identificacao;

    public Percurso(double latUltima, double lngUltima, double latDestino, double lngDestino, boolean finalizado, String horaInicio, String horaFinal, String emailUsuario, String identificacao) {
        this.latUltima = latUltima;
        this.lngUltima = lngUltima;
        this.latDestino = latDestino;
        this.lngDestino = lngDestino;
        this.finalizado = finalizado;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.emailUsuario = emailUsuario;
        this.identificacao = identificacao;
    }

    public Percurso(double latUltima, double lngUltima, double latDestino, double lngDestino, boolean finalizado, String horaInicio, String horaFinal, String emailUsuario) {
        this.latUltima = latUltima;
        this.lngUltima = lngUltima;
        this.latDestino = latDestino;
        this.lngDestino = lngDestino;
        this.finalizado = finalizado;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.emailUsuario = emailUsuario;
    }

    public Percurso(double latUltima, double lngUltima) {
        this.latUltima = latUltima;
        this.lngUltima = lngUltima;
    }

    public Percurso(double latUltima, double lngUltima, boolean finalizado, String horaFinal) {
        this.latUltima = latUltima;
        this.lngUltima = lngUltima;
        this.finalizado = finalizado;
        this.horaFinal = horaFinal;
    }

    public double getLatUltima() {return latUltima;}

    public void setLatUltima(double latUltima) {this.latUltima = latUltima;}

    public double getLngUltima() {return lngUltima;}

    public void setLngUltima(double lngUltima) {this.lngUltima = lngUltima;}

    public double getLatDestino() {return latDestino;}

    public void setLatDestino(double latDestino) {this.latDestino = latDestino;}

    public double getLngDestino() {return lngDestino;}

    public void setLngDestino(double lngDestino) {this.lngDestino = lngDestino;}

    public boolean isFinalizado() {return finalizado;}

    public void setFinalizado(boolean finalizado) {this.finalizado = finalizado;}

    public String getEmailUsuario() {return emailUsuario;}

    public void setEmailUsuario(String emailUsuario) {this.emailUsuario = emailUsuario;}

    public String getHoraInicio() {return horaInicio;}

    public void setHoraInicio(String horaInicio) {this.horaInicio = horaInicio;}

    public String getHoraFinal() {return horaFinal;}

    public void setHoraFinal(String horaFinal) {this.horaFinal = horaFinal;}

    public String getIdentificacao() {return identificacao;}

    public void setIdentificacao(String identificacao) {this.identificacao = identificacao;}
}
