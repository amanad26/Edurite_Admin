package com.abmtech.eduriteadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.Session.Session;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivityLoginBinding;
import com.abmtech.eduriteadmin.models.LoginModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    Activity activity;
    ProgressDialog pd;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        pd = new ProgressDialog(activity);
        session = new Session(activity);

        binding.cardLogin.setOnClickListener(view -> {
            if (isValidate())
                login();
        });

    }

    private boolean isValidate() {
        if (binding.usernameEdit.getText().toString().equalsIgnoreCase("")) {
            binding.usernameEdit.setError("Enter Your Username ..!");
            binding.usernameEdit.requestFocus();
            return false;
        } else if (binding.edtPasswordCode.getText().toString().equalsIgnoreCase("")) {
            binding.edtPasswordCode.setError("Enter Your Password..!");
            binding.edtPasswordCode.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void login() {
        pd.show();
        RetrofitClient.getClient(activity).loginAdmin(
                binding.usernameEdit.getText().toString(),
                binding.edtPasswordCode.getText().toString(),
                "123456789"
        ).enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {

                            if (response.body().getData().getType().equalsIgnoreCase("admin")) {

                                session.setLogin(true);
                                session.setUserId(response.body().getData().getId());
                                session.setUserName(response.body().getData().getName());
                                session.setUserImage(response.body().getData().getProfileImg());

                                startActivity(new Intent(activity, DashboardActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                );
                                finish();
                            } else {
                                Toast.makeText(activity, "Invalid Username Or password..!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(activity, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
            }

            @Override
            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                pd.dismiss();
                Toast.makeText(activity, "Server Not Respond", Toast.LENGTH_SHORT).show();
            }
        });

    }

}