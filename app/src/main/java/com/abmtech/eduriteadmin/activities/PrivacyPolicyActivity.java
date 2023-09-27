package com.abmtech.eduriteadmin.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivityPrivacyPolicyBinding;
import com.abmtech.eduriteadmin.models.PrivacyPolicyModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private Activity activity;
    private ActivityPrivacyPolicyBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        progressDialog = new ProgressDialog(activity);

        binding.icBack.setOnClickListener(view -> onBackPressed());
        binding.editFaq.setOnClickListener(view -> startActivity(new Intent(activity, EditPrivacyActivity.class)
                .putExtra("type", "0")));

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrivacyPolicy();
    }

    private void getPrivacyPolicy() {
        progressDialog.show();
        RetrofitClient.getClient(activity).getPrivacyPolicy().enqueue(new Callback<PrivacyPolicyModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<PrivacyPolicyModel> call, @NonNull Response<PrivacyPolicyModel> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            binding.textDescription.setText(Html.fromHtml(response.body().getPrivacyPolicy().getContent(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PrivacyPolicyModel> call, @NonNull Throwable t) {
                Log.e("TAG", "onFailure() called with: call = [" + call + "], t = [" + t.getLocalizedMessage() + "]");
                Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

    }

}