package com.example.aap.view.viewModel;


import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.aap.modelDominio.Usuario;
import com.example.aap.repository.OnResponseComplete;
import com.example.aap.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> mUsuarioFirebase;

    private MutableLiveData<Usuario> mUsuario;

    private UsuarioRepository usuarioRepository;


    public LoginViewModel() {
        mUsuario = new MutableLiveData<>();
        mUsuarioFirebase = new MutableLiveData<>();
        usuarioRepository = new UsuarioRepository();
    }

    public MutableLiveData<FirebaseUser> getmUsuarioFirebase() {
        return mUsuarioFirebase;
    }

    public MutableLiveData<Usuario> getmUsuario() {
        return mUsuario;
    }

    public void limparEstado() {
        this.mUsuario = new MutableLiveData<>();
    }

    public void loginUsuarioFirebase(Usuario usuario){
        this.usuarioRepository.efetuarLoginUsuarioFirebase(usuario, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(!erro){
                    FirebaseUser usuarioLogado = (FirebaseUser) retorno;
                    mUsuarioFirebase.postValue(usuarioLogado);
                }else {
                    mUsuarioFirebase.postValue(null);
                }
            }
        });
    }

    public void getDadosUsuario(FirebaseUser usuarioFirebaseLogado){
        this.usuarioRepository.getUsuarioByEmailFirebase(usuarioFirebaseLogado.getEmail(), new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(!erro){
                    Usuario usuarioLogado = (Usuario) retorno;
                    Log.d(TAG, "nome: "+usuarioLogado.getNome());

                    mUsuario.postValue(usuarioLogado);
                }else {
                    Log.d(TAG, "erro: "+erro);
                    mUsuario.postValue(null);
                }
            }
        });
    }
}