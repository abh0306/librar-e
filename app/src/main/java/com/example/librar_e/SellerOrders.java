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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Adapters.OrderAdapterCustomer;
import com.example.librar_e.Adapters.OrderAdapterSeller;
import com.example.librar_e.Model.OrderModel;
import com.example.librar_e.Model.SellerOrderModel;
import com.example.librar_e.Preferences.Preferences;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SellerOrders extends AppCompatActivity implements OrderAdapterSeller.OnItemClickListener {

    private RecyclerView orderList;
    private DatabaseReference ordersRef;
    private ArrayList orders;
    private OrderAdapterSeller adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("Sellers").child(Preferences.currentOnlineUser.getUid());
        orderList = findViewById(R.id.orderListShow);
        orderList.setLayoutManager(new LinearLayoutManager(this));

        orders = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //ottengo tramite GET gli ordini ricevuti dal Seller e li inserisco in una recyclerview (definita in Adapters/OrderAdapterSeller)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Orders.json?orderBy=\"sellerUid\"&equalTo=\""+uid+"\"";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        orders.clear();
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = String.valueOf(keys.next()); // this will be your JsonObject key
                            JSONObject childObj = null;
                            try {
                                childObj = response.getJSONObject(key);
                                OrderModel order = new OrderModel();
                                order.setTotalPrice(Double.parseDouble(childObj.getString("total")));
                                order.setDate(childObj.getString("date"));
                                order.setTime(childObj.getString("time"));
                                order.setState(childObj.getString("state"));
                                order.setNameAndSurname(childObj.getString("nameAndSurname"));
                                order.setPhoneNumber(childObj.getString("phoneNumber"));
                                order.setStreet(childObj.getString("street"));
                                order.setHouseNumber(childObj.getString("houseNumber"));
                                order.setCity(childObj.getString("city"));
                                order.setCap(childObj.getString("cap"));
                                order.setCustomerUid(childObj.getString("customerUid"));
                                order.setSellerUid(childObj.getString("sellerUid"));
                                System.out.println("CODICE: " +order.getCode());
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
                adapter = new OrderAdapterSeller(getApplicationContext(),orders);
                orderList.setAdapter(adapter);
                adapter.setOnItemClickListener(SellerOrders.this);
            }
        },null);
        queue.add(jsonObjectRequest);
    }
    //passo all'activity SellerOrderDetails passando come extra l'id dell'ordine e il totale
    @Override
    public void onItemClick(int position) {
        OrderModel clicked = (OrderModel) orders.get(position);
        startActivity(new Intent(SellerOrders.this, SellerOrderDetails.class).putExtra("orderId", clicked.getCode()).putExtra("orderTotal", clicked.getTotalPrice()));
    }




        /*FirebaseRecyclerOptions<SellerOrderModel> options = new FirebaseRecyclerOptions.Builder<SellerOrderModel>()
                .setQuery(ordersRef, SellerOrderModel.class).build();

        FirebaseRecyclerAdapter<SellerOrderModel, SellerOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<SellerOrderModel, SellerOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SellerOrdersViewHolder holder, int position, @NonNull SellerOrderModel model) {
                holder.holderName.setText("Destinatario: " +model.getNameAndSurname());
                holder.holderPhone.setText("Telefono: " +model.getPhoneNumber());
                holder.holderTotal.setText("Totale: " +model.getTotalPrice() +"€");
                holder.holderDate.setText(model.getDate());
                holder.holderAddress.setText("Indirizzo: " +model.getStreet() + " " + model.getHouseNumber() + ", " + model.getCity() + ", " +model.getCap());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(SellerOrders.this, SellerOrderDetails.class).putExtra("orderTotal", model.getTotalPrice()).putExtra("orderId", model.getCode()));

                    }
                });
            }
            @NonNull
            @Override
            public SellerOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                return new SellerOrdersViewHolder(view);
            }
        };

        orderList.setAdapter(adapter);
        adapter.startListening();*/
    }

    /*public static class SellerOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView holderName, holderPhone, holderAddress, holderDate, holderTotal;

        public SellerOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            holderName = itemView.findViewById(R.id.order_name);
            holderPhone = itemView.findViewById(R.id.order_phone);
            holderAddress = itemView.findViewById(R.id.order_address);
            holderDate = itemView.findViewById(R.id.orderDate);
            holderTotal = itemView.findViewById(R.id.book_item_price_order);
        }
    }
     */

