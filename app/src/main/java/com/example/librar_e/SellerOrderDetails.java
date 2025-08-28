package com.example.librar_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Adapters.OrderBookAdapter;
import com.example.librar_e.Model.BookOrderModel;
import com.example.librar_e.Model.Books;
import com.example.librar_e.Preferences.Preferences;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SellerOrderDetails extends AppCompatActivity {

    private String orderId;
    private TextView orderTotalPrice, customerName, customerAddress, customerPhone, orderState, orderDate;
    private Button updateOrderButton;
    private RecyclerView orderBookRecycler;
    private ArrayList books;
    private double totOrderPrice = 0;
    private OrderBookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_details);
        orderId = getIntent().getStringExtra("orderId");
        totOrderPrice = getIntent().getDoubleExtra("orderTotal", 0.00);

        customerName = findViewById(R.id.customerName);
        customerAddress = findViewById(R.id.customerAddress);
        customerPhone = findViewById(R.id.customerPhoneNumber);
        orderState = findViewById(R.id.orderStateSelector);
        orderDate = findViewById(R.id.sellerOrderDate);
        updateOrderButton = findViewById(R.id.orderUpdateButton);

        orderBookRecycler = findViewById(R.id.orderBookListShow);


        orderTotalPrice = findViewById(R.id.orderTotalPrice);
        orderTotalPrice.setText("Totale: " + totOrderPrice + "€");

        books = new ArrayList<>();

        //utilizzo della liberia firebase auth per ottenere il uid dell'utente
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        RequestQueue queue = Volley.newRequestQueue(SellerOrderDetails.this);

        //richiesta tramite metodo GET dei dati dell'acquirente da mostrare nell'activity
        String urlUsrGet = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Orders/" + orderId + ".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlUsrGet, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String customPhoneNumber = response.getString("phoneNumber");
                    String customName = response.getString("nameAndSurname");
                    String orDate = response.getString("date");
                    String orState = response.getString("state");
                    String street = response.getString("street");
                    String houseNumber = response.getString("houseNumber");
                    String city = response.getString("city");
                    String postCode = response.getString("cap");

                    customerName.setText("Destinatario: " + customName);
                    customerPhone.setText("Telefono: " + customPhoneNumber);
                    customerAddress.setText("Indirizzo: " + street + " " + houseNumber + ", " + postCode + ", " + city);
                    orderDate.setText("Ordine ricevuto il: " + orDate);
                    orderState.setText(orState);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SellerOrderDetails.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);

        orderState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence states[] = new CharSequence[]{
                        "Preso in carico", "In elaborazione", "Spedito"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(SellerOrderDetails.this);
                builder.setTitle("Stato dell'ordine");
                builder.setItems(states, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            orderState.setText("Preso in carico");
                        }
                        if (which == 1) {
                            orderState.setText("In elaborazione");
                        }
                        if (which == 2) {
                            orderState.setText("Spedito");
                        }
                    }
                });
                builder.show();
            }
        });
        updateOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateOrder();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //funzione che permette al Seller di aggiornare tramite metodo PATCH lo stato dell'ordine
    private void updateOrder() throws JSONException {
        HashMap<String, Object> orderMap = new HashMap<>();
        JSONObject state = new JSONObject();
        state.put("state", orderState.getText().toString());

        RequestQueue queue = Volley.newRequestQueue(this);
        String updateUrl = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Orders/" + orderId + ".json";
        JsonObjectRequest OrderBookReq = new JsonObjectRequest(Request.Method.PATCH, updateUrl, state, null, null);
        queue.add(OrderBookReq);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Orders/" + orderId + "/Books.json";

        //tramite GET ottengo i libri appartenenti all'ordine e li inserisco in una RecyclerView (definita in Adapters/OrderBookAdapter)
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
                                BookOrderModel book = new BookOrderModel();
                                book.setTitle(childObj.getString("bookTitle"));
                                book.setPriceTotal(Double.parseDouble(childObj.getString("total")));
                                book.setQuantity(Integer.parseInt(childObj.getString("quantity")));
                                books.add(book);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                orderBookRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new OrderBookAdapter(getApplicationContext(), books);
                orderBookRecycler.setAdapter(adapter);
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