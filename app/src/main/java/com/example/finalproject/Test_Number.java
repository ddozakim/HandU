package com.example.finalproject;

import static android.app.ProgressDialog.show;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.tensorflow.lite.Interpreter;
public class Test_Number extends AppCompatActivity {
    private static final int FROM_ALBUM = 1;    // onActivityResult 식별자
    private static final int FROM_CAMERA = 2;   // 카메라는 사용 안함

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_number);

                Intent intent = new Intent();
                intent.setType("/data/data/com.example.finalproject/files/NumberMode/test.png");                      // 이미지만
                intent.setAction(Intent.ACTION_GET_CONTENT);    // 카메라(ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, FROM_ALBUM);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 카메라를 다루지 않기 때문에 앨범 상수에 대해서 성공한 경우에 대해서만 처리
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != FROM_ALBUM || resultCode != RESULT_OK)
            return;

        //각 모델에 따른 input , output shape 각자 맞게 변환
        // mobilenetcheck.h5 일시 224 * 224 * 3
        float[][][][] input = new float[1][224][224][3];
        float[][] output = new float[1][36]; //tflite에 버섯 종류 5개라서 (내기준)

        try {
            int batchNum = 0;
            InputStream buf = getContentResolver().openInputStream(data.getData());
            Bitmap bitmap = BitmapFactory.decodeStream(buf);
            buf.close();



            // x,y 최댓값 사진 크기에 따라 달라짐 (조절 해줘야함)
            for (int x = 0; x < 224; x++) {
                for (int y = 0; y < 224; y++) {
                    int pixel = bitmap.getPixel(x, y);
                    input[batchNum][x][y][0] = Color.red(pixel) / 1.0f;
                    input[batchNum][x][y][1] = Color.green(pixel) / 1.0f;
                    input[batchNum][x][y][2] = Color.blue(pixel) / 1.0f;
                }
            }

            // 자신의 tflite 이름 써주기
            Interpreter lite = getTfliteInterpreter("converted_model.tflite");
            lite.run(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //이미지 뷰에 선택한 사진 띄우기


        TextView tv_output = findViewById(R.id.tv_output);
        int i;

        // 텍스트뷰에 무슨 버섯인지 띄우기 but error남 ㅜㅜ 붉은 사슴뿔만 주구장창
        for (i = 0; i < 5; i++) {
            if (output[0][i] * 100 > 90) {
                if (i == 0) {
                    tv_output.setText(String.format("1  %d %.5f", i, output[0][0] * 100));
                } else if (i == 1) {
                    tv_output.setText(String.format("2,%d  %.5f", i, output[0][1] * 100));
                } else if (i == 2) {
                    tv_output.setText(String.format("새송이버섯,%d, %.5f", i, output[0][2] * 100));
                } else if (i == 3) {
                    tv_output.setText(String.format("표고버섯, %d, %.5f", i, output[0][3] * 100));
                } else {
                    tv_output.setText(String.format("화경버섯, %d, %.5f", i, output[0][4] * 100));
                }
            } else
                continue;
        }
    }
    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(Test_Number.this, modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}