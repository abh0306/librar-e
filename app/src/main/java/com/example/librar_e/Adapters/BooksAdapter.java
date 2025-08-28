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
import com.example.librar_e.Model.Books;
import com.example.librar_e.R;
import com.squareup.picasso.Picasso;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Interface.ItemClickListener;

import static androidx.core.content.ContextCompat.startActivity;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    OnItemClickListener mlistener;
    Context mcontext;
    LayoutInflater inflater;
    List<Books> books;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public BooksAdapter(Context ctx, List<Books> books) {
        this.inflater = LayoutInflater.from(ctx);
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.book_items_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.biName.setText(books.get(position).getTitle());
        holder.biDescription.setText(books.get(position).getDescription());
        String result = String.format("%.2f", books.get(position).getPrice());
        holder.biPrice.setText(""+ result+"€");
        holder.biDescription.setText(books.get(position).getDescription());
        Picasso.get().load(books.get(position).getImage()).fit().centerCrop().into(holder.biImage);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        public TextView biName, biPrice, biDescription;
        public ImageView biImage;
        public ItemClickListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            biImage = itemView.findViewById(R.id.book_item_image);
            biName = itemView.findViewById(R.id.book_item_name);
            biDescription = itemView.findViewById(R.id.book_item_description);
            biPrice = itemView.findViewById(R.id.book_item_price);

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
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }

}
