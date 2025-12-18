package com.example.aap.utils;

public class Validador {

    public static boolean validarTexto(String texto) {
        return !texto.isEmpty();
    }

    public static boolean validarEmail(String email) {
        return !email.isEmpty() && email.contains("@") && email.contains(".");
    }

}
