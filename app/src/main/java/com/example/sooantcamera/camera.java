package com.example.sooantcamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.sql.Date;

public class camera extends AppCompatActivity {
    final private static String TAG = "SOOANT";
    Button btn_photo;
    ImageView iv_photo;

    final static int TAKE_PICTURE = 1;

    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        iv_photo = findViewById(R.id.iv_photo);
        btn_photo = findViewById(R.id.btn_photo);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            }
            else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(camera.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_photo) {
                    dispatchTakePictureIntent();
                }
            }
        });
    }


    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    // 카메라로 촬영한 사진의 썸네일을 가져와 이미지뷰에 띄워줌
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                if (resultCode == RESULT_OK) {
                    File file = new File(mCurrentPhotoPath);
                    Bitmap bitmap;
                    if (Build.VERSION.SDK_INT >= 29) {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source);
                            if (bitmap != null) {
                                iv_photo.setImageBitmap(bitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                            if (bitmap != null) {
                                iv_photo.setImageBitmap(bitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
    // 사진 촬영 후 썸네일만 띄워줌. 이미지를 파일로 저장해야 함
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        System.out.println("aaaaaaa " + mCurrentPhotoPath);
        return image;
    }
    // 카메라 인텐트 실행하는 부분
    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try { photoFile = createImageFile(); }
            catch (IOException ignored) { }
            if (photoFile != null) {
                System.out.println(photoFile.getAbsolutePath());
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.sooantcamera.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

}