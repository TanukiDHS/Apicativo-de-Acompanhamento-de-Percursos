package com.example.aap.modelDominio;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


public class Usuario implements Serializable {
    private String nome;
    private String identifiacaoFirebase;
    private String senha;
    private String email;
    private String emailContatoEmergencia;



    public Usuario(String nome, String identifiacaoFirebase, String senha, String email, String emailContatoEmergencia) {
        this.nome = nome;
        this.identifiacaoFirebase = identifiacaoFirebase;
        this.senha = senha;
        this.email = email;
        this.emailContatoEmergencia = emailContatoEmergencia;
    }

    public Usuario(String nome, String senha, String email) {
        this.nome = nome;
        this.senha = senha;
        this.email = email;
    }

    public Usuario(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public Usuario(String nome, String identifiacaoFirebase, String emailContatoEmergencia, String email) {
        this.nome = nome;
        this.identifiacaoFirebase = identifiacaoFirebase;
        this.emailContatoEmergencia = emailContatoEmergencia;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdentifiacaoFirebase() {
        return identifiacaoFirebase;
    }

    public void setIdentifiacaoFirebase(String identifiacaoFirebase) {this.identifiacaoFirebase = identifiacaoFirebase;}

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailContatoEmergencia() {
        return emailContatoEmergencia;
    }

    public void setEmailContatoEmergencia(String emailContatoEmergencia) {this.emailContatoEmergencia = emailContatoEmergencia;}
}
