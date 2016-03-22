package com.maimemo;

import java.util.Iterator;

/**
 * Created by TJT on 3/21/16.
 */
public class WordIterator implements Iterable<CharSequence> {

    public static final int TYPE_LETTER = 1;
    public static final int TYPE_HYPHEN = 2;
    public static final int TYPE_BREAK = 3;

    private static final char[] HYPHENS = {
            '-', '\''
    };

    private final CharSequence text;
    private final int length;

    public WordIterator(CharSequence input) {
        text = input;
        length = text.length();
    }

    private int currentPos = 0;

    private static int getCharType(char c) {
        if (isLetter(c)) {
            return TYPE_LETTER;
        } else if (isHyhpen(c)) {
            return TYPE_HYPHEN;
        }
        return TYPE_BREAK;
    }

    public boolean nextWord(SubCharSequence outValue) {
        if (currentPos >= length) {
            return false;
        }
        int start = currentPos;

        // beginning must be a letter
        for (; start < length; start++) {
            if (getCharType(text.charAt(start)) == TYPE_LETTER) {
                break;
            }
        }

        // beginning is arrived end
        if (start + 1 == length) {
            currentPos = length;
            return false;
        }

        int end = start + 1;
        int charType;
        // search end of word
        for (; end < length; end++) {
            charType = getCharType(text.charAt(end));
            if (charType == TYPE_BREAK) {
                break;
            }
        }

        currentPos = end + 1;
        outValue.update(text, start, end);
        return true;
    }

    private static boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    private static boolean isHyhpen(char c) {
        for (char hyphen : HYPHENS) {
            if (hyphen == c) {
                return true;
            }
        }
        return false;
    }

    public Iterator<CharSequence> iterator() {
        return new Iterator<CharSequence>() {

            private SubCharSequence seq = new SubCharSequence();

            public boolean hasNext() {
                return currentPos >= text.length();
            }

            public CharSequence next() {
                nextWord(seq);
                return seq;
            }
        };
    }
}
