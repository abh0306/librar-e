package com.example.librar_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText editTextEmail;
    private Button resetButton;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextEmail = (EditText)findViewById(R.id.emailresetpwd);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        resetButton = (Button)findViewById(R.id.resetPwdBtn);

        auth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }

        });
    }
    private void resetPassword() {

        String email = editTextEmail.getText().toString().trim();
        if(email.isEmpty()){
            editTextEmail.setError("Non hai un indirizzo e-mail!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("L'email inserita non è valida!");
            editTextEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Segui le istruzioni nella tua email per reimpostare la password", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(ForgotPassword.this, "Qualcosa è andato storto: riprova!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}