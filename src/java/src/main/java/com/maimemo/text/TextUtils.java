package com.maimemo.text;


/**
 * Text Utils
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

    public static boolean charSeqEquals(CharSequence text1, CharSequence text2) {
        if (text1.length() != text2.length()) {
            return false;
        }
        int length = text1.length();
        for (int i = 0; i < length; i++) {
            if (text1.charAt(i) != text2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 搜索单独的单词，即key是 oo，匹配 “oo,” 不匹配 “wood”
     *
     * @param charSeq 源字符串
     * @param key     要搜索的字符串
     * @param start   开始搜索的偏移量
     * @return 返回搜索到的关键字在源字符串中的位置，搜索不到返回 -1
     */
    public static int standaloneIndexOf(CharSequence charSeq, CharSequence key, int start) {
        if (key.length() == 0) {
            return -1;
        }
        int keyLen = key.length();
        int strLen = charSeq.length();
        if (keyLen == strLen) {
            return charSeqEquals(charSeq, key) ? 0 : -1;
        }
        int pos = start;
        while (pos + keyLen <= strLen) {
            pos = indexOf(charSeq, key, pos);
            if (pos < 0) {
                return -1;
            }
            if (pos == 0) {
                if ((pos + keyLen < charSeq.length()) && !isLetter(charSeq.charAt(pos + keyLen))) {
                    return pos;
                } else {
                    pos += keyLen;
                }
            } else {
                if (!isLetter(charSeq.charAt(pos - 1)) &&
                        (pos + keyLen >= strLen ||
                                !isLetter(charSeq.charAt(pos + keyLen)))) {
                    return pos;
                } else {
                    pos += keyLen;
                }
            }
        }
        return -1;
    }

    public static boolean isLetter(char c) {
        return (c > 47 && c < 58) || (c > 64 && c < 91) || (c > 96 && c < 123 || c == '-');
    }
}
