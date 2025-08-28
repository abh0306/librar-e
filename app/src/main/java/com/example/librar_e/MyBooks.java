package com.example.librar_e;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Adapters.BooksAdapter;
import com.example.librar_e.Model.Books;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MyBooks extends AppCompatActivity {
    private RecyclerView myBooks;
    private DatabaseReference booksRef;
    private ArrayList books;
    private BooksAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        myBooks = findViewById(R.id.sellerMyBooksShow);

        myBooks.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        myBooks.setLayoutManager(layoutManager);

        books = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        RequestQueue queue = Volley.newRequestQueue(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Books.json?orderBy=\"SellerUid\"&equalTo=\""+uid+"\"";

        //tramite metodo GET ottengo tutti i libri nel database appartenenti al Seller e li inserisco in una RecyclerView (definita in Adapters/BooksAdapter)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        books.clear();
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = String.valueOf(keys.next());
                            JSONObject childObj = null;
                            try {
                                childObj = response.getJSONObject(key);
                                Books book = new Books();
                                book.setTitle(childObj.getString("Title"));
                                book.setImage(childObj.getString("Image"));
                                book.setPrice(Double.parseDouble(childObj.getString("Price")));
                                book.setDescription(childObj.getString("Description"));
                                book.setBookid(childObj.getString("Bookid"));
                                books.add(book);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                myBooks.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new BooksAdapter(getApplicationContext(),books);
                myBooks.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);

    }
}