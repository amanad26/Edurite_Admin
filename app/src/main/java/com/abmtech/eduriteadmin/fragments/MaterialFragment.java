package com.abmtech.eduriteadmin.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abmtech.eduriteadmin.adapters.MaterialListAdapter;
import com.abmtech.eduriteadmin.apis.ApiInterface;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.FragmentHomeBinding;
import com.abmtech.eduriteadmin.databinding.FragmentMaterialBinding;
import com.abmtech.eduriteadmin.models.MaterialListModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialFragment extends Fragment {
    private Activity activity;
    private FragmentMaterialBinding binding;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMaterialBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = requireActivity();
        progressDialog = new ProgressDialog(activity);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        // getMaterialList();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMaterialList();
    }

    private void getMaterialList() {
        RetrofitClient.getClient(activity).getMaterialList().enqueue(new Callback<MaterialListModel>() {
            @Override
            public void onResponse(Call<MaterialListModel> call, Response<MaterialListModel> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            binding.recyclerView.setAdapter(new MaterialListAdapter(activity, response.body().getData()));
                        } else {
                            String message = response.body().getMsg();
                            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MaterialListModel> call, Throwable t) {
                Log.e("TAG", "onFailure() called with: call = [" + call + "], t = [" + t.getLocalizedMessage() + "]");
                Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });

    }
}
