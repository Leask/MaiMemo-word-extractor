package com.maimemo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by TJT on 3/21/16.
 */
public class WordIteratorTest {

    WordIterator iterator = new WordIterator("sample test' , someone's coffee'cup walk into bar.");

    @Test
    public void testNextWord() throws Exception {
        SubCharSequence subCharSequence = new SubCharSequence();

        String[] s1 = new String[] {
                "sample", "test'", "someone's", "coffee'cup",
                "walk", "into", "bar"
        };
        for (int i = 0; i < s1.length && iterator.nextWord(subCharSequence); i++) {
            assertEquals(s1[i], subCharSequence.toString());
        }
        assertEquals(iterator.nextWord(subCharSequence), false);


        iterator = new WordIterator("alice' in wonderland- ' -- word");
        String[] s2 = new String[] {
                "alice'", "in", "wonderland-", "word"
        };
        for (int i = 0; i < s2.length && iterator.nextWord(subCharSequence); i++) {
            assertEquals(s2[i], subCharSequence.toString());
        }
        assertEquals(iterator.nextWord(subCharSequence), false);

        iterator = new WordIterator("我 not鸡 you speak 咩，please 重复again。a");
        String[] s3 = new String[]{
                "not", "you", "speak", "please", "again", "a"
        };
        for (int i = 0; i < s3.length && iterator.nextWord(subCharSequence); i++) {
            assertEquals(s3[i], subCharSequence.toString());
        }
        assertEquals(iterator.nextWord(subCharSequence), false);

        iterator = new WordIterator(new CharSequenceWrapper("This is a hyphen-split-test!, hope it work~ awesome!"));
        while (iterator.nextWord(subCharSequence)) {
            System.out.print(subCharSequence + ", ");
        }
    }
}