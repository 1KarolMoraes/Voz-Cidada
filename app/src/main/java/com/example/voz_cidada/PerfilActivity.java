package com.example.voz_cidada;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela "Perfil": avatar, estatísticas, grade/lista das denúncias e botão de sair.
 * O nome vem do documento usuarios/{uid} do Firestore.
 */
public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = "PERFIL";
    private static final String COLECAO_USUARIOS = "usuarios";
    private static final String CAMPO_NOME = "nome";
    private static final String CAMPO_CIDADE = "cidade";

    private FirebaseAuth autenticacao;
    private FirebaseFirestore bancoDados;
    private FirebaseUser usuario;

    /** Publicações pertencentes ao usuário autenticado. */
    private final List<DocumentSnapshot> publicacoes = new ArrayList<>();
    private FotoGradeAdapter adapter;
    private GridLayoutManager layoutManager;

    private TextView txtUsuario;
    private TextView txtLocal;
    private TextView txtAviso;
    private TextView txtQtdDenuncias;
    private TextView txtQtdImpulsionadas;
    private TextView txtVazio;
    private RecyclerView recyclerFotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        autenticacao = FirebaseAuth.getInstance();
        usuario = autenticacao.getCurrentUser();
        if (usuario == null) {
            irParaLogin();
            return;
        }
        bancoDados = FirebaseFirestore.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        txtUsuario = findViewById(R.id.txtUsuario);
        txtLocal = findViewById(R.id.txtLocal);
        txtAviso = findViewById(R.id.txtAviso);
        txtQtdDenuncias = findViewById(R.id.txtQtdDenuncias);
        txtQtdImpulsionadas = findViewById(R.id.txtQtdImpulsionadas);
        txtVazio = findViewById(R.id.txtVazio);
        recyclerFotos = findViewById(R.id.recyclerFotos);


        BarraNavegacao.configurar(this, BarraNavegacao.PERFIL);
        configurarGrade();

        findViewById(R.id.avatarContainer).setOnClickListener(this::mostrarMenuFoto);
        findViewById(R.id.btnSair).setOnClickListener(v -> confirmarSaida());

        carregarPerfil();
        carregarPublicacoes();
    }

    private void carregarPublicacoes() {
        bancoDados.collection("autoriasPublicacoes")
                .whereEqualTo("autorId", usuario.getUid())
                .get()
                .addOnSuccessListener(this, autorias -> {

                    publicacoes.clear();
                    adapter.notifyDataSetChanged();
                    atualizarEstado();

                    if (autorias.isEmpty()) {
                        return;
                    }

                    List<com.google.android.gms.tasks.Task<DocumentSnapshot>> consultas =
                            new ArrayList<>();

                    for (DocumentSnapshot autoria : autorias.getDocuments()) {
                        consultas.add(
                                bancoDados.collection("publicacoes")
                                        .document(autoria.getId())
                                        .get()
                        );
                    }

                    Tasks.whenAllSuccess(consultas)
                            .addOnSuccessListener(this, resultados -> {
                                publicacoes.clear();

                                for (Object resultado : resultados) {
                                    DocumentSnapshot documento =
                                            (DocumentSnapshot) resultado;

                                    if (documento.exists()) {
                                        publicacoes.add(documento);
                                    }
                                }

                                publicacoes.sort((a, b) -> {
                                    com.google.firebase.Timestamp dataA =
                                            a.getTimestamp("criadoEm");
                                    com.google.firebase.Timestamp dataB =
                                            b.getTimestamp("criadoEm");

                                    if (dataA == null && dataB == null) return 0;
                                    if (dataA == null) return 1;
                                    if (dataB == null) return -1;

                                    return dataB.compareTo(dataA);
                                });

                                adapter.notifyDataSetChanged();
                                atualizarEstado();
                            })
                            .addOnFailureListener(this, erro -> {
                                Log.e(TAG, "Erro ao buscar denúncias", erro);
                                Toast.makeText(this,
                                        "Não foi possível carregar suas denúncias.",
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(this, erro -> {
                    Log.e(TAG, "Erro ao consultar autorias", erro);
                    Toast.makeText(this,
                            "Não foi possível carregar suas publicações.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    // ---------- Dados do usuário (Firestore) ----------

    private void carregarPerfil() {
        txtUsuario.setText(R.string.perfil_carregando);
        txtAviso.setVisibility(View.GONE);
        txtLocal.setVisibility(View.GONE);

        bancoDados.collection(COLECAO_USUARIOS)
                .document(usuario.getUid())
                .get()
                .addOnSuccessListener(this, documento -> {
                    if (!documento.exists()) {
                        mostrarNomeAlternativo();
                        mostrarAviso(R.string.perfil_sem_documento, false);
                        return;
                    }

                    String nome = documento.getString(CAMPO_NOME);
                    if (nome == null || nome.trim().isEmpty()) {
                        mostrarNomeAlternativo();
                        mostrarAviso(R.string.perfil_sem_nome, false);
                    } else {
                        txtUsuario.setText(nome.trim());
                    }

                    String cidade = documento.getString(CAMPO_CIDADE);
                    if (cidade != null && !cidade.trim().isEmpty()) {
                        txtLocal.setText(cidade.trim());
                        txtLocal.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(this, erro -> {
                    Log.e(TAG, "Erro ao ler usuarios/" + usuario.getUid(), erro);
                    mostrarNomeAlternativo();
                    mostrarAviso(R.string.perfil_erro_carregar, true);
                });
    }

    /** Nome provisório (Authentication) enquanto o do Firestore não está disponível. */
    private void mostrarNomeAlternativo() {
        String nome = usuario.getDisplayName();
        if (nome == null || nome.trim().isEmpty()) {
            String email = usuario.getEmail();
            if (email != null && email.contains("@")) {
                nome = email.substring(0, email.indexOf('@'));
            } else {
                nome = getString(R.string.perfil_titulo);
            }
        }
        txtUsuario.setText(nome);
    }

    private void mostrarAviso(int mensagem, boolean permiteTentarDeNovo) {
        txtAviso.setText(mensagem);
        txtAviso.setVisibility(View.VISIBLE);
        txtAviso.setClickable(permiteTentarDeNovo);
        txtAviso.setOnClickListener(permiteTentarDeNovo ? v -> carregarPerfil() : null);
    }

    // ---------- Grade / Lista ----------


    // ---------- Grade de publicações ----------

    private void configurarGrade() {
        layoutManager = new GridLayoutManager(this, 3);

        adapter = new FotoGradeAdapter(publicacoes, posicao -> {
            // A abertura dos detalhes será implementada depois.
            emDesenvolvimento();
        });

        recyclerFotos.setLayoutManager(layoutManager);
        recyclerFotos.setAdapter(adapter);

        atualizarEstado();
    }

    private void atualizarEstado() {
        txtQtdDenuncias.setText(String.valueOf(publicacoes.size()));

        // Contagem de impulsos ainda não implementada no perfil.
        txtQtdImpulsionadas.setText("0");

        boolean vazio = publicacoes.isEmpty();

        txtVazio.setVisibility(vazio ? View.VISIBLE : View.GONE);
        recyclerFotos.setVisibility(vazio ? View.GONE : View.VISIBLE);
    }

    // ---------- Ações ainda não implementadas ----------

    private void mostrarMenuFoto(View ancora) {
        PopupMenu menu = new PopupMenu(this, ancora);
        menu.getMenuInflater().inflate(R.menu.menu_foto_perfil, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_trocar_foto || id == R.id.action_remover_foto) {
                // TODO: salvar/remover a foto de perfil no Storage quando for implementado
                emDesenvolvimento();
                return true;
            }
            return false;
        });
        menu.show();
    }

    private void emDesenvolvimento() {
        Toast.makeText(this, R.string.recurso_em_desenvolvimento, Toast.LENGTH_SHORT).show();
    }

    // ---------- Sair da conta ----------

    private void confirmarSaida() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.perfil_sair_titulo)
                .setMessage(R.string.perfil_sair_mensagem)
                .setPositiveButton(R.string.perfil_sair_confirmar, (dialog, which) -> {
                    autenticacao.signOut();
                    irParaLogin();
                })
                .setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void irParaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
