package com.abmtech.eduriteadmin.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.abmtech.eduriteadmin.databinding.ActivityDashboardBinding;
import com.abmtech.eduriteadmin.fragments.HomeFragment;
import com.abmtech.eduriteadmin.fragments.MaterialFragment;
import com.abmtech.eduriteadmin.fragments.NotificationFragment;
import com.abmtech.eduriteadmin.fragments.SettingsFragment;
import com.abmtech.eduriteadmin.fragments.UsersFragment;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.google.android.material.snackbar.Snackbar;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;


public class DashboardActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler {
    private Activity activity;
    private ActivityDashboardBinding binding;

    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private static final String TAG = "Sample";
    private InAppUpdateManager inAppUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        setupNavBar();
        changeFragment(0);

        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
                .resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
                .mode(Constants.UpdateMode.IMMEDIATE)
                .useCustomNotification(true)
                .handler(this);

        inAppUpdateManager.checkForAppUpdate();

    }

    private void setupNavBar() {
        binding.homeAnim.setMinAndMaxProgress(0.0f, 0.6f);
        binding.settingsAnim.setMinAndMaxProgress(0.0f, 0.5f);

        binding.homeAnim.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
        );

        binding.materialAnim.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
        );

        binding.coursesAnim.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
        );

        binding.usersAnim.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
        );

        binding.settingsAnim.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
        );

        binding.rlHome.setOnClickListener(view -> {
            binding.homeAnim.playAnimation();
            changeFragment(0);
        });
        binding.rlMaterial.setOnClickListener(view -> {
            binding.materialAnim.playAnimation();
            changeFragment(1);
        });
        binding.rlCourses.setOnClickListener(view -> {
            binding.coursesAnim.playAnimation();
            changeFragment(2);
        });
        binding.rlSettings.setOnClickListener(view -> {
            binding.settingsAnim.playAnimation();
            changeFragment(3);
        });


        binding.rlUsers.setOnClickListener(view -> {
            binding.usersAnim.playAnimation();
            changeFragment(4);
        });
    }

    private void changeFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();

                break;
            case 1:
                fragment = new MaterialFragment();

                break;
            case 2:
                fragment = new NotificationFragment();

                break;
            case 3:
                fragment = new SettingsFragment();

                break;
            case 4:
                fragment = new UsersFragment();

                break;
            default:
                new HomeFragment();
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(binding.container.getId(), fragment);
            ft.commit();
        }
    }

    @Override
    public void onInAppUpdateError(int code, Throwable error) {

    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {
        if (status.isDownloaded()) {

            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar.make(rootView,
                    "An update has just been downloaded.",
                    Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("RESTART", view -> {
                // Triggers the completion of the update of the app for the flexible flow.
                inAppUpdateManager.completeUpdate();
            });

            snackbar.show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // If the update is cancelled by the user,
                // you can request to start the update again.
                inAppUpdateManager.checkForAppUpdate();
                Log.d(TAG, "Update flow failed! Result code: " + resultCode);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}