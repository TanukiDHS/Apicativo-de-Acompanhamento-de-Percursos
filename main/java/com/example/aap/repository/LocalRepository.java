package com.example.aap.repository;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aap.modelDominio.Local;
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

public class LocalRepository {
    private FirebaseFirestore dataBase;


    public LocalRepository() {
        dataBase = FirebaseFirestore.getInstance();
    }

    public void inserirLocais(List<Local> locaisIntermediarios, OnResponseComplete callback){
        Executor executor = Executors.newSingleThreadExecutor();
        final Boolean[] erro = {false};

        executor.execute(() -> {
            for (Local local: locaisIntermediarios) {
                Map<String, Object> localMap = new HashMap<>();
                localMap.put("lat", local.getLat());
                localMap.put("lng", local.getLng());
                localMap.put("nome", local.getNome());
                localMap.put("idPercurso", local.getIdPercurso());
                dataBase.collection("local").add(localMap).addOnSuccessListener(executor, new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Id local: "+documentReference.getId());
                    }
                }).addOnFailureListener(executor, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        erro[0] = true;
                    }
                });
            }

            if(callback!=null){
                callback.onResponseComplete(erro[0], 1);
            }
        });
    }

    public void listarLocalPorId(String identificao, OnResponseComplete callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        List<Local> listaLocais = new ArrayList<>();

        executor.execute(() -> {
            dataBase.collection("local").whereEqualTo("idPercurso", identificao)
                    .get()
                    .addOnCompleteListener(executor, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "entrou");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Double lat = document.getDouble("lat");
                                    Double lng = document.getDouble("lng");
                                    String nome = document.getString("nome");

                                    Local local = new Local(lat, lng, nome);

                                    listaLocais.add(local);
                                }
                                if(callback!=null){
                                    callback.onResponseComplete(false, listaLocais);
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
