package com.example.voz_cidada;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.AggregateSource;

import java.util.Comparator;

public class EmAltaActivity extends AppCompatActivity {
    private RecyclerView recyclerEmAlta;
    private PublicacaoAdapter publicacaoAdapter;
    private List<DocumentSnapshot> listaPublicacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_em_alta);
        recyclerEmAlta = findViewById(R.id.recyclerEmAlta);

        listaPublicacoes = new ArrayList<>();

        publicacaoAdapter = new PublicacaoAdapter(listaPublicacoes);

        recyclerEmAlta.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerEmAlta.setAdapter(publicacaoAdapter);

        // Ajusta o conteúdo para não ficar atrás das barras do celular
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.emAltaRoot), (v, insets) -> {

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
        findViewById(R.id.btnFeed).setOnClickListener(v -> {
            Intent intent = new Intent(EmAltaActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.btnPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(EmAltaActivity.this, PerfilActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnDenunciar).setOnClickListener(v -> {
            Intent intent = new Intent(EmAltaActivity.this, DenunciaActivity.class);
            startActivity(intent);
        });
    }
    private void carregarPublicacoesEmAlta() {
        FirebaseFirestore bancoDados = FirebaseFirestore.getInstance();

        long agora = System.currentTimeMillis();
        long inicioJanela = agora
                - CriterioEmAlta.JANELA_HORAS * 60L * 60L * 1000L;

        bancoDados.collection("publicacoes")
                .whereGreaterThanOrEqualTo(
                        "criadoEm",
                        new Timestamp(new java.util.Date(inicioJanela))
                )
                .get()
                .addOnSuccessListener(documentos -> {

                    List<DocumentSnapshot> publicacoes =
                            new ArrayList<>(documentos.getDocuments());

                    if (publicacoes.isEmpty()) {
                        listaPublicacoes.clear();
                        publicacaoAdapter.notifyDataSetChanged();
                        return;
                    }

                    java.util.Map<String, Long> curtidas =
                            new java.util.HashMap<>();

                    java.util.Map<String, Long> comentarios =
                            new java.util.HashMap<>();

                    java.util.concurrent.atomic.AtomicInteger pendentes =
                            new java.util.concurrent.atomic.AtomicInteger(
                                    publicacoes.size() * 2
                            );

                    Runnable finalizar = () -> {
                        if (pendentes.decrementAndGet() != 0) {
                            return;
                        }

                        publicacoes.sort(
                                Comparator.comparingDouble(
                                        (DocumentSnapshot publicacao) -> {
                                            Timestamp data =
                                                    publicacao.getTimestamp("criadoEm");

                                            if (data == null) {
                                                return 0.0;
                                            }

                                            return CriterioEmAlta.calcularScore(
                                                    curtidas.getOrDefault(
                                                            publicacao.getId(), 0L
                                                    ),
                                                    comentarios.getOrDefault(
                                                            publicacao.getId(), 0L
                                                    ),
                                                    data.toDate().getTime(),
                                                    agora
                                            );
                                        }
                                ).reversed()
                        );

                        listaPublicacoes.clear();
                        listaPublicacoes.addAll(publicacoes);
                        publicacaoAdapter.notifyDataSetChanged();
                    };

                    for (DocumentSnapshot publicacao : publicacoes) {
                        String id = publicacao.getId();

                        bancoDados.collection("publicacoes")
                                .document(id)
                                .collection("likes")
                                .count()
                                .get(AggregateSource.SERVER)
                                .addOnSuccessListener(resultado -> {
                                    curtidas.put(id, resultado.getCount());
                                    finalizar.run();
                                })
                                .addOnFailureListener(e -> {
                                    curtidas.put(id, 0L);
                                    finalizar.run();
                                });

                        bancoDados.collection("publicacoes")
                                .document(id)
                                .collection("comments")
                                .count()
                                .get(AggregateSource.SERVER)
                                .addOnSuccessListener(resultado -> {
                                    comentarios.put(id, resultado.getCount());
                                    finalizar.run();
                                })
                                .addOnFailureListener(e -> {
                                    comentarios.put(id, 0L);
                                    finalizar.run();
                                });
                    }
                })
                .addOnFailureListener(e ->
                        android.widget.Toast.makeText(
                                EmAltaActivity.this,
                                "Não foi possível carregar as publicações Em Alta.",
                                android.widget.Toast.LENGTH_LONG
                        ).show()
                );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (publicacaoAdapter != null) {
            carregarPublicacoesEmAlta();
        }
    }
}