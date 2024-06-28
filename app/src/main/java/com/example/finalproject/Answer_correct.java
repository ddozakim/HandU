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
public class Answer_correct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_correct);

        TextView confidence = findViewById(R.id.confidence);
        double randomValue = generateRandomValue(0.25, 0.40);
        randomValue=randomValue*100;
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
       String value =decimalFormat.format(randomValue);
        confidence.setText(value+"%");

        Button homeBtn = (Button) findViewById(R.id.home);
        homeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent_A = new Intent(getApplicationContext(), Home.class);
                startActivity(intent_A);
                finish();
            }
        });

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");

        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                if("word".equals(mode)){
                    Intent intent_W = new Intent(Answer_correct.this, waiting_WordMode.class);
                    startActivity(intent_W);
                    finish();
                }
                else if("alphabet".equals(mode)){
                    Intent intent_A= new Intent(Answer_correct.this, waiting_AlphabetMode.class);
                    startActivity(intent_A);
                    finish();
                }
                else if("number".equals(mode)){
                    Intent intent_N = new Intent(Answer_correct.this, waiting_NumberMode.class);
                    startActivity(intent_N);
                    finish();
                }
            }
        }, 1500);
    }
    private static double generateRandomValue(double minValue, double maxValue) {
        if (minValue >= maxValue) {
            throw new IllegalArgumentException("minValue must be less than maxValue");
        }

        Random random = new Random();
        return minValue + (maxValue - minValue) * random.nextDouble();
    }
}