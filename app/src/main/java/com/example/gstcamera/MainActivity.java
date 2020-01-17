package com.example.gstcamera;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import org.freedesktop.gstreamer.GStreamer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        SurfaceView surfaceView = findViewById(R.id.surface);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                NativeStart(holder.getSurface());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }
    public static void init(Context context) throws Exception {

        System.loadLibrary("gstreamer_android");
        GStreamer.init(context);
        System.loadLibrary("gstcamera");
    }
    public static native void NativeStart(Surface surface);
}
