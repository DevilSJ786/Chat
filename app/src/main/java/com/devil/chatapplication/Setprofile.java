package com.devil.chatapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devil.chatapplication.Models.userProfile;
import com.devil.chatapplication.databinding.ActivitySetprofileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Setprofile extends AppCompatActivity {
    private ActivitySetprofileBinding binding;
    private ActivityResultLauncher<String> mgetContent;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private DocumentReference userRef;
    private StorageReference storageReference;
    private Uri imageuri;
    private String imagetoken;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetprofileBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);

        mgetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                imageuri = result;
                dialog.show();
                sendimagetoStorage();
                Log.d("king", "photo: " + result);
            }
        });

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = storage.getReference();
        userRef = firestore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    userProfile userProfile = value.toObject(userProfile.class);
                    Log.d("subhash", "onEvent: "+Setprofile.this);
                    Glide
                            .with(Setprofile.this)
                            .load(userProfile.getImage())
                            .centerCrop()
                            .into(binding.profileImage);
                    imagetoken = userProfile.getImage();
                    binding.usernameEt.setText(userProfile.getName());

                }
            }
        });
        binding.setImageviewButton.setOnClickListener(view -> {
            mgetContent.launch("image/*");
        });
        binding.backButton.setOnClickListener(view -> {
            Intent intent = new Intent(Setprofile.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        binding.saveUser.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(binding.usernameEt.getText().toString())) {
                sendDataForNewUser();

            } else {
                Toast.makeText(getApplicationContext(), "enter details", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendDataForNewUser() {
        Log.d("king", "on sendData: enter ");

        sendDataTocloudstore();
    }


    private void sendimagetoStorage() {
        StorageReference imageref = storageReference.child("Images").child(firebaseAuth.getUid());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bitmap != null;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = imageref.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> imageref
                .getDownloadUrl().addOnSuccessListener(uri -> {
                    imagetoken = uri.toString();

                    Glide
                            .with(this)
                            .load(imagetoken)
                            .centerCrop()
                            .into(binding.profileImage);
                    Log.d("subhash", "photo upl from Storage: " + imagetoken + FirebaseAuth.getInstance().getUid());
                    dialog.dismiss();
                    Map<String, Object> imagedata = new HashMap<>();
                    imagedata.put("image", imagetoken);
                    userRef.update(imagedata);
                }));
    }

    private void sendDataTocloudstore() {

        Map<String, String> userData = new HashMap<>();
        userData.put("name", binding.usernameEt.getText().toString());
        userData.put("image", imagetoken);
        userData.put("uid", firebaseAuth.getUid());
        userData.put("status", "Online");
        userRef.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("king", "user added on Firestore");
                Toast.makeText(getApplicationContext(), "user is add on firestore", Toast.LENGTH_SHORT).show();
            }
        });
    }
}