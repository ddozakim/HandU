package com.example.finalproject;

import static java.nio.file.Files.newOutputStream;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import java.io.File;
import androidx.annotation.NonNull;
import android.os.Environment;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import android.media.MediaScannerConnection;

public class Test_Alphabet extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_alphabet);
        String mode ="alphabet";


        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    long timeInMicroseconds = 100000;
                    int count;
                    for(count=0;count<40;count++){
                        captureFrame(timeInMicroseconds,count);
                        timeInMicroseconds=timeInMicroseconds+100000;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //이거는 모델 적용 후 정답 판별 여부에 따라 작성
                Intent intent = new Intent(Test_Alphabet.this, Answer_correct.class);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        }, 3000);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void captureFrame(long timeInMicroseconds,int count) throws IOException {
        // Replace "your_video_file_path" with the actual path of your video file in external storage
        String videoPath = "/data/data/com.example.finalproject/files/AlphabetMode/video.mp4";

        // Specify the time in microseconds where you want to capture the frame (1 second in this example)


        // Create a MediaMetadataRetriever
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            // Set the data source to the video file
            retriever.setDataSource(this, Uri.parse(videoPath));

            // Capture the frame as a Bitmap
            Bitmap bitmap = retriever.getFrameAtTime(timeInMicroseconds);

            if (bitmap != null) {
                // Save the captured image to external storage
                saveImageToExternalStorage(bitmap, "test"+count+".png");

            } else {
                // Handle the case when the bitmap is null (e.g., frame not found)
                Log.e("CaptureFrame", "Captured frame is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Release the MediaMetadataRetriever
            retriever.release();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveImageToExternalStorage(Bitmap bitmap, String fileName) {
        // Get the path for external storage directory
        String directoryPath = "/data/data/com.example.finalproject/files/AlphabetMode/frames";

        // Create a File object for the directory
        File directory = new File(directoryPath);

        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a File object for the image file
        File imageFile = new File(directory, fileName);

        // Create an OutputStream to write the bitmap data to the file
        try (OutputStream stream = newOutputStream(imageFile.toPath())) {
            // Choose a format and quality for the bitmap data
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Notify the media scanner about the new file so that it's immediately available to other apps
        MediaScannerConnection.scanFile(this, new String[]{imageFile.getAbsolutePath()}, null, null);}

}