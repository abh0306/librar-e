package com.example.librar_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Adapters.CartAdapter;
import com.example.librar_e.Model.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;



public class Cart extends AppCompatActivity implements CartAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private Button nextBtn;
    private ArrayList cart;
    CartAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        nextBtn = findViewById(R.id.cartNextButton);

        cart = new ArrayList<>();

        recyclerView = findViewById(R.id.cartListShow);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size()!=0) {
                    startActivity(new Intent(Cart.this, ConfirmOrder.class).putExtra("totalCartPrice", adapter.TotalCalculate(cart)));
                    finish();
                }
                else{
                    Toast.makeText(Cart.this, "Il carrello è vuoto!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    protected void onStart() {

        super.onStart();

        //ottengo tramite metodo GET tutti i libri nel carrello e li inserisco in una RecyclerView (definita in Adapters/CartAdapter)

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Cart/"+uid+".json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = String.valueOf(keys.next()); // this will be your JsonObject key
                            JSONObject childObj = null;
                            try {
                                childObj = response.getJSONObject(key);
                                CartModel book = new CartModel();
                                book.setBookName(childObj.getString("bookName"));
                                book.setImage(childObj.getString("image"));
                                book.setSingleprice(Double.parseDouble(childObj.getString("singleprice")));
                                book.setTotalprice(Double.parseDouble(childObj.getString("totalprice")));
                                book.setBookID(childObj.getString("bookID"));
                                book.setQuantity(Integer.parseInt(childObj.getString("quantity")));
                                book.setBookSellerUid(childObj.getString("bookSellerUid"));
                                cart.add(book);
                                System.out.println("book: " +book);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new CartAdapter(getApplicationContext(),cart);

                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(Cart.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);

    }

    @Override
    public void onItemClick(int position) {
    }
}