package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;
import android.os.Bundle;
public class waiting_WordMode extends AppCompatActivity {
    String random_word;
    TextView problem;
    TextView Timer;
    String[] word = {"donkey", "mouse", "duck", "bird", "cow", "cat", "frog", "owl", "bee","goose", "tiger", "lion", "horse", "alligator", "kitty", "wolf", "bug", "elephant", "giraffe", "dog", "fish", "zebra", "pig", "puppy", "animal"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_word_mode);

        Button homeBtn = (Button) findViewById(R.id.home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_A = new Intent(getApplicationContext(), Home.class);
                startActivity(intent_A);
                finish();
            }
        });

        problem = findViewById(R.id.word_problem);
        Random random = new Random();
        random_word = word[random.nextInt(25)];
        problem.setText(random_word);


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

                Intent intent = new Intent(waiting_WordMode.this, WordMode.class);
                intent.putExtra("random_word", random_word);
                startActivity(intent);

            }
        }.start();
    }
}