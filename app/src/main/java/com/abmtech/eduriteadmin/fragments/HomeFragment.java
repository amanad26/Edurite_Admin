package com.abmtech.eduriteadmin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abmtech.eduriteadmin.activities.AddCourseActivity;
import com.abmtech.eduriteadmin.adapters.CourseAdapter;
import com.abmtech.eduriteadmin.apis.ApiInterface;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.FragmentHomeBinding;
import com.abmtech.eduriteadmin.models.CourseModel;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        activity = requireActivity();

        binding.textHeading.setOnClickListener(view -> startActivity(new Intent(activity, AddCourseActivity.class)));
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getCourseList();
    }

    private void getCourseList() {
        ApiInterface apiService = RetrofitClient.getClient(activity);
        apiService.getAllCourse().enqueue(new Callback<CourseModel>() {
            @Override
            public void onResponse(@NonNull Call<CourseModel> call, @NonNull Response<CourseModel> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                            linearLayoutManager.setReverseLayout(true);
                            linearLayoutManager.setStackFromEnd(true);
                            binding.recyclerView.setLayoutManager(linearLayoutManager);
                            binding.recyclerView.setAdapter(new CourseAdapter(activity, response.body().getData()));
                        } else {
                            String message = response.body().getMsg();
                            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CourseModel> call, @NonNull Throwable t) {
                Log.e("TAG", "onFailure() called with: call = [" + call + "], t = [" + t.getLocalizedMessage() + "]");
                Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });

    }

}
