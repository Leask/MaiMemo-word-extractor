package com.maimemo;

/**
 * Created by TJT on 3/21/16.
 */
public class SubCharSequence implements CharSequence {

    private CharSequence source;
    private int start;
    private int end;

    public SubCharSequence() {

    }

    void update(CharSequence source, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("end can't less than start");
        }
        this.source = source;
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int length() {
        return end - start;
    }

    public char charAt(int index) {
        if (index >= length()) {
            throw new StringIndexOutOfBoundsException(length());
        }
        return source.charAt(start + index);
    }

    @Override
    public String toString() {
        char[] chars = new char[length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = charAt(i);
        }
        return new String(chars);
    }

    @Override
    public int hashCode() {
        int h = 0;
        if (length() > 0) {
            for (int i = 0; i < length(); i++) {
                h = 31 * h + TextUtils.simpleToLower(charAt(i));
            }
        }
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharSequenceWrapper) {
            return true;
        }
        if (obj instanceof SubCharSequence) {
            SubCharSequence sequence = (SubCharSequence) obj;
            if (sequence.source == source && sequence.start == start && sequence.end == end) {
                return true;
            }
        } else if (obj instanceof CharSequence) {
            CharSequence sequence = (CharSequence) obj;
            if (sequence.length() != length()) {
                return false;
            }
            final int length = length();
            for (int i = 0; i < length; i++) {
                if (sequence.charAt(i) != charAt(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public CharSequence subSequence(int start, int end) {
        throw new IllegalAccessError();
    }
}
