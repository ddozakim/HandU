package com.example.finalproject;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;
import android.os.Bundle;
public class waiting_AlphabetMode extends AppCompatActivity {
    String random_alp;
    TextView problem;
    TextView Timer;
    String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_alphabet_mode);

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
        problem = findViewById(R.id.alphabet_problem);
        Random random = new Random();
        random_alp = alphabet[random.nextInt(26)];
        problem.setText(random_alp);


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

                Intent intent = new Intent(waiting_AlphabetMode.this, AlphabetMode.class);
                intent.putExtra("random_alp", random_alp);
                startActivity(intent);

            }
        }.start();
    }
}