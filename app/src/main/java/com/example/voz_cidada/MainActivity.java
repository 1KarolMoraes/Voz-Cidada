
package com.example.voz_cidada;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerPublicacoes;
    private PublicacaoAdapter publicacaoAdapter;
    private List<DocumentSnapshot> listaPublicacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        autenticacao = FirebaseAuth.getInstance();

        FirebaseUser usuario = autenticacao.getCurrentUser();

        // Verifica se existe uma sessão autenticada
        if (usuario == null) {
            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
            return;
        }

        // Habilita o ajuste de tela às barras do sistema
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_feed);

        // Configura a lista de publicações do Feed
        recyclerPublicacoes = findViewById(R.id.recyclerPublicacoes);

        listaPublicacoes = new ArrayList<>();

        publicacaoAdapter = new PublicacaoAdapter(listaPublicacoes);

        recyclerPublicacoes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerPublicacoes.setAdapter(publicacaoAdapter);

        findViewById(R.id.btnEmAlta).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmAltaActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnDenunciar).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DenunciaActivity.class);
            startActivity(intent);
        });

        // Ajusta o conteúdo para não ficar atrás das barras do celular
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.feedRoot), (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    private void carregarPublicacoes() {

        FirebaseFirestore bancoDados = FirebaseFirestore.getInstance();

        bancoDados.collection("publicacoes")
                .orderBy("criadoEm", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(documentos -> {

                    listaPublicacoes.clear();
                    listaPublicacoes.addAll(documentos.getDocuments());

                    publicacaoAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            MainActivity.this,
                            "Erro ao carregar publicações: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Atualiza o Feed ao abrir ou retornar à tela
        if (publicacaoAdapter != null) {
            carregarPublicacoes();
        }
    }

}
