package com.example.aap.view.viewModel;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aap.modelDominio.Percurso;
import com.example.aap.repository.OnResponseComplete;
import com.example.aap.repository.PercursoRepository;

import java.util.LinkedList;
import java.util.List;

public class ListaPercursosViewModel extends ViewModel {
    private MutableLiveData<List<Percurso>> mListaPercursos;

    PercursoRepository percursoRepository;

    public ListaPercursosViewModel() {
        mListaPercursos = new MutableLiveData<>();
        percursoRepository = new PercursoRepository();
    }

    public MutableLiveData<List<Percurso>> getmListaPercursos() {
        return mListaPercursos;
    }


//    public void listarPercursos(String email) {
//        percursoRepository.listarPercursosPorId(email, new OnResponseComplete() {
//            @Override
//            public void onResponseComplete(boolean erro, Object retorno) {
//                if(!erro){
//                    List<Percurso> listaPercursos = (List<Percurso>) retorno;
//                    mListaPercursos.postValue(listaPercursos);
//                }
//            }
//        });
//    }

    public void listarPercursos(String email) {
        percursoRepository.listarPercursosPorEmail(email, new OnResponseComplete() {
            @Override
            public void onResponseComplete(boolean erro, Object retorno) {
                if(!erro){
                    List<Percurso> listaPercursos = (List<Percurso>) retorno;
                    mListaPercursos.postValue(listaPercursos);

                }else{
                    mListaPercursos.postValue(null);
                }
            }
        });
    }
}