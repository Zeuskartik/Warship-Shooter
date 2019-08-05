package com.mindrops.shooter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Start extends AppCompatActivity {
    LocalStorage localStorage = new LocalStorage(Start.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        TextView lowestTime = findViewById(R.id.lowestTime);
        if(localStorage.getTime()!=null) {
            lowestTime.setText("Lowest Time - " + localStorage.getTime());
        }
        else{
            lowestTime.setText("Lowest Time - " + "No record found.");
        }
        TextView start = findViewById(R.id.start_game);
        start.setOnClickListener(view -> {
            Intent intent = new Intent(Start.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
