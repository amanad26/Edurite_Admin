package com.abmtech.eduriteadmin.activities;

import static com.abmtech.eduriteadmin.apis.BaseUrls.AddCourseAndMaterial;
import static com.abmtech.eduriteadmin.apis.BaseUrls.uploadImage;
import static com.abmtech.eduriteadmin.apis.BaseUrls.uploadVideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abmtech.eduriteadmin.apis.BaseUrls;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivityAddCourseBinding;
import com.abmtech.eduriteadmin.models.SignupModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.file_picker.FilePicker;
import com.github.file_picker.FileType;
import com.github.file_picker.data.model.Media;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCourseActivity extends AppCompatActivity {

    ActivityAddCourseBinding binding;
    AddCourseActivity activity;
    private ArrayList<Uri> photoPaths;
    File imageFile = null;
    File VideoFile = null;
    ProgressDialog pd;
    String selectLevel = "";
    private boolean isImageUpload = false;
    private boolean isVideoUpload = false;
    String imageFileName = "";
    String videoFileName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        pd = new ProgressDialog(activity);

        binding.selectImageLinear.setOnClickListener(view -> new FilePicker.Builder(this)
                .setLimitItemSelection(1)
                .setFileType(FileType.IMAGE)
                .setAccentColor(Color.CYAN)
                .setCancellable(true)
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

        binding.selectVideoLinear.setOnClickListener(view -> new FilePicker.Builder(activity)
                .setLimitItemSelection(1)
                .setFileType(FileType.VIDEO)
                .setAccentColor(Color.CYAN)
                .setCancellable(true)
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
                if (isValidate())
                    addCourse();
            }
        });

        binding.levelSpinner.setItems("Beginner", "Intermediate", "Expert");
        binding.levelSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectLevel = item;
            }
        });


    }

    private void addCourse() {
        pd.show();
        Call<SignupModel> call;

        if (VideoFile != null) {
            call = RetrofitClient.getClient(activity).addCourseWithVideo(
                    binding.courseNameEdit.getText().toString(),
                    binding.courseDescriptionEdit.getText().toString(),
                    imageFileName,
                    selectLevel,
                    "course",
                    videoFileName
            );
        } else {
            call = RetrofitClient.getClient(activity).addCourse(
                    binding.courseNameEdit.getText().toString(),
                    binding.courseDescriptionEdit.getText().toString(),
                    imageFileName,
                    selectLevel,
                    "course"
            );
        }

        call.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(@NonNull Call<SignupModel> call, @NonNull Response<SignupModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            Toast.makeText(AddCourseActivity.this, "Course Added", Toast.LENGTH_SHORT).show();
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
                                imageFileName = filename;
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
                                videoFileName = filename;
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


    private boolean isValidate() {
        if (binding.courseNameEdit.getText().toString().equalsIgnoreCase("")) {
            binding.courseNameEdit.setError("Enter Course Name ..!");
            binding.courseNameEdit.requestFocus();
            return false;
        } else if (binding.courseDescriptionEdit.getText().toString().equalsIgnoreCase("")) {
            binding.courseDescriptionEdit.setError("Enter Course Description..!");
            binding.courseDescriptionEdit.requestFocus();
            return false;
        } else if (imageFile == null) {
            Snackbar.make(binding.getRoot(), "Select image Thumbnail!", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (selectLevel.equalsIgnoreCase("")) {
            Snackbar.make(binding.getRoot(), "Select Level!", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (isVideoUpload || isImageUpload) {
            Snackbar.make(binding.getRoot(), "Waiting for files upload !", Snackbar.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

}