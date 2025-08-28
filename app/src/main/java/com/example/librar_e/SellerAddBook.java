package com.example.librar_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.librar_e.Preferences.Preferences;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerAddBook extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String DownloadImageUrl, CategoryName, TitleValidate,  DescriptionValidate, AuthorValidate, PHValidate, YearValidate, Lang, ISBNValidate, PriceValidate, saveCurrentDate, saveCurrentTime, bookRandomKey;
    private ImageView productPicture;
    private EditText Title, Description, Author, Publishinghouse, Year, Isbn, Price;
    private double PriceValue;
    private Button addBook;
    private StorageReference bookImageRef;
    private ProgressBar progressBar;
    private Uri selectedImageUri;
    private DatabaseReference booksRef, sellerRef;
    public static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_book);

        CategoryName = getIntent().getExtras().get("category").toString();
        Toast.makeText(this, "Categoria: " + CategoryName, Toast.LENGTH_SHORT).show();

        bookImageRef = FirebaseStorage.getInstance().getReference().child("Book Images");
        booksRef = FirebaseDatabase.getInstance().getReference().child("Books");
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Preferences.currentOnlineUser.getUid());

        Title = findViewById(R.id.title);
        Description = findViewById(R.id.description);
        Author = findViewById(R.id.author);
        Publishinghouse = findViewById(R.id.publishinghouse);
        Year = findViewById(R.id.year);
        Isbn = findViewById(R.id.isbn);
        Price = findViewById(R.id.price);
        addBook = findViewById(R.id.add_book_button);
        productPicture = findViewById(R.id.productpicture);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        Spinner colorSpinner = findViewById(R.id.language);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.languages, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        colorSpinner.setAdapter(adapter);
        colorSpinner.setOnItemSelectedListener(this);

        productPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        addBook.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                ValidateProduct();
            }
        });
    }

    private void chooseImage() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==SELECT_PICTURE && data!=null) {
            // Ottengo l'url tramite Data
            selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                // aggiorno l'immagine nell'activity
                productPicture.setImageURI(selectedImageUri);
            }
        }
    }

    //funzione per controllare che tutte le informazioni riguardanti il libro siano state inserite
    private void ValidateProduct(){
        TitleValidate = Title.getText().toString();
        DescriptionValidate = Description.getText().toString();
        AuthorValidate = Author.getText().toString();
        PHValidate = Publishinghouse.getText().toString();
        YearValidate = Year.getText().toString();
        ISBNValidate = Isbn.getText().toString();
        PriceValidate = Price.getText().toString();

        if(selectedImageUri==null){
            Toast.makeText(this, "Devi inserire un'immagine", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(TitleValidate)){
            Title.setError("Devi inserire il titolo!");
            Title.requestFocus();
        }
        else if (TextUtils.isEmpty(DescriptionValidate)){
            Description.setError("Devi inserire la descrizione!");
            Description.requestFocus();
        }
        else if (TextUtils.isEmpty(AuthorValidate)){
            Author.setError("Devi inserire l'autore!");
            Author.requestFocus();
        }
        else if (TextUtils.isEmpty(PHValidate)){
            Publishinghouse.setError("Devi inserire la casaditrice!");
            Publishinghouse.requestFocus();
        }
        else if (TextUtils.isEmpty(YearValidate)){
            Year.setError("Devi inserire l'anno di pubblicazione!");
            Year.requestFocus();
        }
        else if (TextUtils.isEmpty(ISBNValidate)){
            Isbn.setError("Devi inserire il codice ISBN!");
            Isbn.requestFocus();
        }
        else if (TextUtils.isEmpty(PriceValidate)){
            Price.setError("Devi inserire un prezzo!");
            Price.requestFocus();
        }
        else {
            StoreBookInfo();
        }
    }

    //funzione per inserire l'immagine nel FireStorage
    private void StoreBookInfo() {
        progressBar.setVisibility(View.VISIBLE);
        PriceValue = Double.parseDouble(PriceValidate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        bookRandomKey = saveCurrentDate + saveCurrentTime;

        StorageReference path = bookImageRef.child(selectedImageUri.getLastPathSegment() + bookRandomKey + ".jpg");

        final UploadTask uploadTask = path.putFile(selectedImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                String message = e.toString();
                Toast.makeText(SellerAddBook.this, "Error: " +e, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        DownloadImageUrl = path.getDownloadUrl().toString();
                        return path.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            DownloadImageUrl = task.getResult().toString();
                            saveBookInfoDatabase();
                        }
                    }
                });
            }
        });
    }


    //funzione chiamata dopo aver ottenuto l'url dell'immagine da Firebase
    //utilizzo il metodo PUT per inserire il libro nel database come JSONObject
    private void saveBookInfoDatabase() {

        HashMap<String, Object> bookMap = new HashMap<>();
        bookMap.put("Bookid", bookRandomKey);
        bookMap.put("Date", saveCurrentDate);
        bookMap.put("Time", saveCurrentTime);
        bookMap.put("Category", CategoryName);
        bookMap.put("Image", DownloadImageUrl);
        bookMap.put("Title", TitleValidate);
        bookMap.put("Description", DescriptionValidate);
        bookMap.put("Author", AuthorValidate);
        bookMap.put("PH", PHValidate);
        bookMap.put("Year", YearValidate);
        bookMap.put("Language", Lang);
        bookMap.put("ISBN", ISBNValidate);
        bookMap.put("Price", PriceValue);
        bookMap.put("SellerUid", Preferences.currentOnlineUser.getUid());

        String urlBook = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Books/"+bookRandomKey+".json";
        JSONObject updaterJ = new JSONObject(bookMap);
        JsonObjectRequest updateReq = new JsonObjectRequest(Request.Method.PUT, urlBook, updaterJ, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(SellerAddBook.this, "Libro aggiunto al database", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SellerAddBook.this, SellerChooseCategory.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SellerAddBook.this, "C'è stato un problema, riprova", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(SellerAddBook.this);
        queue.add(updateReq);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Lang = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}