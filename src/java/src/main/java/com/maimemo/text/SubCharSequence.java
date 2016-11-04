package com.maimemo.text;

import java.util.Locale;

/**
 * Created by TJT on 3/21/16.
 */
public class SubCharSequence implements
        CharSequence,
        Comparable<CharSequence> {

    private CharSequence source;
    private int start;
    private int end;

    public SubCharSequence() {

    }

    public SubCharSequence(CharSequence str) {
        update(str);
    }

    public void update(CharSequence source, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "end %d can't less than start %d", end, start));
        }
        this.source = source;
        this.start = start;
        this.end = end;
        if (end > source.length()) {
            throw new IndexOutOfBoundsException("fuck");
        }
    }

    public void update(CharSequence str) {
        update(str, 0, str.length());
    }

    public void update(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "end %d can't less than start %d", end, start));
        }
        this.start = start;
        this.end = end;
        if (end > source.length()) {
            throw new IndexOutOfBoundsException("fuck");
        }
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
        if (obj instanceof CharSequence) {
            CharSequence sequence = (CharSequence) obj;
            if (sequence.length() != length()) {
                return false;
            }
            final int length = length();
            for (int i = 0; i < length; i++) {
                if (TextUtils.simpleToLower(sequence.charAt(i)) != TextUtils.simpleToLower(charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(CharSequence o) {
        int n1 = o.length();
        int n2 = length();
        int min = Math.min(n1, n2);
        for (int i = 0; i < min; i++) {
            char c1 = o.charAt(i);
            char c2 = charAt(i);
            if (c1 != c2) {
                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
                if (c1 != c2) {
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if (c1 != c2) {
                        // No overflow because of numeric promotion
                        return c1 - c2;
                    }
                }
            }
        }
        return n1 - n2;
    }

    public CharSequence subSequence(int start, int end) {
        return source.subSequence(this.start + start, this.start + end);
    }
}
