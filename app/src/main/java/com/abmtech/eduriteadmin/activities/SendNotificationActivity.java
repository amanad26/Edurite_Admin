package com.abmtech.eduriteadmin.activities;

import static com.abmtech.eduriteadmin.apis.BaseUrls.uploadImage;
import static com.abmtech.eduriteadmin.utils.SendNotification.sendNotification;
import static com.abmtech.eduriteadmin.utils.SendNotification.sendNotificationToSingleUser;
import static com.abmtech.eduriteadmin.utils.Util.encodeImageBitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.adapters.UsersListAdapter;
import com.abmtech.eduriteadmin.apis.BaseUrls;
import com.abmtech.eduriteadmin.apis.RetrofitClient;
import com.abmtech.eduriteadmin.databinding.ActivitySendNotificationBinding;
import com.abmtech.eduriteadmin.models.SignupModel;
import com.abmtech.eduriteadmin.models.UsersModel;
import com.abmtech.eduriteadmin.utils.ProgressDialog;
import com.abmtech.eduriteadmin.utils.Util;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.file_picker.FilePicker;
import com.github.file_picker.FileType;
import com.google.android.material.snackbar.Snackbar;
import com.leo.searchablespinner.SearchableSpinner;
import com.leo.searchablespinner.interfaces.OnItemSelectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationActivity extends AppCompatActivity {

    ActivitySendNotificationBinding binding;
    Activity activity;
    ProgressDialog pd;
    ArrayList<String> userNameList = new ArrayList<>();
    ArrayList<String> userIdsList = new ArrayList<>();
    ArrayList<String> userFCMsList = new ArrayList<>();
    String selectedUserId = "0";
    String selectedUserFCM = "";
    private File imageFile = null;
    private String image = "ab";
    private boolean isImageUpload = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        pd = new ProgressDialog(activity);

        binding.userSpinner.setOnClickListener(view -> showSearchableSpinner());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
        Date d = new Date();
        String time = formatter2.format(d.getTime());

        Locale locale = new Locale("en", "in");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        String date = dateFormat.format(d);

        Log.e("TAG", "sendNotification: Date " + date + " " + time);

        binding.cardAdd.setOnClickListener(view -> {
            if (isValidate())
                sendNotificationTpAll();
        });


        binding.selectVideoLinear.setOnClickListener(view -> new FilePicker.Builder(this)
                .setLimitItemSelection(1)
                .setFileType(FileType.IMAGE)
                .setAccentColor(Color.CYAN)
                .setCancellable(false)
                .setOnSubmitClickListener(files -> {
                    imageFile = files.get(0).getFile();
                    binding.selectVideo.setText(imageFile.getName());
                    uploadImage();
                    binding.selectVideoTv.setVisibility(View.GONE);
                    binding.progressVideo.setVisibility(View.VISIBLE);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(imageFile));
                        image = encodeImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .setOnItemClickListener((media, pos, adapter) -> {
                    if (!media.getFile().isDirectory()) {
                        adapter.setSelected(pos);
                    }
                })
                .buildAndShow());

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
                                image = filename;
                                isImageUpload = false;
                                binding.linearCircleVideo.setVisibility(View.GONE);
                                Snackbar.make(binding.getRoot(), "image  Uploaded", Snackbar.LENGTH_LONG).show();
                            } else {
                                isImageUpload = false;
                                binding.linearCircleVideo.setVisibility(View.GONE);
                                Snackbar.make(binding.getRoot(), "image  failed to upload", Snackbar.LENGTH_LONG).show();

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

    private void sendNotificationTpAll() {

        pd.show();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
        Date d = new Date();
        String time = formatter2.format(d.getTime());

        Locale locale = new Locale("en", "in");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        String date = dateFormat.format(d);

        Log.e("TAG", "sendNotification: Date " + date + " " + time);

        RetrofitClient.getClient(activity).sendNotificationToUser(
                binding.courseNameEdit.getText().toString(),
                binding.courseDescriptionEdit.getText().toString(),
                image,
                date + " " + time,
                selectedUserId
        ).enqueue(new Callback<SignupModel>() {
            @Override
            public void onResponse(@NonNull Call<SignupModel> call, @NonNull Response<SignupModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            Map<String, String> notiBody = new HashMap<>();
                            Map<String, String> data = new HashMap<>();

                            notiBody.put("title", binding.courseNameEdit.getText().toString());
                            notiBody.put("body", binding.courseDescriptionEdit.getText().toString());

                            data.put("image", BaseUrls.IMAGE_URL + image);

                            if (selectedUserFCM.equalsIgnoreCase("")) {
                                sendNotification("a", notiBody, data);
                            } else {
                                sendNotificationToSingleUser(selectedUserFCM, notiBody, data);
                            }

                            Toast.makeText(activity, "Notification Sent", Toast.LENGTH_SHORT).show();
                            finish();
                        }
            }

            @Override
            public void onFailure(@NonNull Call<SignupModel> call, @NonNull Throwable t) {

            }
        });


    }

    private boolean isValidate() {
        if (binding.courseNameEdit.getText().toString().equalsIgnoreCase("")) {
            binding.courseNameEdit.setError("Enter Title ..!");
            binding.courseNameEdit.requestFocus();
            return false;
        } else if (binding.courseDescriptionEdit.getText().toString().equalsIgnoreCase("")) {
            binding.courseDescriptionEdit.setError("Enter  Description..!");
            binding.courseDescriptionEdit.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void showSearchableSpinner() {

        final SearchableSpinner searchableSpinner = new SearchableSpinner(activity);
        //Optional Parameters
        searchableSpinner.setWindowTitle("SEARCHABLE SPINNER");

        //Setting up list items for spinner
        searchableSpinner.setSpinnerListItems(userNameList);

        searchableSpinner.setOnItemSelectListener((k, s) -> {
            selectedUserId = userIdsList.get(k);
            selectedUserFCM = userFCMsList.get(k);
        });


        searchableSpinner.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getUsersList();
    }

    private void getUsersList() {
        RetrofitClient.getClient(activity).getUsersList().enqueue(new Callback<UsersModel>() {
            @Override
            public void onResponse(@NonNull Call<UsersModel> call, @NonNull Response<UsersModel> response) {
                pd.dismiss();
                if (response.code() == 200)
                    if (response.body() != null)
                        if (response.body().getResult().equalsIgnoreCase("true")) {
                            userFCMsList.clear();
                            userNameList.clear();
                            userIdsList.clear();
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                userIdsList.add(response.body().getData().get(i).getId());
                                userNameList.add(response.body().getData().get(i).getName());
                                userFCMsList.add(response.body().getData().get(i).getFcm());
                            }
                        } else {
                            String message = response.body().getMsg();
                            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                        }
            }

            @Override
            public void onFailure(@NonNull Call<UsersModel> call, @NonNull Throwable t) {
                Log.e("TAG", "onFailure() called with: call = [" + call + "], t = [" + t.getLocalizedMessage() + "]");
                Snackbar.make(binding.getRoot(), "Server Error!", Snackbar.LENGTH_LONG).show();
                pd.dismiss();
            }
        });

    }


}