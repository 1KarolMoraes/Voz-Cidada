package com.example.voz_cidada;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

/**
 * Tela "Nova Denúncia": o usuário escolhe uma imagem da galeria (sem precisar de permissão).
 */
public class DenunciaActivity extends AppCompatActivity {

    private ShapeableImageView imgPreview;
    private View conteudoVazio;
    private MaterialButton btnEscolherFoto;
    private MaterialButton btnContinuar;

    /** Uri da imagem escolhida (null enquanto nada foi selecionado). */
    private Uri imagemSelecionada;

    private final ActivityResultLauncher<PickVisualMediaRequest> seletorImagem =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    imagemSelecionada = uri;
                    mostrarPreview();
                    Toast.makeText(this, R.string.denuncia_imagem_selecionada, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_denuncia);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        imgPreview = findViewById(R.id.imgPreview);
        conteudoVazio = findViewById(R.id.conteudoVazio);
        btnEscolherFoto = findViewById(R.id.btnEscolherFoto);
        btnContinuar = findViewById(R.id.btnContinuar);

        BarraNavegacao.configurar(this, BarraNavegacao.DENUNCIAR);

        View.OnClickListener escolher = v -> seletorImagem.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
        btnEscolherFoto.setOnClickListener(escolher);
        findViewById(R.id.areaImagem).setOnClickListener(escolher);

        btnContinuar.setOnClickListener(v -> continuar());

        // Voltar com uma imagem escolhida pede confirmação antes de descartar
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (imagemSelecionada == null) {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    return;
                }
                confirmarDescarte();
            }
        });
    }

    private void mostrarPreview() {
        imgPreview.setImageURI(imagemSelecionada);
        imgPreview.setVisibility(View.VISIBLE);
        conteudoVazio.setVisibility(View.GONE);
        btnEscolherFoto.setText(R.string.denuncia_trocar_foto);
        btnContinuar.setVisibility(View.VISIBLE);
    }

    private void continuar() {
        if (imagemSelecionada == null) {
            Toast.makeText(this, R.string.denuncia_nenhuma_imagem, Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO (feature/publicacoes): abrir a próxima etapa (descrição, categoria e
        // localização) passando imagemSelecionada e depois enviar ao Firebase.
    }

    private void confirmarDescarte() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.denuncia_descartar_titulo)
                .setMessage(R.string.denuncia_descartar_mensagem)
                .setPositiveButton(R.string.denuncia_descartar, (dialog, which) -> finish())
                .setNegativeButton(R.string.continuar_editando, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
