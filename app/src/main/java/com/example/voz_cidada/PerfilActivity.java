package com.example.voz_cidada;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Tela "Perfil": avatar, estatísticas, grade/lista das denúncias e botão de sair.
 */
public class PerfilActivity extends AppCompatActivity {

    /** Enquanto não há dados reais do Firestore, mostra 2 tiles de exemplo. */
    private static final boolean USAR_DADOS_DE_EXEMPLO = true;

    private FirebaseAuth autenticacao;
    private final List<Uri> fotos = new ArrayList<>();
    private FotoGradeAdapter adapter;
    private GridLayoutManager layoutManager;
    private TextView txtQtdDenuncias;
    private TextView txtVazio;
    private RecyclerView recyclerFotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        autenticacao = FirebaseAuth.getInstance();
        FirebaseUser usuario = autenticacao.getCurrentUser();
        if (usuario == null) {
            irParaLogin();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        txtQtdDenuncias = findViewById(R.id.txtQtdDenuncias);
        txtVazio = findViewById(R.id.txtVazio);
        recyclerFotos = findViewById(R.id.recyclerFotos);

        ((TextView) findViewById(R.id.txtUsuario)).setText(nomeDeExibicao(usuario));
        // TODO: ler "impulsionadas" (e cidade) do Firestore quando as publicações existirem
        ((TextView) findViewById(R.id.txtQtdImpulsionadas)).setText("0");

        BarraNavegacao.configurar(this, BarraNavegacao.PERFIL);
        configurarGrade();
        configurarAbas();

        findViewById(R.id.avatarContainer).setOnClickListener(this::mostrarMenuFoto);
        findViewById(R.id.btnSair).setOnClickListener(v -> confirmarSaida());
    }

    private String nomeDeExibicao(FirebaseUser usuario) {
        String nome = usuario.getDisplayName();
        if (nome != null && !nome.trim().isEmpty()) {
            return nome;
        }
        String email = usuario.getEmail();
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf('@'));
        }
        return getString(R.string.perfil_titulo);
    }

    // ---------- Grade / Lista ----------

    private void configurarGrade() {
        if (USAR_DADOS_DE_EXEMPLO) {
            fotos.add(Uri.EMPTY);
            fotos.add(Uri.EMPTY);
        }
        layoutManager = new GridLayoutManager(this, 3);
        adapter = new FotoGradeAdapter(fotos, this::confirmarExclusao);
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
        boolean vazio = fotos.isEmpty();
        txtVazio.setVisibility(vazio ? View.VISIBLE : View.GONE);
        recyclerFotos.setVisibility(vazio ? View.GONE : View.VISIBLE);
    }

    // ---------- Popup Menu do avatar ----------

    private void mostrarMenuFoto(View ancora) {
        PopupMenu menu = new PopupMenu(this, ancora);
        menu.getMenuInflater().inflate(R.menu.menu_foto_perfil, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_trocar_foto) {
                // TODO: abrir o seletor de imagens e salvar a foto de perfil
                Toast.makeText(this, R.string.nav_em_breve, Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_remover_foto) {
                // TODO: remover a foto de perfil salva
                Toast.makeText(this, R.string.nav_em_breve, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        menu.show();
    }

    // ---------- AlertDialogs ----------

    private void confirmarExclusao(int posicao) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.perfil_excluir_titulo)
                .setMessage(R.string.perfil_excluir_mensagem)
                .setPositiveButton(R.string.perfil_excluir, (dialog, which) -> {
                    // TODO: excluir também do Firestore/Storage
                    fotos.remove(posicao);
                    adapter.notifyItemRemoved(posicao);
                    atualizarEstado();
                    Toast.makeText(this, R.string.perfil_excluida, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss())
                .show();
    }

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
