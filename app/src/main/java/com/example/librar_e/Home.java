package com.example.librar_e;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Adapters.BooksAdapter;
import com.example.librar_e.Model.Books;
import com.example.librar_e.Preferences.Preferences;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Book;
import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BooksAdapter.OnItemClickListener {


    private RecyclerView recyclerView;
    private ArrayList books;
    private Button filterButton;
    private String filter = "0";
    BooksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        filterButton = findViewById(R.id.filterButton);

        filter=getIntent().getStringExtra("filter");

        if (!(filter.equals("0"))){
            filterButton.setBackground(getDrawable(R.drawable.removefilter));
        }
        else{
            filterButton.setBackground(getDrawable(R.drawable.filter));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.user_name);
        TextView emailTextView = headerView.findViewById(R.id.user_email);
        CircleImageView userimageImageView = headerView.findViewById(R.id.user_image);

        books = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclermenu);

        //il bottone filtro serve per selezionare i libri in base alla categoria che si vuole visualizzare
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filter.equals("0")) {
                    CharSequence filters[] = new CharSequence[]{
                            "Libri", "Fumetti", "Riviste", "Istruzione", "Guide", "Enciclopedie"
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    builder.setTitle("Filtri:");
                    builder.setItems(filters, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                filter = "Libri";
                            }
                            if (which == 1) {
                                filter = "Fumetti";
                            }
                            if (which == 2) {
                                filter = "Riviste";
                            }
                            if (which == 3) {
                                filter = "Istruzione";
                            }
                            if (which == 4) {
                                filter = "Guide";
                            }
                            if (which == 5) {
                                filter = "Enciclopedie";
                            }
                            startActivity(new Intent(Home.this, Home.class).putExtra("filter", filter));
                            finish();
                        }
                    });
                    builder.show();
                }
                else {
                    filter = "0";
                    startActivity(new Intent(Home.this, Home.class).putExtra("filter", filter));;
                    finish();
                }
            }
        });
        //utilizzo della liberia firebase auth per ottenere il uid dell'utente
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        RequestQueue queue = Volley.newRequestQueue(Home.this);
        //richiesta tramite metodo GET dei dati dell'utente da mostrare dentro il menù
        String urlUsrGet = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Users/"+uid+".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlUsrGet, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String mail = response.getString("email");
                    String uName = response.getString("fullName");
                    String imgpath = response.getString("image");
                    usernameTextView.setText(uName);
                    emailTextView.setText(mail);
                    if(!(imgpath.equals("null"))){
                        Picasso.get().load(imgpath).fit().centerCrop().into(userimageImageView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(Home.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);
    }

    protected void onStart() {
        super.onStart();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url;
        //in base al filtro decido quali libri mostrare
        if(filter.equals("0")) {
            url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Books.json";
        }
        else{
            url = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Books.json?orderBy=\"Category\"&equalTo=\""+filter+"\"";
        }

        //effettuo una richiesta GET al database per ottenere i libri e li inserisco in una recycler view (definita in Adapters/BooksAdapter)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        books.clear();
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = String.valueOf(keys.next()); // this will be your JsonObject key
                            JSONObject childObj = null;
                            try {
                                childObj = response.getJSONObject(key);
                                Books book = new Books();
                                book.setTitle(childObj.getString("Title"));
                                book.setImage(childObj.getString("Image"));
                                book.setPrice(Double.parseDouble(childObj.getString("Price")));
                                book.setDescription(childObj.getString("Description"));
                                book.setBookid(childObj.getString("Bookid"));
                                books.add(book);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new BooksAdapter(getApplicationContext(),books);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(Home.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);


        /*if (filter.equals("0")) {
            FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(BooksRef, Books.class).build();
            FirebaseRecyclerAdapter<Books, BookViewHolder> adapter = new FirebaseRecyclerAdapter<Books, BookViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Books model) {
                    holder.biName.setText(model.getTitle());
                    holder.biDescription.setText(model.getDescription());
                    holder.biPrice.setText(model.getPrice() + "€");
                    Picasso.get().load(model.getImage()).fit().centerCrop().into(holder.biImage);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Home.this, BookDetails.class).putExtra("bid", model.getBookid()));
                        }
                    });
                }

                @NonNull
                @Override
                public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_items_layout, parent, false);
                    BookViewHolder holder = new BookViewHolder(view);
                    return holder;
                }
            };

            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
        else{
            FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(BooksRef.orderByChild("Category").equalTo(filter), Books.class).build();
            FirebaseRecyclerAdapter<Books, BookViewHolder> adapter = new FirebaseRecyclerAdapter<Books, BookViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Books model) {
                    holder.biName.setText(model.getTitle());
                    holder.biDescription.setText(model.getDescription());
                    holder.biPrice.setText(model.getPrice() + "€");
                    Picasso.get().load(model.getImage()).fit().centerCrop().into(holder.biImage);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Home.this, BookDetails.class).putExtra("bid", model.getBookid()));
                        }
                    });
                }

                @NonNull
                @Override
                public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_items_layout, parent, false);
                    BookViewHolder holder = new BookViewHolder(view);
                    return holder;
                }
            };

            recyclerView.setAdapter(adapter);
            adapter.startListening();*/
        }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        return super.onOptionsItemSelected(item);
    }

    //gestion del menù laterale
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
        }
        if (id == R.id.nav_cart)
        {
            startActivity(new Intent(Home.this, Cart.class));
        }
        else if (id == R.id.nav_orders)
        {
            startActivity(new Intent(Home.this, CustomerOrder.class));
        }
        else if (id == R.id.nav_settings)
        {
            startActivity(new Intent(Home.this, Settings.class));
            finish();
        }
        else if (id == R.id.nav_logout)
        {
            Paper.book().write(Preferences.usrEmail, "");
            Paper.book().write(Preferences.usrPwd, "");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Home.this, Login.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //funzione per ottenere l'id del libro clickato nella recyclerview e passarlo come extra all'activity BookDetails
    @Override
    public void onItemClick(int position) {
        Books clickedBook = (Books) books.get(position);
        Intent activity =  new Intent(Home.this, BookDetails.class).putExtra("bid", clickedBook.getBookid());

        startActivity(activity);
    }
}