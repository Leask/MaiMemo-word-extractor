package com.maimemo;

import java.util.Iterator;

/**
 * 遍历所有词组
 * <p/>
 * Created by TJT on 3/21/16.
 */
public class WordGroupIterator implements Iterable<SubCharSequence> {

    private static final int MINIMUM_WORD_GROUP_SIZE = 1;

    private CharSequence text;
    private int currentPos = 0;
    private int currentSize = MINIMUM_WORD_GROUP_SIZE;
    private FastIntPairArray splitResult = new FastIntPairArray(3);
    private int size;

    public WordGroupIterator() {
    }

    public void update(CharSequence newText) {
        text = newText;
        currentPos = 0;
        TextUtils.fastSplit(newText, ' ', splitResult);
        size = splitResult.size();
    }

    /**
     *
     */
    public boolean nextWordGroup(SubCharSequence outValue) {
        if (size < 2) {
            return false;
        }
        if (currentPos + currentSize >= size || currentSize >= 6) {
            currentSize = MINIMUM_WORD_GROUP_SIZE;
            currentPos++;
        }
        if (currentPos == size - 1) {
            return false;
        }

        int start = splitResult.getStart(currentPos);
        int end = splitResult.getEnd(currentPos + currentSize);

        // first char must be a letter
        char c;
        for (; start < end; start++) {
            c = text.charAt(start);
            if (TextUtils.isLetter(c)) {
                break;
            }
        }

        int i = 0;
        // last char must be a letter and can't be hyphen
        while (end > start) {
            c = text.charAt(end - 1);
            if (TextUtils.isLetter(c)) {
                if (i == 0 && c == '-') {
                    end--;
                    continue;
                }
                break;
            }
            i++;
            end--;
        }

        outValue.update(text, start, end);
        currentSize++;
        return true;
    }

    @Override
    public Iterator<SubCharSequence> iterator() {
        return new Iterator<SubCharSequence>() {

            SubCharSequence subCharSequence = new SubCharSequence();

            @Override
            public boolean hasNext() {
                return nextWordGroup(subCharSequence);
            }

            @Override
            public SubCharSequence next() {
                return subCharSequence;
            }
        };
    }
}
