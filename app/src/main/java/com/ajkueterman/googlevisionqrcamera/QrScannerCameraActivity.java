package com.ajkueterman.googlevisionqrcamera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by ajkueterman on 8/20/17.
 *
 * The Camera Activity for scanning QR codes
 */

public class QrScannerCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    public final static String QR_CODE_EXTRA_KEY = "qr_code_extra_key";
    private CameraSource cameraSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner_camera);

        SurfaceView cameraSurface = (SurfaceView) findViewById(R.id.cameraSurface);
        cameraSurface.getHolder().addCallback(this);

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes != null && detections.getDetectedItems().size() > 0) {
                    finishQrScannerCameraActivity(barcodes.valueAt(0));
                }

            }
        });

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920, 1080)
                .build();
    }

    private void finishQrScannerCameraActivity(Barcode qrCode) {
        Intent i = new Intent();
        i.putExtra(QR_CODE_EXTRA_KEY, qrCode);
        setResult(RESULT_OK, i);
        finish();
    }

    /*
     *
     * SurfaceHolder Callback Methods
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            // check camera permissions
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(surfaceHolder);
            }
        } catch (IOException ie) {
            Log.e("CAMERA SOURCE", ie.getMessage());
            cameraSource.release();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
       cameraSource.stop();
    }
}
