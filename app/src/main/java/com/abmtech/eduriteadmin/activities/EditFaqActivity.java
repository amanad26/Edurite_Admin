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
import com.abmtech.eduriteadmin.models.SignupModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFaqActivity extends AppCompatActivity {


    ActivityEditFaqBinding binding;
    Activity activity;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditFaqBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        pd = new ProgressDialog(activity);

        binding.icBack.setOnClickListener(view -> onBackPressed());

        binding.cardLogin.setOnClickListener(view -> {
            if (isValidate())
                addFaq();
        });


    }

    private void addFaq() {
        pd.show();
        RetrofitClient.getClient(activity).addFaq(
                binding.faqType.getText().toString(),
                binding.question.getText().toString(),
                binding.answer.getText().toString()
        ).enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(@NonNull Call<SignupModel> call, @NonNull Response<SignupModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            Toast.makeText(activity, "FAQ Added", Toast.LENGTH_SHORT).show();
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
        if (binding.faqType.getText().toString().equalsIgnoreCase("")) {
            binding.faqType.setError("Enter FAQ Type!");
            binding.faqType.requestFocus();
            return false;
        } else if (binding.question.getText().toString().equalsIgnoreCase("")) {
            binding.question.setError("Enter Question!");
            binding.question.requestFocus();
            return false;
        } else if (binding.answer.getText().toString().equalsIgnoreCase("")) {
            binding.answer.setError("Enter Answer!");
            binding.answer.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}