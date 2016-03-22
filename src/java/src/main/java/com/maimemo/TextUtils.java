package com.maimemo;

/**
 * Created by TJT on 3/22/16.
 */
public class TextUtils {

    public static int indexOf(CharSequence text, char c, int start) {
        int count = text.length();
        if (start >= count) {
            return -1;
        }
        for (int i = start; i < count; i++) {
            if (c == text.charAt(i)) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(CharSequence text, CharSequence subSeq, int start) {
        if (start < 0) {
            start = 0;
        }
        int subCount = subSeq.length();
        int _count = text.length();
        if (subCount > 0) {
            if (subCount + start > _count) {
                return -1;
            }
            char firstChar = subSeq.charAt(0);
            while (true) {
                int i = indexOf(text, firstChar, start);
                if (i == -1 || subCount + i > _count) {
                    return -1; // handles subCount > count || start >= count
                }
                int o1 = i, o2 = 0;
                //noinspection StatementWithEmptyBody
                while (++o2 < subCount && text.charAt(++o1) == subSeq.charAt(o2)) {
                    // Intentionally empty
                }
                if (o2 == subCount) {
                    return i;
                }
                start = i + 1;
            }
        }
        return start < _count ? start : _count;
    }

    public static char simpleToLower(char c) {
        if (c > 64 && c <= 90) {
            return (char) (c + 32);
        }
        return c;
    }

    /**
     * fast split
     */
    public static FastIntPairArray fastSplit(CharSequence src, CharSequence needle) {
        FastIntPairArray ranges = new FastIntPairArray(3);
        if (needle.length() == 0 || src.length() == 0) {
            return ranges;
        }
        int start = 0;
        int end;
        while (true) {
            end = indexOf(src, needle, start);
            if (end == -1) {
                if (start == 0) {
                    return ranges;
                }
                ranges.add(start, src.length());
                break;
            }
            ranges.add(start, end);
            start = end + needle.length();
        }
        return ranges;
    }

    /**
     * fast split
     */
    public static FastIntPairArray fastSplit(CharSequence src, char needle) {
        FastIntPairArray ranges = new FastIntPairArray(3);
        fastSplit(src, needle, ranges);
        return ranges;
    }

    /**
     * fast split
     */
    public static void fastSplit(CharSequence src, char needle, FastIntPairArray outValue) {
        outValue.clear();
        if (src.length() == 0) {
            return;
        }
        int start = 0;
        int end;
        while (true) {
            end = indexOf(src, needle, start);
            if (end == -1) {
                if (start == 0) {
                    return;
                }
                outValue.add(start, src.length());
                break;
            }
            outValue.add(start, end);
            start = end + 1;
        }
    }
}
