package com.example.voz_cidada;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputSenha;
    private Button btnLogin, btnAbaCadastro;
    private TextView txtEsqueciSenha;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = FirebaseAuth.getInstance();

        // Verifica se já existe um usuário autenticado
        FirebaseUser usuario = autenticacao.getCurrentUser();

        if (usuario != null) {
            abrirTelaPrincipal();
            return;
        }

        // Referências aos componentes da tela
        inputEmail = findViewById(R.id.inputEmail);
        inputSenha = findViewById(R.id.inputSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnAbaCadastro = findViewById(R.id.btnAbaCadastro);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);

        // Ação do botão de login
        btnLogin.setOnClickListener(v -> realizarLogin());

        // Navegação para cadastro
        btnAbaCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,
                    CadastroActivity.class);
            startActivity(intent);
        });

        // Recuperação de senha
        txtEsqueciSenha.setOnClickListener(v -> recuperarSenha());
    }

    private void realizarLogin() {

        String email = inputEmail.getText().toString().trim();
        String senha = inputSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this,
                    "Preencha todos os campos.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        autenticacao.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, tarefa -> {

                    btnLogin.setEnabled(true);

                    if (tarefa.isSuccessful()) {
                        Toast.makeText(this,
                                "Login realizado com sucesso!",
                                Toast.LENGTH_SHORT).show();

                        abrirTelaPrincipal();

                    } else {
                        Toast.makeText(this,
                                "Não foi possível realizar o login. Verifique seus dados.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void recuperarSenha() {

        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this,
                    "Informe seu e-mail primeiro.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        autenticacao.sendPasswordResetEmail(email)
                .addOnCompleteListener(tarefa -> {

                    if (tarefa.isSuccessful()) {
                        Toast.makeText(this,
                                "E-mail de recuperação enviado.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "Não foi possível enviar o e-mail.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(LoginActivity.this,
                MainActivity.class);
        startActivity(intent);
        finish();
    }
}