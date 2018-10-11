// Copyright (c) 2018 GoMeta. All right reserved.

package io.gometa.support.cameraview;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.os.SystemClock;
import androidx.annotation.NonNull;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import java.nio.ByteBuffer;
import java.util.Arrays;

import timber.log.Timber;

/**
 *
 */
public class FrameProcessingRunnable implements Runnable {

    public interface OnFrameDataReleasedListener {
        void onFrameDataReleased(ByteBuffer data);
    }
    private long mStartTimeMillis = SystemClock.elapsedRealtime();

    // This lock guards all of the member variables below.
    private final Object mLock = new Object();
    private boolean mActive = true;

    // These pending variables hold the state associated with the new frame awaiting processing.
    private long mPendingTimeMillis;
    private int mPendingFrameId = 0;
    private ByteBuffer mPendingFrameData;
    private int mPreviewWidth = 0;
    private int mPreviewHeight = 0;
    private int mPreviewRotation = 0;

    private final Detector mDetector;
    private final OnFrameDataReleasedListener mOnFrameDataReleasedListener;

    FrameProcessingRunnable(@NonNull Detector detector,
            @NonNull OnFrameDataReleasedListener onFrameDataReleasedListener) {
        mDetector = detector;
        mOnFrameDataReleasedListener = onFrameDataReleasedListener;
    }

    void setPreviewSpecs(int width, int height, int rotation) {
        Timber.d("setPreviewSpecs(width = %d, height = %d, rotation = %d)", width, height, rotation);
        mPreviewWidth = width;
        mPreviewHeight = height;
        mPreviewRotation = rotation;
    }

    /**
     * Releases the underlying receiver.  This is only safe to do after the associated thread
     * has completed, which is managed in camera source's release method above.
     */
    @SuppressLint("Assert")
    void release() {
        mDetector.release();
    }

    /**
     * Marks the runnable as active/not active.  Signals any blocked threads to continue.
     */
    void setActive(boolean active) {
        synchronized (mLock) {
            mActive = active;
            mLock.notifyAll();
        }
    }

    /**
     * Sets the frame data received from the camera.
     */
    ByteBuffer setNextFrame(ByteBuffer data) {
        ByteBuffer evictedFrame = null;
        synchronized (mLock) {
            if (mPendingFrameData != null) {
                evictedFrame = mPendingFrameData;
                mPendingFrameData = null;
            }

            if (data == null) {
                Timber.d("Skipping frame. Could not find ByteBuffer associated with the image data from the camera");
                return evictedFrame;
            }

            // Timestamp and frame ID are maintained here, which will give downstream code some
            // idea of the timing of frames received and when frames were dropped along the way.
            mPendingTimeMillis = SystemClock.elapsedRealtime() - mStartTimeMillis;
            mPendingFrameId++;
            mPendingFrameData = data;

            // Notify the processor thread if it is waiting on the next frame (see below).
            mLock.notifyAll();
        }
        return evictedFrame;
    }

    /**
     * As long as the processing thread is active, this executes detection on frames
     * continuously.  The next pending frame is either immediately available or hasn't been
     * received yet.  Once it is available, we transfer the frame info to local variables and
     * run detection on that frame.  It immediately loops back for the next frame without
     * pausing.
     * <p/>
     * If detection takes longer than the time in between new frames from the camera, this will
     * mean that this loop will run without ever waiting on a frame, avoiding any context
     * switching or frame acquisition time latency.
     * <p/>
     * If you find that this is using more CPU than you'd like, you should probably decrease the
     * FPS setting above to allow for some idle time in between frames.
     */
    @Override
    public void run() {
        Frame outputFrame;
        ByteBuffer data;

        while (true) {
            synchronized (mLock) {
                while (mActive && (mPendingFrameData == null)) {
                    try {
                        // Wait for the next frame to be received from the camera, since we
                        // don't have it yet.
                        mLock.wait();
                    } catch (InterruptedException e) {
                        Timber.d(e, "Frame processing loop terminated.");
                        return;
                    }
                }

                if (!mActive) {
                    // Exit the loop once this camera source is stopped or released.  We check
                    // this here, immediately after the wait() above, to handle the case where
                    // setActive(false) had been called, triggering the termination of this
                    // loop.
                    return;
                }

                try {
                    outputFrame = new Frame.Builder()
                            .setImageData(mPendingFrameData, mPreviewWidth, mPreviewHeight,
                                    ImageFormat.NV21)
                            .setId(mPendingFrameId)
                            .setTimestampMillis(mPendingTimeMillis)
                            .setRotation(mPreviewRotation)
                            .build();
                } catch (IllegalArgumentException ex) {
                    Timber.d("Preview size? (%d x %d), rotation? %d", mPreviewWidth, mPreviewHeight,
                            mPreviewRotation);
                    throw ex;
                }

                // We need to clear mPendingFrameData to ensure that this buffer isn't
                // recycled back to the camera before we are done using that data.
                data = mPendingFrameData;
                mPendingFrameData = null;
            }

            // The code below needs to run outside of synchronization, because this will allow
            // the camera to add pending frame(s) while we are running detection on the current
            // frame.

            if (!mDetector.isOperational()) {
                Timber.w("Detector is not operational");
            }

            try {
                mDetector.receiveFrame(outputFrame);
            } catch (Throwable t) {
                Timber.e(t, "Exception thrown from receiver.");
            } finally {
                mOnFrameDataReleasedListener.onFrameDataReleased(data);
            }
        }
    }
}
