package com.maimemo;

/**
 * Created by TJT on 4/13/16.
 */
class SentenceIterator {

    private int currentPos = 0;
    private int length;
    private CharSequence text;

    public SentenceIterator(CharSequence input) {
        text = input;
        length = text.length();
    }

    public boolean nextSentence(SubCharSequence outValue) {
        if (currentPos >= length - 1) {
            return false;
        }

        char c;
        int start = currentPos;
        if (currentPos > 0) {
            start++;
        }
        for (currentPos += 3; currentPos < length; currentPos++) {
            c = text.charAt(currentPos);
            if (c == ',' || c == '.' || c == '\n' || c == ';') {
                break;
            }
        }
        outValue.update(text, start, currentPos);
        return true;
    }
}
