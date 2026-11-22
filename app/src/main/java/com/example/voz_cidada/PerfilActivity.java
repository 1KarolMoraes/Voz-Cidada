package com.example.voz_cidada;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

    /** Publicações do usuário. Fica vazia até a integração com as publicações. */
    private final List<Uri> fotos = new ArrayList<>();
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
        configurarAbas();

        findViewById(R.id.avatarContainer).setOnClickListener(this::mostrarMenuFoto);
        findViewById(R.id.btnSair).setOnClickListener(v -> confirmarSaida());

        carregarPerfil();
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

    private void configurarGrade() {
        layoutManager = new GridLayoutManager(this, 3);
        adapter = new FotoGradeAdapter(fotos, posicao -> emDesenvolvimento());
        recyclerFotos.setLayoutManager(layoutManager);
        recyclerFotos.setAdapter(adapter);
        atualizarEstado();
    }

    private void configurarAbas() {
        TabLayout abas = findViewById(R.id.tabs);
        abas.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Grade = 3 colunas, Lista = 1 coluna
                layoutManager.setSpanCount(tab.getPosition() == 0 ? 3 : 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void atualizarEstado() {
        txtQtdDenuncias.setText(String.valueOf(fotos.size()));
        // TODO (publicações): somar os impulsos das publicações do usuário
        txtQtdImpulsionadas.setText("0");

        boolean vazio = fotos.isEmpty();
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
