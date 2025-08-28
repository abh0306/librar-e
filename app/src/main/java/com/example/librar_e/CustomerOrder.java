package com.example.librar_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Adapters.BooksAdapter;
import com.example.librar_e.Adapters.OrderAdapterCustomer;
import com.example.librar_e.Model.BookOrderModel;
import com.example.librar_e.Model.Books;
import com.example.librar_e.Model.CartModel;
import com.example.librar_e.Model.CustomerOrderModel;
import com.example.librar_e.Model.OrderModel;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class CustomerOrder extends AppCompatActivity implements OrderAdapterCustomer.OnItemClickListener{

    private RecyclerView orderList;
    private ArrayList orders;
    private OrderAdapterCustomer adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        orders = new ArrayList<>();
        orderList = findViewById(R.id.orderCustomerListShow);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //ottengo tramite metodo GET gli ordini effettuati dall'utente per inserirli in una RecyclerView (definita in Adapters/OrderAdapterCustomer)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Orders.json?orderBy=\"customerUid\"&equalTo=\""+uid+"\"";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        orders.clear();
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = String.valueOf(keys.next());
                            JSONObject childObj = null;
                            try {
                                childObj = response.getJSONObject(key);
                                OrderModel order = new OrderModel();
                                order.setTotalPrice(Double.parseDouble(childObj.getString("total")));
                                order.setDate(childObj.getString("date"));
                                order.setTime(childObj.getString("time"));
                                order.setState(childObj.getString("state"));
                                order.setCustomerUid(childObj.getString("customerUid"));
                                order.setSellerUid(childObj.getString("sellerUid"));
                                orders.add(order);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                orderList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new OrderAdapterCustomer(getApplicationContext(),orders);
                orderList.setAdapter(adapter);
                adapter.setOnItemClickListener(CustomerOrder.this);
            }
            },null);
                queue.add(jsonObjectRequest);
            }

    @Override
    public void onItemClick(int position) {
        OrderModel clicked = (OrderModel) orders.get(position);
        startActivity(new Intent(CustomerOrder.this, OrderDetails.class).putExtra("orderId", clicked.getCode()).putExtra("orderTotal", clicked.getTotalPrice()));
    }

}
