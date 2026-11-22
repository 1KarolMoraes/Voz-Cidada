package com.example.voz_cidada;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PublicacaoAdapter
        extends RecyclerView.Adapter<PublicacaoAdapter.PublicacaoViewHolder> {

    private final List<DocumentSnapshot> publicacoes;

    public PublicacaoAdapter(List<DocumentSnapshot> publicacoes) {
        this.publicacoes = publicacoes;
    }

    @NonNull
    @Override
    public PublicacaoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_publicacao, parent, false);

        return new PublicacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PublicacaoViewHolder holder,
            int position
    ) {
        DocumentSnapshot publicacao = publicacoes.get(position);

        String categoria = publicacao.getString("categoria");
        String localizacao = publicacao.getString("localizacao");
        String descricao = publicacao.getString("descricao");
        Boolean anonimo = publicacao.getBoolean("anonimo");

        holder.tagCategory.setText(
                categoria != null ? categoria.toUpperCase() : "OUTRO"
        );

        holder.txtLocation.setText(
                localizacao != null ? localizacao : ""
        );

        holder.txtCaption.setText(
                descricao != null ? descricao : ""
        );

        // Respeita a opção de anonimato na interface
        if (Boolean.TRUE.equals(anonimo)) {
            holder.txtUsername.setText("Cidadão anônimo");
        } else {
            holder.txtUsername.setText("Cidadão");
        }

        // Curtidas e comentários ainda não implementados
        holder.txtImpulseCount.setText("0");
        holder.txtCommentCount.setText("0");

        Timestamp criadoEm = publicacao.getTimestamp("criadoEm");

        if (criadoEm != null) {
            SimpleDateFormat formatoData = new SimpleDateFormat(
                    "dd/MM/yyyy 'às' HH:mm",
                    new Locale("pt", "BR")
            );

            holder.txtTime.setText(
                    formatoData.format(criadoEm.toDate())
            );
        } else {
            holder.txtTime.setText("Data indisponível");
        }

        holder.txtToggleComments.setVisibility(View.GONE);

        // Ainda não armazenamos fotografias
        holder.imgPost.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return publicacoes.size();
    }

    static class PublicacaoViewHolder extends RecyclerView.ViewHolder {

        TextView txtUsername;
        TextView txtLocation;
        TextView tagCategory;
        TextView txtCaption;
        TextView txtImpulseCount;
        TextView txtCommentCount;
        TextView txtToggleComments;
        View imgPost;
        TextView txtTime;

        public PublicacaoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            tagCategory = itemView.findViewById(R.id.tagCategory);
            txtCaption = itemView.findViewById(R.id.txtCaption);
            txtImpulseCount = itemView.findViewById(R.id.txtImpulseCount);
            txtCommentCount = itemView.findViewById(R.id.txtCommentCount);
            txtToggleComments = itemView.findViewById(R.id.txtToggleComments);
            imgPost = itemView.findViewById(R.id.imgPost);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
