package com.devil.chatapplication.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.devil.chatapplication.Adapter.HomeAdapter;
import com.devil.chatapplication.Models.userProfile;
import com.devil.chatapplication.databinding.FragmentHomeBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class HomeFragment extends Fragment {
   private FragmentHomeBinding binding;
    public HomeFragment() {
        // Required empty public constructor
    }
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference_poll = db.collection("users");
    private HomeAdapter homeAdapter;

    @Override
    public void onStart() {
        super.onStart();
        homeAdapter.startListening();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater, container, false);

        Query query =  collectionReference_poll.whereEqualTo("friendlist."+FirebaseAuth.getInstance().getUid(),true);

        FirestoreRecyclerOptions<userProfile> allusersPoll = new FirestoreRecyclerOptions.Builder<userProfile>()
                .setQuery(query,userProfile.class).build();

        homeAdapter=new HomeAdapter(allusersPoll,requireContext());
        binding.recyclerViewHome.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.recyclerViewHome.setAdapter(homeAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (homeAdapter != null) {
            homeAdapter.stopListening();
        }
        binding = null;
    }


}