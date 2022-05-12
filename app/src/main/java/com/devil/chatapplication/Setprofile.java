package com.devil.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.devil.chatapplication.Models.userProfile;
import com.devil.chatapplication.databinding.ActivitySetprofileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private Boolean userexists = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetprofileBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);

        mgetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                imageuri = result;
                dialog.show();
                sendimagetoStorage();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = storage.getReference();
        userRef = firestore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        userRef.addSnapshotListener((value, error) -> {

            if (value != null && value.exists()) {
                userexists = true;
                userProfile userProfile = value.toObject(userProfile.class);
                assert userProfile != null;
                Glide
                        .with(Setprofile.this)
                        .load(userProfile.getImage())
                        .centerCrop()
                        .into(binding.profileImage);
                imagetoken = userProfile.getImage();
                binding.usernameEt.setText(userProfile.getName());


            }
        });
        binding.backButtonProfile.setOnClickListener(view -> finish());
        binding.setImageviewButton.setOnClickListener(view -> {
            mgetContent.launch("image/*");
        });


        binding.saveUser.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(binding.usernameEt.getText().toString())) {
                sendDataTocloudstore();

            } else {
                Toast.makeText(getApplicationContext(), "enter details", Toast.LENGTH_SHORT).show();
            }
        });


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
                    dialog.dismiss();
                    Map<String, Object> imagedata = new HashMap<>();
                    imagedata.put("image", imagetoken);
                    userRef.update(imagedata);
                })).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Setprofile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataTocloudstore() {


        if (userexists) {
            userRef.update("name",binding.usernameEt.getText().toString()).addOnSuccessListener(unused -> {

            });
        } else {
            String number = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
            Map<String, Boolean> map = new HashMap<>();
            map.put(firebaseAuth.getUid(), false);
            userProfile profile = new userProfile();
            profile.setName(Objects.requireNonNull(binding.usernameEt.getText()).toString());
            profile.setImage(imagetoken);
            profile.setNumber(number);
            profile.setToken("hi");
            profile.setStatus("online");
            profile.setUid(firebaseAuth.getUid());
            profile.setFriendlist(map);
            userRef.set(profile).addOnSuccessListener(unused -> {
                Toast.makeText(getApplicationContext(), "Add new User", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Setprofile.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}