package com.trueconf.circleview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Custom45CircleView custom45CircleView = findViewById(R.id.circleView);
        findViewById(R.id.clickBtn).setOnClickListener(v -> {
            custom45CircleView.onSectorClick(100);

            DialogFragment newFragment = new ConfiguringPtzDialog();
            newFragment.show(getSupportFragmentManager(), "dialog");
        });
    }
}