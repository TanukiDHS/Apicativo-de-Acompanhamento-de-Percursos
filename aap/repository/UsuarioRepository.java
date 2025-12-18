package com.example.aap.repository;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aap.modelDominio.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UsuarioRepository {

    private FirebaseAuth mAuth;

    private FirebaseFirestore dataBase;


    public UsuarioRepository() {
        mAuth = FirebaseAuth.getInstance();
        dataBase = FirebaseFirestore.getInstance();
    }


    public void inserirUsuarioFirebase(Usuario usuario, OnResponseComplete callback){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            mAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                    .addOnCompleteListener(executor, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");

                                //pegamos os dados do FirebaseUser que acabamos de cadastrar
                                FirebaseUser user = mAuth.getCurrentUser();
                                //criamos um hash map e inserimos os dados que desejamos salvar no banco
                                Map<String, Object> usuarioMap = new HashMap<>();
                                usuarioMap.put("identificacao", user.getUid());
                                usuarioMap.put("nome", usuario.getNome());
                                usuarioMap.put("emailContato", "");


                                //chamamos a instância do banco, informando o nome da coleção desejada
                                //colocamos como o nome do documento o e-mail do usuário
                                // e informamos que a informação do documento é o mapa que acabamos de criar
                                dataBase.collection("usuarios").document(usuario.getEmail())
                                        .set(usuarioMap).addOnSuccessListener(executor, new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //em caso de sucesso informaos que não ocorreu um erro
                                                // e devolvemos o usuário inserido
                                                if(callback != null){
                                                    callback.onResponseComplete(false, user);
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if(callback != null){
                                                    //em caso de falha informaos que ocorreu um erro
                                                    // e devolvemos um objeto nulo
                                                    callback.onResponseComplete(true, null);
                                                }
                                            }
                                        });
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                callback.onResponseComplete(false, null);
                            }
                        }
                    });
        });
    }


    public void atualizaUsuarioFirebase(Usuario usuario, OnResponseComplete callback){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            //Criando e carregando o HashMap com os dados
            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("nome", usuario.getNome());
            usuarioMap.put("emailContato", usuario.getEmailContatoEmergencia());

            //Buscando um documento com o email como identificação
            //E atualizando os dados com o metodo update
            dataBase.collection("usuarios")
                    .document(usuario.getEmail())
                    .update(usuarioMap).addOnSuccessListener(executor, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if(callback != null) {
                                callback.onResponseComplete(false, usuario.getEmail());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(callback != null) {
                                callback.onResponseComplete(true, usuario.getEmail());
                            }
                        }
                    });
        });
    }

    public void getUsuarioByEmailFirebase(String email, OnResponseComplete callback){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            dataBase.collection("usuarios").document(email)
                    .get()
                    .addOnCompleteListener(executor, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                String nome = document.getString("nome");
                                String emailContato = document.getString("emailContato");
                                String identificacao = document.getString("identificacao");

                                Usuario usuario = new Usuario(nome, identificacao, emailContato, email);
                                if (callback != null) {
                                    boolean erro = document.getData()==null;
                                    callback.onResponseComplete(erro, usuario);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                callback.onResponseComplete(true, null);
                            }
                        }
                    });
        });
    }

    public void getUsuarioByEmailAcompanhante(String email, OnResponseComplete callback){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            dataBase.collection("usuarios").whereEqualTo("emailContato", email)
                    .get()
                    .addOnCompleteListener(executor, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String email = document.getString("email");

                                if (callback != null) {
                                    boolean erro = document.getData()==null;
                                    callback.onResponseComplete(erro, email);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                if (callback != null) {
                                    callback.onResponseComplete(true, null);
                                }
                            }
                        }
                    });
        });
    }

    public void efetuarLoginUsuarioFirebase(Usuario usuario, OnResponseComplete callback){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            mAuth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                    .addOnCompleteListener(executor, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(callback!= null){
                                boolean erro = user == null;
                                callback.onResponseComplete(erro, user);
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
        });
    }

}
