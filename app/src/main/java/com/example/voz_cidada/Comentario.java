package com.example.voz_cidada;

import java.util.Date;

public class Comentario {

    private String id;
    private String postId;
    private String authorId;
    private String authorName;
    private String text;
    private Date createdAt;

    public Comentario() {
        // Construtor vazio exigido pelo Firestore
    }

    public Comentario(String postId, String authorId, String authorName, String text) {
        this.postId = postId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.text = text;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
