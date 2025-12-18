package com.example.aap.view.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aap.modelDominio.Usuario;
import com.google.firebase.auth.FirebaseUser;

public class InformacoesViewModel extends ViewModel {
    private MutableLiveData<Usuario> mUsuarioLogado;

    private MutableLiveData<FirebaseUser> mFirebaseUsuarioLogado;
    public InformacoesViewModel() {
        this.mUsuarioLogado = new MutableLiveData<>();
        mFirebaseUsuarioLogado = new MutableLiveData<>();
    }

    public MutableLiveData<Usuario> getmUsuarioLogado() {
        return mUsuarioLogado;
    }

    public MutableLiveData<FirebaseUser> getmFirebaseUsuarioLogado() {
        return mFirebaseUsuarioLogado;
    }

    public void informaUsuarioLogado(FirebaseUser firebaseUser){
        mFirebaseUsuarioLogado.postValue(firebaseUser);

    }


}
