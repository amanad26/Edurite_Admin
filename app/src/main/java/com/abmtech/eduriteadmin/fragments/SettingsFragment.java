package com.abmtech.eduriteadmin.fragments;

import static com.abmtech.eduriteadmin.utils.Util.decodeImage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abmtech.eduriteadmin.Session.Session;
import com.abmtech.eduriteadmin.activities.AboutUsActivity;
import com.abmtech.eduriteadmin.activities.FaqActivity;
import com.abmtech.eduriteadmin.activities.PrivacyPolicyActivity;
import com.abmtech.eduriteadmin.activities.ProfileActivity;
import com.abmtech.eduriteadmin.activities.TermsActivity;
import com.abmtech.eduriteadmin.databinding.FragmentSettingsBinding;

import org.jetbrains.annotations.NotNull;

public class SettingsFragment extends Fragment {
    private Activity activity;
    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        activity = requireActivity();
        Session session = new Session(activity);

        binding.imageProfile.setImageBitmap(decodeImage(activity, session.getUserImage()));

        binding.llAboutUs.setOnClickListener(view -> startActivity(new Intent(activity, AboutUsActivity.class)));
        binding.llPrivacyPolicy.setOnClickListener(view -> startActivity(new Intent(activity, PrivacyPolicyActivity.class)));
        binding.llTerms.setOnClickListener(view -> startActivity(new Intent(activity, TermsActivity.class)));
        binding.llFaq.setOnClickListener(view -> startActivity(new Intent(activity, FaqActivity.class)));
        binding.llProfileCard.setOnClickListener(view -> startActivity(new Intent(activity, ProfileActivity.class)));
        binding.llUserProfile.setOnClickListener(view -> startActivity(new Intent(activity, ProfileActivity.class)));
        binding.textLogout.setOnClickListener(view -> session.logout());

        return binding.getRoot();
    }
}
