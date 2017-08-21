package com.ajkueterman.googlevisionqrcamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import javax.xml.datatype.Duration;

/**
 * Created by ajkueterman on 8/20/17.
 *
 * The initial landing activity of the app.
 */

public class CameraLaunchActivity extends AppCompatActivity {

    private static final int QR_SCANNER_ID = 107;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 207;

    private TextView qrCodeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_launch);

        qrCodeResult = (TextView) findViewById(R.id.qrCodeResult);

        Button launchCameraButton = (Button) findViewById(R.id.launchCameraButton);
        launchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Camera Activity
                if (checkCameraPermissions()) {
                    Intent i = new Intent(getApplication(), QrScannerCameraActivity.class);
                    startActivityForResult(i, QR_SCANNER_ID);
                }
            }
        });
    }

    private boolean checkCameraPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

            return false;

        } else {

            return true;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Intent i = new Intent(getApplication(), QrScannerCameraActivity.class);
            startActivityForResult(i, QR_SCANNER_ID);

        } else {
            Toast.makeText(this, "Can't do much without Camera permissions :(", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QR_SCANNER_ID && resultCode == RESULT_OK && data.getExtras() != null &&
                data.getExtras().get(QrScannerCameraActivity.QR_CODE_EXTRA_KEY) instanceof Barcode) {
            Barcode qr = (Barcode) data.getExtras().get(QrScannerCameraActivity.QR_CODE_EXTRA_KEY);
            if (qr != null) {
                qrCodeResult.setText(qr.displayValue);
            }
        }
    }
}
