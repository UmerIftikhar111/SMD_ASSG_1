package comumer.i200784;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Video extends AppCompatActivity {

    ImageView capture,flip;

    private PreviewView previewView;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;

    private static String EXTRA_VIDEO_PATH;

    ExecutorService service;
    Recording recording = null;
    VideoCapture<Recorder> videoCapture = null;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            startCamera(cameraFacing);
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // close capture icon
        ImageView crossIcn = findViewById(R.id.close_capture);
        crossIcn.setOnClickListener(view -> {
            Intent intent = new Intent(Video.this, ItemActivity.class);
            startActivity(intent);
        });

        previewView = findViewById(R.id.camera_preview);
        capture = findViewById(R.id.camera_icon);
        flip = findViewById(R.id.flip);

        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraFacing == CameraSelector.LENS_FACING_FRONT)
                    cameraFacing = CameraSelector.LENS_FACING_BACK;
                else
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                startCamera(cameraFacing);
            }
        });
        startCamera(cameraFacing);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureVideo();
            }
        });

        service = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.shutdown();
    }

    public void startCamera(int cameraFacing) {
        ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(Video.this);

        processCameraProvider.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = processCameraProvider.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                Recorder recorder = new Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build();
                videoCapture = VideoCapture.withOutput(recorder);

                cameraProvider.unbindAll();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(Video.this));
    }

    @SuppressLint("MissingPermission")
    public void captureVideo() {
        capture.setImageResource(R.drawable.baseline_fiber_smart_record_24);
        Recording recording1 = recording;
        if (recording1 != null) {
            recording1.stop();
            recording = null;
            return;
        }
        String name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video");

        MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues).build();


        recording = videoCapture.getOutput().prepareRecording(Video.this, options).withAudioEnabled().start(ContextCompat.getMainExecutor(Video.this), videoRecordEvent -> {
            if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                capture.setEnabled(true);
            } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
                    // Get the output URI
                    Uri outputUri = ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();

                    // Create an Intent to send the URI to the previous activity
                    Intent resultIntent = new Intent();
                    resultIntent.setData(outputUri);

                    // Set the result for the previous activity
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Close this activity

                    // Show a Toast message (optional)
                    String msg = "Video capture succeeded: " + outputUri;
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    recording.close();
                    recording = null;
                    String msg = "Error: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getError();
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                capture.setImageResource(R.drawable.capture_icon_set);
            }
        });
    }


}