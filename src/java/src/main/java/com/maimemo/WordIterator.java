package com.maimemo;

import java.util.Iterator;

/**
 * Created by TJT on 3/21/16.
 */
public class WordIterator implements Iterable<CharSequence> {

    public static final int TYPE_LETTER = 1;
    public static final int TYPE_HYPHEN = 2;
    public static final int TYPE_QUOTE = 3;
    public static final int TYPE_BREAK = 4;

    private final CharSequence text;
    private final int length;
    private final FastIntPairArray inPlaceSearchResult = new FastIntPairArray(2);
    private int inPlaceSearchIndex = 0;
    private int inPlaceSearchStart = 0;

    public WordIterator(CharSequence input) {
        text = input;
        length = text.length();
    }

    private int currentPos = 0;

    private static int getCharType(char c) {
        if (isLetter(c)) {
            return TYPE_LETTER;
        } else if (c == '-') {
            return TYPE_HYPHEN;
        } else if (c == '\'') {
            return TYPE_QUOTE;
        }
        return TYPE_BREAK;
    }

    public boolean nextWord(SubCharSequence outValue) {
        if (inPlaceSearchResult.size() > 0) {
            outValue.update(text, inPlaceSearchStart + inPlaceSearchResult.getStart(inPlaceSearchIndex),
                    inPlaceSearchStart + inPlaceSearchResult.getEnd(inPlaceSearchIndex));
            inPlaceSearchIndex++;
            if (inPlaceSearchIndex >= inPlaceSearchResult.size()) {
                inPlaceSearchResult.clear();
                inPlaceSearchIndex = 0;
            }
            return true;
        }
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
        boolean containHyphen = false;
        // search end of word
        for (; end < length; end++) {
            charType = getCharType(text.charAt(end));
            if (charType == TYPE_HYPHEN) {
                containHyphen = true;
            }
            if (charType == TYPE_BREAK) {
                break;
            }
        }

        currentPos = end + 1;
        outValue.update(text, start, end);
        if (containHyphen) {
            // cancel research when end char is '-'
            if (text.charAt(end - 1) != '-') {
                inPlaceSearchStart = start;
                TextUtils.fastSplit(outValue, '-', inPlaceSearchResult);
            }
        }
        return true;
    }

    private static boolean isLetter(char c) {
        return (c > 47 && c < 58) || (c > 64 && c < 91) || (c > 96 && c < 123);
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
