package com.example.librar_e;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.librar_e.Preferences.Preferences;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class SellerChooseCategory extends AppCompatActivity{
    private ImageButton libri, fumetti, riviste, istruzione, guide, enciclopedie, userButton;
    private Button checkOrdersButton, myBooksButton;

    //menù principale per il seller dove può accedere alle varie funzioni per il suo account

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_category);
        userButton = (ImageButton) findViewById(R.id.user);
        checkOrdersButton = findViewById(R.id.checkOrdersButton);
        libri = findViewById(R.id.libri);
        fumetti = findViewById(R.id.comics);
        riviste= findViewById(R.id.magazines);
        istruzione = findViewById(R.id.education);
        guide = findViewById(R.id.guides);
        enciclopedie = findViewById(R.id.encyclopedia);
        myBooksButton=findViewById(R.id.myBooksButton);

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().write(Preferences.usrEmail, "");
                Paper.book().write(Preferences.usrPwd, "");
                Paper.book().write(Preferences.fullName, "");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SellerChooseCategory.this, Login.class));
                finish();
            }
        });

        checkOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerChooseCategory.this, SellerOrders.class));
            }
        });

        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerChooseCategory.this, MyBooks.class));
            }
        });

        libri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerChooseCategory.this, SellerAddBook.class);
                intent.putExtra("category", "Libri");
                startActivity(intent);
            }
        });
        fumetti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerChooseCategory.this, SellerAddBook.class);
                intent.putExtra("category", "Fumetti");
                startActivity(intent);
            }
        });
        riviste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerChooseCategory.this, SellerAddBook.class);
                intent.putExtra("category", "Riviste");
                startActivity(intent);
            }
        });
        istruzione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerChooseCategory.this, SellerAddBook.class);
                intent.putExtra("category", "Istruzione");
                startActivity(intent);
            }
        });
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerChooseCategory.this, SellerAddBook.class);
                intent.putExtra("category", "Guide");
                startActivity(intent);
            }
        });
        enciclopedie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerChooseCategory.this, SellerAddBook.class);
                intent.putExtra("category", "Enciclopedie");
                startActivity(intent);
            }
        });
    }
}