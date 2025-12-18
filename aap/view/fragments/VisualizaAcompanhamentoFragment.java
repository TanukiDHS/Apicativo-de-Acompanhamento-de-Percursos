package com.example.aap.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aap.R;
import com.example.aap.databinding.FragmentMapaBinding;
import com.example.aap.databinding.FragmentVisualizaAcompanhamentoBinding;
import com.example.aap.modelDominio.Local;
import com.example.aap.modelDominio.Percurso;
import com.example.aap.utils.Converter;
import com.example.aap.view.activities.MainActivity;
import com.example.aap.view.viewModel.InformacoesViewModel;
import com.example.aap.view.viewModel.MapaViewModel;
import com.example.aap.view.viewModel.VisualizaAcompanhamentoViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class VisualizaAcompanhamentoFragment extends Fragment implements OnMapReadyCallback {

    private VisualizaAcompanhamentoViewModel mViewModel;
    private FragmentVisualizaAcompanhamentoBinding binding;
    private InformacoesViewModel informacoesViewModel;
    private long ultimoHorario = 0;
    int index = 201;
    private Percurso percursoMostrado;
    private List<Local> listaIntermediarios = new LinkedList<>();
    private GoogleMap myMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean primeiraVez = true;

    private Marker destinoMarcador = null,  origemMarcador = null;

    private List<Marker> listaMarcadoresAntigo = new LinkedList<Marker>();


    public static VisualizaAcompanhamentoFragment newInstance() {
        return new VisualizaAcompanhamentoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVisualizaAcompanhamentoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.getSupportActionBar().setTitle("Acompanhamento");
        }

        mViewModel = new ViewModelProvider(this).get(VisualizaAcompanhamentoViewModel.class);
        informacoesViewModel = new ViewModelProvider(getActivity()).get(InformacoesViewModel.class);

        mViewModel.getmPercurso().observe(getViewLifecycleOwner(), observaPercurso);
        mViewModel.getmListaIntermediarios().observe(getViewLifecycleOwner(), observaListaIntermediarios);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapAcompanhamento);

        mapFragment.getMapAsync(VisualizaAcompanhamentoFragment.this);
        handler.post(runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mViewModel.getAcompanhamento(informacoesViewModel.getmFirebaseUsuarioLogado().getValue().getEmail());
            handler.postDelayed(this, 55000);
        }
    };

    public void carregaMarcadores(){

        if(percursoMostrado != null){

            if(!listaIntermediarios.isEmpty()) {
                for (Local local : listaIntermediarios) {
                    LatLng localizacaoMarcador = new LatLng(local.getLat(), local.getLng());

                    MarkerOptions optionsInter = new MarkerOptions().position(localizacaoMarcador).title(local.getNome()).zIndex(index);
                    optionsInter.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    myMap.addMarker(optionsInter);
                    index++;

                }
            }



        }
    }

    Observer<Percurso> observaPercurso = new Observer<Percurso>() {
        @Override
        public void onChanged(Percurso percurso) {
            if(percurso!=null){

                percursoMostrado = percurso;

                LatLng localizacaoFinal = new LatLng(percursoMostrado.getLatUltima(), percursoMostrado.getLngUltima());

                if(origemMarcador!=null) {
                    origemMarcador.remove();
                }
                MarkerOptions optionFinal = new MarkerOptions().position(localizacaoFinal).title("Última localização conhecida").zIndex(001);
                optionFinal.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                origemMarcador = myMap.addMarker(optionFinal);

                LatLng destinoLatLng = new LatLng(percursoMostrado.getLatDestino(), percursoMostrado.getLngDestino());

                destinoMarcador = myMap.addMarker(new MarkerOptions().position(destinoLatLng).title("Destino").zIndex(002).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mViewModel.getLocaisIntermediarios(percursoMostrado.getIdentificacao());

                if(primeiraVez) {
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacaoFinal, 15));
                    primeiraVez=false;
                }
            }else{
                Toast.makeText(getContext(), "Não tem percurso para acompanhar", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Observer<List<Local>> observaListaIntermediarios = new Observer<List<Local>>() {
        @Override
        public void onChanged(List<Local> locais) {
            if (!locais.isEmpty()) {
                listaIntermediarios = locais;
            }
            carregaMarcadores();
        }
    };
}