package com.maimemo.text;

import org.junit.Test;

import static com.maimemo.text.TextUtils.simpleToLower;
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
        String s1 = "alice in wonderland";
        String[] s2 = new String[] {
                "alice", "in", "wonderland"
        };
        FastIntPairArray intPairArray = TextUtils.fastSplit(s1, " ");
        assertEquals(3, intPairArray.size());
        for (int i = 0; i < s2.length; i++) {
            assertEquals(s2[i], s1.substring(intPairArray.getStart(i), intPairArray.getEnd(i)));
        }
    }

    @Test
    public void testFastSplit1() throws Exception {
        String s1 = "alice-in-wonderland";
        String[] s2 = new String[] {
                "alice", "in", "wonderland"
        };
        FastIntPairArray intPairArray = TextUtils.fastSplit(s1, "-");
        assertEquals(3, intPairArray.size());
        for (int i = 0; i < s2.length; i++) {
            assertEquals(s2[i], s1.substring(intPairArray.getStart(i), intPairArray.getEnd(i)));
        }
    }

    @Test
    public void testIndexOf() throws Exception {
        String s = "abcdefg";
        assertEquals(2, TextUtils.indexOf(s, 'c', 0));
        assertEquals(2, TextUtils.indexOf(s, 'c', 2));
        assertEquals(-1, TextUtils.indexOf(s, 'c', 3));
        assertEquals(-1, TextUtils.indexOf(s, 'z', 4));
        assertEquals(6, TextUtils.indexOf(s, 'g', 4));
    }

    @Test
    public void testIndexOf1() throws Exception {
        String s = "abcdefg";
        assertEquals(2, TextUtils.indexOf(s, "cd", 0));
        assertEquals(2, TextUtils.indexOf(s, "cd", 2));
        assertEquals(-1, TextUtils.indexOf(s, "cd", 3));
        assertEquals(-1, TextUtils.indexOf(s, "z", 4));
        assertEquals(5, TextUtils.indexOf(s, "fg", 4));
    }
}