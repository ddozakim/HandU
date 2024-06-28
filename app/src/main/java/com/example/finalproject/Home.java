package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button numberBtn = (Button) findViewById(R.id.numberMode);
        numberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_N = new Intent(getApplicationContext(), waiting_NumberMode.class);
                startActivity(intent_N);
            }
        });

        Button alphabetBtn = (Button) findViewById(R.id.alphabetMode);
        alphabetBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent_A = new Intent(getApplicationContext(), waiting_AlphabetMode.class);
                startActivity(intent_A);
            }
        });

        Button wordBtn = (Button) findViewById(R.id.wordMode);
        wordBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent_W = new Intent(getApplicationContext(), waiting_WordMode.class);
                startActivity(intent_W);
            }
        });
    }
}