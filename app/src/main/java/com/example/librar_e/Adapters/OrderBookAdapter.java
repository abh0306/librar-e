package com.example.librar_e.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.example.librar_e.BookDetails;
import com.example.librar_e.Model.BookOrderModel;
import com.example.librar_e.Model.Books;
import com.example.librar_e.R;
import com.squareup.picasso.Picasso;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Interface.ItemClickListener;

import static androidx.core.content.ContextCompat.startActivity;

public class OrderBookAdapter extends RecyclerView.Adapter<OrderBookAdapter.ViewHolder> {
    Context mcontext;
    LayoutInflater inflater;
    List<BookOrderModel> books;

    public OrderBookAdapter(Context ctx, List<BookOrderModel> books) {
        this.inflater = LayoutInflater.from(ctx);
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bookitemorderslayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        System.out.println("QUAQUA "+ books.get(position).getPriceTotal());
        holder.holderTitle.setText(books.get(position).getTitle());
        holder.holderQuantity.setText("Quantità: " +String.valueOf(books.get(position).getQuantity()));
        String result = String.format("%.2f", books.get(position).getPriceTotal());
        holder.holderPrice.setText("Totale: " +result+"€");
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView holderTitle, holderQuantity, holderPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            holderTitle = itemView.findViewById(R.id.orderBookTitle);
            holderQuantity = itemView.findViewById(R.id.orderBookQuantity);
            holderPrice = itemView.findViewById(R.id.orderBookPrice);

        }


    }
}
