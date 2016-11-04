package com.maimemo.text;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by TJT on 3/22/16.
 */
public class SubCharSequenceTest {

    String source = "Alice in wonderland";
    SubCharSequence sub = new SubCharSequence();

    public SubCharSequenceTest() {
        sub.update(source, 2, 10);
    }

    @Test
    public void testLength() throws Exception {
        assertEquals(sub.length(), 8);
    }

    @Test
    public void testCharAt() throws Exception {
        for (int i = 0; i < sub.length(); i++) {
            assertEquals(source.charAt(2 + i), sub.charAt(i));
        }
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(sub.toString(), "ice in w");
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(true, sub.equals("ice in w"));
    }
}