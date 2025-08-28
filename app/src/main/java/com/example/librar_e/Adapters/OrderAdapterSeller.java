package com.example.librar_e.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.BookDetails;
import com.example.librar_e.Model.OrderModel;
import com.example.librar_e.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Interface.ItemClickListener;

import static androidx.core.content.ContextCompat.startActivity;

public class OrderAdapterSeller extends RecyclerView.Adapter<OrderAdapterSeller.ViewHolder> {
    OnItemClickListener mlistener;
    Context mcontext;
    LayoutInflater inflater;
    List<OrderModel> orders;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public OrderAdapterSeller(Context ctx, List<OrderModel> orders) {
        this.inflater = LayoutInflater.from(ctx);
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.orders_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.holderDate.setText("Data: " +orders.get(position).getDate());
        holder.holderPhone.setText("Telefono: " +orders.get(position).getPhoneNumber());
        holder.holderName.setText("Destinatario: " + orders.get(position).getNameAndSurname());
        holder.holderAddress.setText("Indirizzo: " +orders.get(position).getStreet() + " " + orders.get(position).getHouseNumber() + ", " + orders.get(position).getCity() + ", " +orders.get(position).getCap());
        String result = String.format("%.2f", orders.get(position).getTotalPrice());
        holder.holderTotal.setText(result +"€");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        public TextView holderName, holderPhone, holderAddress, holderDate, holderTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            holderName = itemView.findViewById(R.id.order_name);
            holderPhone = itemView.findViewById(R.id.order_phone);
            holderAddress = itemView.findViewById(R.id.order_address);
            holderDate = itemView.findViewById(R.id.orderDate);
            holderTotal = itemView.findViewById(R.id.book_item_price_order);

            mcontext = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener!=null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mlistener.onItemClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }

}
