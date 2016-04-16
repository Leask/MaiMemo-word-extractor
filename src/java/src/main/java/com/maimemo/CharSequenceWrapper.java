package com.maimemo;

/**
 * override {@link #hashCode()}, make sure it same with {@link SubCharSequence#hashCode()}
 * <p>
 * Created by TJT on 3/22/16.
 */
public class CharSequenceWrapper implements
        CharSequence,
        Comparable<CharSequence> {

    private final CharSequence source;
    /**
     * cache hash code
     */
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
    public String toString() {
        return source.toString();
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
}
