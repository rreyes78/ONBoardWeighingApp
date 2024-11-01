package com.example.onboardweighingapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.onboardweighingapp.R;
import com.example.onboardweighingapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void insertHalfScreenFragment(View view) {
        UpperSection upperSection = new UpperSection();
        FragmentManager fragmentManager = getChildFragmentManager(); // Use getChildFragmentManager() for nested fragments
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the fragment in the container
//        transaction.replace(R.id.fragment_container, upperSection);
//        transaction.commit();
    }
}