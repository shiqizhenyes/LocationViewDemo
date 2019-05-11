package me.nice.locationviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button start;
    private LocationView locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationView = findViewById(R.id.locationView);
        start = findViewById(R.id.start);
        start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        locationView.startAnimation();
    }
}
