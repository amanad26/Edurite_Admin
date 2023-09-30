package com.abmtech.eduriteadmin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;

import com.abmtech.eduriteadmin.R;
import com.abmtech.eduriteadmin.apis.BaseUrls;
import com.abmtech.eduriteadmin.databinding.ActivityPlayVideoBinding;
import com.khizar1556.mkvideoplayer.MKPlayer;

public class PlayVideoActivity extends AppCompatActivity {

    ActivityPlayVideoBinding binding;
    Activity activity;
    String title = "";
    String videoUrl = "";
    MKPlayer mkplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        title = getIntent().getStringExtra("title");
        videoUrl = getIntent().getStringExtra("url");

        binding.videoTitle.setText(title);

        Log.e("TAG", "onCreate: Url " + BaseUrls.IMAGE_URL + videoUrl);

        Uri uri = Uri.parse(BaseUrls.IMAGE_URL + videoUrl);

        // sets the resource from the
        // videoUrl to the videoView
        binding.idVideoView.setVideoURI(uri);

        // creating object of
        // media controller class
        MediaController mediaController = new MediaController(activity);

        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(binding.idVideoView);

        // sets the media player to the videoView
        mediaController.setMediaPlayer(binding.idVideoView);

        // sets the media controller to the videoView
        binding.idVideoView.setMediaController(mediaController);

        // starts the video
        binding.idVideoView.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.idVideoView.stopPlayback();
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.idVideoView.stopPlayback();
    }
}