package com.example.finalproject;

import static java.nio.file.Files.newOutputStream;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
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
public class FrameCapture extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_capture);
        try {
            captureFrame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void captureFrame() throws IOException {
        // Replace "your_video_file_path" with the actual path of your video file in external storage
        String videoPath = "/data/data/com.example.finalproject/files/WordMode/video.mp4";

        // Specify the time in microseconds where you want to capture the frame (1 second in this example)
        long timeInMicroseconds = 1000000;

        // Create a MediaMetadataRetriever
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            // Set the data source to the video file
            retriever.setDataSource(this, Uri.parse(videoPath));

            // Capture the frame as a Bitmap
            Bitmap bitmap = retriever.getFrameAtTime(timeInMicroseconds);

            if (bitmap != null) {
                // Save the captured image to external storage
                saveImageToExternalStorage(bitmap, "test.png");

                // Display the captured image in an ImageView
                ImageView imageView = findViewById(R.id.frame); // Assuming you have an ImageView in your layout
                imageView.setImageBitmap(bitmap);
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

    private void requestStoragePermission() {
        // Check if the app has permission to read external storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST);
        } else {
            // If permission is already granted or not needed, proceed with loading data
            loadData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("FrameCapture", "허가 완료");
            // Permission granted, proceed with loading data
            loadData();
        } else {
            // Permission denied, handle accordingly (e.g., show a message or exit the app)
            Log.e("FrameCapture", "Permission denied. Cannot load data.");
        }
    }

    private void loadData() {
        String imagePath = "/data/data/com.example.finalproject/files/NumberMode/inputData.png";

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        if (bitmap != null) {
            ImageView imageView = findViewById(R.id.frame); // Assuming you have an ImageView in your layout
            imageView.setImageBitmap(bitmap);
        } else {
            // Handle the case when the bitmap is null (e.g., file not found or corrupted)
            Log.e("FrameCapture", "Failed to decode the image file");
        }
    }
}