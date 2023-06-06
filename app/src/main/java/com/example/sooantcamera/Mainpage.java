package com.example.sooantcamera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Mainpage extends AppCompatActivity {

    private Button btn_photosearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        btn_photosearch = findViewById(R.id.btn_photosearch);

        btn_photosearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mainpage.this, camera.class);
                startActivity(intent);  //액티비티 이동 구문
            }
        });


    }
}
