package com.example.librar_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Model.CartModel;
import com.example.librar_e.Preferences.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class ConfirmOrder extends AppCompatActivity {
    private EditText NameAndSurname, phoneNumber, Street, HouseNumber, City, PostCode;
    private Button confirmOrder;
    private Double totalPrice;
    private String sellerUid;
    private Integer ready = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        totalPrice = getIntent().getDoubleExtra("totalCartPrice", 0.00);
        NameAndSurname = findViewById(R.id.shippingName);
        phoneNumber = findViewById(R.id.shippingPhone);
        Street = findViewById(R.id.shippingStreet);
        HouseNumber = findViewById(R.id.shippingHouseNumber);
        City = findViewById(R.id.shippingCity);
        PostCode = findViewById(R.id.shippingCAP);

        userDisplayInfos(phoneNumber, Street, HouseNumber, City, PostCode);

        confirmOrder = findViewById(R.id.confirmOrderButton);
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkShippingInfos();
            }
        });
    }

    private void userDisplayInfos(EditText phoneNumber, EditText street, EditText houseNumber, EditText city, EditText postCode) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        RequestQueue queue = Volley.newRequestQueue(ConfirmOrder.this);

        //richiesta tramite metodo GET dei dati dell'utente per velocizzare la conferma dell'ordine riempendo i campi vuoti
        String urlUsrGet = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Users/" + uid + ".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlUsrGet, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String phone = response.getString("phone");
                    String streetReq = response.getString("street");
                    String houseNumberReq = response.getString("houseNumber");
                    String cap = response.getString("cap");
                    String cityReq = response.getString("city");

                    if (!(phone.equals("null"))) {
                        phoneNumber.setText(phone);
                    }
                    if (!(street.equals("null"))) {
                        street.setText(streetReq);
                    }
                    if (!(houseNumber.equals("null"))) {
                        houseNumber.setText(houseNumberReq);
                    }
                    if (!(city.equals("null"))) {
                        city.setText(cityReq);
                    }
                    if (!(cap.equals("null"))) {
                        postCode.setText(cap);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ConfirmOrder.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);
    }
    //controllo che tutti i dati siano stati inseriti
    private void checkShippingInfos() {
        if(TextUtils.isEmpty(NameAndSurname.getText().toString())){
            NameAndSurname.setError("Devi inserire nome e cognome");
            NameAndSurname.requestFocus();
        }
        else if(TextUtils.isEmpty(phoneNumber.getText().toString())){
            phoneNumber.setError("Devi inserire il numero di telefono");
            phoneNumber.requestFocus();
        }
        else if(TextUtils.isEmpty(Street.getText().toString())){
            Street.setError("Devi la via");
            Street.requestFocus();
        }
        else if(TextUtils.isEmpty(HouseNumber.getText().toString())){
            HouseNumber.setError("Devi inserire il numero civico");
            HouseNumber.requestFocus();
        }
        else if(TextUtils.isEmpty(City.getText().toString())){
            City.setError("Devi inserire la città");
            City.requestFocus();
        }
        else if(TextUtils.isEmpty(PostCode.getText().toString())){
            PostCode.setError("Devi inserire il CAP");
            PostCode.requestFocus();
        }
        else{
            confirm();
            Toast.makeText(ConfirmOrder.this, "L'ordine è stato effettuato", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /*
    Funzione per confermare l'ordine chiamata solo quando tutti i dati sono stati inseriti.
    La funzione cancella il carrello dopo aver inserito il corrispettivo ordine nel database.
    L'ordine viene suddiviso in più ordini in base al seller dei vari libri.
    Se vi fossero due seller differenti per i libri richiesti ci sarebbero quindi due ordini differenti.
    L'ordine viene inserito nel database tramite il metodo PUT.
    I libri riguardanti l'ordine vengono aggiunti dopo aver inserito i dati nel database tramite metodo PATCH.
     */
    private void confirm() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        String orderinfos = saveCurrentTime + saveCurrentDate + Preferences.currentOnlineUser.getUid();
        String orderCode = orderinfos.replaceAll("\\s+","");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        RequestQueue queue = Volley.newRequestQueue(ConfirmOrder.this);

        String getUrl = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Cart/"+uid+".json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    Iterator<String> keys = response.keys();
                    while (keys.hasNext()) {
                        String key = String.valueOf(keys.next());
                        JSONObject childObj = null;
                        try {
                            RequestQueue orderQueue = Volley.newRequestQueue(ConfirmOrder.this);
                            childObj = response.getJSONObject(key);

                            String sellerUid = childObj.getString("bookSellerUid");
                            String bookId = childObj.getString("bookID");
                            String pr = childObj.getString("totalprice");

                            //map da inserire sul database per i dati utili al venditore
                            HashMap<String, Object> orderMap = new HashMap<>();
                            orderMap.put("nameAndSurname", NameAndSurname.getText().toString());
                            orderMap.put("phoneNumber", phoneNumber.getText().toString());
                            orderMap.put("street", Street.getText().toString());
                            orderMap.put("houseNumber", HouseNumber.getText().toString());
                            orderMap.put("city", City.getText().toString());
                            orderMap.put("cap", PostCode.getText().toString());
                            orderMap.put("date", saveCurrentDate);
                            orderMap.put("time", saveCurrentTime);
                            orderMap.put("sellerUid", sellerUid);
                            orderMap.put("customerUid", uid);
                            orderMap.put("state", "In attesa del venditore");
                            orderMap.put("total", totalPrice);
                            JSONObject Order = new JSONObject(orderMap);

                            //map dei libri ordinati appartenenti al venditore
                            HashMap<String, Object> orderBookMap = new HashMap<>();
                            orderBookMap.put("bookTitle", childObj.getString("bookName"));
                            orderBookMap.put("quantity", childObj.getString("quantity"));
                            orderBookMap.put("total", childObj.getString("totalprice"));

                            JSONObject OrderBook = new JSONObject(orderBookMap);

                            //inserisco i dettagli dell'ordine nel database
                            String putOrderUrl = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Orders/"+orderCode+sellerUid+".json";
                            JsonObjectRequest OrderReq = new JsonObjectRequest(Request.Method.PUT, putOrderUrl, Order, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (response != null) {
                                        //inserisco i libri appartenenti all'ordine nel database
                                        String putOrderBookUrl = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Orders/"+orderCode+sellerUid+"/Books/"+bookId+".json";
                                        JsonObjectRequest OrderBookReq = new JsonObjectRequest(Request.Method.PATCH, putOrderBookUrl, OrderBook, null, null);
                                        orderQueue.add(OrderBookReq);
                                    }
                                }}, null);
                            String delUrl = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Cart/"+uid+"/"+childObj.getString("bookID")+".json";
                            StringRequest delReq = new StringRequest(Request.Method.DELETE, delUrl,  null, null);
                            orderQueue.add(OrderReq);
                            orderQueue.add(delReq);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }}, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ConfirmOrder.this, error.toString(), Toast.LENGTH_LONG).show(); }
        });
        queue.add(jsonObjectRequest);
    }
}
