package com.maimemo;

import org.junit.Test;

import static com.maimemo.TextUtils.simpleToLower;
import static org.junit.Assert.*;

/**
 * Created by TJT on 3/22/16.
 */
public class TextUtilsTest {

    static final char[] upperChars = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    static final char[] lowerChars = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    @Test
    public void testSimpleToLower() throws Exception {
        assertEquals(simpleToLower((char) 64), (char) 64);
        assertEquals(simpleToLower((char) 91), (char) 91);
        assertEquals(simpleToLower('你'), '你');

        for (int i = 0; i < upperChars.length; i++) {
            assertEquals(simpleToLower(upperChars[i]), lowerChars[i]);
            assertEquals(simpleToLower(lowerChars[i]), lowerChars[i]);
        }
    }

    @Test
    public void testFastSplit() throws Exception {
        String s = "alice in wonderland";
        FastIntPairArray intPairArray = TextUtils.fastSplit(s, " ");
        assertEquals(3, intPairArray.size());
        assertEquals(0, intPairArray.getStart(0));
    }

    @Test
    public void testFastSplit1() throws Exception {

    }
}