package com.maimemo;

import javafx.scene.SubScene;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by TJT on 3/21/16.
 */
public class WordIteratorTest {

    WordIterator iterator = new WordIterator("sample test' , someone's coffee-cup walk into bar.");

    @Test
    public void testNextWord() throws Exception {
        SubCharSequence subCharSequence = new SubCharSequence();

        boolean searched = iterator.nextWord(subCharSequence);
        assertEquals(searched, true);
        assertEquals(subCharSequence.toString(), "sample");

        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "test'");

        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "someone's");

        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "coffee-cup");

        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "walk");

        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "into");

        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "bar");

        assertEquals(iterator.nextWord(subCharSequence), false);

        iterator = new WordIterator("alice' in worder' ' -- word");
        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "alice'");

        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "in");

        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "worder'");

        iterator.nextWord(subCharSequence);
        assertEquals(subCharSequence.toString(), "word");

        assertEquals(iterator.nextWord(subCharSequence), false);

        iterator = new WordIterator("我 not鸡 you speak 咩，please 重复again。");
        while (iterator.nextWord(subCharSequence)) {

        }
    }
}