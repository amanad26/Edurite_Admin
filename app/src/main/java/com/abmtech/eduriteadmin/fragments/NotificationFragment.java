package com.abmtech.eduriteadmin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abmtech.eduriteadmin.databinding.FragmentHomeBinding;
import com.abmtech.eduriteadmin.databinding.FragmentNotificationBinding;

public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }
}
