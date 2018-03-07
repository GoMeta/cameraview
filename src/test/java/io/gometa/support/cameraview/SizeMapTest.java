// Copyright (c) 2018 GoMeta. All right reserved.

package io.gometa.support.cameraview;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import io.gometa.support.cameraview.AspectRatio;
import io.gometa.support.cameraview.Size;
import io.gometa.support.cameraview.SizeMap;

public class SizeMapTest {

    @Test
    public void testAdd_simple() {
        SizeMap map = new SizeMap();
        map.add(new Size(3, 4));
        map.add(new Size(9, 16));
        assertThat(map.ratios().size(), is(2));
    }

    @Test
    public void testAdd_duplicate() {
        SizeMap map = new SizeMap();
        map.add(new Size(3, 4));
        map.add(new Size(6, 8));
        map.add(new Size(9, 12));
        assertThat(map.ratios().size(), is(1));
        AspectRatio ratio = (AspectRatio) map.ratios().toArray()[0];
        assertThat(ratio.toString(), is("3:4"));
        assertThat(map.sizes(ratio).size(), is(3));
    }

    @Test
    public void testClear() {
        SizeMap map = new SizeMap();
        map.add(new Size(12, 34));
        assertThat(map.ratios().size(), is(1));
        map.clear();
        assertThat(map.ratios().size(), is(0));
    }

}
