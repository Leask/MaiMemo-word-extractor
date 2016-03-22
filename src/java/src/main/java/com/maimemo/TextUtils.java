package com.maimemo;

/**
 * Created by TJT on 3/22/16.
 */
public class TextUtils {

    public static char simpleToLower(char c) {
        if (c > 64 && c <= 90) {
            return (char) (c + 32);
        }
        return c;
    }

    /**
     * fast split
     */
    public static FastIntPairArray fastSplit(String src, String needle) {
        FastIntPairArray ranges = new FastIntPairArray(3);
        if (needle.length() == 0 || src.length() == 0) {
            return ranges;
        }
        int start = 0;
        int end;
        while (true) {
            end = src.indexOf(needle, start);
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
    public static FastIntPairArray fastSplit(String src, char needle) {
        FastIntPairArray ranges = new FastIntPairArray(3);
        if (src.length() == 0) {
            return ranges;
        }
        int start = 0;
        int end;
        while (true) {
            end = src.indexOf(needle, start);
            if (end == -1) {
                if (start == 0) {
                    return ranges;
                }
                ranges.add(start, src.length());
                break;
            }
            ranges.add(start, end);
            start = end + 1;
        }
        return ranges;
    }
}
