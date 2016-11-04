package com.maimemo.text.stemmer;/*
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

import java.lang.ref.WeakReference;
import java.util.Map;

import static com.maimemo.text.stemmer.Porter2.endsWith;

/**
 * A com.maimemo.stemmer.Stemmer wrap The com.maimemo.stemmer.Porter2 stemmer
 * Created by TJT on 7/19/16.
 */
public class Stemmer {

    private static WeakReference<Map<CharSequence, CharSequence>> stemCache;

    /**
     * <b>notice</b>: the Stemmer only retains the weak reference of cache, you should has strong reference.
     *
     * @param cache cache
     */
    public static void installStemCache(Map<CharSequence, CharSequence> cache) {
        stemCache = new WeakReference<>(cache);
    }

    public static void uninstallStemCache() {
        if (stemCache != null) {
            stemCache.clear();
            stemCache = null;
        }
    }

    public static CharSequence stem(CharSequence word) {
        Map<CharSequence, CharSequence> cache = null;
        if (stemCache != null) {
            cache = stemCache.get();
        }
        CharSequence stemmedWord = null;
        if (cache != null) {
            stemmedWord = cache.get(word);
        }
        if (stemmedWord == null) {
            stemmedWord = Porter2.stem(word);
            if (cache != null) {
                cache.put(word, stemmedWord);
            }
        }
        return stemmedWord;
    }

    public static CharSequence normalize(CharSequence word, CharSequence origin) {
        if (endsWith(origin, "y") && endsWith(word, "i")) {
            return Porter2.substring(word, 0, word.length() - 1) + "y";
        }
        return word;
    }

    public static boolean matchWithOrigin(CharSequence wordToStem, CharSequence origin) {
        CharSequence stemmedWord = stem(wordToStem);
        if (wordToStem.equals(stemmedWord)) {
            return false;
        }
        if (origin.equals(stemmedWord + "e")) {
            return true;
        }
        return normalize(stemmedWord, origin).equals(origin);
    }

    public static boolean isOriginTense(CharSequence word) {
        final int length = word.length();
        // past tense
        if (endsWith(word, "ed") && !endsWith(word, "eed")) {
            return false;
        }
        // present continuous
        if (length > 4 && endsWith(word, "ing")) {
            return false;
        }
        // plural
        if (length > 3) {
            if (endsWith(word, "ses")) {
                return false;
            }
            char c = word.charAt(length - 2);
            if (endsWith(word, "s")
                    && c != 'e'
                    && c != 's'
                    && c != 'a'
                    && c != 'i'
                    && c != 'o'
                    && c != 'u') {
                return false;
            }
        }
        return true;
    }
}
