package com.example.librar_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.librar_e.Preferences.Preferences;
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

import io.paperdb.Paper;

public class LoadScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mAuth = FirebaseAuth.getInstance();
        Paper.init(this);
        String UserMail = Paper.book().read(Preferences.usrEmail);
        String UserPass = Paper.book().read(Preferences.usrPwd);
        int secondsDelayed = 1;


        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!TextUtils.isEmpty(UserMail) && !TextUtils.isEmpty(UserPass)) {
                        AllowAccess(UserMail, UserPass);
                    }
                else {
                    Paper.book().write(Preferences.usrEmail, "");
                    Paper.book().write(Preferences.usrPwd, "");
                    startActivity(new Intent(LoadScreen.this, Login.class));
                    finish();
                }
            }

            private void AllowAccess(String UserMail, String UserPass) {
                mAuth.signInWithEmailAndPassword(UserMail, UserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String role = dataSnapshot.child("role").getValue().toString();
                                String name = dataSnapshot.child("fullName").getValue().toString();
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoadScreen.this, "Bentornato " + name , Toast.LENGTH_LONG).show();
                                    Preferences.currentOnlineUser = user;
                                    if (role.equals("seller")) {
                                        startActivity(new Intent(LoadScreen.this, SellerChooseCategory.class));
                                        finish();
                                    }
                                    else {
                                        startActivity(new Intent(LoadScreen.this, Home.class).putExtra("filter","0"));
                                        finish();
                                    }
                                }
                                else{
                                    Paper.book().write(Preferences.usrEmail, "");
                                    Paper.book().write(Preferences.usrPwd, "");
                                    startActivity(new Intent(LoadScreen.this, Login.class));
                                    finish();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });
            }
        }, secondsDelayed * 500);
    }
}

