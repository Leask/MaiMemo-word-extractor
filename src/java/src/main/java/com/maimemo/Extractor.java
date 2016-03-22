package com.maimemo;

import java.util.*;

/**
 * main implement
 * <p/>
 * Created by TJT on 3/22/16.
 */
public class Extractor {

    private final CharSequence[] library;
    private Map<CharSequence, Metadata> map;

    public Extractor(CharSequence[] library) {
        this.library = library;
        buildMap();
    }

    private void buildMap() {
        map = new HashMap<CharSequence, Metadata>(library.length);
        for (CharSequence word : library) {
            Metadata metadata = new Metadata();
            metadata.word = word;
            map.put(new CharSequenceWrapper(word), metadata);
        }
    }

    public void extract(String inputText) {
        WordIterator wordIterator = new WordIterator(inputText);
        SubCharSequence subCharSequence = new SubCharSequence();
        Set<CharSequence> result = new HashSet<CharSequence>();
        while (wordIterator.nextWord(subCharSequence)) {
//            map.containsKey(subCharSequence);
            Metadata metadata = map.get(subCharSequence);
            if (metadata != null) {
                result.add(metadata.word);
            }
        }
        System.out.println(result.size());
    }


    public static class Metadata {
        public CharSequence word;
    }
}
