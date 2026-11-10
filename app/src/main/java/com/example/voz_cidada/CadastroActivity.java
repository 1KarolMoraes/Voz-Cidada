package com.example.voz_cidada;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
public class CadastroActivity extends AppCompatActivity {

    private EditText inputNome, inputEmail, inputSenha;
    private Button btnCadastrar, btnAbaEntrar;
    private TextView txtChange;

    private FirebaseAuth autenticacao;
    private FirebaseFirestore bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        autenticacao = FirebaseAuth.getInstance();
        bancoDados = FirebaseFirestore.getInstance();

        inputNome = findViewById(R.id.inputNome);
        inputEmail = findViewById(R.id.inputEmail);
        inputSenha = findViewById(R.id.inputSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnAbaEntrar = findViewById(R.id.btnAbaEntrar);

        btnCadastrar.setOnClickListener(v -> realizarCadastro());

        btnAbaEntrar.setOnClickListener(v -> {
            finish();
        });
    }

    private void realizarCadastro() {

        String nome = inputNome.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String senha = inputSenha.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this,
                    "Preencha todos os campos.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Informe um e-mail válido.");
            return;
        }

        if (senha.length() < 6) {
            inputSenha.setError("A senha deve ter pelo menos 6 caracteres.");
            return;
        }

        btnCadastrar.setEnabled(false);

        autenticacao.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, tarefa -> {

                    if (tarefa.isSuccessful()) {

                        String uid = autenticacao.getCurrentUser().getUid();

                        Map<String, Object> dadosUsuario = new HashMap<>();
                        dadosUsuario.put("nome", nome);
                        dadosUsuario.put("email", email);

                        bancoDados.collection("usuarios")
                                .document(uid)
                                .set(dadosUsuario)
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(this,
                                            "Cadastro realizado com sucesso!",
                                            Toast.LENGTH_SHORT).show();

                                    abrirTelaPrincipal();
                                })
                                .addOnFailureListener(erro -> {

                                    btnCadastrar.setEnabled(true);

                                    Toast.makeText(this,
                                            "Conta criada, mas não foi possível salvar os dados do perfil.",
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {

            btnCadastrar.setEnabled(true);

            Exception erro = tarefa.getException();

            Log.e("CADASTRO_FIREBASE", "Erro ao criar conta", erro);

            String mensagem = erro != null
                    ? erro.getMessage()
                    : "Erro desconhecido";

            Toast.makeText(this,
                    "Erro: " + mensagem,
                    Toast.LENGTH_LONG).show();
        }
                });
    }

    private void abrirTelaPrincipal() {

        Intent intent = new Intent(
                CadastroActivity.this,
                MainActivity.class
        );

        startActivity(intent);
        finish();
    }
}
