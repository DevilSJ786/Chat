package com.devil.chatapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.devil.chatapplication.Models.userProfile;
import com.devil.chatapplication.databinding.ActivitySearchFriendBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SearchFriend extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ActivitySearchFriendBinding binding;
    private CollectionReference friendRef;
    private DocumentReference userDocument, friendDocument;
    private  userProfile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        friendRef = firestore.collection("users");
        userDocument=firestore.collection("users").document(Objects.requireNonNull(firebaseAuth.getUid()));
        String userNumber = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();

        binding.addfriendButton.setOnClickListener(view -> {
            friendDocument=firestore.collection("users").document(user.getUid());
            Map<String,Boolean> map=new HashMap<>();
            map.put(user.getUid(),true);
            userDocument.update("friendlist."+user.getUid(),true).addOnSuccessListener(unused -> {
                Map<String,Boolean> map1=new HashMap<>();
                map1.put(firebaseAuth.getUid(),true);
                friendDocument.update("friendlist."+firebaseAuth.getUid(),true).addOnSuccessListener(unused1 -> finish());
            });

        });
        binding.searchButtonFriend.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(Objects.requireNonNull(binding.phoneNumberEt.getText()).toString())) {
                friendRef.whereEqualTo("number", binding.phoneNumberEt.getText().toString()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        user = queryDocumentSnapshots.getDocuments().get(0).toObject(userProfile.class);
                        if (user != null) {
                            binding.newFriendLayout.setVisibility(View.VISIBLE);
                            binding.usernametv.setText(user.getName());
                            Glide
                                    .with(SearchFriend.this)
                                    .load(user.getImage())
                                    .centerCrop()
                                    .into(binding.imageRecycler);
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(SearchFriend.this, "No user found", Toast.LENGTH_SHORT).show());
            }
        });

    }
}