package com.example.voz_cidada;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** Adapter da grade/lista de fotos das denúncias do perfil. */
public class FotoGradeAdapter extends RecyclerView.Adapter<FotoGradeAdapter.FotoViewHolder> {

    public interface OnFotoLongClick {
        void aoSegurar(int posicao);
    }

    private final List<Uri> fotos;
    private final OnFotoLongClick listener;

    public FotoGradeAdapter(List<Uri> fotos, OnFotoLongClick listener) {
        this.fotos = fotos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto_grade, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        // setImageURI só carrega Uris locais. Para URLs do Firebase Storage,
        // trocar por uma biblioteca de imagens (ex.: Glide).
        holder.imagem.setImageURI(fotos.get(position));
        holder.imagem.setOnLongClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.aoSegurar(pos);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    static class FotoViewHolder extends RecyclerView.ViewHolder {
        final SquareImageView imagem;

        FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.imgFoto);
        }
    }
}
