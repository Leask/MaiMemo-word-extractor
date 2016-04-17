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
        Set<Result> result = new HashSet<>();
        while (wordIterator.nextWord(subCharSequence)) {
            Metadata metadata = map.get(subCharSequence);
            if (metadata != null) {
                if (metadata.alternative != null) {
                    Result r = new Result(metadata.alternative, subCharSequence.getStart());
                    result.add(r);
                }
                result.add(new Result(metadata.word, subCharSequence.getStart()));
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
                    result.add(new Result(metadata.word, subCharSequence.getStart() + subCharSequence1.getStart()));
                }
            }

            List<CharSequence> charSequences = searchTree.search(subCharSequence);
            if (charSequences != null) {
                for (CharSequence s : charSequences) {
                    result.add(new Result(s, subCharSequence.getStart()));
                }
            }
        }
        Result[] r = new Result[result.size()];
        result.toArray(r);
        Arrays.sort(r);
    }


    public static class Metadata {
        public CharSequence word;
        public CharSequence alternative;
    }

    private static class Result implements Comparable<Result> {

        Result(CharSequence word, int index) {
            this.word = word;
            this.index = index;
        }

        public CharSequence word;
        public int index;

        @Override
        public int compareTo(Result o) {
            return index - o.index;
        }

        @Override
        public String toString() {
            return word + "@" + index;
        }

        @Override
        public int hashCode() {
            return word.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Result) {
                Result result = (Result) obj;
                return word.equals(result.word);
            }
            return false;
        }
    }
}
