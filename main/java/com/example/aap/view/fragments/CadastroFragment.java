package com.example.aap.view.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aap.R;
import com.example.aap.databinding.FragmentCadastroBinding;
import com.example.aap.modelDominio.Usuario;
import com.example.aap.utils.Validador;
import com.example.aap.view.activities.MainActivity;
import com.example.aap.view.viewModel.CadastroViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CadastroFragment extends Fragment {

    private CadastroViewModel mViewModel;

    FragmentCadastroBinding binding;


    public static CadastroFragment newInstance() {
        return new CadastroFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCadastroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.getSupportActionBar().setTitle("Cadastrar");
        }

        mViewModel = new ViewModelProvider(this).get(CadastroViewModel.class);

        mViewModel.getmResultado().observe(getViewLifecycleOwner(), observaCadastroUsuario);

        binding.bCadastroCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validador.validarTexto(binding.etCadastroSenha.getText().toString())) {
                    binding.etCadastroSenha.setError("Campo inválido");
                    binding.etCadastroSenha.requestFocus();
                    return;
                }

                if (!Validador.validarTexto(binding.etCadastroRepetir.getText().toString())) {
                    binding.etCadastroRepetir.setError("Campo inválido");
                    binding.etCadastroRepetir.requestFocus();
                    return;
                }

                if (!Validador.validarEmail(binding.etCadastroEmail.getText().toString())) {
                    binding.etCadastroEmail.setError("Campo inválido");
                    binding.etCadastroEmail.requestFocus();
                    return;
                }

                String senha = binding.etCadastroSenha.getText().toString();
                if(!senha.equals(binding.etCadastroRepetir.getText().toString())){
                    binding.etCadastroRepetir.setError("Campo de senhas devem ser iguais");
                    binding.etCadastroSenha.requestFocus();
                    binding.etCadastroRepetir.requestFocus();
                    return;
                }
                String nome = binding.etCadastroNome.getText().toString();
                String email = binding.etCadastroEmail.getText().toString();

                Usuario usuario = new Usuario(nome, senha, email);

                mViewModel.inserirUsuarioFirebase(usuario);
            }
        });

        binding.bCadastroVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        limpaCampos();
    }

    @Override
    public void onResume() {
        super.onResume();
        limpaCampos();
    }

    public void limpaCampos(){
        binding.etCadastroEmail.setText("");
        binding.etCadastroNome.setText("");
        binding.etCadastroRepetir.setText("");
        binding.etCadastroSenha.setText("");
    }

    Observer<Boolean> observaCadastroUsuario = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if(aBoolean){
                Toast.makeText(getContext(), "Usuário cadastrado com sucessso!", Toast.LENGTH_LONG).show();
                limpaCampos();
                Navigation.findNavController(requireView()).popBackStack();
            }else{
                Toast.makeText(getContext(), "Erro! Usuário não cadastrado", Toast.LENGTH_LONG).show();
            }
        }
    };
}
