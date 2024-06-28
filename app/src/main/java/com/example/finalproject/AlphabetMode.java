package com.example.finalproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.AspectRatio;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import android.content.Intent;
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
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import android.widget.TextView;
import android.os.CountDownTimer;
public class AlphabetMode extends AppCompatActivity {
    PreviewView previewView;
    ProcessCameraProvider processCameraProvider;
    int lensFacing = CameraSelector.LENS_FACING_FRONT;
    ImageCapture imageCapture;
    VideoCapture videoCapture;
    String get_alp;
    TextView problem;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alphabet_mode);

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

        if (ActivityCompat.checkSelfPermission(AlphabetMode.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            bindPreview();
        }

        //number question
        problem = findViewById(R.id.alphabet_problem);
        Intent intent = getIntent();
        get_alp = intent.getStringExtra("random_alp");
        problem.setText(get_alp);


        CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {

            @Override
            public void onTick(long l) {

            }

            public void onFinish() {
                //이미지 캡처
                capturePhoto(get_alp);
            }
        }.start();



    }
    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }
    @SuppressLint("RestrictedApi")
    void bindPreview() {
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

     /*   videoCapture = new VideoCapture.Builder()
                .setVideoFrameRate(30)
                .build();*/
        processCameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview,imageCapture);
    }
    private void capturePhoto(String alphabet){
        File photoDir = new File("/data/data/com.example.finalproject/files/AlphabetMode");
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
                        Toast.makeText(AlphabetMode.this, "Your answer is submitted.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AlphabetMode.this, AlphabetClassificationActivity.class);
                        intent.putExtra("problem", alphabet);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(AlphabetMode.this, "Error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }
 /*   @SuppressLint("RestrictedApi")
    private void recordVideo() {
        if (videoCapture != null) {
            File movieDir = new File("/data/data/com.example.finalproject/files/AlphabetMode");
            if (!movieDir.exists())
                movieDir.mkdir();

            String vidFilePath = movieDir.getAbsolutePath() + "/"+"video"+".mp4";
            File vidFile = new File(vidFilePath);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            videoCapture.startRecording(
                    new VideoCapture.OutputFileOptions.Builder(vidFile).build(),
                    getExecutor(),
                    new VideoCapture.OnVideoSavedCallback() {

                        @Override
                        public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                            Toast.makeText(AlphabetMode.this, "Your answer is submitted.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                            Toast.makeText(AlphabetMode.this, "error.", Toast.LENGTH_SHORT).show();
                        }
                    }

            );
        }
    }
    @SuppressLint("RestrictedApi")
    private void stopRecording() {
        if (videoCapture != null) {
            videoCapture.stopRecording();
        }
    }*/
}