package com.example.vinayak.hw07;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Intent i =  new Intent(getApplicationContext(),LoginActivity.class);

        finish();

        startActivity(i);

    }
}
