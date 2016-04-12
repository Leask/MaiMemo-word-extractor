package com.maimemo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by TJT on 4/12/16.
 */
public class WordGroupIteratorTest {

    private WordGroupIterator iterator = new WordGroupIterator();

    @Before
    public void setup() {

    }

    @Test
    public void testNextWordGroup() throws Exception {
        String text = "there is a simple test";
        iterator.update(text);
        SubCharSequence subCharSequence = new SubCharSequence();
        iterator.nextWordGroup(false, subCharSequence);
        assertEquals("there is", subCharSequence.toString());

        iterator.nextWordGroup(true, subCharSequence);
        assertEquals("is a", subCharSequence.toString());

        iterator.nextWordGroup(false, subCharSequence);
        assertEquals("is a simple", subCharSequence.toString());

        iterator.nextWordGroup(true, subCharSequence);
        assertEquals("a simple", subCharSequence.toString());

        iterator.nextWordGroup(false, subCharSequence);
        assertEquals("a simple test", subCharSequence.toString());

        iterator.nextWordGroup(false, subCharSequence);
        assertEquals("simple test", subCharSequence.toString());

        assertEquals(false, iterator.nextWordGroup(false, subCharSequence));

        iterator.update("fuck");
        assertEquals(false, iterator.nextWordGroup(false, subCharSequence));

        iterator.update("simple test");
        iterator.nextWordGroup(true, subCharSequence);
        assertEquals("simple test", subCharSequence.toString());
        assertEquals(false, iterator.nextWordGroup(false, subCharSequence));
    }
}