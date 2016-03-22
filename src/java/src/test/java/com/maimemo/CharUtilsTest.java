package com.maimemo;

import org.junit.Test;

import static com.maimemo.CharUtils.simpleToLower;
import static org.junit.Assert.*;

/**
 * Created by TJT on 3/22/16.
 */
public class CharUtilsTest {

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
}