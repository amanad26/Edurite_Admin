package com.abmtech.eduriteadmin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abmtech.eduriteadmin.adapters.CourseMaterialListAdapter;
import com.abmtech.eduriteadmin.adapters.CourseReviewListAdapter;
import com.abmtech.eduriteadmin.adapters.MaterialListAdapter;
import com.abmtech.eduriteadmin.apis.BaseUrls;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivityCourseDetailBinding;
import com.abmtech.eduriteadmin.models.CourseDetailsModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailActivity extends AppCompatActivity {

    ActivityCourseDetailBinding binding;
    String courseId = "";
    ProgressDialog pd;
    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        pd = new ProgressDialog(activity);

        courseId = getIntent().getStringExtra("course_id");

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity,EditCourseActivity.class)
                        .putExtra("course_id",courseId));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCourseDetails();
    }

    private void getCourseDetails() {

        pd.show();
        RetrofitClient.getClient(activity).getCourseDetails(courseId).enqueue(new Callback<CourseDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<CourseDetailsModel> call, @NonNull Response<CourseDetailsModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            CourseDetailsModel.Data data = response.body().getData();
                            CourseDetailsModel.Data.Course courseData = data.getCourse();

                            binding.textDesc.setText(courseData.getDescription());
                            binding.textHeading.setText(courseData.getTitle());

                            binding.rating.setRating(Float.parseFloat(courseData.getAvgRating()));
                            binding.textRating.setText(courseData.getAvgRating());
                            binding.textLevel.setText(courseData.getLevel());
                            binding.textTotalRating.setText("(" + courseData.getReviewCount() + ")");

                            if (!courseData.getImage().equalsIgnoreCase("")) {
                                Picasso.get().load(BaseUrls.IMAGE_URL + courseData.getImage())
                                        .into(binding.imageThumbnail);
                            }

                            if (data.getMaterials().size() != 0) {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                                linearLayoutManager.setReverseLayout(true);
                                linearLayoutManager.setStackFromEnd(true);
                                binding.recyclerViewMaterials.setLayoutManager(linearLayoutManager);
                                binding.recyclerViewMaterials.setAdapter(new CourseMaterialListAdapter(activity, data.getMaterials()));
                            } else {
                                binding.textEmptyMaterial.setVisibility(View.VISIBLE);
                            }


                            if (data.getReviews().size() != 0) {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                                linearLayoutManager.setReverseLayout(true);
                                linearLayoutManager.setStackFromEnd(true);
                                binding.recyclerViewRating.setLayoutManager(linearLayoutManager);
                                binding.recyclerViewRating.setAdapter(new CourseReviewListAdapter(activity, data.getReviews()));
                            } else {
                                binding.textEmptyRating.setVisibility(View.VISIBLE);
                            }


                        } else {
                            Toast.makeText(activity, "Course Details Not Found!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
            }

            @Override
            public void onFailure(Call<CourseDetailsModel> call, Throwable t) {
                pd.dismiss();
            }
        });


    }
}
