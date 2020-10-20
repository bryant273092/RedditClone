package com.example.reddit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class SetupActivity extends AppCompatActivity {


    private String user_id;

    private EditText setupName;
    private Button setupButton;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebase_store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Settings");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebase_store = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setupName = findViewById(R.id.setupName);
        setupButton = findViewById(R.id.enter_name_btn);

        setupButton.setEnabled(false);

        firebase_store.collection("USERS").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String name = task.getResult().getString("name");
                        setupName.setText(name);


                        Toast.makeText(SetupActivity.this, "Data Exists", Toast.LENGTH_LONG).show();

                    }else{

                        Toast.makeText(SetupActivity.this, "(FIRESTORE Retrieve Error) : " , Toast.LENGTH_LONG).show();

                    }
                    setupButton.setEnabled(true);

                }
            }

        });

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String user_name = setupName.getText().toString();

                if(!TextUtils.isEmpty(user_name)){
                    String user_id = firebaseAuth.getCurrentUser().getUid();

                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("name",user_name);

                    firebase_store.collection("USERS").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(SetupActivity.this, "USER Settings have been saved", Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();

                            } else {
                                Toast.makeText(SetupActivity.this, "(FIRESTORE Error) : ", Toast.LENGTH_LONG).show();
                            }



                        }
                    });


                }
            }
        });


    }
}
