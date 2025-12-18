package com.example.aap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aap.databinding.ItemListRowBinding;
import com.example.aap.modelDominio.Percurso;

import java.util.List;

public class PercursoAdapter extends RecyclerView.Adapter<PercursoAdapter.MyViewHolder > {
    private List<Percurso> listaPercursos;
    private PercursoOnClickListener percursoOnClickListener;

    public PercursoAdapter(List<Percurso> listaPercursos, PercursoOnClickListener percursoOnClickListener) {
        this.listaPercursos = listaPercursos;
        this.percursoOnClickListener = percursoOnClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListRowBinding itemListRowBinding = ItemListRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new MyViewHolder(itemListRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Percurso percurso = listaPercursos.get(position);

        holder.itemListRowBinding.tvTituloPercuso.setText("Percurso: "+position);
        holder.itemListRowBinding.tvPercursoInicio.setText("Inicio: "+percurso.getHoraInicio());
        holder.itemListRowBinding.tvPercursoFim.setText("Percurso: "+percurso.getHoraFinal());

        if(percursoOnClickListener != null){
            holder.itemListRowBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    percursoOnClickListener.OnClickPercurso(holder.itemView, position, percurso);
                }
            });
        }

    }

    @Override
    public int getItemCount() {return listaPercursos.size();}

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        public ItemListRowBinding itemListRowBinding;
        public MyViewHolder(@NonNull ItemListRowBinding itemListRowBinding) {
            super(itemListRowBinding.getRoot());
            this.itemListRowBinding = itemListRowBinding;
        }
    }

    public interface PercursoOnClickListener{
        public void OnClickPercurso(View view, int position, Percurso percurso);
    }
}
