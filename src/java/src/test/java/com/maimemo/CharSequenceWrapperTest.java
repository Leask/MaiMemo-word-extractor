package com.maimemo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by TJT on 3/22/16.
 */
public class CharSequenceWrapperTest {

    String source = "test string";

    CharSequenceWrapper wrapper = new CharSequenceWrapper(source);

    @Test
    public void testLength() throws Exception {
        assertEquals(source.length(), wrapper.length());
    }

    @Test
    public void testCharAt() throws Exception {
        for (int i = 0; i < source.length(); i++) {
            assertEquals(source.charAt(i), wrapper.charAt(i));
        }
    }

    @Test
    public void testSubSequence() throws Exception {
        assertEquals(source.subSequence(0, 5), wrapper.subSequence(0, 5));
    }

    @Test
    public void testHashCode() throws Exception {
        SubCharSequence subCharSequence = new SubCharSequence();
        subCharSequence.update(source, 0, source.length());
        assertEquals(subCharSequence.hashCode(), wrapper.hashCode());
    }
}