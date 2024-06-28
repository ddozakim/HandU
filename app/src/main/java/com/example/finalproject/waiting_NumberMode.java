package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;
import android.os.Bundle;
public class waiting_NumberMode extends AppCompatActivity {
    String random_num;
    TextView problem;
    TextView Timer;
    String[] number = {"0","1","2","3","4","5","6","7","8","9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_number_mode);

        Button homeBtn = (Button) findViewById(R.id.home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_A = new Intent(getApplicationContext(), Home.class);
                startActivity(intent_A);
                finish();
            }
        });
        //number question
        problem = findViewById(R.id.number_problem);
        Random random = new Random();
        random_num = number[random.nextInt(10)];
        problem.setText(random_num);


        System.out.println("랜덤 전달됨" );

        //timer
        Timer = findViewById(R.id.timer);
        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                int count = (int) (millisUntilFinished / 1000);
                Timer.setText(Integer.toString(count));
            }

            public void onFinish() {
                System.out.println("초 끝남" );

                Intent intent = new Intent(waiting_NumberMode.this, NumberMode.class);
                intent.putExtra("random_num", random_num);
                startActivity(intent);

            }
        }.start();


    }
}