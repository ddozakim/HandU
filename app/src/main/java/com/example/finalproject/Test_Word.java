package com.example.finalproject;

import static java.nio.file.Files.newOutputStream;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.PointF;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import android.media.MediaScannerConnection;
import java.util.Random;
public class Test_Word extends AppCompatActivity {
    int random_num;
    int[] number = {1,2,3,4,5};
    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_word);

        Random random = new Random();
        random_num = number[random.nextInt(5)];
        int answer= random_num%2;

        String mode ="word";

        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (answer == 1) {
                    Intent intent = new Intent(Test_Word.this, Answer_correct.class);
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                } else if (answer == 0) {
                    Intent intent = new Intent(Test_Word.this, Answer_wrong.class);
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                }
            }
            }, 3000);
    }


    //프레임 캡처
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void captureFrame(long timeInMicroseconds,int count) throws IOException {
        // Replace "your_video_file_path" with the actual path of your video file in external storage
        String videoPath = "/data/data/com.example.finalproject/files/WordMode/video.mp4";

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
    //프레임 저장
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveImageToExternalStorage(Bitmap bitmap, String fileName) {
        // Get the path for external storage directory
        String directoryPath = "/data/data/com.example.finalproject/files/WordMode/frames";

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


    private Bitmap loadData() {
        String imagePath = "/data/data/com.example.finalproject/files/NumberMode/test.png";

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        return bitmap;
    }


}