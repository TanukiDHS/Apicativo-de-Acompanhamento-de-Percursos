package com.example.aap.view.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aap.R;
import com.example.aap.databinding.FragmentMainBinding;
import com.example.aap.modelDominio.Usuario;
import com.example.aap.view.activities.MainActivity;
import com.example.aap.view.viewModel.InformacoesViewModel;
import com.example.aap.view.viewModel.MainViewModel;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private InformacoesViewModel informacoesViewModel;
    FragmentMainBinding binding;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.getSupportActionBar().setTitle("AAP");
        }

        informacoesViewModel = new ViewModelProvider(getActivity()).get(InformacoesViewModel.class);



        binding.bMainCriarPer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Usuario usuario = informacoesViewModel.getmUsuarioLogado().getValue();
                if(!usuario.getEmailContatoEmergencia().equals("")){
                    Navigation.findNavController(requireView()).navigate(R.id.acao_mainFragment_to_mapaFragment);
                }else{
                    Toast.makeText(getContext(), "Contato de emergencia não registrado.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Antes de iniciar o percurso, por favor informe ele nos seus dados", Toast.LENGTH_SHORT).show();
                    binding.bMeusDados.setError("informe o contato de emergência aqui ");
                }

            }
        });

        binding.bVerPer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.acao_mainFragment_to_listaPercursosFragment);
            }
        });



        binding.bVeriAcompa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_visualizaAcompanhamentoFragment);
            }
        });

        binding.bMeusDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_global_meusDadosFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}