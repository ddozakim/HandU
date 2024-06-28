package com.example.finalproject;

import static java.nio.file.Files.newOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.VideoCapture;
import androidx.camera.view.PreviewView;
import androidx.camera.core.Preview;
import androidx.camera.core.AspectRatio;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.media.MediaScannerConnection;
import android.net.Uri;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import java.util.concurrent.Executor;
import java.io.FileOutputStream;
import java.io.File;
import java.util.concurrent.ExecutionException;

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaMetadataRetriever;

public class WordMode extends AppCompatActivity {

    PreviewView previewView;
    ProcessCameraProvider processCameraProvider;
    int lensFacing = CameraSelector.LENS_FACING_FRONT;
    VideoCapture videoCapture;
    String get_word;
    TextView problem;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_mode);

        Button homeBtn = (Button) findViewById(R.id.home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_A = new Intent(getApplicationContext(), Home.class);
                intent_A.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_A);
            }
        });
        //camera
        previewView = findViewById(R.id.previewView);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // 녹음 권한이 없으면 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }


        try {
            processCameraProvider = ProcessCameraProvider.getInstance(this).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(WordMode.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            bindPreview();
        }


        //number question
        problem = findViewById(R.id.word_problem);
        Intent intent = getIntent();
        get_word = intent.getStringExtra("random_word");
        problem.setText(get_word);

        if (!isRecording) {
            recordVideo();
            Toast.makeText(WordMode.this, "Start recording.", Toast.LENGTH_SHORT).show();

            // Schedule a task to stop recording after 4 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecording();
                    Toast.makeText(WordMode.this, "Stop recording after 4 seconds.", Toast.LENGTH_SHORT).show();
                }
            }, 4000);  // 4 seconds delay
        } else {
            stopRecording();
            Toast.makeText(WordMode.this, "Stop recording.", Toast.LENGTH_SHORT).show();

        }
        isRecording = !isRecording;


    }
    protected void onStart(Bundle savedInstanceState) {}
    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
   private void bindPreview() {
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) //디폴트 표준 비율
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());


        videoCapture = new VideoCapture.Builder()
                .setVideoFrameRate(30)
                .build();

        processCameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, videoCapture);
    }

    @SuppressLint("RestrictedApi")
    private void recordVideo() {
        if (videoCapture != null) {
            File movieDir = new File("/data/data/com.example.finalproject/files/WordMode");
            if (!movieDir.exists())
                movieDir.mkdir();

            String vidFilePath = movieDir.getAbsolutePath() + "/" + "video.mp4";
            File vidFile = new File(vidFilePath);

            // 이미 존재하는 파일인 경우 삭제
            if (vidFile.exists()) {
                vidFile.delete();
            }

            // 새로운 파일 생성
            try {
                vidFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                // 파일 생성 실패 시 예외 처리
                return;
            }


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            videoCapture.startRecording(
                    new VideoCapture.OutputFileOptions.Builder(vidFile).build(),
                    getExecutor(),
                    new VideoCapture.OnVideoSavedCallback() {

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                            Toast.makeText(WordMode.this, "Your answer is submitted.", Toast.LENGTH_SHORT).show();
                            Intent intent_M = new Intent(WordMode.this,Test_Word.class);
                            startActivity(intent_M);

                        }

                        @Override
                        public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                            Toast.makeText(WordMode.this, "error.", Toast.LENGTH_SHORT).show();
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
    }



}