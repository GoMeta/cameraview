// Copyright (c) 2018 GoMeta. All right reserved.

package io.gometa.cameraview;

import android.app.Activity;
import android.os.Bundle;

import io.gometa.cameraview.cameraview.test.R;

import io.gometa.support.cameraview.CameraView;

public class CameraViewActivity extends Activity {

    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        mCameraView = (CameraView) findViewById(R.id.camera);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

}
