package com.mindrops.sixfeet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.ar.core.ArCoreApk;
import com.mindrops.sixfeet.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        checkARAvailability();
    }

    private void checkARAvailability() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            new Handler().postDelayed(this::checkARAvailability, 200);
        }
        if (availability.isSupported()) {
            mainBinding.launchAr.setVisibility(View.VISIBLE);
            mainBinding.launchAr.setEnabled(true);
            mainBinding.launchAr.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,ARActivity.class));
            });
        } else { // Unsupported or unknown.
            mainBinding.launchAr.setVisibility(View.GONE);
            mainBinding.launchAr.setEnabled(false);
            mainBinding.warningMessage.setVisibility(View.VISIBLE);
            mainBinding.warningMessage.setEnabled(true);
        }
    }


}


