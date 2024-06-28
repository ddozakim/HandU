package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;
import java.text.DecimalFormat;
public class Answer_Number_wrong extends AppCompatActivity {
    float confidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_number_wrong);
        TextView confidenceText = findViewById(R.id.confidence);

        Intent intent = getIntent();
        confidence= intent.getFloatExtra("confidence",0.0f);
        float percent=100*confidence;
        DecimalFormat decimalFormat = new DecimalFormat("#.#######");
        String value =decimalFormat.format(percent);
        confidenceText.setText(value+"%");


        Button homeBtn = (Button) findViewById(R.id.home);
        homeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent_A = new Intent(getApplicationContext(), Home.class);
                startActivity(intent_A);
                finish();
            }
        });

        int mode = intent.getIntExtra("mode",0);

        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mode == 0) {
                    Intent intent_N = new Intent(Answer_Number_wrong.this, waiting_NumberMode.class);
                    startActivity(intent_N);
                    finish();
                }
                else if(mode==1){
                    Intent intent_N = new Intent(Answer_Number_wrong.this, waiting_AlphabetMode.class);
                    startActivity(intent_N);
                    finish();
                }

            }
        }, 3000);
    }
}