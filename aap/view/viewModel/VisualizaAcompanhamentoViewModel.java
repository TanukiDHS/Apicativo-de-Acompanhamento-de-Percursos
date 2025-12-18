package com.example.aap.view.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aap.modelDominio.Local;
import com.example.aap.modelDominio.Percurso;
import com.example.aap.repository.LocalRepository;
import com.example.aap.repository.OnResponseComplete;
import com.example.aap.repository.PercursoRepository;
import com.example.aap.repository.UsuarioRepository;

import java.util.List;

public class VisualizaAcompanhamentoViewModel extends ViewModel {

    private MutableLiveData<Percurso> mPercurso;
    private MutableLiveData<List<Local>> mListaIntermediarios;

    private LocalRepository localRepository;

    private PercursoRepository percursoRepository;

    private UsuarioRepository usuarioRepository;
    public VisualizaAcompanhamentoViewModel() {
        mPercurso = new MutableLiveData<>();
        mListaIntermediarios = new MutableLiveData<>();
        localRepository = new LocalRepository();
        percursoRepository = new PercursoRepository();
        usuarioRepository = new UsuarioRepository();
    }

    public MutableLiveData<Percurso> getmPercurso() {
        return mPercurso;
    }

    public MutableLiveData<List<Local>> getmListaIntermediarios() {
        return mListaIntermediarios;
    }

    public void getAcompanhamento(String email){
        usuarioRepository.getUsuarioByEmailAcompanhante(email, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(retorno!=null) {
                    String emailAcompanhado = (String) retorno;
                    percursoRepository.getPercursosAtivo(emailAcompanhado, new OnResponseComplete() {
                        @Override
                        public void onResponseComplete(boolean erro, Object retorno) {
                            if (!erro) {
                                Percurso percurso = (Percurso) retorno;
                                mPercurso.postValue(percurso);
                            } else {
                                mPercurso.postValue(null);
                            }
                        }
                    });
                }
            }
        });

    }

    public void getLocaisIntermediarios(String identificacao) {
        localRepository.listarLocalPorId(identificacao, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(!erro){
                    List<Local> listaIntermediarios = (List<Local>) retorno;
                    mListaIntermediarios.postValue(listaIntermediarios);
                }
            }
        });
    }
}