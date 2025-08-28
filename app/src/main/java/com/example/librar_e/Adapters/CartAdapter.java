package com.example.librar_e.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Cart;
import com.example.librar_e.Home;
import com.example.librar_e.Model.CartModel;
import com.example.librar_e.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Interface.ItemClickListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Cart;
import com.example.librar_e.Home;
import com.example.librar_e.Model.CartModel;
import com.example.librar_e.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Interface.ItemClickListener;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    OnItemClickListener mlistener;
    Context mcontext;
    LayoutInflater inflater;
    Double total;
    List<CartModel> cart;

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public CartAdapter(Context ctx, List<CartModel> cart) {
        this.inflater = LayoutInflater.from(ctx);
        this.cart = cart;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.book_cart_items_layout, parent, false);
        total = 0.00;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bookItemNameCart.setText(cart.get(position).getBookName());
        holder.bookItemQuantityCart.setText(cart.get(position).getQuantity().toString());
        String result = String.format("%.2f", cart.get(position).getTotalprice());
        holder.bookItemPriceCart.setText(result + "€");
        total = total + cart.get(position).getTotalprice();

        holder.removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    RequestQueue queue = Volley.newRequestQueue(mcontext);
                    String url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Cart/" + uid + "/" + cart.get(pos).getBookID() + ".json";
                    StringRequest request = new StringRequest(Request.Method.DELETE, url, null, null);
                    queue.add(request);
                    Toast.makeText(mcontext, "Libro rimosso dal carrello", Toast.LENGTH_SHORT).show();
                    cart.remove(pos);
                    notifyDataSetChanged();
                }
            }
        });

        holder.addQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Integer quantity = Integer.parseInt(holder.bookItemQuantityCart.getText().toString());
                    Double price = cart.get(pos).getSingleprice();
                    quantity++;
                    price = price * quantity;
                    cart.get(pos).setQuantity(quantity);
                    cart.get(pos).setTotalprice(price);
                    notifyDataSetChanged();

                    //modifico la quantità e prezzo totale del libro nel database
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    RequestQueue queue = Volley.newRequestQueue(mcontext);
                    String updateUrl = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Cart/" + uid + "/" + cart.get(pos).getBookID() + ".json";
                    HashMap<String, Object> updater = new HashMap<>();
                    updater.put("quantity", quantity);
                    updater.put("totalprice", price);
                    JSONObject updaterJ = new JSONObject(updater);
                    JsonObjectRequest OrderBookReq = new JsonObjectRequest(Request.Method.PATCH, updateUrl, updaterJ, null, null);
                    queue.add(OrderBookReq);
                }
            }
        });

        holder.removeQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Integer quantity = Integer.parseInt(holder.bookItemQuantityCart.getText().toString());
                    if (quantity > 1) {
                        Double price = cart.get(pos).getSingleprice();
                        quantity--;
                        price = price * quantity;
                        cart.get(pos).setQuantity(quantity);
                        cart.get(pos).setTotalprice(price);
                        notifyDataSetChanged();

                        //modifico la quantità e prezzo totale del libro nel database
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        RequestQueue queue = Volley.newRequestQueue(mcontext);
                        String updateUrl = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Cart/" + uid + "/" + cart.get(pos).getBookID() + ".json";
                        HashMap<String, Object> updater = new HashMap<>();
                        updater.put("quantity", quantity);
                        updater.put("totalprice", price);
                        JSONObject updaterJ = new JSONObject(updater);
                        JsonObjectRequest OrderBookReq = new JsonObjectRequest(Request.Method.PATCH, updateUrl, updaterJ, null, null);
                        queue.add(OrderBookReq);
                    }
                }
            }
        });

        Picasso.get().load(cart.get(position).getImage()).fit().centerCrop().into(holder.bookItemImageCart);
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView bookItemNameCart, bookItemPriceCart, bookItemQuantityCart;
        public ImageView bookItemImageCart;
        public ImageButton addQuantityButton, removeQuantityButton, removeFromCart;
        public ItemClickListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookItemNameCart = itemView.findViewById(R.id.book_item_name_cart);
            bookItemPriceCart = itemView.findViewById(R.id.book_item_price_cart);
            bookItemQuantityCart = itemView.findViewById(R.id.book_item_quantity_cart);
            bookItemImageCart = itemView.findViewById(R.id.book_item_image_cart);
            addQuantityButton = itemView.findViewById(R.id.addnumberCart);
            removeQuantityButton = itemView.findViewById(R.id.removenumberCart);
            removeFromCart = itemView.findViewById(R.id.removeAllItemBooksFromCart);

            mcontext = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mlistener.onItemClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }

    }

    public double TotalCalculate(List<CartModel> cartCal) {
        double totalPrice = 0;

        for (int i = 0; i < cartCal.size(); i++) {
            totalPrice += cartCal.get(i).getTotalprice();
        }
        return totalPrice;
    }
}
