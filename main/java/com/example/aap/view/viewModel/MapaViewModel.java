package com.example.aap.view.viewModel;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aap.R;
import com.example.aap.modelDominio.Local;
import com.example.aap.modelDominio.Percurso;
import com.example.aap.repository.LocalRepository;
import com.example.aap.repository.OnResponseComplete;
import com.example.aap.repository.PercursoRepository;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


import java.util.LinkedList;
import java.util.List;

public class MapaViewModel extends ViewModel {

    private MutableLiveData<List<Local>> mListaLocais;
    private MutableLiveData<List<Local>> mListaLocaisIntermediarios;
    private MutableLiveData<String> mPercursoID;
    private MutableLiveData<Integer> mPercursoInserido;
    private MutableLiveData<Boolean> mLocaisInserido;

    private MutableLiveData<Boolean> mFinalizaPercurso;
    private MutableLiveData<Boolean> mAtualizaPercurso;
    private MutableLiveData<Percurso> mPercursoAtivo;
    private PercursoRepository percursoRepository;
    private LocalRepository localRepository;




    public MapaViewModel() {
        mListaLocais = new MutableLiveData<>();
        mPercursoInserido = new MutableLiveData<>();
        mLocaisInserido = new MutableLiveData<>();
        mPercursoID = new MutableLiveData<>();
        mAtualizaPercurso = new MutableLiveData<>();
        mFinalizaPercurso = new MutableLiveData<>();
        mPercursoAtivo = new MutableLiveData<>();
        mListaLocaisIntermediarios = new MutableLiveData<>();
        percursoRepository = new PercursoRepository();
        localRepository = new LocalRepository();
    }

    public MutableLiveData<List<Local>> getmListaLocais() {
        return mListaLocais;
    }

    public MutableLiveData<Integer> getmPercursoInserido() {
        return mPercursoInserido;
    }

    public MutableLiveData<Boolean> getmLocaisInserido() {
        return mLocaisInserido;
    }

    public MutableLiveData<String> getmPercursoID() {
        return mPercursoID;
    }

    public MutableLiveData<Boolean> getmFinalizaPercurso() {
        return mFinalizaPercurso;
    }

    public MutableLiveData<Boolean> getmAtualizaPercurso() {
        return mAtualizaPercurso;
    }

    public MutableLiveData<Percurso> getmPercursoAtivo() {
        return mPercursoAtivo;
    }

    public MutableLiveData<List<Local>> getmListaLocaisIntermediarios() {
        return mListaLocaisIntermediarios;
    }

    public void limpaEstado(){
        mListaLocais = new MutableLiveData<>();
    }

    public void atuallizarLista(Local local) {
        if(local != null){
            List<Local> listaLocais = new LinkedList<>();
            if(mListaLocais.getValue() != null) {
                listaLocais = mListaLocais.getValue();
            }

            listaLocais.add(local);

            mListaLocais.postValue(listaLocais);

        }
    }

    public void iniciaPercurso(Percurso percurso){
        percursoRepository.inserirPercurso(percurso, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(!erro){
                    String idPercurso = retorno.toString();
                    mPercursoID.postValue(idPercurso);
                }else{
                    mPercursoID.postValue(null);
                }
            }
        });
    }

    public void atualizaPercurso(Percurso percurso){
        percurso.setIdentificacao(mPercursoID.getValue());
        percursoRepository.atualizaPercurso(percurso, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                mAtualizaPercurso.postValue(!erro);
            }
        });
    }

    public void finalizarPercurso(Percurso percurso){
        percurso.setIdentificacao(mPercursoID.getValue());
        percursoRepository.finalizaPercurso(percurso, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(!erro){
                    mFinalizaPercurso.postValue(true);
                }else{
                    mFinalizaPercurso.postValue(false);
                }

            }
        });
    }

    public void insereLocaisIntermediarios(List<Local> listaLocais){
        for (Local local: listaLocais) {
            local.setIdPercurso(mPercursoID.getValue());
        }
        localRepository.inserirLocais(listaLocais, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(!erro){
                    mLocaisInserido.postValue(true);
                }else{
                    mLocaisInserido.postValue(false);
                }
            }
        });
    }

    public void verificaPercursoAtivo(String email){
        percursoRepository.getPercursosAtivo(email, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if (!erro) {

                    Percurso percurso = (Percurso) retorno;
                    mPercursoID.postValue(percurso.getIdentificacao());
                    Log.d(TAG, "Identificacao percurso: " + mPercursoID.getValue());
                    mPercursoAtivo.postValue(percurso);
                } else {
                    mPercursoAtivo.postValue(null);
                }
            }
        });
    }

    public void getLocaisIntermediarios() {
        Log.d(TAG, "Identificacao percurso: " + mPercursoID.getValue());
        localRepository.listarLocalPorId(mPercursoID.getValue(), new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(!erro){
                    List<Local> listaIntermediarios = (List<Local>) retorno;
                    mListaLocaisIntermediarios.postValue(listaIntermediarios);
                }else{
                    mListaLocaisIntermediarios.postValue(null);
                }
            }
        });
    }
}