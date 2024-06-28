package com.example.finalproject;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Matrix;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ImageClassificationActivity extends AppCompatActivity {

    private Interpreter tflite;
    private TextView resultTextView;
    private ImageView imageView;
    String get_num;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_classification);

        resultTextView = findViewById(R.id.resultTextView);

        Intent intent = getIntent();
        get_num = intent.getStringExtra("random_num");
        int problem = Integer.valueOf(get_num);

        // Load the TensorFlow Lite model
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load an image from external storage (replace with your image path)
        Bitmap bitmap = loadImageFromFile("/data/data/com.example.finalproject/files/NumberMode/test.png");
        float rotationAngle = 270.0f;
        Bitmap bitmap2=rotateBitmap(bitmap, rotationAngle);
        Bitmap inputBitmap=flipHorizontal(bitmap2);


        // Preprocess the image and run inference
        if (inputBitmap != null) {
            ByteBuffer inputBuffer = preprocessImage(inputBitmap);
            runInference(inputBuffer,problem);
        }
    }
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    public static Bitmap flipHorizontal(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1); // X 축 방향으로 반전
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("Number_Model_Plus.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private Bitmap loadImageFromFile(String imagePath) {
        return BitmapFactory.decodeFile(imagePath);
    }

    private ByteBuffer preprocessImage(Bitmap bitmap) {
        int inputSize = 224; // Adjust this based on your model input size
        int channels = 3;    // Assuming RGB image
        imageView = findViewById(R.id.imageView);
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * channels);
        inputBuffer.order(ByteOrder.nativeOrder());

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true);
        inputBuffer.rewind();
        imageView.setImageBitmap(resizedBitmap);
        int[] intValues = new int[inputSize * inputSize];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        for (int pixelValue : intValues) {
            inputBuffer.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f);
            inputBuffer.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f);
            inputBuffer.putFloat((pixelValue & 0xFF) / 255.0f);
        }

        return inputBuffer;
    }

    private void runInference(ByteBuffer inputBuffer,int problem) {

        // Output buffer (you may need to adjust the size based on your model)
        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(4 * 10);
        outputBuffer.order(ByteOrder.nativeOrder());

        // Run inference
        tflite.run(inputBuffer, outputBuffer);

        // Post-process the output (replace with your post-processing logic)
        postprocessOutput(outputBuffer,problem);


    }

    // Post-process the output (replace with your post-processing logic)
    private void postprocessOutput(ByteBuffer outputBuffer,int problem) {
        int numLabels=10;
        // Assuming the output is a float array
        float[] outputValues = new float[numLabels];
        outputBuffer.rewind();
        outputBuffer.asFloatBuffer().get(outputValues);

        // Find the index of the class with the highest confidence
        int maxIndex = 0;
        float maxConfidence = outputValues[0];
        for (int i = 1; i < numLabels; i++) {
            if (outputValues[i] > maxConfidence) {
                maxConfidence = outputValues[i];
                maxIndex = i;
            }
        }

        // Assuming you have an array of class labels
        String[] classLabels = { "0","1","2","3","4","5","6","7","8","9"};

        // Get the label corresponding to the class with the highest confidence
        String resultLabel = classLabels[maxIndex];

        for(int i=0;i<numLabels;i++){
            System.out.println(classLabels[i]+" : "+outputValues[i]);
        }
        System.out.println("max :"+outputValues[maxIndex]);
        int mode=0;
        //correct
        if(classLabels[problem].equals(resultLabel)){
            Intent intent_c = new Intent(ImageClassificationActivity.this, Answer_Number_correct.class);
            intent_c.putExtra("confidence", maxConfidence);
            intent_c.putExtra("mode", mode);
            startActivity(intent_c);
        }
        //wrong
        else{
            float confidence=outputValues[problem];
            Intent intent_w = new Intent(ImageClassificationActivity.this, Answer_Number_wrong.class);
            intent_w.putExtra("confidence", confidence);
            intent_w.putExtra("mode", mode);
            startActivity(intent_w);
        }
        // Build the result string
        String result = "Predicted Label: " + resultLabel + "\nConfidence: " + maxConfidence;


    }

    @Override
    protected void onDestroy() {
        if (tflite != null) {
            tflite.close();
        }
        super.onDestroy();
    }
}