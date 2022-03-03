package com.devil.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.devil.chatapplication.Adapter.ViewPagerFragmentadapter;
import com.devil.chatapplication.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private String[] title = new String[]{"Home", "Status"};
    private ViewPagerFragmentadapter viewPagerFragmentadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.profile) {
                    Intent intent = new Intent(MainActivity.this, Setprofile.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, LoginUser.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        viewPagerFragmentadapter = new ViewPagerFragmentadapter(this);
        binding.viewPager.setAdapter(viewPagerFragmentadapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, ((tab, position) -> tab.setText(title[position]))).attach();


    }
}