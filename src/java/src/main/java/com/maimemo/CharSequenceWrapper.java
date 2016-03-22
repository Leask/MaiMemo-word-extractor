package com.maimemo;

/**
 * override {@link #hashCode()}, make sure it same with {@link SubCharSequence#hashCode()}
 *
 * Created by TJT on 3/22/16.
 */
public class CharSequenceWrapper implements CharSequence {

    private final CharSequence source;
    /** cache hash code */
    private int hash = 0;

    public CharSequenceWrapper(CharSequence source) {
        this.source = source;
    }

    public int length() {
        return source.length();
    }

    public char charAt(int index) {
        return source.charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        return source.subSequence(start, end);
    }

    @Override
    public int hashCode() {
        if (hash == 0 && length() > 0) {
            for (int i = 0; i < length(); i++) {
                hash = 31 * hash + TextUtils.simpleToLower(charAt(i));
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return true;
    }
}
