package com.example.aap.view.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aap.R;
import com.example.aap.adapter.PercursoAdapter;
import com.example.aap.databinding.FragmentListaPercursosBinding;
import com.example.aap.modelDominio.Percurso;
import com.example.aap.view.activities.MainActivity;
import com.example.aap.view.viewModel.CadastroViewModel;
import com.example.aap.view.viewModel.InformacoesViewModel;
import com.example.aap.view.viewModel.ListaPercursosViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListaPercursosFragment extends Fragment {

    private ListaPercursosViewModel mViewModel;
    private InformacoesViewModel informacoesViewModel;


    FragmentListaPercursosBinding binding;

    PercursoAdapter percursoAdapter;

    public static ListaPercursosFragment newInstance() {
        return new ListaPercursosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentListaPercursosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.getSupportActionBar().setTitle("Lista dos seus percursos");
        }

        mViewModel = new ViewModelProvider(this).get(ListaPercursosViewModel.class);
        informacoesViewModel = new ViewModelProvider(getActivity()).get(InformacoesViewModel.class);


        mViewModel.getmListaPercursos().observe(getViewLifecycleOwner(), observaListaPercursos);



        mViewModel.listarPercursos(informacoesViewModel.getmFirebaseUsuarioLogado().getValue().getEmail());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void atualizaListagem(List<Percurso> listaPercursos){
        if(listaPercursos==null) {
            listaPercursos = new LinkedList<>();
        }
            percursoAdapter = new PercursoAdapter(listaPercursos, trataCliquePercurso);

            binding.rvVisualizarPercursos.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvVisualizarPercursos.setItemAnimator(new DefaultItemAnimator());
            binding.rvVisualizarPercursos.setAdapter(percursoAdapter);

    }

    PercursoAdapter.PercursoOnClickListener trataCliquePercurso = new PercursoAdapter.PercursoOnClickListener() {
        @Override
        public void OnClickPercurso(View view, int position, Percurso percurso) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("percurso", percurso);

            //Mudar para tela do percurso
            Navigation.findNavController(requireView()).navigate(R.id.acao_listaPercursosFragment_to_visualizaPercursoFragment, bundle);
        }
    };

    Observer<List<Percurso>> observaListaPercursos = new Observer<List<Percurso>>() {
        @Override
        public void onChanged(List<Percurso> percursos) {
            if(percursos == null) {
                Toast.makeText(getContext(), "Não foi possível carregar a lista de percursos!", Toast.LENGTH_SHORT).show();
                return;
            }

            atualizaListagem(percursos);
        }
    };
}