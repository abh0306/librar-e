package com.example.librar_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.librar_e.Model.Books;
import com.example.librar_e.Preferences.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Book;

public class MyBookDetails extends AppCompatActivity {
    private Button updateBTN;
    private ImageView bookImage;
    private TextView title, description, author, ph, year, language, isbn, price;
    private String bookID ="";
    private String sellerUid;
    private Integer n = 1;
    private String dbImage;
    private double dbPrice;
    private DatabaseReference bookRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_details);

        bookID = getIntent().getStringExtra("bid");
        updateBTN = findViewById(R.id.updatebook);
        bookImage = findViewById(R.id.bookpicturedetails);
        title = findViewById(R.id.booktitledetails);
        description = findViewById(R.id.bookdescriptiondetails);
        author = findViewById(R.id.bookauthordetails);
        ph = findViewById(R.id.bookphdetails);
        year = findViewById(R.id.bookyeardetails);
        isbn = findViewById(R.id.bookISBNdetails);
        price = findViewById(R.id.bookpricedetail);
        bookRef = FirebaseDatabase.getInstance().getReference().child("Books").child(bookID);
        getBookDetails();

        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBook();
            }
        });
    }

    private void updateBook() {

        final HashMap<String, Object> bookUpMap = new HashMap<>();
        bookUpMap.put("Title", title.getText().toString());
        bookUpMap.put("Price", Double.parseDouble(price.getText().toString()));
        bookUpMap.put("Author", author.getText().toString());
        bookUpMap.put("Description", description.getText().toString());
        bookUpMap.put("Year", year.getText().toString());
        bookUpMap.put("PH", ph.getText().toString());
        bookUpMap.put("ISBN", isbn.getText().toString());


        bookRef.updateChildren(bookUpMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                            Toast.makeText(MyBookDetails.this, "Dati del libro aggiornati", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                }
            });
    }

    private void getBookDetails()
    {
        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String dbTitle = snapshot.child("Title").getValue().toString();
                    title.setText(dbTitle);

                    String dbDescription = snapshot.child("Description").getValue().toString();
                    description.setText(dbDescription);

                    String dbAuthor = snapshot.child("Author").getValue().toString();
                    author.setText(dbAuthor);

                    String dbPH = snapshot.child("PH").getValue().toString();
                    ph.setText(dbPH);

                    String dbYear = snapshot.child("Year").getValue().toString();
                    year.setText(dbYear);

                    String dbISBN = snapshot.child("ISBN").getValue().toString();
                    isbn.setText(dbISBN);


                    price.setText("" + snapshot.child("Price").getValue());

                    dbImage = snapshot.child("Image").getValue().toString();
                    Picasso.get().load(dbImage).fit().centerCrop().into(bookImage);

                    sellerUid = snapshot.child("SellerUid").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}