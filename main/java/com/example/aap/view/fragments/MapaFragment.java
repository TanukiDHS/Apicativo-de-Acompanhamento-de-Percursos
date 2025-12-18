package com.example.aap.view.fragments;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;


import com.example.aap.R;
import com.example.aap.databinding.FragmentMapaBinding;
import com.example.aap.modelDominio.Local;
import com.example.aap.modelDominio.Percurso;
import com.example.aap.utils.Converter;
import com.example.aap.view.activities.MainActivity;
import com.example.aap.view.viewModel.InformacoesViewModel;
import com.example.aap.view.viewModel.MapaViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MapaFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private MapaViewModel mViewModel;
    private InformacoesViewModel informacoesViewModel;

    private FragmentMapaBinding binding;

    private GoogleMap myMap;

    private Location localizacaoAtual;
    private String horarioInicio, horarioAtual, horarioFinalizacao;
    private  FusedLocationProviderClient fusedLocationProviderClient;

    private SimpleDateFormat formatarHora = new SimpleDateFormat("h:mm a");

    private  LatLng destinoLatLng;

    private Marker destinoMarcador = null;
    private  Marker origemMarcador = null;

    private Boolean percursoEmProgresso = false,requisitandoAtualizacaoLocalAtual;
    private Handler handler = new Handler(Looper.getMainLooper());
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Task<LocationSettingsResponse> task;
    private List<Marker> listaMarcadoresAntigo = new LinkedList<Marker>();

    private Local localAntigo = null;

    private List<Local> listaLocaisAntigo = new LinkedList<Local>();


    public static MapaFragment newInstance() {
        return new MapaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.getSupportActionBar().setTitle("Percurso");
        }


        mViewModel = new ViewModelProvider(this).get(MapaViewModel.class);
        informacoesViewModel = new ViewModelProvider(getActivity()).get(InformacoesViewModel.class);

        //iniciando o fusedLoactionProvider que nos permite chamar funções referente a localização
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getLocalAtual(); //<- função que pega a localização
        //como o mapa é um fragment não tem como indexalo através do binding, tendo que pega-lo da forma antiga
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        //setando observadores
        mViewModel.getmListaLocais().observe(getViewLifecycleOwner(), observaNovoLocal);
        mViewModel.getmPercursoID().observe(getViewLifecycleOwner(), observaInsercao);
        mViewModel.getmLocaisInserido().observe(getViewLifecycleOwner(), observaInsercaoLocais);
        mViewModel.getmAtualizaPercurso().observe(getViewLifecycleOwner(), observaAtualizacao);
        mViewModel.getmFinalizaPercurso().observe(getViewLifecycleOwner(), observaFinalizacao);
        mViewModel.getmPercursoAtivo().observe(getViewLifecycleOwner(), observaPercursoAtivo);
        mViewModel.getmListaLocaisIntermediarios().observe(getViewLifecycleOwner(), observaLocaisIntermediarios);

        //fazendo o botão de finalizar ficar desabilitado, só habilitando quando estiver perto do destino
        binding.bMapaFinalizar.setEnabled(false);

        //função que permite requisitar a atualização da localização do usuário
        createLocationRequest();

        //a task a seguir faz a requisição para garantir que temos acesso a localização aproximada atual do usuário
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(requireActivity()    );
        task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(requireActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //se temos não fazemos nada
            }
        });

        task.addOnFailureListener(requireActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        //caso não tivermos pedimos para o usuário
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(requireActivity(),  REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        //precisa estar em um trycatch e caso não tiver nada aqui o catch fica com um erro
                    }
                }
            }
        });


        mViewModel.verificaPercursoAtivo(informacoesViewModel.getmFirebaseUsuarioLogado().getValue().getEmail());

        //mapsearch e o texto de pesquisa de destino
        binding.mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //passa o texto para uma string, no futuro é possível requisitar a cidade do usuário
                // e adicionar ela ao final da String para facilitar achar o local certo
                String destino = binding.mapSearch.getQuery().toString();
                List<Address> addressList;

                if (destino != null && !percursoEmProgresso) {
                    Geocoder geocoder = new Geocoder(getContext());

                    try {
                        //pega o geocoder o local pelo nome digitado
                        addressList = geocoder.getFromLocationName(destino, 1);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(addressList.isEmpty()){
                        //caso não encontre nada no nome digitado avisa o usuário e finaliza a função
                        Toast.makeText(getContext(), "Local não encontrado", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    //pega o endereço da lista
                    Address address = addressList.get(0);
                    //caso já tenha um marcador no destino remove ele para evitar duplicata
                    if (destinoMarcador != null) {
                        destinoMarcador.remove();
                    }
                    //pega a latitude e longitude e passa para um latlang para poder colocar no marcador
                    destinoLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                    //guarda marcador para poder testar e remover caso já exista
                    destinoMarcador = myMap.addMarker(new MarkerOptions().position(destinoLatLng).title("Destino").zIndex(002));

                    //move a camera para mostrar o destino
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinoLatLng, 15));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //aqui podemos adicionar texto auxiliar para ajudar o usuário a encontrar o local que queira
                return false;
            }
        });

        //inicia a atualizar a loalização do usuário
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }
        };
        startLocationUpdates();

        //Botão para iniciar o percurso
        binding.bMapaIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //testa se localização atual e do destino não são nulas
                if (localizacaoAtual != null && destinoLatLng != null) {
                    //verifica se percurso está em progresso
                    if(percursoEmProgresso){
                        AlertDialog.Builder alb = new AlertDialog.Builder(getContext());
                        alb.setTitle("Encerrou percurso no meio");
                        alb.setMessage("Deseja manter ele em aberto para continuar mais tarde?");
                        alb.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Navigation.findNavController(requireView()).popBackStack();
                            }
                        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Date tempoAtual = Calendar.getInstance().getTime();
                                horarioFinalizacao = formatarHora.format(tempoAtual);

                                String emailUsuario = informacoesViewModel.getmUsuarioLogado().getValue().getEmail();


                                Percurso percurso = new Percurso(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude(),
                                        destinoLatLng.latitude, destinoLatLng.longitude, true, horarioInicio,
                                        horarioFinalizacao, emailUsuario);

                                mViewModel.finalizarPercurso(percurso);
                            }
                        });
                        alb.show();
                        //se está troca o texto para o de iniciar, inverte o booleano do teste
                        // e remove os marcadores de lugares que passou
                        binding.bMapaIniciar.setText(R.string.iniciar);
                        percursoEmProgresso = !percursoEmProgresso;
                        for (Marker op: listaMarcadoresAntigo) {
                            op.remove();
                        }
                        listaLocaisAntigo.clear();

                    } else {
                        Date tempoAtual = Calendar.getInstance().getTime();
                        horarioInicio = formatarHora.format(tempoAtual);
                        //se está em progresso troca o texto para o de encerrar, inverte o booleano do teste
                        // e inicia o run para começar a atualizar a localização e ficar registrando locais antigos
                        binding.bMapaIniciar.setText(R.string.encerrar);
                        percursoEmProgresso = !percursoEmProgresso;

                        String emailUsuario = informacoesViewModel.getmUsuarioLogado().getValue().getEmail();

                        Percurso percurso = new Percurso(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude(),
                                destinoLatLng.latitude, destinoLatLng.longitude, false, horarioInicio,
                                "em andamento", emailUsuario);
                        mViewModel.iniciaPercurso(percurso);
                        handler.postDelayed(runnable, 10000);
                    }
                }
            }
        });

        binding.bMapaFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localizacaoAtual != null && destinoLatLng != null) {
                    if (percursoEmProgresso) {
                        Date tempoAtual = Calendar.getInstance().getTime();
                        horarioFinalizacao = formatarHora.format(tempoAtual);

                        String emailUsuario = informacoesViewModel.getmUsuarioLogado().getValue().getEmail();


                        Percurso percurso = new Percurso(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude(),
                                destinoLatLng.latitude, destinoLatLng.longitude, true, horarioInicio,
                                horarioFinalizacao, emailUsuario);

                        mViewModel.finalizarPercurso(percurso);
                    }

                }
            }
        });
    }



    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (localizacaoAtual != null && percursoEmProgresso) {
                localAntigo = new Local(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude(), horarioAtual);
                getLocalAtual();



                handler.postDelayed(this, 60000);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocationUpdates();
        mViewModel.limpaEstado();
        localizacaoAtual = null;
        try {
            Fragment fragment = (getChildFragmentManager()
                    .findFragmentById(R.id.map));
            FragmentTransaction ft = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            ft.remove(fragment);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("REQUESTING_LOCATION_UPDATES_KEY",
                requisitandoAtualizacaoLocalAtual);
        super.onSaveInstanceState(outState);
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(2000)
                .build();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        if (localizacaoAtual != null) {
            LatLng localizacao = new LatLng(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude());
            if(origemMarcador != null){
                origemMarcador.remove();
            }
            MarkerOptions options = new MarkerOptions().position(localizacao).title("Minha localização").zIndex(001);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            origemMarcador = myMap.addMarker(options);

            if(percursoEmProgresso && destinoLatLng!=null) {
                float distanciaDestinoM = Converter.calculaDistancia(localizacaoAtual.getLatitude(), localizacaoAtual.getLatitude(), destinoLatLng.latitude, destinoLatLng.latitude);
                if (distanciaDestinoM <= 10) {
                    binding.bMapaFinalizar.setEnabled(true);
                }
            }

            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacao, 15));
        }
    }

    private void getLocalAtual() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permissão a localização negada", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            localizacaoAtual = location;

                            Date tempoAtual = Calendar.getInstance().getTime();


                            horarioAtual = formatarHora.format(tempoAtual);
                            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

                            mapFragment.getMapAsync(MapaFragment.this);
                            mViewModel.atuallizarLista(localAntigo);
                        } else {
                            Toast.makeText(getContext(), "Não foi possível obter a localização, verifique se está ativa", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    Observer<List<Local>> observaNovoLocal = new Observer<List<Local>>() {
        @Override
        public void onChanged(List<Local> locals) {
            if(percursoEmProgresso) {
                float index = 201;
                float indexDoUltimo = 201;
                if (!listaMarcadoresAntigo.isEmpty()) {
                    indexDoUltimo = 201 + listaMarcadoresAntigo.size();
                }
                for (Local local : locals) {
                    if (indexDoUltimo <= index) {
                        listaLocaisAntigo.add(local);
                        LatLng localizacaoAntiga = new LatLng(local.getLat(), local.getLng());
                        MarkerOptions options = new MarkerOptions().position(localizacaoAntiga).title(local.getNome()).zIndex(index);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        listaMarcadoresAntigo.add(myMap.addMarker(options));
                    }
                    index++;
                }
                Percurso percurso = new Percurso(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude());
                mViewModel.atualizaPercurso(percurso);
            }

        }
    };

    Observer<String> observaInsercao = new Observer<String>() {
        @Override
        public void onChanged(String aString) {
            if(aString!=null) {

            }else{
                Toast.makeText(getContext(), "Ocorreu um erro ao salvar o percurso", Toast.LENGTH_LONG).show();
            }
        }
    };

    Observer<Boolean> observaInsercaoLocais = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if(aBoolean) {
                listaLocaisAntigo.clear();
            }else{
                Toast.makeText(getContext(), "Ocorreu um erro ao salvar os locais do percurso", Toast.LENGTH_LONG).show();
            }
        }
    };

    Observer<Boolean> observaAtualizacao = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if(aBoolean){
                //se foi inserido esvazia a lista
                mViewModel.insereLocaisIntermediarios(listaLocaisAntigo);
            } else {
                Toast.makeText(getContext(), "Não foi possível registrar a atualização do percurso", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Observer<Boolean> observaFinalizacao = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if(aBoolean){
                binding.bMapaIniciar.setText(R.string.iniciar);
                percursoEmProgresso = !percursoEmProgresso;
                for (Marker op : listaMarcadoresAntigo) {
                    op.remove();
                }
                binding.bMapaFinalizar.setEnabled(false);
                if(destinoMarcador!=null) {
                    destinoMarcador.remove();
                }
                Toast.makeText(getContext(), "Percurso finalizaddo com sucesso", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }else{
                Toast.makeText(getContext(), "Ocorreu um erro registrar a finalização do percurso", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Observer<Percurso> observaPercursoAtivo = new Observer<Percurso>() {
        @Override
        public void onChanged(Percurso percurso) {
            if(percurso!=null){
                AlertDialog.Builder alb = new AlertDialog.Builder(getContext());
                alb.setTitle("Encontramos perecurso em aberto!");
                alb.setMessage("Encontramos um percurso em andamento, deseja continuar ele?");
                alb.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.bMapaIniciar.setText(R.string.encerrar);
                        percursoEmProgresso = !percursoEmProgresso;
                        destinoLatLng = new LatLng(percurso.getLatDestino(), percurso.getLngDestino());
                        destinoMarcador = myMap.addMarker(new MarkerOptions().position(destinoLatLng).title("Destino").zIndex(002));
                        mViewModel.getLocaisIntermediarios();
                        handler.post(runnable);
                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date tempoAtual = Calendar.getInstance().getTime();
                        horarioFinalizacao = formatarHora.format(tempoAtual);
                        percurso.setHoraFinal(horarioFinalizacao);
                        percurso.setFinalizado(true);

                        mViewModel.finalizarPercurso(percurso);
                    }
                });
                alb.show();
            }
        }
    };

    Observer<List<Local>> observaLocaisIntermediarios = new Observer<List<Local>>() {
        @Override
        public void onChanged(List<Local> locals) {

            int index = 201;
            for (Local local : locals) {
                LatLng localizacaoMarcador = new LatLng(local.getLat(), local.getLng());
                MarkerOptions optionsInter = new MarkerOptions().position(localizacaoMarcador).title(local.getNome()).zIndex(index);
                optionsInter.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                listaMarcadoresAntigo.add(myMap.addMarker(optionsInter));
                index++;
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(MapaFragment.this);

        }
    };

}