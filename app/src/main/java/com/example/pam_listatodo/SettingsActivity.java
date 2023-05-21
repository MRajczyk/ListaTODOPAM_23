package com.example.pam_listatodo;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pam_listatodo.fragments.SettingsFragment;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null)
            savedInstanceState.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");

        if (findViewById(R.id.idFrameLayout) != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.idFrameLayout, new SettingsFragment())
                    .commit();
        }
    }
}