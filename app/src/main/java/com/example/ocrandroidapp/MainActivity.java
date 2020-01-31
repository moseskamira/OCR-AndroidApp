package com.example.ocrandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    ImageView ocrImageView;
    Button captureImageButton;
    Button generateTextButton;
    TextView generatedTextView;
    protected String imagePath;
    protected boolean imageTaken;
    protected static final String PHOTO_TAKEN = "photo_taken";
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    Bitmap capturedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ocrImageView = findViewById(R.id.image_view);
        captureImageButton = findViewById(R.id.cap_img_btn);
        generateTextButton = findViewById(R.id.gen_text_btn);
        generatedTextView = findViewById(R.id.gen_text);
        imagePath = Environment.getExternalStorageDirectory() + "/images/ocr_image.jpg";

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }

            }
        });

//        generateTextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                generateText();
//
//            }
//        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            capturedPhoto = (Bitmap) data.getExtras().get("data");
            setImageToView();

        }
    }

    private void setImageToView() {
        if (capturedPhoto != null) {
            ocrImageView.setImageBitmap(capturedPhoto);
            captureImageButton.setVisibility(View.GONE);
            generateTextButton.setVisibility(View.VISIBLE);

            generateTextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HERE", "GENETEXT");
                 generateText();
                }
            });
            generatedTextView.setVisibility(View.VISIBLE);

        }
    }

    private void generateText() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "Could Not Recognize Text", Toast.LENGTH_LONG).show();

        }else {
            if (capturedPhoto !=null) {
                Log.d("NOW", "HERE");
                Frame frame = new Frame.Builder().setBitmap(capturedPhoto).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                StringBuilder stringBuilder = new StringBuilder();
                Log.d("ITEMSLIST", items.toString());
                Log.d("BLOCKBUILDER", stringBuilder.toString());

                for (int i=0; i<items.size(); i++) {
                    TextBlock myItem = items.valueAt(i);
                    Log.d("ITEM", items.toString());
                    stringBuilder.append(myItem.getValue());
                    stringBuilder.append("\n");

                }
                generateTextButton.setVisibility(View.GONE);
                generatedTextView.setVisibility(View.VISIBLE);
                generatedTextView.setText(stringBuilder.toString());

            }else {
                Toast.makeText(this, "capturedPhoto is null", Toast.LENGTH_LONG).show();

            }


        }

    }





}
