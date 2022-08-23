package com.buenatech.staytune.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.buenatech.staytune.R;
import com.buenatech.staytune.fragments.TimeSettingsFragment;
import com.buenatech.staytune.utils.PreferenceUtil;


public class TimeSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        toolbar.setTitle(null);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, new TimeSettingsFragment())
                .commit();

    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(this, SummaryActivity.class));
        PreferenceUtil.setStartActivityShown(this, true);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
