package com.example.librar_e;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.librar_e.Model.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.librar_e.Model.User;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private ImageView logo;
    private Button createAccountBtn;
    private EditText editTextName, editTextEmail, editTextPassword;
    private String role = "user";
    private ProgressBar progressBar;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private final String USER = "user";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USER);
        mAuth = FirebaseAuth.getInstance();

        logo = (ImageView) findViewById(R.id.librare_logo);

        createAccountBtn = (Button) findViewById(R.id.create_account_button);
        createAccountBtn.setOnClickListener(this);

        editTextName = (EditText) findViewById(R.id.nameinput);
        editTextEmail = (EditText) findViewById(R.id.emailinput);
        editTextPassword = (EditText) findViewById(R.id.passwordinput);
        progressBar = (ProgressBar) findViewById(R.id.progress);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.create_account_button) {
            registerUser();
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String fullName = editTextName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String role = "user";
        String street = "null";
        String houseNumber = "null";
        String city = "null";
        String cap = "null";
        String phone = "null";
        String image = "null";

        if (fullName.isEmpty()) {
            editTextName.setError("Non hai inserito il tuo nome!");
            editTextName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Non hai inserito il tuo indirizzo e-mail!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Non hai inserito un'email valida!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Non hai inserito una password!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("La password dev'essere lunga almeno 6 caratteri!");
            editTextPassword.requestFocus();
            return;
        }

        user = new User(fullName, email, role, phone, image, street, houseNumber, city, cap);
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.sendEmailVerification();
                                Toast.makeText(Register.this, "Vai alla tua mail per completare la registrazione", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                finish();
                            } else {
                                Toast.makeText(Register.this, "Registrazione fallita. Riprova", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(Register.this, "Registrazione fallita. Riprova", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
