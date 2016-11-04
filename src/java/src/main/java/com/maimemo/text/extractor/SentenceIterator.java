package com.maimemo.text.extractor;

import com.maimemo.text.SubCharSequence;
import com.maimemo.text.TextUtils;

import java.util.Iterator;

/**
 * Created by TJT on 4/13/16.
 */
class SentenceIterator implements Iterable<SubCharSequence> {

    private int currentPos = 0;
    private int length;
    private CharSequence text;

    public SentenceIterator(CharSequence input) {
        text = input;
        length = text.length();
    }

    private boolean lookBackward(CharSequence word, int position) {
        if (position > word.length()) {
            for (int i = 1; i <= word.length(); i++) {
                char c1 = TextUtils.simpleToLower(text.charAt(position - i));
                char c2 = word.charAt(word.length() - i);
                if (c1 != c2){
                    return false;
                }
            }
            return true;
        }
        return false;
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
            if (c == ',' || c == '\n' || c == ';') {
                break;
            }
            if (c == '.') {
                // prevent break sentence when occurred 'sth.', 'sb.', '...'
                if (currentPos < length -1 && text.charAt(currentPos + 1) == '.') {
                    continue;
                }
                if (lookBackward("sth", currentPos) || lookBackward("sb", currentPos) || lookBackward("..", currentPos)) {
                    continue;
                }
                break;
            }
        }
        if (currentPos >= length) {
            currentPos = length -1;
        }
        outValue.update(text, start, currentPos);
        return true;
    }

    @Override
    public Iterator<SubCharSequence> iterator() {
        return new Iterator<SubCharSequence>() {

            SubCharSequence subCharSequence = new SubCharSequence();

            @Override
            public boolean hasNext() {
                return nextSentence(subCharSequence);
            }

            @Override
            public SubCharSequence next() {
                return subCharSequence;
            }

            @Override
            public void remove() {

            }
        };
    }
}
