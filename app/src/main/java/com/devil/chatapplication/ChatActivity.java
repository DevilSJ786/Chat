package com.devil.chatapplication;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.devil.chatapplication.Adapter.MessageAdapter;
import com.devil.chatapplication.Models.Message;
import com.devil.chatapplication.Models.StatuslastMessage;
import com.devil.chatapplication.Models.userProfile;
import com.devil.chatapplication.databinding.ActivityChatBinding;
import com.devil.chatapplication.databinding.MessageDeleteBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements MessageAdapter.OnMessageTouchListener {
    private ActivityChatBinding binding;
    private ActivityResultLauncher<String> mgetContent;
    private MessageAdapter messageAdapter;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private CollectionReference collectionReference;
    private DocumentReference documentReference, senderlastmessageRef, receiverlastmessageRefs, senderRef, receiverRef;
    private String receiverid, senderid, senderRoom, receiverRoom, key, imagetoken;
    private ProgressDialog dialog;
    private String name, token;
    private final Boolean send_notification = true;
    private final String tokenkey = "Key=AAAAmR7AHDM:APA91bFL8dqJ2n09Oj13CrYTnZ3SPG4jKIUeV7eAoM697MQH7kxxe7L35k8UgIeDqZZrxfpStjPzb4JNzASSLPlBP6acUwBI6xdShJ4yQA_3kijYqW2HiV9FXkGMQs9MadwpwU9UGa6b";


    @Override
    protected void onStart() {
        super.onStart();
        messageAdapter.startListening();
        setStatus("Online");
    }

    private void sendNotification(String token, String name, String message) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://fcm.googleapis.com/fcm/send";
        JSONObject object = new JSONObject();
        try {
            object.put("title", name);
            object.put("body", message);
            JSONObject notification = new JSONObject();
            notification.put("notification", object);
            notification.put("to", token);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, notification, response -> {

            }, error -> {

            }) {
                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("Authorization", tokenkey);
                    return map;
                }

            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setStatus(String online) {
        receiverid = getIntent().getStringExtra("itemId");
        senderid = FirebaseAuth.getInstance().getUid();
        senderRoom = senderid + receiverid;
        receiverRoom = receiverid + senderid;
        firestore = FirebaseFirestore.getInstance();

        senderlastmessageRef = firestore.collection("chats").document(senderRoom);
        senderlastmessageRef.update("status", online);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        setStatus("Offline");
        if (messageAdapter != null) {
            messageAdapter.stopListening();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButtonChat.setOnClickListener(view -> {
            finish();
        });
        receiverid = getIntent().getStringExtra("itemId");
        name = getIntent().getStringExtra("name");
        token = getIntent().getStringExtra("token");
        senderid = FirebaseAuth.getInstance().getUid();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        senderRoom = senderid + receiverid;
        receiverRoom = receiverid + senderid;

        senderlastmessageRef = firestore.collection("chats").document(senderRoom);
        receiverlastmessageRefs = firestore.collection("chats").document(receiverRoom);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Sending Image...");


//        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            setStatus("Online");
//
//        } else {
//            setStatus("Offline");
//        }


        collectionReference = firestore.collection("chats").document(senderRoom).collection("messages");

        Query query = collectionReference.orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> allMessages = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();
        messageAdapter = new MessageAdapter(allMessages, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerviewChat.setLayoutManager(linearLayoutManager);
        binding.recyclerviewChat.setAdapter(messageAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Message message = messageAdapter.getSnapshots().getSnapshot(viewHolder.getAbsoluteAdapterPosition()).toObject(Message.class);
                if (message != null) {
                    MessageDeleteBinding deleteBinding = MessageDeleteBinding.bind(LayoutInflater.from(getApplicationContext()).inflate(R.layout.message_delete, null));
                    senderRef = firestore.collection("chats").document(senderRoom).collection("messages").document(message.getMessageId());
                    receiverRef = firestore.collection("chats").document(receiverRoom).collection("messages").document(message.getMessageId());
                    AlertDialog dialog = new AlertDialog.Builder(ChatActivity.this).setTitle("Delete Message").setView(deleteBinding.getRoot()).create();
                    deleteBinding.deleteEveryone.setOnClickListener(view -> senderRef.delete().addOnSuccessListener(unused -> receiverRef.delete().addOnSuccessListener(unused1 -> dialog.dismiss())));
                    deleteBinding.deleteMe.setOnClickListener(view -> senderRef.delete().addOnSuccessListener(unused -> dialog.dismiss()));
                    deleteBinding.cancel.setOnClickListener(view -> dialog.dismiss());
                    dialog.show();
                }
            }
        }).attachToRecyclerView(binding.recyclerviewChat);

        binding.camMessage.setOnClickListener(view -> mgetContent.launch("image/*"));

        binding.sendbutton.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(binding.sendMessagetext.getText())) {
                sendMessage();
            }
        });

        binding.sendMessagetext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                senderlastmessageRef.update("status", "typing...");
                new Handler().postDelayed(() -> senderlastmessageRef.update("status", "Online"), 5000);
            }
        });
        receiverlastmessageRefs.addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                if (Objects.equals(value.getString("status"), "Offline")) {
                    binding.statusChat.setVisibility(View.GONE);
                } else {
                    binding.statusChat.setVisibility(View.VISIBLE);
                    binding.statusChat.setText(value.getString("status"));
                }
            }
        });

        documentReference = firestore.collection("users").document(receiverid);
        documentReference.addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
            if (error != null) {
                return;
            }

            if (value != null && value.exists()) {
                userProfile user;
                user = value.toObject(userProfile.class);
                if (user != null && user.getName() != null) {
                    binding.usernameChat.setText(user.getName());
                    Glide.with(getApplicationContext()).load(user.getImage()).centerCrop().into(binding.userimageChat);
                }
            }

        });
        mgetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                dialog.show();
                StorageReference imageref = storage.getReference().child("Images")
                        .child(senderRoom + new Timestamp(new Date()));


                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
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
                            sendMessage();
                        }));
            }
        });
    }

    private void sendMessage() {
        key = firestore.collection("chats").document(senderRoom).collection("messages").document().getId();
        senderRef = firestore.collection("chats").document(senderRoom).collection("messages").document(key);
        receiverRef = firestore.collection("chats").document(receiverRoom).collection("messages").document(key);
        WriteBatch batch = firestore.batch();


        if (imagetoken != null) {
            Message message = new Message(key, "Photo", senderid, imagetoken, -1, new Date().getTime());
            StatuslastMessage statuslastMessage = new StatuslastMessage("Photo", new Timestamp(new Date()));
            batch.set(senderRef, message);
            batch.set(receiverRef, message);
            batch.set(senderlastmessageRef, statuslastMessage, SetOptions.mergeFields("lastMessage", "timestamp"));
            batch.set(receiverlastmessageRefs, statuslastMessage, SetOptions.mergeFields("lastMessage", "timestamp"));
            batch.commit().addOnSuccessListener(unused -> {
                binding.sendMessagetext.setText("");
                imagetoken = null;
                dialog.dismiss();
                binding.recyclerviewChat.smoothScrollToPosition(Objects.requireNonNull(binding.recyclerviewChat.getAdapter()).getItemCount());
                sendNotification(token, name, message.getMessage());
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Message message = new Message(key, binding.sendMessagetext.getText().toString(), senderid, -1, new Date().getTime());
            StatuslastMessage statuslastMessage = new StatuslastMessage(binding.sendMessagetext.getText().toString(), new Timestamp(new Date()));
            batch.set(senderRef, message);
            batch.set(receiverRef, message);
            batch.set(senderlastmessageRef, statuslastMessage, SetOptions.mergeFields("lastMessage", "timestamp"));
            batch.set(receiverlastmessageRefs, statuslastMessage, SetOptions.mergeFields("lastMessage", "timestamp"));
            batch.commit().
                    addOnSuccessListener(unused -> {
                        binding.sendMessagetext.setText("");
                        binding.recyclerviewChat.smoothScrollToPosition(Objects.requireNonNull(binding.recyclerviewChat.getAdapter()).getItemCount());
                        sendNotification(token, name, message.getMessage());
                    }).addOnFailureListener(e -> {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }

    }

    @Override
    public void onMessageTouch(Message message, int pos) {
        senderRef = firestore.collection("chats").document(senderRoom).collection("messages").document(message.getMessageId());
        receiverRef = firestore.collection("chats").document(receiverRoom).collection("messages").document(message.getMessageId());

        senderRef.update("feeling", pos).addOnSuccessListener(unused -> receiverRef.update("feeling", pos).addOnSuccessListener(unused1 -> {

        }));

    }
}