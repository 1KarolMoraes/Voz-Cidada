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

public class InteracoesRepository {

    private static final String POSTS = "posts";
    private static final String LIKES = "likes";
    private static final String COMMENTS = "comments";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    // Curtir ou descurtir; devolve true se agora está curtido
    public void alternarCurtida(String postId, String userId, Callback<Boolean> callback) {
        DocumentReference postRef = db.collection(POSTS).document(postId);
        DocumentReference likeRef = postRef.collection(LIKES).document(userId);

        db.runTransaction(transaction -> {
            boolean jaCurtiu = transaction.get(likeRef).exists();
            if (jaCurtiu) {
                transaction.delete(likeRef);
                transaction.update(postRef, "likesCount", FieldValue.increment(-1));
                return false;
            } else {
                Map<String, Object> like = new HashMap<>();
                like.put("createdAt", FieldValue.serverTimestamp());
                transaction.set(likeRef, like);
                transaction.update(postRef, "likesCount", FieldValue.increment(1));
                return true;
            }
        }).addOnSuccessListener(callback::onSuccess)
          .addOnFailureListener(callback::onError);
    }

    // Verifica se o usuário já curtiu o post
    public void usuarioJaCurtiu(String postId, String userId, Callback<Boolean> callback) {
        db.collection(POSTS).document(postId)
                .collection(LIKES).document(userId).get()
                .addOnSuccessListener(doc -> callback.onSuccess(doc.exists()))
                .addOnFailureListener(callback::onError);
    }

    // Grava o comentário e soma 1 no contador do post, na mesma operação
    public void adicionarComentario(Comentario comentario, Callback<String> callback) {
        DocumentReference postRef = db.collection(POSTS).document(comentario.getPostId());
        DocumentReference comentarioRef = postRef.collection(COMMENTS).document();

        Map<String, Object> dados = new HashMap<>();
        dados.put("postId", comentario.getPostId());
        dados.put("authorId", comentario.getAuthorId());
        dados.put("authorName", comentario.getAuthorName());
        dados.put("text", comentario.getText());
        dados.put("createdAt", FieldValue.serverTimestamp());

        WriteBatch batch = db.batch();
        batch.set(comentarioRef, dados);
        batch.update(postRef, "commentsCount", FieldValue.increment(1));
        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess(comentarioRef.getId()))
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
