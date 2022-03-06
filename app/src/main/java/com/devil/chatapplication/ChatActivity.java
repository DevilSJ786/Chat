package com.devil.chatapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.devil.chatapplication.Models.userProfile;
import com.devil.chatapplication.databinding.ActivityChatBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backButtonChat.setOnClickListener(view -> finish());

        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("users").document(getIntent().getStringExtra("itemId"));
        documentReference.addSnapshotListener((value, error) -> {

            if (value != null) {
                userProfile user;
                user = value.toObject(userProfile.class);
                if (user != null) {
                    binding.usernameChat.setText(user.getName());
                    binding.statusChat.setText(user.getStatus());
                    Glide
                            .with(ChatActivity.this)
                            .load(user.getImage())
                            .centerCrop()
                            .into(binding.userimageChat);

                }
            }
        });

    }
}