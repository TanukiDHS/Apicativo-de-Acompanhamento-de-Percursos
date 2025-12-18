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
import com.example.aap.databinding.FragmentMainBinding;
import com.example.aap.databinding.FragmentMeusDadosBinding;
import com.example.aap.modelDominio.Usuario;
import com.example.aap.utils.Validador;
import com.example.aap.view.activities.MainActivity;
import com.example.aap.view.viewModel.InformacoesViewModel;
import com.example.aap.view.viewModel.MeusDadosViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.net.IDN;

public class MeusDadosFragment extends Fragment {

    private MeusDadosViewModel mViewModel;
    private InformacoesViewModel informacoesViewModel;
    private FragmentMeusDadosBinding binding;

    public static MeusDadosFragment newInstance() {
        return new MeusDadosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMeusDadosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.getSupportActionBar().setTitle("Dados do Usuário");
        }

        informacoesViewModel = new ViewModelProvider(getActivity()).get(InformacoesViewModel.class);
        mViewModel = new ViewModelProvider(this).get(MeusDadosViewModel.class);

        Usuario usuario = informacoesViewModel.getmUsuarioLogado().getValue();
        FirebaseUser firebaseUser = informacoesViewModel.getmFirebaseUsuarioLogado().getValue();

        mViewModel.getmEncontraContato().observe(getViewLifecycleOwner(), observaBuscaContato);
        mViewModel.getmUsuarioAtualizado().observe(getViewLifecycleOwner(), observaAtualizacao);

        binding.etDadosID.setEnabled(false);
        binding.etDadosEmail.setEnabled(false);

        binding.etDadosID.setText(firebaseUser.getUid());
        binding.etDadosNome.setText(usuario.getNome());
        binding.etDadosEmail.setText(firebaseUser.getEmail());
        if (!usuario.getEmailContatoEmergencia().equals("")){
            binding.etDadosContato.setText(String.valueOf(usuario.getEmailContatoEmergencia()));
        }

        binding.bDadosEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validador.validarTexto(binding.etDadosNome.getText().toString())) {
                    binding.etDadosNome.setError("Campo inválido");
                    binding.etDadosNome.requestFocus();
                    return;
                }

                if (!Validador.validarEmail(binding.etDadosEmail.getText().toString())) {
                    binding.etDadosEmail.setError("Campo inválido");
                    binding.etDadosEmail.requestFocus();
                    return;
                }

                if (!Validador.validarTexto(binding.etDadosContato.getText().toString())) {
                    binding.etDadosContato.setError("Campo inválido");
                    binding.etDadosContato.requestFocus();
                    return;
                }
                if(binding.etDadosNome.getText().toString().equals(usuario.getNome())) {
                    if(binding.etDadosEmail.getText().toString().equals(usuario.getEmail())) {
                        if(binding.etDadosContato.getText().toString().equals(usuario.getEmailContatoEmergencia())) {
                            Toast.makeText(getContext(), "Não necessário editar", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                if(binding.etDadosContato.getText().toString().equals(usuario.getEmail())){
                    //no momento irei deixar, mas posteriormente irá ser impossibilitado
                }

                String emailContato = binding.etDadosContato.getText().toString();
                mViewModel.confirmaIDFirebase(emailContato);
            }
        });

        binding.bDadosVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    Observer<Boolean> observaBuscaContato =  new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if (aBoolean){
                String userid = informacoesViewModel.getmFirebaseUsuarioLogado().getValue().getUid();
                String nome = informacoesViewModel.getmUsuarioLogado().getValue().getNome();
                String senha = informacoesViewModel.getmUsuarioLogado().getValue().getSenha();
                String email = binding.etDadosEmail.getText().toString();
                String contatoID = (binding.etDadosContato.getText().toString());

                Usuario usuarioAtualizado = new Usuario(nome, userid, senha, email, contatoID );
                mViewModel.atualizaUsuarioFirebase(usuarioAtualizado);
            }else {
                Toast.makeText(getContext(), "E-mail do contato não encontrato, confira se digitou corretamente", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Observer<Usuario> observaAtualizacao = new Observer<Usuario>() {
        @Override
        public void onChanged(Usuario usuario) {
            if(usuario != null){
                informacoesViewModel.getmUsuarioLogado().postValue(usuario);
                Toast.makeText(getContext(), "Atualização salva", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(), "Erro ao atualizar cadastro", Toast.LENGTH_SHORT).show();
            }
        }
    };
}