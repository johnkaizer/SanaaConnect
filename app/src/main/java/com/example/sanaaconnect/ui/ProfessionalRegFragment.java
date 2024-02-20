package com.example.sanaaconnect.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sanaaconnect.AddSkillProfileActivity;
import com.example.sanaaconnect.R;
import com.example.sanaaconnect.databinding.FragmentJobsBinding;
import com.example.sanaaconnect.databinding.FragmentProfessionalRegBinding;

public class ProfessionalRegFragment extends Fragment {
    private FragmentProfessionalRegBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfessionalRegBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.addSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to open the activity for adding new skills
                Intent intent = new Intent(requireContext(), AddSkillProfileActivity.class);
                // Start the activity
                startActivity(intent);
            }
        });



        return root;
    }
}