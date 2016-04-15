package com.maimemo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        map = new HashMap<>(library.length);
        for (CharSequence word : library) {
            CharSequenceWrapper wrapper = new CharSequenceWrapper(word);
            Metadata metadata = map.get(wrapper);
            if (metadata != null) {
                metadata.alternative = wrapper;
                continue;
            } else {
                metadata = new Metadata();
            }
            metadata.word = word;
            map.put(wrapper, metadata);
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
            if (TextUtils.charSeqEquals(subCharSequence, "deal")) {
                System.currentTimeMillis();
            }
            Metadata metadata = map.get(subCharSequence);
            if (metadata != null) {
                if (metadata.alternative != null) {
                    result.add(metadata.alternative);
                }
                result.add(metadata.word);
            }
        }
        PhraseSearchTree searchTree = new PhraseSearchTree(library);
        searchTree.build();
        SentenceIterator sentenceIterator = new SentenceIterator(inputText);
        SubCharSequence subCharSequence1 = new SubCharSequence();
        while (sentenceIterator.nextSentence(subCharSequence)) {
            WordGroupIterator iterator = new WordGroupIterator();
            iterator.update(subCharSequence);
            while (iterator.nextWordGroup(subCharSequence1)) {
                Metadata metadata = map.get(subCharSequence1);
                if (metadata != null) {
                    result.add(metadata.word);
//                    System.out.println(metadata.word);
                }
            }

            CharSequence[] charSequences = searchTree.search(subCharSequence);
            if (charSequences != null) {
                for (CharSequence s : charSequences) {
                    result.add(s);
                }
            }
        }
        System.out.println(result);
    }


    public static class Metadata {
        public CharSequence word;
        public CharSequence alternative;
    }
}
