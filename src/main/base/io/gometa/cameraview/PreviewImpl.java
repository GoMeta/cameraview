// Copyright (c) 2017 GoMeta. All right reserved.

package io.gometa.cameraview;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import timber.log.Timber;


/**
 * Encapsulates all the operations related to camera preview in a backward-compatible manner.
 */
abstract class PreviewImpl {

    interface Callback {
        void onSurfaceChanged();
    }

    private Callback mCallback;

    private int mWidth;

    private int mHeight;

    void setCallback(Callback callback) {
        mCallback = callback;
    }

    abstract Surface getSurface();

    abstract View getView();

    abstract Class getOutputClass();

    abstract void setDisplayOrientation(int displayOrientation);

    abstract boolean isReady();

    protected void dispatchSurfaceChanged() {
        mCallback.onSurfaceChanged();
    }

    SurfaceHolder getSurfaceHolder() {
        return null;
    }

    Object getSurfaceTexture() {
        return null;
    }

    void setBufferSize(int width, int height) {
    }

    void setSize(int width, int height) {
        Timber.d("setSize(%d, %d)", width, height);
        mWidth = width;
        mHeight = height;
    }

    int getWidth() {
        return mWidth;
    }

    int getHeight() {
        return mHeight;
    }

}
