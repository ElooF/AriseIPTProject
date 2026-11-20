package com.example.arise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.arise.data.remote.FirebaseManager;
import com.example.arise.databinding.FragmentProfileBinding;
import com.example.arise.viewmodel.UserViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        observeUserStats();

        binding.btnSyncNow.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Syncing data to Cloud...", Toast.LENGTH_SHORT).show();
            userViewModel.syncNow(new FirebaseManager.SyncCallback() {
                @Override
                public void onSuccess() {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Cloud Sync Successful!", Toast.LENGTH_SHORT).show()
                        );
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Sync Failed: " + errorMessage, Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        });

        binding.btnLogout.setOnClickListener(v -> {
            userViewModel.logout();
            startActivity(new Intent(requireContext(), AuthActivity.class));
            requireActivity().finish();
        });
    }

    private void observeUserStats() {
        userViewModel.getUserStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                binding.tvProfileName.setText(stats.getDisplayName());
                binding.tvProfileEmail.setText(stats.getEmail());
                binding.tvProfileLevel.setText("Level " + stats.getCurrentLevel() + " Explorer");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
