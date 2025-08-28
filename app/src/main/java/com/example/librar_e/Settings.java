package com.example.librar_e;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Settings extends AppCompatActivity {
    private ImageView userImage;
    private EditText userName, street, houseNumber, city, CAP, phoneNumber;
    private CheckBox rolecheckbox;
    private Button savechanges, close, changeImage;
    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageImageReference;
    private StorageTask uploadTask;
    public static final int SELECT_PICTURE = 1;
    boolean pictureUpdated;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageImageReference = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        userImage = findViewById(R.id.userimagesettings);
        userName = findViewById(R.id.usernamesettings);

        street = findViewById(R.id.shippingStreetSettings);
        houseNumber = findViewById(R.id.shippingHouseNumberSettings);
        city = findViewById(R.id.shippingCitySettings);
        CAP = findViewById(R.id.shippingCAPSettings);
        phoneNumber = findViewById(R.id.phonesettings);
        changeImage = findViewById(R.id.changeimagesettings);
        close = findViewById(R.id.closesettings);
        savechanges = findViewById(R.id.save_settings_button);
        rolecheckbox = findViewById(R.id.checkboxseller);

        userDisplayInfos(userImage, userName, street, houseNumber, city, CAP, phoneNumber, rolecheckbox);



        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        savechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namecheck = userName.getText().toString().trim();
                if(namecheck.isEmpty()){
                    userName.setError("Il nome utente è obbligatorio");
                    userName.requestFocus();
                }
                else {
                    if (pictureUpdated) {
                        updateuserinfoandpicture();
                    }
                    else {
                        updateuserinfo();
                    }
                }
            }
        });
    }

    private void userDisplayInfos(ImageView userImage, EditText userName, EditText address, EditText houseNumber, EditText city, EditText CAP, EditText phoneNumber, CheckBox rolecheckbox) {

        //utilizzo della liberia firebase auth per ottenere il uid dell'utente
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        RequestQueue queue = Volley.newRequestQueue(Settings.this);

        //richiesta tramite metodo GET dei dati dell'utente da mostrare dentro il menù
        String urlUsrGet = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Users/"+uid+".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlUsrGet, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String imgpath = response.getString("image");
                    userName.setText(response.getString("fullName"));
                    if(!response.getString("street").equals("null"))
                    {
                        street.setText(response.getString("street"));
                    }
                    if(!response.getString("houseNumber").equals("null")) {
                        houseNumber.setText(response.getString("houseNumber"));
                    }
                    if(!response.getString("city").equals("null")) {
                        city.setText(response.getString("city"));
                    }
                    if(!response.getString("phone").equals("null")) {
                        phoneNumber.setText(response.getString("phone"));
                    }
                    if(!response.getString("cap").equals("null")) {
                        CAP.setText(response.getString("cap"));
                    }
                    if(!(imgpath.equals("null"))){
                        Picasso.get().load(imgpath).fit().centerCrop().into(userImage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(Settings.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);

    }

    private void chooseImage(){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, SELECT_PICTURE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==SELECT_PICTURE && data!=null) {
            // Ottengo l'url dell'immagine da Data
            imageUri = data.getData();
            if (null != imageUri) {
                // modifico l'immagine nell'activity
                userImage.setImageURI(imageUri);
                pictureUpdated = true;
            }
            else{
                pictureUpdated = false;
            }
        }
        else {
            startActivity(new Intent(Settings.this, Settings.class));
            Toast.makeText(Settings.this, "Si è verificato un problema, riprova", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void updateuserinfoandpicture() {
        final StorageReference fileRef = storageImageReference.child(Preferences.currentOnlineUser.getUid() + ".jpg");
        if (rolecheckbox.isChecked()) { role = "seller"; }
        else { role = "user"; }

        uploadTask = fileRef.putFile(imageUri);

        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {

                    //metodo utilizzato solamente se l'immagine viene modificata
                    //aggiorno i dati dell'utente tramite metodo PATCH
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            String urlUsrGet = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Users/"+uid+".json";


                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("fullName", userName.getText().toString());
                            userMap.put("street", street.getText().toString());
                            userMap.put("houseNumber", houseNumber.getText().toString());
                            userMap.put("city", city.getText().toString());
                            userMap.put("cap", CAP.getText().toString());
                            userMap.put("phone", phoneNumber.getText().toString());
                            userMap.put("image", myUrl);
                            userMap.put("role", role);

                            JSONObject updaterJ = new JSONObject(userMap);
                            JsonObjectRequest updateReq = new JsonObjectRequest(Request.Method.PATCH, urlUsrGet, updaterJ, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(Settings.this, "Profilo aggiornato correttamente", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Settings.this, Home.class).putExtra("filter", "0"));
                                    finish();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(Settings.this, "C'è stato un problema, riprova", Toast.LENGTH_SHORT).show();
                                }
                            });
                            RequestQueue queue = Volley.newRequestQueue(Settings.this);
                            queue.add(updateReq);




                            startActivity(new Intent(Settings.this, Home.class).putExtra("filter", "0"));
                            Toast.makeText(Settings.this, "Profilo aggiornato correttamente", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(Settings.this, "Si è verificato un errore, riprova", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //aggiorno i dati dell'utente tramite metodo PATCH
    private void updateuserinfo() {
        if (rolecheckbox.isChecked()) { role = "seller"; }
        else { role = "user"; }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String urlUsrGet = "https://librar-e-default-rtdb.europe-west1.firebasedatabase.app/Users/"+uid+".json";

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", userName.getText().toString());
        userMap.put("street", street.getText().toString());
        userMap.put("houseNumber", houseNumber.getText().toString());
        userMap.put("city", city.getText().toString());
        userMap.put("cap", CAP.getText().toString());
        userMap.put("phone", phoneNumber.getText().toString());
        userMap.put("role", role);

        JSONObject updaterJ = new JSONObject(userMap);
        JsonObjectRequest updateReq = new JsonObjectRequest(Request.Method.PATCH, urlUsrGet, updaterJ, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(Settings.this, "Profilo aggiornato correttamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.this, Home.class).putExtra("filter", "0"));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Settings.this, "C'è stato un problema, riprova", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(Settings.this);
        queue.add(updateReq);

    }

}
