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
import com.example.aap.databinding.FragmentLoginBinding;
import com.example.aap.modelDominio.Usuario;
import com.example.aap.utils.Validador;
import com.example.aap.view.activities.MainActivity;
import com.example.aap.view.viewModel.InformacoesViewModel;
import com.example.aap.view.viewModel.LoginViewModel;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;

    private InformacoesViewModel informacoesViewModel;

    FragmentLoginBinding binding;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        informacoesViewModel = new ViewModelProvider(getActivity()).get(InformacoesViewModel.class);

        mViewModel.getmUsuarioFirebase().observe(getViewLifecycleOwner(), observaUsuarioFirebase);
        mViewModel.getmUsuario().observe(getViewLifecycleOwner(), observaUsuario);

        binding.bLoginEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validador.validarTexto(binding.etLoginUsuario.getText().toString())) {
                    binding.etLoginUsuario.setError("Campo inválido.");
                    binding.etLoginUsuario.requestFocus();
                    return;
                }

                if (!Validador.validarTexto(binding.etLoginSenha.getText().toString())) {
                    binding.etLoginSenha.setError("Campo inválido");
                    binding.etLoginSenha.requestFocus();
                    return;
                }

                String email = binding.etLoginUsuario.getText().toString();
                String senha = binding.etLoginSenha.getText().toString();
                Usuario usuario = new Usuario(email, senha);

                mViewModel.loginUsuarioFirebase(usuario);
            }
        });



        binding.bLoginCadastroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.acao_loginFragment_to_cadastroFragment);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mViewModel.limparEstado();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.getSupportActionBar().show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        limparCampos();
        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.getSupportActionBar().hide();
        }
    }

    public void limparCampos() {
        binding.etLoginUsuario.setText("");
        binding.etLoginSenha.setText("");
    }

    Observer<FirebaseUser> observaUsuarioFirebase  = new Observer<FirebaseUser>() {
        @Override
        public void onChanged(FirebaseUser firebaseUser) {
            if (firebaseUser == null){
                Toast.makeText(getContext(), "Credenciais inválidas", Toast.LENGTH_SHORT).show();
                return;
            }
            informacoesViewModel.getmFirebaseUsuarioLogado().setValue(firebaseUser);
            mViewModel.getDadosUsuario(firebaseUser);
        }
    };

    Observer<Usuario> observaUsuario = new Observer<Usuario>() {
        @Override
        public void onChanged(Usuario usuario) {
            if(usuario == null){
                Toast.makeText(getContext(), "Credenciais inválidas", Toast.LENGTH_SHORT).show();
                return;
            }
            informacoesViewModel.getmUsuarioLogado().setValue(usuario);
            Navigation.findNavController(requireView()).navigate(R.id.acao_loginFragment_to_mainFragment);
        }
    };

}