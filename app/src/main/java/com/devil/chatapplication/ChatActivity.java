package com.devil.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.devil.chatapplication.Adapter.MessageAdapter;

import com.devil.chatapplication.Models.Message;
import com.devil.chatapplication.Models.StatuslastMessage;
import com.devil.chatapplication.Models.userProfile;
import com.devil.chatapplication.databinding.ActivityChatBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private MessageAdapter messageAdapter;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private DocumentReference documentReference, senderlastmessageRef, receiverlastmessageRefs, senderRef, receiverRef;
    private String receiverid, senderid, senderRoom, receiverRoom, key;


    @Override
    protected void onStart() {
        super.onStart();
        receiverid = getIntent().getStringExtra("itemId");
        senderid = FirebaseAuth.getInstance().getUid();
        senderRoom = senderid + receiverid;
        receiverRoom = receiverid + senderid;
        firestore = FirebaseFirestore.getInstance();
        senderlastmessageRef = firestore.collection("chats").document(senderRoom);
        senderlastmessageRef.update("status","Online");

        messageAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageAdapter != null) {
            messageAdapter.stopListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        receiverid = getIntent().getStringExtra("itemId");
        senderid = FirebaseAuth.getInstance().getUid();
        senderRoom = senderid + receiverid;
        receiverRoom = receiverid + senderid;
        firestore = FirebaseFirestore.getInstance();
        senderlastmessageRef = firestore.collection("chats").document(senderRoom);
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", "Offline");
        senderlastmessageRef.update(map);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButtonChat.setOnClickListener(view -> finish());
        receiverid = getIntent().getStringExtra("itemId");
        senderid = FirebaseAuth.getInstance().getUid();
        firestore = FirebaseFirestore.getInstance();

        senderRoom = senderid + receiverid;
        receiverRoom = receiverid + senderid;

        senderlastmessageRef = firestore.collection("chats").document(senderRoom);
        receiverlastmessageRefs = firestore.collection("chats").document(receiverRoom);


        collectionReference = firestore.collection("chats").document(senderRoom).collection("messages");

        Query query = collectionReference.orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> allMessages = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();
        messageAdapter = new MessageAdapter(allMessages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);


        binding.recyclerviewChat.setLayoutManager(linearLayoutManager);
        binding.recyclerviewChat.setAdapter(messageAdapter);


        binding.sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.sendMessagetext.getText())) {
                    key = firestore.collection("chats").document(senderRoom).collection("messages").document().getId();
                    senderRef = firestore.collection("chats").document(senderRoom).collection("messages").document(key);
                    receiverRef = firestore.collection("chats").document(receiverRoom).collection("messages").document(key);


                    Log.d("raju", "onClick: " + senderid + receiverid);
                    Message message = new Message(key, binding.sendMessagetext.getText().toString(), senderid, -1,new Date().getTime());


                    WriteBatch batch = firestore.batch();
                    batch.set(senderRef, message);
                    batch.set(receiverRef, message);
                    batch.set(senderlastmessageRef, new StatuslastMessage( binding.sendMessagetext.getText().toString(), new Timestamp(new Date())), SetOptions.mergeFields("lastMessage","timestamp"));
                    batch.set(receiverlastmessageRefs, new StatuslastMessage(binding.sendMessagetext.getText().toString(), new Timestamp(new Date())), SetOptions.mergeFields("lastMessage","timestamp"));

                    batch.commit().
                            addOnSuccessListener(unused ->
                                    {
                                        binding.sendMessagetext.setText("");
                                        Toast.makeText(ChatActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                                    }
                                   ).
                            addOnFailureListener(e -> Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
//        binding.sendMessagetext.
        binding.sendMessagetext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("deepak", "beforeTextChanged: "+charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("deepak", "onTextChanged: "+charSequence.toString());
//                if(charSequence.length() != 0)
//                      receiverlastmessageRefs.update("status","Typing...");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("deepak", "afterTextChanged: "+editable.toString());
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        receiverlastmessageRefs.update("status","Online");
//
//                    }
//                },5000);

            }
        });
        receiverlastmessageRefs.addSnapshotListener((value, error) -> {
            if (value != null) {
                if (!Objects.equals(value.getString("status"), "Offline")) {
                    binding.statusChat.setVisibility(View.VISIBLE);
                    binding.statusChat.setText(value.getString("status"));
                }
                else {
                    binding.statusChat.setVisibility(View.GONE);
                }
            }
        });

        documentReference = firestore.collection("users").document(receiverid);
        documentReference.addSnapshotListener((value, error) -> {

            if (value != null) {
                userProfile user;
                user = value.toObject(userProfile.class);
                if (user != null) {
                    binding.usernameChat.setText(user.getName());
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