package com.maimemo.text.extractor;

import com.maimemo.text.FastIntPairArray;
import com.maimemo.text.SubCharSequence;
import com.maimemo.text.TextUtils;

import java.util.Iterator;

/**
 * Created by TJT on 3/21/16.
 */
public class WordIterator implements Iterable<SubCharSequence> {

    public static final int TYPE_LETTER = 1;
    public static final int TYPE_HYPHEN = 2;
    public static final int TYPE_QUOTE = 3;
    public static final int TYPE_BREAK = 4;

    private CharSequence text;
    private int length;
    private boolean enableSplitHyphen = true;
    private FastIntPairArray inPlaceSearchResult = new FastIntPairArray(2);
    private int inPlaceSearchIndex = 0;
    private int inPlaceSearchStart = 0;

    int start;
    int end;

    private final SubCharSequence subCharSequence = new SubCharSequence();

    public WordIterator() {

    }

    public WordIterator(CharSequence input) {
        text = input;
        length = text.length();
    }

    public void setEnableSplitHyphen(boolean enable) {
        enableSplitHyphen = enable;
    }

    public void update(CharSequence input) {
        text = input;
        length = text.length();
        currentPos = 0;
        inPlaceSearchResult.clear();
        inPlaceSearchIndex = 0;
        inPlaceSearchStart = 0;
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
        if (nextWord()) {
            outValue.update(text, start, end);
            return true;
        }
        return false;
    }

    public void justSplit(FastIntPairArray outValue) {
        outValue.clear();
        while (nextWord()) {
            outValue.add(start, end);
        }
    }

    private boolean nextWord() {
        if (enableSplitHyphen && inPlaceSearchResult.size() > 0) {
            this.start = inPlaceSearchStart + inPlaceSearchResult.getStart(inPlaceSearchIndex);
            this.end = inPlaceSearchStart + inPlaceSearchResult.getEnd(inPlaceSearchIndex);
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
        if (start + 1 > length) {
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

        if (end > length) {
            end = length;
        }

        currentPos = end;

        while (text.charAt(end - 1) == '-') {
            end--;
            containHyphen = false;
        }

        this.start = start;
        this.end = end;

        if (end <= start) {
            return false;
        }
        if (containHyphen && enableSplitHyphen) {
            // cancel research when last char is '-'
            if (text.charAt(end - 1) != '-') {
                inPlaceSearchStart = start;
                subCharSequence.update(text, start, end);
                TextUtils.fastSplit(subCharSequence, '-', inPlaceSearchResult);
            }
        }
        return true;
    }

    private static boolean isLetter(char c) {
        return (c > 47 && c < 58) || (c > 64 && c < 91) || (c > 96 && c < 123);
    }

    public Iterator<SubCharSequence> iterator() {
        return new Iterator<SubCharSequence>() {

            private SubCharSequence seq = new SubCharSequence();

            public boolean hasNext() {
                return nextWord(seq);
            }

            public SubCharSequence next() {
                return seq;
            }

            @Override
            public void remove() {

            }
        };
    }
}