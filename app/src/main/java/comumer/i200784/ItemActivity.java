package comumer.i200784;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.Manifest;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemActivity extends AppCompatActivity {

    RelativeLayout image, video;
    EditText name, desc, rate;
    Button post;
    Spinner city;
    String userId;

    private static final int IMAGE_REQUEST = 1;
    private static final int VIDEO_REQUEST = 2;
    private Uri selectedImageUri;
    private Uri selectedVideoUri;

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        image = findViewById(R.id.image);
        video = findViewById(R.id.video);
        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);
        city = findViewById(R.id.city);
        rate = findViewById(R.id.rate);
        post = findViewById(R.id.post);

        // Populate the Spinner with city options
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this, R.array.pakistani_cities, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        // Close screen icon
        TextView homeIcn = findViewById(R.id.nav_back);
        homeIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ItemActivity.this, WelcomeActivityActivity.class);
            startActivity(intent);
        });

        // Capture pic icon (Image selection)
        image.setOnClickListener(view -> showMediaSelectionDialog(IMAGE_REQUEST));

        // Capture pic icon (Video selection)
        video.setOnClickListener(view -> showMediaSelectionDialog(VIDEO_REQUEST));

        // Handle posting to Firestore
        post.setOnClickListener(view -> {
            String itemName = name.getText().toString();
            String itemDesc = desc.getText().toString();
            String itemCity = city.getSelectedItem().toString();
            String itemRate = rate.getText().toString();

            if (selectedImageUri != null || selectedVideoUri != null) {
                // Upload selected image and video to Firebase Storage
                uploadImageAndVideo(itemName, itemDesc, itemCity, itemRate);
            } else {
                // Handle the case where no image or video is selected
                Toast.makeText(ItemActivity.this, "Please select an image or video.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMediaSelectionDialog(int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Media Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Gallery option selected
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    if (requestCode == IMAGE_REQUEST) {
                        galleryIntent.setType("image/*");
                    } else if (requestCode == VIDEO_REQUEST) {
                        galleryIntent.setType("video/*");
                    }
                    startActivityForResult(galleryIntent, requestCode);
                    break;
                case 1:
                    // Camera option selected
                    // You need to implement camera capture logic here
                    Toast.makeText(this, "Camera option selected", Toast.LENGTH_SHORT).show();
                    openCamera();
                    break;
            }
        });
        builder.show();
    }

    private void uploadImageAndVideo(String itemName, String itemDesc, String itemCity, String itemRate) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child("images/" + UUID.randomUUID().toString());
        StorageReference videoRef = storage.getReference().child("videos/" + UUID.randomUUID().toString());

        UploadTask imageUploadTask = imageRef.putFile(selectedImageUri);
        UploadTask videoUploadTask = videoRef.putFile(selectedVideoUri);

        imageUploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                videoUploadTask.addOnSuccessListener(taskSnapshot1 -> {
                    videoRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        String videoUrl = uri1.toString();
                        storeItemDetails(itemName, itemDesc, itemCity, itemRate, imageUrl, videoUrl);
                    });
                });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(ItemActivity.this, "Failed to upload image. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void storeItemDetails(String itemName, String itemDesc, String itemCity, String itemRate, String imageUrl, String videoUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("items").document();

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("itemName", itemName);
        itemData.put("itemDesc", itemDesc);
        itemData.put("itemCity", itemCity);
        itemData.put("itemRate", itemRate);
        itemData.put("userId", userId);
        itemData.put("imageUrl", imageUrl);
        itemData.put("videoUrl", videoUrl);

        docRef.set(itemData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ItemActivity.this, "Item posted successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ItemActivity.this, WelcomeActivityActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ItemActivity.this, "Failed to post the item. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST) {
                selectedImageUri = data.getData();
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == VIDEO_REQUEST) {
                selectedVideoUri = data.getData();
                Toast.makeText(this, "Video selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final int CAMERA_PERMISSION_CODE = 101;

    private void openCamera() {
        // Check for camera permission and request it if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            return; // Return if permission not granted yet
        }

        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    createCameraPreviewSession();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    cameraDevice.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    cameraDevice.close();
                    cameraDevice = null;
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void createCameraPreviewSession() {
        try {
            imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(reader -> {
                // Handle the captured image here
                Image image = reader.acquireLatestImage();
                // Convert image to Bitmap or save it
                // ...
                image.close();
            }, backgroundHandler);

            Surface surface = imageReader.getSurface();

            CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cameraDevice == null) {
                        return;
                    }

                    captureSession = session;
                    try {
                        captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(ItemActivity.this, "Failed to configure camera session.", Toast.LENGTH_SHORT).show();
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Rest of the code (upload and Firestore) remains the same

    // Be sure to handle camera release in onDestroy() or onPause() to avoid resource leaks
    // Release the camera and background thread in onDestroy()
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraDevice != null) {
            cameraDevice.close();
        }
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
        }
    }
}
