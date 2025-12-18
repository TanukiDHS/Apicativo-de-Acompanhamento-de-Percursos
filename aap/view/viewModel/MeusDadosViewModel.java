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

public class MeusDadosViewModel extends ViewModel {
    private UsuarioRepository usuarioRepository;
    private MutableLiveData<Boolean> mEncontraContato;
    private MutableLiveData<Usuario> mUsuarioAtualizado;

    public MeusDadosViewModel() {
        mEncontraContato = new MutableLiveData<>();
        mUsuarioAtualizado = new MutableLiveData<>();
        usuarioRepository = new UsuarioRepository();
    }

    public MutableLiveData<Boolean> getmEncontraContato() {
        return mEncontraContato;
    }

    public MutableLiveData<Usuario> getmUsuarioAtualizado() {
        return mUsuarioAtualizado;
    }

    public void confirmaIDFirebase(String emailContato) {
        usuarioRepository.getUsuarioByEmailFirebase(emailContato, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                mEncontraContato.postValue(!erro);
            }
        });
    }

    public void atualizaUsuarioFirebase(Usuario usuarioAtualizado){
        usuarioRepository.atualizaUsuarioFirebase(usuarioAtualizado, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                usuarioRepository.getUsuarioByEmailFirebase((String) retorno, new OnResponseComplete() {
                    @Override
                    public void onResponseComplete(boolean erro, Object retorno) {
                        if(!erro) {
                            Usuario usuarioLogado = (Usuario) retorno;
                            Log.d(TAG, ": "+usuarioLogado.getNome());

                            mUsuarioAtualizado.postValue(usuarioLogado);
                        }else {
                            mUsuarioAtualizado.postValue(null);
                        }
                    }
                });
            }
        });
    }
}