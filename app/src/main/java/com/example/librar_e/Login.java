package com.example.librar_e;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import com.example.librar_e.Preferences.Preferences;
import com.example.librar_e.Register;
import com.example.librar_e.ForgotPassword;
import com.example.librar_e.SellerChooseCategory;
import com.example.librar_e.Home;
import io.paperdb.Paper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Preferences.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeUrl;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button loginButton;
    private EditText editTextEmail, editTextPassword;
    private TextView register, forgotPassword;
    private ProgressBar progressBar;
    private CheckBox checkBoxRemember;
    private FirebaseAuth mAuth;
    private String role, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.forgot);
        forgotPassword.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.access_button);
        loginButton.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.emailinput);
        editTextPassword = (EditText) findViewById(R.id.passwordinput);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();

        checkBoxRemember = (CheckBox) findViewById(R.id.checkremember);
        Paper.init(this);

        String UserMail = Paper.book().read(Preferences.usrEmail);
        String UserPass = Paper.book().read(Preferences.usrPwd);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            startActivity(new Intent(this, Register.class));
        } else if (v.getId() == R.id.access_button) {
            login();
        } else if (v.getId() == R.id.forgot) {
            startActivity(new Intent(this, ForgotPassword.class));
        }

    }

    private void login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Non hai un indirizzo e-mail!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("L'email inserita non è valida!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Non hai inserito una password!");
            editTextPassword.requestFocus();
            return;
        }

        //l'accesso, la registrazione e il recupero della password sono stati creati tramite la libreria di firebase per poterne sfruttare le funzioni
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Preferences.currentOnlineUser = user;
                    String uid = user.getUid();
                    if (user.isEmailVerified()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                role = dataSnapshot.child("role").getValue().toString();
                                name = dataSnapshot.child("fullName").getValue().toString();
                                if (checkBoxRemember.isChecked()) {
                                    Paper.book().write(Preferences.usrEmail, email);
                                    Paper.book().write(Preferences.usrPwd, password);
                                }
                                if (dataSnapshot.child("role").exists() && role.equals("seller")) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login.this, "Benvenuto " + name, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Login.this, SellerChooseCategory.class));
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login.this, "Benvenuto " + name, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Login.this, Home.class).putExtra("filter", "0"));
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "La tua email dev'essere verificata per accedere", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Login.this, "I dati inseriti sono errati!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
