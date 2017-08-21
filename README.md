# GoogleVisionQrCameraSample

A sample app using the Google Vision Barcode Detector.

## Scanner Activity `QrScannerCameraActivity`

The `QrScannerCameraActivity` is responsible for capturing a QR code from `CameraSource` output.  It does this using the [Barcode Detector API](https://developers.google.com/android/reference/com/google/android/gms/vision/barcode/BarcodeDetector) from Google Vision.

### Init the `SurfaceView` layout view

The scanner activity consists mostly of a SurfaceView layout, which we init and assign a callback to.  

```java
SurfaceView cameraSurface = (SurfaceView) findViewById(R.id.cameraSurface);
cameraSurface.getHolder().addCallback(this);
```

In this sample the callback is implemented by the `QrScannerCameraActivity`.  The callback is primarily responsible for starting and stopping the `CameraSource`.

```java
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
```

### Init `CameraSource`

In `onCreate` we init the `CameraSource`.

```java
cameraSource = new CameraSource.Builder(this, barcodeDetector)
                  .setAutoFocusEnabled(true)
                  .setRequestedPreviewSize(1920, 1080)
                  .build();
```

### Build `BarcodeDetector` and assign a `Processor`

```java
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
```

The processor will call receiveDetections for each frame.  When a barcode is detected we can do what we want with the list of barcodes.
