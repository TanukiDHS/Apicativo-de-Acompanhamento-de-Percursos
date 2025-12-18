package com.example.aap.modelDominio;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


public class Local implements Serializable {
    private Double lat;
    private Double lng;

    private String nome;

    private String idPercurso;

    public Local(Double lat, Double lng, String nome, String idPercurso) {
        this.lat = lat;
        this.lng = lng;
        this.nome= nome;
        this.idPercurso = idPercurso;
    }

    public Local(Double lat, Double lng, String nome) {
        this.lat = lat;
        this.lng = lng;
        this.nome= nome;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lang) {
        this.lng = lang;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdPercurso() {
        return idPercurso;
    }

    public void setIdPercurso(String idPercurso) {
        this.idPercurso = idPercurso;
    }
}
