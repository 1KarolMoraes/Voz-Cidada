package com.example.voz_cidada;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

public class EmAltaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_em_alta);

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
    }
}