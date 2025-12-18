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

import java.util.List;

public class VisualizaPercursoViewModel extends ViewModel {
    private MutableLiveData<Percurso> mPercurso;
    private MutableLiveData<List<Local>> mListaIntermediarios;
    private LocalRepository localRepository;

    public VisualizaPercursoViewModel() {
        mPercurso = new MutableLiveData<>();
        mListaIntermediarios = new MutableLiveData<>();
        localRepository = new LocalRepository();
    }

    public MutableLiveData<Percurso> getmPercurso() {
        return mPercurso;
    }

    public MutableLiveData<List<Local>> getmListaIntermediarios() {
        return mListaIntermediarios;
    }

//    public void getLocaisIntermediarios(int idPercurso) {
//        localRepository.listarLocalPorId(idPercurso, new OnResponseComplete() {
//            @Override
//            public void onResponseComplete(boolean erro, Object retorno) {
//                if(!erro){
//                    List<Local> listaIntermediarios = (List<Local>) retorno;
//                    mListaIntermediarios.postValue(listaIntermediarios);
//                }
//            }
//        });
//    }

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