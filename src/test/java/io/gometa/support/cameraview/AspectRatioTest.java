// Copyright (c) 2018 GoMeta. All right reserved.

package io.gometa.support.cameraview;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.HashSet;

import io.gometa.support.cameraview.AspectRatio;
import io.gometa.support.cameraview.Size;

public class AspectRatioTest {

    @Test
    public void testGcd() {
        AspectRatio r;
        r = AspectRatio.of(1, 2);
        assertThat(r.getX(), is(1));
        r = AspectRatio.of(2, 4);
        assertThat(r.getX(), is(1));
        assertThat(r.getY(), is(2));
        r = AspectRatio.of(391, 713);
        assertThat(r.getX(), is(17));
        assertThat(r.getY(), is(31));
    }

    @Test
    public void testMatches() {
        AspectRatio ratio = AspectRatio.of(3, 4);
        assertThat(ratio.matches(new Size(6, 8)), is(true));
        assertThat(ratio.matches(new Size(1, 2)), is(false));
    }

    @Test
    public void testGetters() {
        AspectRatio ratio = AspectRatio.of(2, 4); // Reduced to 1:2
        assertThat(ratio.getX(), is(1));
        assertThat(ratio.getY(), is(2));
    }

    @Test
    public void testToString() {
        AspectRatio ratio = AspectRatio.of(1, 2);
        assertThat(ratio.toString(), is("1:2"));
    }

    @Test
    public void testEquals() {
        AspectRatio a = AspectRatio.of(1, 2);
        AspectRatio b = AspectRatio.of(2, 4);
        AspectRatio c = AspectRatio.of(2, 3);
        assertThat(a.equals(b), is(true));
        assertThat(a.equals(c), is(false));
    }

    @Test
    public void testHashCode() {
        int max = 100;
        HashSet<Integer> codes = new HashSet<>();
        for (int x = 1; x <= 100; x++) {
            codes.add(AspectRatio.of(x, 1).hashCode());
        }
        assertThat(codes.size(), is(max));
        codes.clear();
        for (int y = 1; y <= 100; y++) {
            codes.add(AspectRatio.of(1, y).hashCode());
        }
        assertThat(codes.size(), is(max));
    }

    @Test
    public void testInverse() {
        AspectRatio r = AspectRatio.of(4, 3);
        assertThat(r.getX(), is(4));
        assertThat(r.getY(), is(3));
        AspectRatio s = r.inverse();
        assertThat(s.getX(), is(3));
        assertThat(s.getY(), is(4));
    }

    @Test
    public void testParse() {
        AspectRatio r = AspectRatio.parse("23:31");
        assertThat(r.getX(), is(23));
        assertThat(r.getY(), is(31));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFailure() {
        AspectRatio.parse("MALFORMED");
    }

}
