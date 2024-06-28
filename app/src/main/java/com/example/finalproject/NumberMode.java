package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.AspectRatio;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.ImageView;
import java.util.Random;
public class NumberMode extends AppCompatActivity {
    PreviewView previewView;
    ProcessCameraProvider processCameraProvider;
    int lensFacing = CameraSelector.LENS_FACING_FRONT;
    ImageCapture imageCapture;
    String get_num;
    TextView problem;
    TextView Timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_mode);

        Button homeBtn = (Button) findViewById(R.id.home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_A = new Intent(getApplicationContext(), Home.class);
                startActivity(intent_A);
                finish();
            }
        });
        //camera
        previewView = findViewById(R.id.previewView);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);

        try {
            processCameraProvider = ProcessCameraProvider.getInstance(this).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(NumberMode.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            bindPreview();
        }


        //number question
        problem = findViewById(R.id.number_problem);
        Intent intent = getIntent();
        get_num = intent.getStringExtra("random_num");
        problem.setText(get_num);

        //timer
        Timer = findViewById(R.id.timer);
        CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {


            @Override
            public void onTick(long l) {

            }

            public void onFinish() {
                //이미지 캡처
                capturePhoto(get_num);
            }
        }.start();

    }
    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }
    private void bindPreview() {
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) //디폴트 표준 비율
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        processCameraProvider.bindToLifecycle(this, cameraSelector, preview);

        //image capture use case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        processCameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }
    private void capturePhoto(String random_num){
        File photoDir = new File("/data/data/com.example.finalproject/files/NumberMode");
        if(!photoDir.exists())
            photoDir.mkdir();

        String photoFilePath = photoDir.getAbsolutePath()+"/"+"test"+".png";
        File photoFile=new File(photoFilePath);

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(NumberMode.this, "Your answer is submitted.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NumberMode.this, ImageClassificationActivity.class);
                        intent.putExtra("random_num", random_num);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(NumberMode.this, "Error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

}