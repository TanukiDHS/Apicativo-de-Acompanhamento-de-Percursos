package com.example.aap.repository;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aap.modelDominio.Percurso;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PercursoRepository {

    private FirebaseFirestore dataBase;

    public PercursoRepository() {
        dataBase = FirebaseFirestore.getInstance();
    }

    public void inserirPercurso(Percurso percurso, OnResponseComplete callback) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            Map<String, Object> percursoMap = new HashMap<>();

            percursoMap.put("latUltima", percurso.getLatUltima());
            percursoMap.put("lngUltima", percurso.getLngUltima());
            percursoMap.put("latDestino", percurso.getLatDestino());
            percursoMap.put("lngDestino", percurso.getLngDestino());
            percursoMap.put("finalizado", percurso.isFinalizado());
            percursoMap.put("horaInicio", percurso.getHoraInicio());
            percursoMap.put("horaFinal", percurso.getHoraFinal());
            percursoMap.put("emailUsuario", percurso.getEmailUsuario());

            dataBase.collection("percursos").add(percursoMap)
                    .addOnSuccessListener(executor, new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "id: "+documentReference.getId());
                    if(callback != null){
                        callback.onResponseComplete(false, documentReference.getId());
                    }
                }
            }).addOnFailureListener(executor, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(callback != null){
                        callback.onResponseComplete(true, "");
                    }
                }
            });
        });
    }

    public void atualizaPercurso(Percurso percurso, OnResponseComplete callback) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            Map<String, Object> percursoMap = new HashMap<>();

            percursoMap.put("latUltima", percurso.getLatUltima());
            percursoMap.put("lngUltima", percurso.getLngUltima());

            dataBase.collection("percursos")
                    .document(percurso.getIdentificacao())
                    .update(percursoMap).addOnSuccessListener(executor, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if(callback != null) {
                                callback.onResponseComplete(false, percurso);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(callback != null) {
                                callback.onResponseComplete(true,null);
                            }
                        }
                    });
        });
    }

    public void finalizaPercurso(Percurso percurso, OnResponseComplete callback) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            Map<String, Object> percursoMap = new HashMap<>();

            percursoMap.put("latUltima", percurso.getLatUltima());
            percursoMap.put("lngUltima", percurso.getLngUltima());
            percursoMap.put("finalizado", percurso.isFinalizado());
            percursoMap.put("horaFinal", percurso.getHoraFinal());

            dataBase.collection("percursos")
                    .document(percurso.getIdentificacao())
                    .update(percursoMap).addOnSuccessListener(executor, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if(callback != null) {
                                callback.onResponseComplete(false, percurso);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(callback != null) {
                                callback.onResponseComplete(true,null);
                            }
                        }
                    });
        });
    }

    public void listarPercursosPorEmail(String email, OnResponseComplete callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        List<Percurso> listaPercursos = new ArrayList<>();

        Log.d(TAG, "email: " + email);


        executor.execute(() -> {
            dataBase.collection("percursos").whereEqualTo("emailUsuario", email)
                    .get()
                    .addOnCompleteListener(executor, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "entrou");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Double latUltima = document.getDouble("latUltima");
                                    Double lngUltima = document.getDouble("lngUltima");
                                    Double latDestino = document.getDouble("latDestino");
                                    Double lngDestino = document.getDouble("lngDestino");
                                    String horaInicio = document.getString("horaInicio");
                                    String horaFinal = document.getString("horaFinal");
                                    Boolean finalizado = document.getBoolean("finalizado");
                                    String emailUsuario = document.getString("emailUsuario");
                                    String identificacao = document.getId();

                                    Percurso percurso = new Percurso(latUltima, lngUltima, latDestino, lngDestino, finalizado, horaInicio, horaFinal, emailUsuario, identificacao);

                                    listaPercursos.add(percurso);
                                }
                                if(callback!=null){
                                    callback.onResponseComplete(false, listaPercursos);
                                }
                            } else {
                                Log.d(TAG, "Erro buscando documento: ", task.getException());
                                callback.onResponseComplete(true, null);
                            }
                        }
                    });
        });
    }

    public void getPercursosAtivo(String email, OnResponseComplete callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        final Percurso[] acompanhamento = new Percurso[1];

        executor.execute(() -> {
            dataBase.collection("percursos").whereEqualTo("emailUsuario", email)
                    .whereEqualTo("finalizado", false)
                    .get()
                    .addOnCompleteListener(executor, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Double latUltima = document.getDouble("latUltima");
                                    Double lngUltima = document.getDouble("lngUltima");
                                    Double latDestino = document.getDouble("latDestino");
                                    Double lngDestino = document.getDouble("lngDestino");
                                    String horaInicio = document.getString("horaInicio");
                                    String horaFinal = document.getString("horaFinal");
                                    Boolean finalizado = document.getBoolean("finalizado");
                                    String emailUsuario = document.getString("emailUsuario");
                                    String identificacao = document.getId();

                                    Percurso percurso = new Percurso(latUltima, lngUltima, latDestino, lngDestino, finalizado, horaInicio, horaFinal, emailUsuario, identificacao);

                                    acompanhamento[0] = percurso;
                                }
                                if(callback!=null){
                                    boolean erro = acompanhamento[0]==null;
                                    callback.onResponseComplete(erro, acompanhamento[0]);
                                }
                            } else {
                                Log.d(TAG, "Erro buscando documento: ", task.getException());
                                callback.onResponseComplete(true, null);
                            }
                        }
                    });
        });
    }
}
