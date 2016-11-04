/*
 * Copyright (C) 2016 MaiMemo Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maimemo.text.stemmer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of the Porter2 stemming algorithm.
 * refer: http://snowball.tartarus.org/algorithms/english/stemmer.html
 * <p/>
 * translate from pyporter2: https://github.com/mdirolf/pyporter2
 * Created by TJT on 7/18/16.
 */
@SuppressWarnings("SpellCheckingInspection")
class Porter2 {

    private static final Pattern PATTERN1 = Pattern.compile("[^aeiouy]*[aeiouy]+[^aeiouy](\\w*)");
    private static final Pattern PATTERN2 = Pattern.compile("^[aeiouy][^aeiouy]$");
    private static final Pattern PATTERN3 = Pattern.compile(".*[^aeiouy][aeiouy][^aeiouywxY]$");
    private static final Pattern PATTERN4 = Pattern.compile("([aeiouy])y");
    private static final Pattern PATTERN5 = Pattern.compile("[aeiouy].");
    private static final Pattern PATTERN6 = Pattern.compile("[aeiouy]");

    public static boolean startsWith(CharSequence string, CharSequence prefix) {
        return startsWith(string, prefix, 0);
    }

    public static boolean startsWith(CharSequence string, CharSequence prefix, int toffset) {
        int to = toffset;
        int po = 0;
        int pc = prefix.length();
        // Note: toffset might be near -1>>>1.
        if ((toffset < 0) || (toffset > string.length() - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (string.charAt(to++) != prefix.charAt(po++)) {
                return false;
            }
        }
        return true;
    }

    public static boolean endsWith(CharSequence string, String suffix) {
        return startsWith(string, suffix, string.length() - suffix.length());
    }

    public static boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y';
    }

    public static boolean matched(Pattern pattern, CharSequence word) {
        return pattern.matcher(word).find();
    }

    public static CharSequence substring(CharSequence word, int start, int end) {
        if (end < 0) {
            end = word.length() + end;
        }
        return word.subSequence(start, end);
    }

    public static CharSequence substring(CharSequence word, int start, int end, String append) {
        return substring(word, start, end) + append;
    }

    public static int getR1(CharSequence word) {
        if (startsWith(word, "gener") || startsWith(word, "arsen")) {
            return 5;
        }
        if (startsWith(word, "commun")) {
            return 6;
        }
        Matcher matcher = PATTERN1.matcher(word);
        if (matcher.find()) {
            return matcher.start(1);
        }
        return word.length();
    }

    public static int getR2(CharSequence word) {
        Matcher matcher = PATTERN1.matcher(word);
        if (matcher.find(getR1(word))) {
            return matcher.start(1);
        }
        return word.length();
    }

    public static int getR2(CharSequence word, int r1) {
        Matcher matcher = PATTERN1.matcher(word);
        if (matcher.find(r1)) {
            return matcher.start(1);
        }
        return word.length();
    }

    public static boolean endsWithShortSyllable(CharSequence word) {
        if (word.length() == 2) {
            return matched(PATTERN2, word);
        }
        return matched(PATTERN3, word);
    }

    public static boolean isShortWord(CharSequence word) {
        if (endsWithShortSyllable(word)) {
            if (word.length() == getR1(word)) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence capitalizeConsonantYs(CharSequence word) {
        if (startsWith(word, "y")) {
            word = "Y" + word.subSequence(1, word.length());
        }
        Matcher matcher = PATTERN4.matcher(word);
        if (matcher.find()) {
            word = matcher.replaceFirst("$1Y");
        }
        return word;
    }

    public static CharSequence step0(CharSequence word) {
        if (endsWith(word, "'s'")) {
            return substring(word, 0, -3);
        }
        if (endsWith(word, "'s")) {
            return substring(word, 0, -2);
        }
        if (endsWith(word, "'")) {
            return substring(word, 0, -1);
        }
        return word;
    }

    public static CharSequence step1a(CharSequence word) {
        if (endsWith(word, "sses")) {
            return substring(word, 0, -2);
        }
        if (endsWith(word, "ied") || endsWith(word, "ies")) {
            if (word.length() > 4) {
                return substring(word, 0, -3, "i");
            } else {
                return substring(word, 0, -3, "ie");
            }
        }
//        if (endsWith(word, "us") || endsWith(word, "ss")) {
//            return word;
//        }
        if (endsWith(word, "s")) {
            CharSequence preceding = word.subSequence(0, word.length() - 1);
            if (matched(PATTERN5, preceding)) {
                return preceding;
            }
        }
        return word;
    }

    private static final String[] DOUBLES = {
            "bb", "dd", "ff", "gg", "mm", "nn", "pp", "rr", "tt"
    };

    public static boolean endsWithDouble(CharSequence word) {
        for (String aDouble : DOUBLES) {
            if (endsWith(word, aDouble)) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence step1bHelper(CharSequence word) {
        if (endsWith(word, "at") || endsWith(word, "bl") || endsWith(word, "iz")) {
            return word + "e";
        }
        if (endsWithDouble(word)) {
            return word.subSequence(0, word.length() - 1);
        }
        if (isShortWord(word)) {
            return word + "e";
        }
        return word;
    }

    private static final String[] STEP_1B_SUFFIXES = {
            "ed", "edly", "ing", "ingly"
    };

    public static CharSequence step1b(CharSequence word, int r1) {
        if (endsWith(word, "eedly")) {
            if (word.length() - 5 >= r1) {
                return substring(word, 0, -3);
            }
            return word;
        }
        if (endsWith(word, "eed")) {
            if (word.length() - 3 >= r1) {
                return substring(word, 0, -1);
            }
            return word;
        }

        for (String suffix : STEP_1B_SUFFIXES) {
            if (endsWith(word, suffix)) {
                CharSequence preceding = substring(word, 0, -suffix.length());
                if (matched(PATTERN6, preceding)) {
                    return step1bHelper(preceding);
                }
                break;
            }
        }
        return word;
    }

    public static CharSequence step1c(CharSequence word) {
        if (endsWith(word, "y") || endsWith(word, "Y")) {
            if (word.length() > 2 && !isVowel(word.charAt(word.length() - 2))) {
                if (word.length() > 2) {
                    return substring(word, 0, -1, "i");
                }
            }
        }
        return word;
    }

    public static CharSequence step2Helper(CharSequence word, int r1, String end, String repl, String[] prev) {
        if (endsWith(word, end)) {
            if (word.length() - end.length() >= r1) {
                if (prev == null || prev.length == 0) {
                    return substring(word, 0, -end.length(), repl);
                }
                for (String p : prev) {
                    if (endsWith(substring(word, 0, -end.length()), p)) {
                        return substring(word, 0, -end.length(), repl);
                    }
                }
            }
            return word;
        }
        return null;
    }

    private static Object[] STEP2_TRIPLES = {
            "ization", "ize", null,
            "ational", "ate", null,
            "fulness", "ful", null,
            "ousness", "ous", null,
            "iveness", "ive", null,
            "tional", "tion", null,
            "biliti", "ble", null,
            "lessli", "less", null,
            "entli", "ent", null,
            "ation", "ate", null,
            "alism", "al", null,
            "aliti", "al", null,
            "ousli", "ous", null,
            "iviti", "ive", null,
            "fulli", "ful", null,
            "enci", "ence", null,
            "anci", "ance", null,
            "abli", "able", null,
            "izer", "ize", null,
            "ator", "ate", null,
            "alli", "al", null,
            "bli", "ble", null,
            "ogi", "og", new String[]{"l"},
            "li", "", new String[]{"c", "d", "e", "g", "h", "k", "m", "n", "r", "t"}
    };

    public static CharSequence step2(CharSequence word, int r1) {
        for (int i = 0; i < STEP2_TRIPLES.length; i += 3) {
            CharSequence attempt = step2Helper(word, r1,
                    (String) STEP2_TRIPLES[i], (String) STEP2_TRIPLES[i + 1], (String[]) STEP2_TRIPLES[i + 2]);
            if (attempt != null) {
                return attempt;
            }
        }
        return word;
    }

    public static CharSequence step3Helper(CharSequence word, int r1, int r2, String end, String repl, Boolean r2Necessary) {
        if (endsWith(word, end)) {
            if (word.length() - end.length() >= r1) {
                if (r2Necessary) {
                    if (word.length() - end.length() >= r2) {
                        return substring(word, 0, -end.length(), repl);
                    }
                } else {
                    return substring(word, 0, -end.length(), repl);
                }
            }
            return word;
        }
        return null;
    }

    private static final Object[] STEP3_TRIPLES = {
            "ational", "ate", false,
            "tional", "tion", false,
            "alize", "al", false,
            "icate", "ic", false,
            "iciti", "ic", false,
            "ative", "", true,
            "ical", "ic", false,
            "ness", "", false,
            "ful", "", false
    };

    public static CharSequence step3(CharSequence word, int r1, int r2) {
        for (int i = 0; i < STEP3_TRIPLES.length; i += 3) {
            CharSequence attempt = step3Helper(word, r1, r2, (String) STEP3_TRIPLES[i], (String) STEP3_TRIPLES[i + 1], (Boolean) STEP3_TRIPLES[i + 2]);
            if (attempt != null) {
                return attempt;
            }
        }
        return word;
    }

    private static final String[] STEP4_DELETE_LIST = {
            "al", "ance", "ence", "er", "ic", "able", "ible", "ant", "ement", "ment", "ent", "ism", "ate", "iti", "ous", "ive", "ize"
    };

    public static CharSequence step4(CharSequence word, int r2) {
        for (String end : STEP4_DELETE_LIST) {
            if (endsWith(word, end)) {
                if (word.length() - end.length() >= r2) {
                    return substring(word, 0, -end.length());
                }
                return word;
            }
        }
        if (endsWith(word, "sion") || endsWith(word, "tion")) {
            if (word.length() - 3 >= r2) {
                return substring(word, 0, -3);
            }
        }
        return word;
    }

    public static CharSequence step5(CharSequence word, int r1, int r2) {
        if (endsWith(word, "l")) {
            if (word.length() - 1 >= r2 && word.charAt(word.length() - 2) == 'l') {
                return substring(word, 0, -1);
            }
            return word;
        }
        if (endsWith(word, "e")) {
            if (word.length() - 1 >= r2) {
                return substring(word, 0, -1);
            }
            if (word.length() - 1 >= r1 && !endsWithShortSyllable(substring(word, 0, -1))) {
                return substring(word, 0, -1);
            }
        }
        return word;
    }

    public static CharSequence normalizeYs(CharSequence word) {
        return word.toString().replace('Y', 'y');
    }

    private static final String[] EXCEPTIONAL_FORMS = {
            "skis", "ski",
            "skies", "sky",
            "dying", "die",
            "lying", "lie",
            "tying", "tie",
            "idly", "idl",
            "gently", "gentl",
            "ugly", "ugli",
            "early", "earli",
            "only", "onli",
            "singly", "singl",
            "sky", "sky",
            "news", "news",
            "howe", "howe",
            "atlas", "atlas",
            "cosmos", "cosmos",
            "bias", "bias",
            "andes", "andes",
    };

    private static final String[] EXCEPTION_EARLY_EXIT_POST_1A = {
            "inning", "outing", "canning", "herring", "earring", "proceed", "exceed", "succeed", "this"
    };

    public static CharSequence stem(CharSequence inputWord) {
        CharSequence word = capitalizeConsonantYs(inputWord.toString().toLowerCase());

        for (int i = 0; i < EXCEPTIONAL_FORMS.length; i += 2) {
            if (EXCEPTIONAL_FORMS[i].equals(word)) {
                return EXCEPTIONAL_FORMS[i + 1];
            }
        }

        final int r1 = getR1(word);
        final int r2 = getR2(word, r1);
        word = step0(word);
        word = step1a(word);

        for (String s : EXCEPTION_EARLY_EXIT_POST_1A) {
            if (s.equals(word)) {
                return word;
            }
        }

        word = step1b(word, r1);
        word = step1c(word);
//        word = step2(word, r1);
//        word = step3(word, r1, r2);
//        word = step4(word, r2);
//        word = step5(word, r1, r2);

        return normalizeYs(word);
    }
}
