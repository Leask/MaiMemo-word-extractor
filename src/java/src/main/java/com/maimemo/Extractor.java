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

    public void buildMap() {
        map = new HashMap<CharSequence, Metadata>(library.length);
        for (CharSequence word : library) {
            Metadata metadata = new Metadata();
            metadata.word = word;
            map.put(new CharSequenceWrapper(word), metadata);
        }
        Set<CharSequence> set = new HashSet<CharSequence>();
        for (CharSequence word : library) {
            set.add(new CharSequenceWrapper(word));
        }
    }

    public void extract(String inputText) {
        WordIterator wordIterator = new WordIterator(inputText);
        SubCharSequence subCharSequence = new SubCharSequence();
        Set<CharSequence> result = new HashSet<CharSequence>();
        while (wordIterator.nextWord(subCharSequence)) {
            Metadata metadata = map.get(subCharSequence);
            if (metadata != null) {
                result.add(metadata.word);
            }
        }
        WordGroupIterator iterator = new WordGroupIterator();
        iterator.update(inputText);
        boolean stopExpand = false;
        while (iterator.nextWordGroup(stopExpand, subCharSequence)) {
            Metadata metadata = map.get(subCharSequence);
            if (metadata != null) {
                stopExpand = false;
                result.add(metadata.word);
            } else {
                stopExpand = true;
            }
        }
        System.out.println(result);
    }


    public static class Metadata {
        public CharSequence word;
    }
}
