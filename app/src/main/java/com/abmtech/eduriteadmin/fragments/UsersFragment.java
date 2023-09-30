package com.abmtech.eduriteadmin.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.adapters.UsersListAdapter;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.FragmentUsersBinding;
import com.abmtech.eduriteadmin.models.UsersModel;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersFragment extends Fragment {

    FragmentUsersBinding binding;
    Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(getLayoutInflater());
        activity = requireActivity();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getUsersList();
    }

    private void getUsersList() {
        RetrofitClient.getClient(activity).getUsersList().enqueue(new Callback<UsersModel>() {
            @Override
            public void onResponse(@NonNull Call<UsersModel> call, @NonNull Response<UsersModel> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                            linearLayoutManager.setReverseLayout(true);
                            linearLayoutManager.setStackFromEnd(true);
                            binding.recyclerView.setLayoutManager(linearLayoutManager);
                            binding.recyclerView.setAdapter(new UsersListAdapter(activity, response.body().getData()));
                        } else {
                            String message = response.body().getMsg();
                            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                        }
            }

            @Override
            public void onFailure(@NonNull Call<UsersModel> call, @NonNull Throwable t) {
                Log.e("TAG", "onFailure() called with: call = [" + call + "], t = [" + t.getLocalizedMessage() + "]");
                Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });

    }
}