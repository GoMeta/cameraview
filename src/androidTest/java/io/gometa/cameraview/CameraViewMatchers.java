// Copyright (c) 2018 GoMeta. All right reserved.

package io.gometa.cameraview;

import android.support.annotation.NonNull;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import io.gometa.support.cameraview.AspectRatio;
import io.gometa.support.cameraview.CameraView;

class CameraViewMatchers {

    static Matcher<View> hasAspectRatio(@NonNull final AspectRatio ratio) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("has aspect ratio of " + ratio);
            }

            @Override
            protected boolean matchesSafely(View view) {
                return ratio.equals(((CameraView) view).getAspectRatio());
            }
        };
    }

}
