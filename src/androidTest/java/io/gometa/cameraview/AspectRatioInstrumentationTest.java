// Copyright (c) 2018 GoMeta. All right reserved.

package io.gometa.cameraview;

import static junit.framework.Assert.assertNotNull;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;

import android.os.Parcel;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.gometa.support.cameraview.AspectRatio;


@RunWith(AndroidJUnit4.class)
public class AspectRatioInstrumentationTest {

    @Test
    public void testParcel() {
        final AspectRatio original = AspectRatio.of(4, 3);
        final Parcel parcel = Parcel.obtain();
        try {
            parcel.writeParcelable(original, 0);
            parcel.setDataPosition(0);
            final AspectRatio restored = parcel.readParcelable(getClass().getClassLoader());
            assertNotNull(restored);
            assertThat(restored.getX(), is(4));
            assertThat(restored.getY(), is(3));
            // As the first instance is alive, the parceled result should still be the same instance
            assertThat(restored, is(sameInstance(original)));
        } finally {
            parcel.recycle();
        }
    }

}
