package com.example.librar_e;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.librar_e.R;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import com.squareup.picasso.Picasso;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookDetails extends AppCompatActivity {

    private Button addToCartBTN;
    private ImageView bookImage;
    private ImageButton addNumber, removeNumber;
    private TextView title, description, author, ph, year, language, isbn, price, number, descriptionText;
    private String bookID = "";
    private String Title, imgpath, sellerUid;
    private Integer n = 1;
    private Double bPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        bookID = getIntent().getStringExtra("bid");

        addToCartBTN = findViewById(R.id.addtocart);
        bookImage = findViewById(R.id.bookpicturedetails);
        number = findViewById(R.id.number);
        addNumber = findViewById(R.id.addnumber);
        removeNumber = findViewById(R.id.removenumber);
        title = findViewById(R.id.booktitledetails);
        description = findViewById(R.id.bookdescriptiondetails);
        descriptionText = findViewById(R.id.bookdescriptiontext);
        author = findViewById(R.id.bookauthordetails);
        ph = findViewById(R.id.bookphdetails);
        year = findViewById(R.id.bookyeardetails);
        language = findViewById(R.id.booklanguagedetails);
        isbn = findViewById(R.id.bookISBNdetails);
        price = findViewById(R.id.bookpricedetail);

        number.setText("" + n);
        getBookDetails();

        //bottoni per aumentare o diminuire la quantità del libro da aggiungere al carrello
        addNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n++;
                number.setText("" + n);

            }
        });
        removeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (n > 1) {
                    n--;
                    number.setText("" + n);
                }
            }
        });
        addToCartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartList();
            }
        });
    }

    //funzione per aggiungere libri al carrello
    private void addToCartList() {

        double totalPrice = (n * bPrice);
        totalPrice = totalPrice * 100;
        totalPrice = Math.round(totalPrice);
        totalPrice = totalPrice / 100;

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart");
        //utilizzo un json object per effettuare una richiesta put a Firebase e aggiungere il libro
        final Map<String, Object> cartMap = new HashMap<>();
        cartMap.put("bookID", bookID);
        cartMap.put("bookName", Title);
        cartMap.put("quantity", n);
        cartMap.put("singleprice", bPrice);
        cartMap.put("totalprice", totalPrice);
        cartMap.put("image", imgpath);
        cartMap.put("bookSellerUid", sellerUid);

        //converto l'hash map in un oggetto json
        JSONObject cart = new JSONObject(cartMap);

        //utilizzo il metodo POST per inserire l'oggetto json nel database
        String url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Cart/" + Preferences.currentOnlineUser.getUid() + "/" + bookID + ".json";
        RequestQueue queue = Volley.newRequestQueue(BookDetails.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, cart, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                Toast.makeText(BookDetails.this, "Libro aggiunto al carrello", Toast.LENGTH_LONG).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BookDetails.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);
    }


    /*cartListRef.child("User View").child(Preferences.currentOnlineUser.getUid()).child("Books").child(title.getText().toString()).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    cartListRef.child("Seller View").child(Preferences.currentOnlineUser.getUid()).child(sellerUid).child("Books").child(title.getText().toString()).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(BookDetails.this, "Libro aggiunto al carrello", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });
    }*/
    private void getBookDetails() {
        //tramite metodo GET ottengo i dati del libro da firebase
        RequestQueue queue = Volley.newRequestQueue(BookDetails.this);
        String urlUsrGet = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Books/" + bookID + ".json";
        JsonObjectRequest reqGet = new JsonObjectRequest(Request.Method.GET, urlUsrGet, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String bTitle = response.getString("Title");
                    String bAuthor = response.getString("Author");
                    String bDescriptionn = response.getString("Description");
                    String bPH = response.getString("PH");
                    String bYear = response.getString("Year");
                    String bLanguage = response.getString("Language");
                    String bISBN = response.getString("ISBN");
                    sellerUid = response.getString("SellerUid");
                    bPrice = Double.parseDouble(response.getString("Price"));
                    imgpath = response.getString("Image");
                    title.setText(bTitle);
                    Title = bTitle;
                    description.setText(bDescriptionn);
                    author.setText(bAuthor);
                    ph.setText(bPH);
                    year.setText(bYear);
                    language.setText(bLanguage);
                    isbn.setText(bISBN);
                    price.setText(bPrice.toString());
                    if (!(imgpath.equals("null"))) {
                        Picasso.get().load(imgpath).fit().centerCrop().into(bookImage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BookDetails.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(reqGet);
    }
}
