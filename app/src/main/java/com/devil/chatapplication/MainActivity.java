package com.devil.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devil.chatapplication.Adapter.ViewPagerFragmentadapter;
import com.devil.chatapplication.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private final String[] title = new String[]{"Home", "Status"};
    private ViewPagerFragmentadapter viewPagerFragmentadapter;
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
//        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        userRef = firestore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        FirebaseMessaging.getInstance().getToken()
                        .addOnSuccessListener(s -> userRef.update("token", s)
                        .addOnFailureListener(e -> {
                            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }));

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(MainActivity.this, Setprofile.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.logout) {
//                throw new RuntimeException("Test Crash");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginUser.class);
                startActivity(intent);
                finish();
                return true;
            }else if (item.getItemId()==R.id.search_friend){
                Intent intent = new Intent(MainActivity.this, SearchFriend.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        viewPagerFragmentadapter = new ViewPagerFragmentadapter(this);
        binding.viewPager.setAdapter(viewPagerFragmentadapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, ((tab, position) -> tab.setText(title[position]))).attach();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}