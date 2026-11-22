
package com.example.voz_cidada;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class DetalhesPublicacaoActivity extends AppCompatActivity {

    private ImageView imgPublicacao;
    private ChipGroup grupoCategorias;
    private TextInputEditText inputLocalizacao;
    private TextInputEditText inputDescricao;
    private MaterialSwitch switchAnonimo;
    private MaterialButton btnPublicar;
    private FirebaseAuth autenticacao;
    private FirebaseFirestore bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializa os serviços do Firebase
        autenticacao = FirebaseAuth.getInstance();
        bancoDados = FirebaseFirestore.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhes_publicacao);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets barras = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            barras.left,
                            barras.top,
                            barras.right,
                            barras.bottom
                    );

                    return insets;
                }
        );

        // Conecta os componentes do formulário
        imgPublicacao = findViewById(R.id.imgPublicacao);
        grupoCategorias = findViewById(R.id.grupoCategorias);
        inputLocalizacao = findViewById(R.id.inputLocalizacao);
        inputDescricao = findViewById(R.id.inputDescricao);
        switchAnonimo = findViewById(R.id.switchAnonimo);
        btnPublicar = findViewById(R.id.btnPublicar);

        // Recebe a imagem da tela anterior
        String imagemUri = getIntent().getStringExtra("imagemUri");

        if (imagemUri == null || imagemUri.isEmpty()) {
            finish();
            return;
        }

        // Exibe a fotografia selecionada
        Uri uri = Uri.parse(imagemUri);
        imgPublicacao.setImageURI(uri);

        // Botão Voltar
        findViewById(R.id.btnVoltar).setOnClickListener(
                v -> finish()
        );

        // Botão Publicar
        btnPublicar.setOnClickListener(
                v -> validarPublicacao()
        );
    }

    private void validarPublicacao() {

        // Verifica se uma categoria foi selecionada
        if (grupoCategorias.getCheckedChipId() == -1) {
            Toast.makeText(
                    this,
                    "Selecione uma categoria.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Verifica a localização
        String localizacao = inputLocalizacao.getText() == null
                ? ""
                : inputLocalizacao.getText().toString().trim();

        if (localizacao.isEmpty()) {
            inputLocalizacao.setError("Informe a localização.");
            inputLocalizacao.requestFocus();
            return;
        }

        // Verifica a descrição
        String descricao = inputDescricao.getText() == null
                ? ""
                : inputDescricao.getText().toString().trim();

        if (descricao.isEmpty()) {
            inputDescricao.setError("Informe a descrição.");
            inputDescricao.requestFocus();
            return;
        }

        // Identifica se a publicação será anônima
        boolean anonimo = switchAnonimo.isChecked();


        // Identifica a categoria selecionada
        int categoriaId = grupoCategorias.getCheckedChipId();

        com.google.android.material.chip.Chip chipSelecionado =
                findViewById(categoriaId);

        String categoria = chipSelecionado.getText().toString().trim();

        // Envia os dados validados para o Firestore
        salvarPublicacao(
                categoria,
                localizacao,
                descricao,
                anonimo
        );

    }

    private void salvarPublicacao(
            String categoria,
            String localizacao,
            String descricao,
            boolean anonimo
    ) {
        FirebaseUser usuario = autenticacao.getCurrentUser();

        // Verifica se existe um usuário autenticado
        if (usuario == null) {
            Toast.makeText(
                    this,
                    "Sua sessão expirou. Faça login novamente.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // Organiza os dados da publicação
        Map<String, Object> publicacao = new HashMap<>();

        publicacao.put("autorId", usuario.getUid());
        publicacao.put("categoria", categoria);
        publicacao.put("localizacao", localizacao);
        publicacao.put("descricao", descricao);
        publicacao.put("anonimo", anonimo);
        publicacao.put("criadoEm", FieldValue.serverTimestamp());

        // Evita vários cliques enquanto salva
        btnPublicar.setEnabled(false);

        // Salva a denúncia no Firestore
        bancoDados.collection("publicacoes")
                .add(publicacao)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "Denúncia publicada com sucesso!",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    btnPublicar.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Erro ao publicar: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

}
