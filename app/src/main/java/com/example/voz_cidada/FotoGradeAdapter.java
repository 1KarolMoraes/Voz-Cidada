package com.example.voz_cidada;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class FotoGradeAdapter
        extends RecyclerView.Adapter<FotoGradeAdapter.PublicacaoViewHolder> {

    public interface OnPublicacaoClick {
        void aoClicar(int posicao);
    }

    private final List<DocumentSnapshot> publicacoes;
    private final OnPublicacaoClick listener;

    public FotoGradeAdapter(
            List<DocumentSnapshot> publicacoes,
            OnPublicacaoClick listener
    ) {
        this.publicacoes = publicacoes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PublicacaoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto_grade, parent, false);

        return new PublicacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PublicacaoViewHolder holder,
            int position
    ) {
        DocumentSnapshot publicacao = publicacoes.get(position);

        String categoria = publicacao.getString("categoria");
        String descricao = publicacao.getString("descricao");

        holder.txtCategoria.setText(
                categoria != null ? categoria : "Outro"
        );

        holder.txtDescricao.setText(
                descricao != null ? descricao : ""
        );

        holder.itemView.setOnClickListener(v -> {
            int posicao = holder.getBindingAdapterPosition();

            if (posicao != RecyclerView.NO_POSITION) {
                listener.aoClicar(posicao);
            }
        });
    }

    @Override
    public int getItemCount() {
        return publicacoes.size();
    }

    static class PublicacaoViewHolder extends RecyclerView.ViewHolder {

        final TextView txtCategoria;
        final TextView txtDescricao;

        PublicacaoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCategoria = itemView.findViewById(R.id.txtCategoriaGrade);
            txtDescricao = itemView.findViewById(R.id.txtDescricaoGrade);
        }
    }
}
