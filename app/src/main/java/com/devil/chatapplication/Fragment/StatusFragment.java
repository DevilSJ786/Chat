package com.devil.chatapplication.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devil.chatapplication.R;
import com.devil.chatapplication.databinding.FragmentStatusBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class StatusFragment extends Fragment {


    public StatusFragment() {
        // Required empty public constructor
    }

    private FragmentStatusBinding binding;
    private FirebaseFirestore firestore;
    private DocumentReference jaipurref, fuser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(inflater, container, false);
        firestore = FirebaseFirestore.getInstance();
        fuser = firestore.collection("Users").document("fuser");
        jaipurref = firestore.collection("Users").document("fuser")
                .collection("user").document("jaipur");
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "kisn");
//        jaipurref.set(map);
//        fuser.set(map);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}