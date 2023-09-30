package com.abmtech.eduriteadmin.activities;

import static com.abmtech.eduriteadmin.apis.BaseUrls.uploadImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.adapters.MaterialReviewListAdapter;
import com.abmtech.eduriteadmin.apis.ApiInterface;
import com.abmtech.eduriteadmin.apis.BaseUrls;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivityEditMaterialActivtyBinding;
import com.abmtech.eduriteadmin.models.CourseModel;
import com.abmtech.eduriteadmin.models.MaterialDetailsModel;
import com.abmtech.eduriteadmin.models.SignupModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMaterialActivty extends AppCompatActivity {


    ActivityEditMaterialActivtyBinding binding;
    Activity activity;
    String materialId = "";
    ProgressDialog pd;
    private ArrayList<String> courseList = new ArrayList<>();
    private ArrayList<String> courseIdsList = new ArrayList<>();
    private String selectedCourseId = "";

    File materialFile = null;
    String materialFilename = "";
    boolean isFileOnUpload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditMaterialActivtyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        pd = new ProgressDialog(activity);


        materialId = getIntent().getStringExtra("mat_id");

        binding.selectImageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .enableImageCapture(false)
                        .setMaxSelection(1)
                        .setShowFiles(true)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent, 1000);
            }
        });


        binding.cardAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMaterial();
            }
        });


    }


    private void addMaterial() {

        pd.show();
        Call<SignupModel> call;

        if (selectedCourseId.equalsIgnoreCase("")) {
            call = RetrofitClient.getClient(activity).updateMaterial(
                    binding.courseNameEdit.getText().toString(),
                    binding.courseDescriptionEdit.getText().toString(),
                    materialFilename,
                    "material",
                    materialId

            );
        } else {
            call = RetrofitClient.getClient(activity).updateMaterialWithCourse(
                    binding.courseNameEdit.getText().toString(),
                    binding.courseDescriptionEdit.getText().toString(),
                    materialFilename,
                    "material",
                    selectedCourseId,
                    materialId
            );
        }

        call.enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(@NonNull Call<SignupModel> call, @NonNull Response<SignupModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            Toast.makeText(activity, "Material Updated", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            Log.e("TAG", "onActivityResult: File Name " + files.get(0).getName());
            materialFile = new File(files.get(0).getPath());
            binding.selectFile.setText(materialFile.getName());
            uploadImage();
            isFileOnUpload = true;
            binding.selectPdfTv.setVisibility(View.GONE);
            binding.progressImage.setVisibility(View.VISIBLE);
        }
    }


    private void uploadImage() {
        //pd.show();

        ANRequest.MultiPartBuilder anAdd = AndroidNetworking.upload((BaseUrls.URL + uploadImage));
        if (materialFile != null) anAdd.addMultipartFile("image", materialFile);
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
                                materialFilename = filename;
                                isFileOnUpload = false;
                                binding.linearCircleFile.setVisibility(View.GONE);
                                Snackbar.make(binding.getRoot(), "File Uploaded", Snackbar.LENGTH_LONG).show();
                            } else {
                                isFileOnUpload = false;
                                binding.linearCircleFile.setVisibility(View.GONE);
                                Snackbar.make(binding.getRoot(), "File failed to upload", Snackbar.LENGTH_LONG).show();

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


    @Override
    protected void onResume() {
        super.onResume();
        getCourseList();
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

                            binding.courseDescriptionEdit.setText(matData.getMatDescription());
                            binding.courseNameEdit.setText(matData.getMatName());

                            selectedCourseId = matData.getCourseId();
                            materialFilename = matData.getPdfFile();
                            binding.selectFile.setText(materialFilename);


                            for (int i = 0; i < courseIdsList.size(); i++) {
                                if (selectedCourseId.equalsIgnoreCase(courseIdsList.get(i))) {
                                    binding.courseSpinner.setSelectedIndex(i);
                                }
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


    private void getCourseList() {
        ApiInterface apiService = RetrofitClient.getClient(activity);
        apiService.getAllCourse().enqueue(new Callback<CourseModel>() {
            @Override
            public void onResponse(@NonNull Call<CourseModel> call, @NonNull Response<CourseModel> response) {

                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            courseList.clear();
                            courseIdsList.clear();
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                courseList.add(response.body().getData().get(i).getTitle());
                                courseIdsList.add(response.body().getData().get(i).getCourseId());
                            }


                            binding.courseSpinner.setItems(courseList);
                            binding.courseSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                                @Override
                                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                                    selectedCourseId = courseIdsList.get(position);
                                    Log.e("TAG", "onItemSelected: " + item);
                                }
                            });


                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CourseModel> call, @NonNull Throwable t) {
                Log.e("TAG", "onFailure() called with: call = [" + call + "], t = [" + t.getLocalizedMessage() + "]");
                Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
            }
        });

    }
}