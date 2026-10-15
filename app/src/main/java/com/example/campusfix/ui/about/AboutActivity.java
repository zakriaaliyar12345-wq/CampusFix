package com.example.campusfix.ui.about;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusfix.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String version;
        try {
            version = getPackageManager()
                    .getPackageInfo(getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            // Never surface stack traces to users; fall back to unknown.
            version = "?";
        }

        android.widget.TextView versionView =
                findViewById(R.id.tvVersion);
        versionView.setText(
                getString(R.string.about_version, version));
    }
}
