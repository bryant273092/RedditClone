package com.example.reddit;

import android.content.Intent;
import android.graphics.Bitmap;

import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;




public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;

    private EditText newPostDesc;
    private Button newPostBtn;



    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;


    @Override
protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostDesc = findViewById(R.id.post_description);
        newPostBtn = findViewById(R.id.post_btn);



        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = newPostDesc.getText().toString();

                if(!TextUtils.isEmpty(desc) ){

                    final String randomName = UUID.randomUUID().toString();

                        Map<String, Object> postMap = new HashMap<>();
                        postMap.put("desc", desc);
                        postMap.put("user_id", current_user_id);
                        postMap.put("timestamp", FieldValue.serverTimestamp());


                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(NewPostActivity.this, "Post was added",Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();




                                }else{
                                    Toast.makeText(NewPostActivity.this, "Post was not added",Toast.LENGTH_LONG).show();

                                }

                            }
                        });

                }

            }
        });

}

}

