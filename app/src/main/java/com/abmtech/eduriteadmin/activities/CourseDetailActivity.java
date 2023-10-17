package com.abmtech.eduriteadmin.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.Session.Session;
import com.abmtech.eduriteadmin.adapters.CourseReviewListAdapter;
import com.abmtech.eduriteadmin.apis.ApiInterface;
import com.abmtech.eduriteadmin.apis.BaseUrls;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivityCourseDetailBinding;
import com.abmtech.eduriteadmin.databinding.AddReviewDialogBinding;
import com.abmtech.eduriteadmin.models.CourseDetailsModel;
import com.abmtech.eduriteadmin.models.LoginModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailActivity extends AppCompatActivity {
    private Activity activity;
    private ActivityCourseDetailBinding binding;
    private Session session;
    private ProgressDialog progressDialog;
    private ExoPlayer player;
    private String courseId = "";


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null)
            player.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null)
            player.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null)
            player.play();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        session = new Session(activity);
        progressDialog = new ProgressDialog(activity);

        courseId = getIntent().getStringExtra("course_id");
        getCourseDetails(courseId);
        player = new ExoPlayer.Builder(activity).build();

        binding.imageBack.setOnClickListener(v -> onBackPressed());
        // binding.textAddRating.setOnClickListener(v -> addRating());
    }

    private void addRating() {
        AddReviewDialogBinding dialogBinding = AddReviewDialogBinding.inflate(LayoutInflater.from(activity));

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(activity, R.style.MyRounded_MaterialComponents_MaterialAlertDialog)
                .setView(dialogBinding.getRoot())
                .show();

        alertDialog.setCanceledOnTouchOutside(true);

        dialogBinding.imageClose.setOnClickListener(view -> alertDialog.dismiss());
        dialogBinding.textAddRating.setOnClickListener(view -> {
            if (dialogBinding.rating.getRating() > 0) {
                if (dialogBinding.edtRatingText.getText().toString().length() > 5) {
                    //addReview(alertDialog, dialogBinding.rating.getRating(), dialogBinding.edtRatingText.getText().toString().trim());
                } else {
                    dialogBinding.edtRatingText.setError("Enter at least 6 letters!");
                    dialogBinding.edtRatingText.requestFocus();
                }
            } else Toast.makeText(activity, "Select Rating in Stars!", Toast.LENGTH_SHORT).show();
        });
    }

    /* private void addReview(AlertDialog alertDialog, float rating, String ratingText) {
         progressDialog.show();
         ApiInterface apiService = RetrofitClient.getClient(activity);
         apiService.addReviewCourse(session.getUserId(), courseId, session.getUserName(), ratingText, String.valueOf(rating))
                 .enqueue(new Callback<LoginModel>() {
                     @Override
                     public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                         progressDialog.dismiss();
                         if (response.code() == 200) {
                             if (response.body() != null) {
                                 if (response.body().getResult().equalsIgnoreCase("true")) {
                                     binding.textAddRating.setVisibility(View.GONE);
                                     alertDialog.dismiss();
                                 } else {
                                     Snackbar.make(binding.getRoot(), response.body().getMsg(), Snackbar.LENGTH_LONG).show();
                                     Log.d(TAG, "onResponse() called with: call = [" + call + "], response msg = [" + response.body().getMsg() + "]");
                                 }
                             }
                         }
                     }

                     @Override
                     public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                         Log.e(TAG, "onFailure() called with: call = [" + call + "], t = [" + t.getLocalizedMessage() + "]");
                         Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
                         progressDialog.dismiss();
                     }
                 });
     }
 */

    @SuppressLint("UnsafeOptInUsageError")
    private void setPlayer() {
        try {
            binding.playerView.setShowNextButton(false);
            binding.playerView.setShowPreviousButton(false);
        } catch (Exception e) {
            Log.e(TAG, "onResponse: ", e);
        }
    }

    private void getCourseDetails(String courseId) {
        progressDialog.show();
        ApiInterface apiService = RetrofitClient.getClient(activity);
        apiService.getCourseDetails(courseId).enqueue(new Callback<CourseDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<CourseDetailsModel> call, @NonNull Response<CourseDetailsModel> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            CourseDetailsModel.Data data = response.body().getData();

                            binding.textHeading.setText(data.getCourse().getTitle());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                binding.textDesc.setText(Html.fromHtml(data.getCourse().getDescription(), Html.FROM_HTML_MODE_COMPACT));
                            }
                            binding.textLevel.setText(data.getCourse().getLevel());
                            binding.textRating.setText(data.getCourse().getAvgRating());
                            binding.rating.setRating(Float.parseFloat(data.getCourse().getAvgRating()));
                            binding.textTotalRating.setText(String.valueOf(data.getReviews().size()));

                            binding.recyclerViewRating.setAdapter(new CourseReviewListAdapter(activity, response.body().getData().getReviews()));
                            binding.recyclerViewRating.setLayoutManager(new LinearLayoutManager(activity));

                            boolean flag = true;
                            for (CourseDetailsModel.Data.Review review : response.body().getData().getReviews()) {
                                if (review.getUserId().equalsIgnoreCase(session.getUserId()))
                                    flag = false;
                            }

                            // if (flag) binding.textAddRating.setVisibility(View.VISIBLE);

                            if (!data.getCourse().getImage().isEmpty())
                                Glide.with(getApplicationContext()).load(BaseUrls.IMAGE_URL + data.getCourse().getImage()).into(binding.imageThumbnail);

                            if (data.getCourse().getVideo().isEmpty()) {
                                binding.playerView.setVisibility(View.GONE);
                                binding.imageThumbnail.setVisibility(View.VISIBLE);
                            } else {
                                binding.playerView.setVisibility(View.VISIBLE);
                                binding.imageThumbnail.setVisibility(View.GONE);

                                binding.playerView.setPlayer(player);

                                setPlayer();

                                MediaItem mediaItem = MediaItem.fromUri(BaseUrls.IMAGE_URL + data.getCourse().getVideo());
                                player.setMediaItem(mediaItem);
                                player.prepare();
                                player.play();
                            }

                            binding.progressbar.setVisibility(View.GONE);
                            if (data.getReviews().size() > 0)
                                binding.textEmpty.setVisibility(View.GONE);
                            else binding.textEmpty.setVisibility(View.VISIBLE);
                        } else {
                            Snackbar.make(binding.getRoot(), response.body().getMsg(), Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, "onResponse() called with: call = [" + call + "], response msg = [" + response.body().getMsg() + "]");
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CourseDetailsModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure() called with: call = [" + call + "], t = [" + t.getLocalizedMessage() + "]");
                Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

}