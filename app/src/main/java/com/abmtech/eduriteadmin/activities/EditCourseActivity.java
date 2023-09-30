package com.abmtech.eduriteadmin.activities;

import static com.abmtech.eduriteadmin.apis.BaseUrls.uploadImage;
import static com.abmtech.eduriteadmin.apis.BaseUrls.uploadVideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.adapters.CourseMaterialListAdapter;
import com.abmtech.eduriteadmin.adapters.CourseReviewListAdapter;
import com.abmtech.eduriteadmin.apis.BaseUrls;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivityEditCourseBinding;
import com.abmtech.eduriteadmin.models.CourseDetailsModel;
import com.abmtech.eduriteadmin.models.SignupModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.file_picker.FilePicker;
import com.github.file_picker.FileType;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCourseActivity extends AppCompatActivity {

    String courseId = "";
    ProgressDialog pd;
    Activity activity;
    ActivityEditCourseBinding binding;
    String courseImageName = "", courseVideoName = "";
    private String selectLevel = "";
    ArrayList<String> levelList = new ArrayList<>();

    File imageFile = null;
    File VideoFile = null;
    private boolean isImageUpload = false;
    private boolean isVideoUpload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        pd = new ProgressDialog(activity);

        courseId = getIntent().getStringExtra("course_id");


        binding.selectImageLinear.setOnClickListener(view -> new FilePicker.Builder(this)
                .setLimitItemSelection(1)
                .setFileType(FileType.IMAGE)
                .setAccentColor(Color.CYAN)
                .setCancellable(false)
                .setOnSubmitClickListener(files -> {
                    imageFile = files.get(0).getFile();
                    binding.selectImage.setText(imageFile.getName());
                    binding.selectImageTv.setVisibility(View.GONE);
                    binding.progressImage.setVisibility(View.VISIBLE);
                    isImageUpload = true;
                    uploadImage();
                })
                .setOnItemClickListener((media, pos, adapter) -> {
                    if (!media.getFile().isDirectory()) {
                        adapter.setSelected(pos);
                    }
                })
                .buildAndShow());

        binding.selectVideoLinear.setOnClickListener(view -> new FilePicker.Builder(this)
                .setLimitItemSelection(1)
                .setFileType(FileType.VIDEO)
                .setAccentColor(Color.CYAN)
                .setCancellable(false)
                .setOnSubmitClickListener(files -> {
                    VideoFile = files.get(0).getFile();
                    binding.selectVideo.setText(VideoFile.getName());
                    binding.selectVideoTv.setVisibility(View.GONE);
                    binding.progressVideo.setVisibility(View.VISIBLE);
                    isVideoUpload = true;
                    uploadVideo();
                })
                .setOnItemClickListener((media, pos, adapter) -> {
                    if (!media.getFile().isDirectory()) {
                        adapter.setSelected(pos);
                    }
                })
                .buildAndShow());


        binding.cardAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourse();
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

                            binding.courseNameEdit.setText(courseData.getTitle());
                            binding.courseDescriptionEdit.setText(courseData.getDescription());

                            courseImageName = courseData.getImage();
                            courseVideoName = courseData.getVideo();
                            binding.selectImage.setText(courseImageName);
                            binding.selectVideo.setText(courseVideoName);

                            selectLevel = courseData.getLevel();


                            levelList.add("Beginner");
                            levelList.add("Intermediate");
                            levelList.add("Expert");
                            binding.levelSpinner.setItems(levelList);

                            binding.levelSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                                @Override
                                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                                    selectLevel = item;
                                }
                            });

                            if (!selectLevel.equalsIgnoreCase("")) {
                                for (int i = 0; i < levelList.size(); i++) {
                                    if (levelList.get(i).equalsIgnoreCase(selectLevel)) {
                                        binding.levelSpinner.setSelectedIndex(i);
                                    }
                                }
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

    private void addCourse() {
        pd.show();
        Call<SignupModel> call;

        if (VideoFile != null || !courseVideoName.equalsIgnoreCase("")) {
            call = RetrofitClient.getClient(activity).updateCourseWithVideo(
                    binding.courseNameEdit.getText().toString(),
                    binding.courseDescriptionEdit.getText().toString(),
                    courseImageName,
                    selectLevel,
                    "course",
                    courseVideoName,
                    courseId,
                    "0"
            );
        } else {
            call = RetrofitClient.getClient(activity).updateCourse(
                    binding.courseNameEdit.getText().toString(),
                    binding.courseDescriptionEdit.getText().toString(),
                    courseImageName,
                    selectLevel,
                    "course",
                    courseId,
                    "0"
            );
        }

        call.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(@NonNull Call<SignupModel> call, @NonNull Response<SignupModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            Toast.makeText(EditCourseActivity.this, "Course Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Snackbar.make(binding.getRoot(), response.body().getMsg(), Snackbar.LENGTH_LONG).show();
                            pd.dismiss();
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

    private void uploadImage() {
        //pd.show();

        ANRequest.MultiPartBuilder anAdd = AndroidNetworking.upload((BaseUrls.URL + uploadImage));
        if (imageFile != null) anAdd.addMultipartFile("image", imageFile);
        anAdd.setPriority(Priority.HIGH);
        anAdd.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pd.dismiss();
                        try {
                            Log.d("---rrrProfile", "save_postsave_post" + jsonObject.toString());
                            String result = jsonObject.getString("result");
                            String filename = jsonObject.getString("filename");
                            if (result.equalsIgnoreCase("true")) {
                                courseImageName = filename;
                                isImageUpload = false;
                                binding.linearCircleImage.setVisibility(View.GONE);
                                Snackbar.make(binding.getRoot(), "image Thumbnail Uploaded", Snackbar.LENGTH_LONG).show();
                            } else {
                                isImageUpload = false;
                                binding.linearCircleImage.setVisibility(View.GONE);
                                Snackbar.make(binding.getRoot(), "image Thumbnail failed to upload", Snackbar.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error On Server ", "Error " + anError.toString());
                        Toast.makeText(activity, "Some  Thing Went Wrong", Toast.LENGTH_SHORT).show();
                        pd.dismiss();

                    }
                });


    }

    private void uploadVideo() {
        //pd.show();

        ANRequest.MultiPartBuilder anAdd = AndroidNetworking.upload((BaseUrls.URL + uploadVideo));
        if (VideoFile != null) anAdd.addMultipartFile("video", VideoFile);
        anAdd.setPriority(Priority.HIGH);
        anAdd.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pd.dismiss();
                        try {
                            Log.d("---rrrProfile", "save_postsave_post" + jsonObject.toString());
                            String result = jsonObject.getString("result");
                            String filename = jsonObject.getString("filename");
                            Log.e("TAG", "onResponse: FileNAme " + filename);
                            if (result.equalsIgnoreCase("true")) {
                                courseVideoName = filename;
                                isVideoUpload = false;
                                binding.linearCircleVideo.setVisibility(View.GONE);
                                Snackbar.make(binding.getRoot(), "Video Uploaded", Snackbar.LENGTH_LONG).show();
                            } else {
                                isVideoUpload = false;
                                binding.linearCircleVideo.setVisibility(View.GONE);
                                Snackbar.make(binding.getRoot(), "Video failed to upload", Snackbar.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error On Server ", "Error " + anError.toString());
                        Toast.makeText(activity, "Some  Thing Went Wrong", Toast.LENGTH_SHORT).show();
                        pd.dismiss();

                    }
                });


    }

}