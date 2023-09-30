package com.abmtech.eduriteadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.adapters.CourseReviewListAdapter;
import com.abmtech.eduriteadmin.adapters.MaterialReviewListAdapter;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivityMaterialDetailsActivtityBinding;
import com.abmtech.eduriteadmin.models.MaterialDetailsModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialDetailsActivtity extends AppCompatActivity {

    ActivityMaterialDetailsActivtityBinding binding;
    Activity activity;
    String materialId = "";
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaterialDetailsActivtityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        pd = new ProgressDialog(activity);
        materialId = getIntent().getStringExtra("mat_id");
        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity,EditMaterialActivty.class)
                        .putExtra("mat_id", materialId));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getMaterialDetails();
    }

    private void getMaterialDetails() {

        pd.show();
        RetrofitClient.getClient(activity).getMaterialDetails(materialId).enqueue(new Callback<MaterialDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<MaterialDetailsModel> call, @NonNull Response<MaterialDetailsModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {

                            MaterialDetailsModel.Data data = response.body().getData();
                            MaterialDetailsModel.Data.Material matData = data.getMaterial();

                            binding.textDesc.setText(matData.getMatDescription());
                            binding.textHeading.setText(matData.getMatName());
                            binding.textRating.setText(matData.getAvgRating());
                            binding.textTotalRating.setText(matData.getReviewCount());
                            binding.rating.setRating(Float.parseFloat(matData.getAvgRating()));


                            if (data.getReviews().size() != 0) {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                                linearLayoutManager.setReverseLayout(true);
                                linearLayoutManager.setStackFromEnd(true);
                                binding.recyclerViewRating.setLayoutManager(linearLayoutManager);
                                binding.recyclerViewRating.setAdapter(new MaterialReviewListAdapter(activity, data.getReviews()));
                            } else {
                                binding.textEmptyRating.setVisibility(View.VISIBLE);
                            }


                        } else {
                            Toast.makeText(activity, "Material Details Not Found!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
            }

            @Override
            public void onFailure(@NonNull Call<MaterialDetailsModel> call, @NonNull Throwable t) {

            }
        });

    }

}