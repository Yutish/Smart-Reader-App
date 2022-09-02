package com.example.smartreader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ScanningActivity extends AppCompatActivity implements BottomSheetTextReciever.BottomSheetListener {

    public static final String TAG = "ScanningActivity";

    public static final String PermissionCamera = "Manifest.permission.CAMERA";

    private SurfaceView displayCameraSurfaceView;
    private TextView scannedText;
    CameraSource cameraSource;

    boolean isStopped = false;
    Runnable runnable;

    //permission id.....
    final int requestCameraPermissionId = 1001;

    //for camera resource.....
    final int i1 = 1280, i2 = 1024;

    //permission request...
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case requestCameraPermissionId:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, PermissionCamera) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    //starting camera...
                    try {
                        cameraSource.start(displayCameraSurfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        displayCameraSurfaceView = findViewById(R.id.displayCameraSurfaceView);
        scannedText = findViewById(R.id.scannedText);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

        //setting camera resource.....
        if (!textRecognizer.isOperational()) {
            Log.i(TAG, "Detector dependencies are not yet granted");
        } else {
            cameraSource = new CameraSource.Builder(this, textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(i1, i2)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true).build();

            //using surface view for operations.....
            displayCameraSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    //permission check...
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ScanningActivity.this, new String[]{PermissionCamera}, requestCameraPermissionId);
                        return;
                    }

                    //starting camera...
                    try {
                        cameraSource.start(displayCameraSurfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            //recognizing text.....
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> sparseArray = detections.getDetectedItems();

                    if (sparseArray.size() != 0) {

                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (isStopped) {
                                    return;
                                }
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < sparseArray.size(); i++) {
                                    TextBlock item = sparseArray.get(i);
                                    if (item != null) {
                                        stringBuilder.append(item.getValue());
                                        stringBuilder.append("\n");
                                    }
                                }
                                scannedText.setText(stringBuilder.toString());

                            }
                        };

                        scannedText.post(runnable);
                    }
                }
            });
        }

        //on click on surface view......
        displayCameraSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStopped = true;
                BottomSheetTextReciever bottomSheetShower = new BottomSheetTextReciever();
                String s = scannedText.getText().toString();
                bottomSheetShower.setTextToDisplay(s);
                bottomSheetShower.show(getSupportFragmentManager(), "");
            }
        });

    }

    @Override
    public void setState(boolean state) {
        isStopped = state;
        scannedText.post(runnable);
    }
}
