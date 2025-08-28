package com.example.librar_e;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Adapters.OrderBookAdapter;
import com.example.librar_e.Model.BookOrderModel;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;

public class OrderDetails extends AppCompatActivity {

    private String orderId;
    private TextView orderTotalPrice;
    private RecyclerView orderBookRecycler;
    private double totOrderPrice = 0;
    private OrderBookAdapter adapter;
    private ArrayList books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        orderId = getIntent().getStringExtra("orderId");
        totOrderPrice = getIntent().getDoubleExtra("orderTotal", 0.00);

        orderTotalPrice = findViewById(R.id.orderTotalPrice);
        orderTotalPrice.setText("Totale: " +totOrderPrice +"€");

        books = new ArrayList<>();
        orderBookRecycler = findViewById(R.id.orderBookListShow);
    }
    @Override
    protected void onStart() {
        super.onStart();

        //ottengo tramite una richiesta GET i libri appartenenti all'ordine selezionato e li inserisco in una recycler view (definita in Adapters/OrderBookAdapter)
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Orders/"+orderId+"/Books.json";

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
                adapter = new OrderBookAdapter(getApplicationContext(),books);
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