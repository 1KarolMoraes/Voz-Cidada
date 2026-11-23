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
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.EditText;
import android.widget.Toast;

public class PublicacaoAdapter
        extends RecyclerView.Adapter<PublicacaoAdapter.PublicacaoViewHolder> {

    private final List<DocumentSnapshot> publicacoes;
    private final InteracoesRepository interacoesRepository = new InteracoesRepository();
    private void buscarNomeUsuario(InteracoesRepository.Callback<String> callback) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            callback.onError(new IllegalStateException("Usuário não autenticado."));
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(usuario.getUid())
                .get()
                .addOnSuccessListener(documento -> {
                    String nome = documento.getString("nome");

                    if (nome == null || nome.trim().isEmpty()) {
                        callback.onError(
                                new IllegalStateException("Nome do usuário não encontrado.")
                        );
                        return;
                    }

                    callback.onSuccess(nome.trim());
                })
                .addOnFailureListener(callback::onError);
    }

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
        String publicacaoId = publicacao.getId();
        holder.publicacaoIdAtual = publicacaoId;

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

// Limpa os contadores enquanto os dados são carregados
        holder.txtImpulseCount.setText("...");
        holder.txtCommentCount.setText("...");

// Consulta a quantidade real de curtidas
        interacoesRepository.contarCurtidas(
                publicacaoId,
                new InteracoesRepository.Callback<Long>() {
                    @Override
                    public void onSuccess(Long quantidade) {
                        if (!publicacaoId.equals(holder.publicacaoIdAtual)) {
                            return;
                        }

                        holder.txtImpulseCount.setText(
                                String.valueOf(quantidade)
                        );
                    }

                    @Override
                    public void onError(Exception e) {
                        android.util.Log.e("VOZ_CIDADA", "Erro ao contar curtidas", e);
                        if (!publicacaoId.equals(holder.publicacaoIdAtual)) {
                            return;
                        }

                        holder.txtImpulseCount.setText("-");
                    }
                }
        );

// Consulta a quantidade real de comentários
        interacoesRepository.contarComentarios(
                publicacaoId,
                new InteracoesRepository.Callback<Long>() {
                    @Override
                    public void onSuccess(Long quantidade) {
                        if (!publicacaoId.equals(holder.publicacaoIdAtual)) {
                            return;
                        }

                        holder.txtCommentCount.setText(
                                String.valueOf(quantidade)
                        );
                    }

                    @Override
                    public void onError(Exception e) {
                        android.util.Log.e("VOZ_CIDADA", "Erro ao contar comentários", e);
                        if (!publicacaoId.equals(holder.publicacaoIdAtual)) {
                            return;
                        }

                        holder.txtCommentCount.setText("-");
                    }
                }
        );

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
        holder.iconImpulse.setOnClickListener(v -> {
            holder.iconImpulse.setEnabled(false);

            interacoesRepository.alternarCurtida(
                    publicacaoId,
                    new InteracoesRepository.Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean curtiu) {
                            if (!publicacaoId.equals(holder.publicacaoIdAtual)) {
                                return;
                            }

                            holder.iconImpulse.setEnabled(true);

                            interacoesRepository.contarCurtidas(
                                    publicacaoId,
                                    new InteracoesRepository.Callback<Long>() {
                                        @Override
                                        public void onSuccess(Long quantidade) {
                                            if (publicacaoId.equals(holder.publicacaoIdAtual)) {
                                                holder.txtImpulseCount.setText(
                                                        String.valueOf(quantidade)
                                                );
                                            }
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            if (publicacaoId.equals(holder.publicacaoIdAtual)) {
                                                holder.txtImpulseCount.setText("-");
                                            }
                                        }
                                    }
                            );
                        }

                        @Override
                        public void onError(Exception e) {
                            if (!publicacaoId.equals(holder.publicacaoIdAtual)) {
                                return;
                            }

                            holder.iconImpulse.setEnabled(true);

                            android.widget.Toast.makeText(
                                    holder.itemView.getContext(),
                                    "Não foi possível atualizar a curtida.",
                                    android.widget.Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        });
        holder.txtToggleComments.setVisibility(View.VISIBLE);
        holder.txtToggleComments.setText("Ver comentários");
        holder.txtToggleComments.setOnClickListener(v -> {

            interacoesRepository.listarComentarios(
                    publicacaoId,
                    new InteracoesRepository.Callback<List<Comentario>>() {

                        @Override
                        public void onSuccess(List<Comentario> comentarios) {
                            if (!publicacaoId.equals(holder.publicacaoIdAtual)) {
                                return;
                            }

                            StringBuilder texto = new StringBuilder();

                            if (comentarios.isEmpty()) {
                                texto.append("Nenhum comentário ainda.");
                            } else {
                                for (Comentario comentario : comentarios) {
                                    texto.append(comentario.getAuthorName())
                                            .append(":\n")
                                            .append(comentario.getText())
                                            .append("\n\n");
                                }
                            }

                            EditText campoComentario = new EditText(
                                    holder.itemView.getContext()
                            );
                            campoComentario.setHint("Escreva um comentário");
                            campoComentario.setSingleLine(false);
                            campoComentario.setMaxLines(3);

                            AlertDialog dialog = new AlertDialog.Builder(
                                    holder.itemView.getContext()
                            )
                                    .setTitle("Comentários")
                                    .setMessage(texto.toString().trim())
                                    .setView(campoComentario)
                                    .setNegativeButton("Fechar", null)
                                    .setPositiveButton("Enviar", null)
                                    .create();

                            dialog.setOnShowListener(ignored -> {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                        .setOnClickListener(v -> {
                                            String mensagem = campoComentario.getText()
                                                    .toString().trim();

                                            if (mensagem.isEmpty() || mensagem.length() > 1000) {
                                                campoComentario.setError(
                                                        "Informe um comentário de até 1000 caracteres."
                                                );
                                                return;
                                            }

                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                                    .setEnabled(false);

                                            buscarNomeUsuario(
                                                    new InteracoesRepository.Callback<String>() {
                                                        @Override
                                                        public void onSuccess(String nome) {
                                                            FirebaseUser usuario = FirebaseAuth
                                                                    .getInstance().getCurrentUser();

                                                            if (usuario == null) {
                                                                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                                                        .setEnabled(true);
                                                                Toast.makeText(
                                                                        holder.itemView.getContext(),
                                                                        "Faça login para comentar.",
                                                                        Toast.LENGTH_SHORT
                                                                ).show();
                                                                return;
                                                            }

                                                            Comentario comentario = new Comentario(
                                                                    publicacaoId,
                                                                    usuario.getUid(),
                                                                    nome,
                                                                    mensagem
                                                            );

                                                            interacoesRepository.adicionarComentario(
                                                                    comentario,
                                                                    new InteracoesRepository.Callback<String>() {
                                                                        @Override
                                                                        public void onSuccess(String id) {
                                                                            dialog.dismiss();

                                                                            Toast.makeText(
                                                                                    holder.itemView.getContext(),
                                                                                    "Comentário enviado!",
                                                                                    Toast.LENGTH_SHORT
                                                                            ).show();

                                                                            interacoesRepository.contarComentarios(
                                                                                    publicacaoId,
                                                                                    new InteracoesRepository.Callback<Long>() {
                                                                                        @Override
                                                                                        public void onSuccess(Long quantidade) {
                                                                                            if (publicacaoId.equals(holder.publicacaoIdAtual)) {
                                                                                                holder.txtCommentCount.setText(
                                                                                                        String.valueOf(quantidade)
                                                                                                );
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onError(Exception e) {
                                                                                            if (publicacaoId.equals(holder.publicacaoIdAtual)) {
                                                                                                holder.txtCommentCount.setText("-");
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            );
                                                                        }

                                                                        @Override
                                                                        public void onError(Exception e) {
                                                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                                                                    .setEnabled(true);
                                                                            Toast.makeText(
                                                                                    holder.itemView.getContext(),
                                                                                    "Não foi possível enviar o comentário.",
                                                                                    Toast.LENGTH_SHORT
                                                                            ).show();
                                                                        }
                                                                    }
                                                            );
                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                                                    .setEnabled(true);
                                                            Toast.makeText(
                                                                    holder.itemView.getContext(),
                                                                    "Não foi possível identificar o usuário.",
                                                                    Toast.LENGTH_SHORT
                                                            ).show();
                                                        }
                                                    }
                                            );
                                        });
                            });

                            dialog.show();
                        }

                        @Override
                        public void onError(Exception e) {
                            android.widget.Toast.makeText(
                                    holder.itemView.getContext(),
                                    "Não foi possível carregar os comentários.",
                                    android.widget.Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        });
        holder.iconComment.setOnClickListener(v -> {
            holder.txtToggleComments.performClick();
        });

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
        ImageView iconImpulse;
        ImageView iconComment;
        TextView txtToggleComments;
        View imgPost;
        TextView txtTime;
        String publicacaoIdAtual;

        public PublicacaoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            tagCategory = itemView.findViewById(R.id.tagCategory);
            txtCaption = itemView.findViewById(R.id.txtCaption);
            txtImpulseCount = itemView.findViewById(R.id.txtImpulseCount);
            txtCommentCount = itemView.findViewById(R.id.txtCommentCount);
            iconImpulse = itemView.findViewById(R.id.iconImpulse);
            iconComment = itemView.findViewById(R.id.iconComment);
            txtToggleComments = itemView.findViewById(R.id.txtToggleComments);
            imgPost = itemView.findViewById(R.id.imgPost);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
