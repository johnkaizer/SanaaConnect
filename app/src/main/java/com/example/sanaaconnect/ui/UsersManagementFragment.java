package com.example.sanaaconnect.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sanaaconnect.R;
import com.example.sanaaconnect.databinding.FragmentMessagesBinding;
import com.example.sanaaconnect.databinding.FragmentUsersManagementBinding;

public class UsersManagementFragment extends Fragment {
    private FragmentUsersManagementBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}