package com.example.aap.view.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aap.modelDominio.Usuario;
import com.example.aap.repository.OnResponseComplete;
import com.example.aap.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CadastroViewModel extends ViewModel {
    private UsuarioRepository usuarioRepository;

    private MutableLiveData<Boolean> mResultado;


    public CadastroViewModel() {
        usuarioRepository = new UsuarioRepository();
        mResultado = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getmResultado() {
        return mResultado;
    }



    public void inserirUsuarioFirebase(Usuario usuario){
        this.usuarioRepository.inserirUsuarioFirebase(usuario, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(retorno!=null){
                    mResultado.postValue(true);
                }else{
                    mResultado.postValue(false);
                }
            }
        });
    }
}