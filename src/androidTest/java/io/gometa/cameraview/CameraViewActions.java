// Copyright (c) 2018 GoMeta. All right reserved.

package io.gometa.cameraview;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

import androidx.annotation.NonNull;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import io.gometa.support.cameraview.AspectRatio;
import io.gometa.support.cameraview.CameraView;

class CameraViewActions {

    static ViewAction setAspectRatio(@NonNull final AspectRatio ratio) {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(CameraView.class);
            }

            @Override
            public String getDescription() {
                return "Set aspect ratio to " + ratio;
            }

            @Override
            public void perform(UiController controller, View view) {
                ((CameraView) view).setAspectRatio(ratio);
            }
        };
    }

}
