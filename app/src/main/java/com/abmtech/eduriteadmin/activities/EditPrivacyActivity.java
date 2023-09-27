package com.abmtech.eduriteadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivityEditFaqBinding;
import com.abmtech.eduriteadmin.databinding.ActivityEditPrivacyBinding;
import com.abmtech.eduriteadmin.models.SignupModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPrivacyActivity extends AppCompatActivity {


    ActivityEditPrivacyBinding binding;
    Activity activity;
    ProgressDialog pd;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        pd = new ProgressDialog(activity);

        type = getIntent().getStringExtra("type");

        if (type.equalsIgnoreCase("0")) {
            binding.titleText.setText("Add Privacy Policy");
            binding.buttonText.setText("Add Privacy Policy");
        } else if (type.equalsIgnoreCase("1")) {
            binding.titleText.setText("Add Terms Conditions");
            binding.buttonText.setText("Add Terms Conditions");
        } else {
            binding.titleText.setText("Add About");
            binding.buttonText.setText("Add About");
        }

        binding.icBack.setOnClickListener(view -> onBackPressed());

        binding.cardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidate())
                    addContent();
            }
        });

    }

    private void addContent() {
        pd.show();

        Call<SignupModel> call;
        if (type.equalsIgnoreCase("0")) {
            call = RetrofitClient.getClient(activity).addPrivacy(binding.content.getText().toString());
        } else if (type.equalsIgnoreCase("1")) {
            call = RetrofitClient.getClient(activity).addTermsConditions(binding.content.getText().toString());
        } else {
            call = RetrofitClient.getClient(activity).addAbout(binding.content.getText().toString());
        }

        call.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(@NonNull Call<SignupModel> call, @NonNull Response<SignupModel> response) {
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            Toast.makeText(EditPrivacyActivity.this, "Content Added", Toast.LENGTH_SHORT).show();
                            finish();
                        }
            }

            @Override
            public void onFailure(@NonNull Call<SignupModel> call, @NonNull Throwable t) {
                Log.e("TAG", "onFailure() called with: call = [" + call + "], t = [" + t.getLocalizedMessage() + "]");
                Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
                pd.dismiss();
            }
        });

    }

    private boolean isValidate() {
        if (binding.content.getText().toString().equalsIgnoreCase("")) {
            binding.content.setError("Enter Content!");
            binding.content.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}