package com.example.voz_cidada;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InteracoesRepository {

    private static final String POSTS = "publicacoes";
    private static final String LIKES = "likes";
    private static final String COMMENTS = "comments";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    // Curtir ou descurtir; devolve true se agora está curtido
    public void alternarCurtida(String postId, Callback<Boolean> callback) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            callback.onError(new IllegalStateException(
                    "É necessário estar logado para curtir uma publicação."
            ));
            return;
        }

        String userId = usuario.getUid();

        DocumentReference postRef = db.collection(POSTS).document(postId);
        DocumentReference likeRef = postRef.collection(LIKES).document(userId);

        db.runTransaction(transaction -> {
            boolean jaCurtiu = transaction.get(likeRef).exists();
            if (jaCurtiu) {
                transaction.delete(likeRef);

                return false;
            } else {
                Map<String, Object> like = new HashMap<>();
                like.put("createdAt", FieldValue.serverTimestamp());
                transaction.set(likeRef, like);

                return true;
            }
        }).addOnSuccessListener(callback::onSuccess)
          .addOnFailureListener(callback::onError);
    }

    // Verifica se o usuário já curtiu o post
    public void usuarioJaCurtiu(String postId, Callback<Boolean> callback) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            callback.onError(new IllegalStateException(
                    "É necessário estar logado para verificar curtidas."
            ));
            return;
        }

        String userId = usuario.getUid();

        db.collection(POSTS).document(postId)
                .collection(LIKES).document(userId).get()
                .addOnSuccessListener(doc -> callback.onSuccess(doc.exists()))
                .addOnFailureListener(callback::onError);
    }
    public void contarCurtidas(String postId, Callback<Long> callback) {
        db.collection(POSTS)
                .document(postId)
                .collection(LIKES)
                .count()
                .get(com.google.firebase.firestore.AggregateSource.SERVER)
                .addOnSuccessListener(snapshot ->
                        callback.onSuccess(snapshot.getCount()))
                .addOnFailureListener(callback::onError);
    }

    // Grava o comentário e soma 1 no contador do post, na mesma operação
    public void adicionarComentario(Comentario comentario, Callback<String> callback) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            callback.onError(new IllegalStateException(
                    "É necessário estar logado para comentar."
            ));
            return;
        }

        DocumentReference postRef = db.collection(POSTS).document(comentario.getPostId());
        DocumentReference comentarioRef = postRef.collection(COMMENTS).document();

        Map<String, Object> dados = new HashMap<>();
        dados.put("postId", comentario.getPostId());
        dados.put("authorId", usuario.getUid());
        dados.put("authorName", comentario.getAuthorName());
        dados.put("text", comentario.getText());
        dados.put("createdAt", FieldValue.serverTimestamp());

        WriteBatch batch = db.batch();
        batch.set(comentarioRef, dados);

        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess(comentarioRef.getId()))
                .addOnFailureListener(callback::onError);
    }
    public void contarComentarios(String postId, Callback<Long> callback) {
        db.collection(POSTS)
                .document(postId)
                .collection(COMMENTS)
                .count()
                .get(com.google.firebase.firestore.AggregateSource.SERVER)
                .addOnSuccessListener(snapshot ->
                        callback.onSuccess(snapshot.getCount()))
                .addOnFailureListener(callback::onError);
    }
    // Lista os comentários do post, do mais antigo para o mais novo
    public void listarComentarios(String postId, Callback<List<Comentario>> callback) {
        db.collection(POSTS).document(postId).collection(COMMENTS)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Comentario> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Comentario c = doc.toObject(Comentario.class);
                        c.setId(doc.getId());
                        lista.add(c);
                    }
                    callback.onSuccess(lista);
                })
                .addOnFailureListener(callback::onError);
    }
}
