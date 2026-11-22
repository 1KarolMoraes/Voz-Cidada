
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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

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
}
