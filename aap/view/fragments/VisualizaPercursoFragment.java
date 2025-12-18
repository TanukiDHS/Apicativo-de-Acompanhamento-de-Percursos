package com.example.aap.view.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aap.R;
import com.example.aap.databinding.FragmentVisualizaPercursoBinding;
import com.example.aap.modelDominio.Local;
import com.example.aap.modelDominio.Percurso;
import com.example.aap.view.activities.MainActivity;
import com.example.aap.view.viewModel.VisualizaPercursoViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;
import java.util.List;

public class VisualizaPercursoFragment extends Fragment implements OnMapReadyCallback {

    private VisualizaPercursoViewModel mViewModel;
    private FragmentVisualizaPercursoBinding binding;
    private Percurso percursoMostrado;
    private List<Local> listaIntermediarios = new LinkedList<>();
    private GoogleMap myMap;

    private FusedLocationProviderClient fusedLocationProviderClient;

    public static VisualizaPercursoFragment newInstance() {
        return new VisualizaPercursoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVisualizaPercursoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.getSupportActionBar().setTitle("Percurso");
        }

        mViewModel = new ViewModelProvider(this).get(VisualizaPercursoViewModel.class);

        mViewModel.getmPercurso().observe(getViewLifecycleOwner(), observaPercurso);
        mViewModel.getmListaIntermediarios().observe(getViewLifecycleOwner(), observaListaIntermediarios);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapVisualiza);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());



        mapFragment.getMapAsync(VisualizaPercursoFragment.this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Percurso percurso = (Percurso) bundle.getSerializable("percurso");
            mViewModel.getmPercurso().setValue(percurso);
        }else {
            Toast.makeText(getContext(), "Bundle veio nulo", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
    }


    public void carregaMarcadores(){
        if(percursoMostrado != null) {
            if (!listaIntermediarios.isEmpty()) {
                int index = 201;
                for (Local local : listaIntermediarios) {
                    LatLng localizacaoMarcador = new LatLng(local.getLat(), local.getLng());
                    MarkerOptions options = new MarkerOptions().position(localizacaoMarcador).title(local.getNome()).zIndex(index);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    myMap.addMarker(options);
                    index++;
                }
            }
                LatLng localizacaoFinal = new LatLng(percursoMostrado.getLatUltima(), percursoMostrado.getLngUltima());
                MarkerOptions options = new MarkerOptions().position(localizacaoFinal).title("Última localização conhecida").zIndex(001);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                myMap.addMarker(options);


                LatLng destinoLatLng = new LatLng(percursoMostrado.getLatDestino(), percursoMostrado.getLngDestino());

                myMap.addMarker(new MarkerOptions().position(destinoLatLng).title("Destino").zIndex(002).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinoLatLng, 15));

        }
    }

    Observer<Percurso> observaPercurso = new Observer<Percurso>() {
        @Override
        public void onChanged(Percurso percurso) {
            if(percurso!=null){
                percursoMostrado = percurso;
                mViewModel.getLocaisIntermediarios(percursoMostrado.getIdentificacao());
            }
        }
    };

    Observer<List<Local>> observaListaIntermediarios = new Observer<List<Local>>() {
        @Override
        public void onChanged(List<Local> locais) {
            if (!locais.isEmpty()) {
                listaIntermediarios.addAll(locais);
            }
            carregaMarcadores();
        }
    };


}
