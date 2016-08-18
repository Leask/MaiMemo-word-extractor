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
    private PhraseSearchTree searchTree;

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
        searchTree = new PhraseSearchTree(library);
        searchTree.build();
    }

    public Result[] extract(String inputText) {
        WordIterator wordIterator = new WordIterator(inputText);
        Set<Result> result = new HashSet<>();
        for (SubCharSequence word : wordIterator) {
            Metadata metadata = map.get(word);
            if (metadata != null) {
                if (metadata.alternative != null) {
                    Result r = new Result(metadata.alternative, word.getStart());
                    result.add(r);
                }
                result.add(new Result(metadata.word, word.getStart()));
            }
        }
        SentenceIterator sentenceIterator = new SentenceIterator(inputText);
        for (SubCharSequence sentence : sentenceIterator) {
            WordGroupIterator iterator = new WordGroupIterator();
            iterator.update(sentence);
            for (SubCharSequence wordGroup : iterator) {
                Metadata metadata = map.get(wordGroup);
                if (metadata != null) {
                    result.add(new Result(metadata.word, sentence.getStart() + wordGroup.getStart()));
                }
            }

            List<CharSequence> charSequences = searchTree.search(sentence);
            Metadata info = map.get(sentence);
            if (info != null) {
                result.add(new Result(info.word, sentence.getStart()));
            }
            if (charSequences != null) {
                for (CharSequence s : charSequences) {
                    result.add(new Result(s, sentence.getStart()));
                }
            }
        }
        Result[] r = new Result[result.size()];
        result.toArray(r);
        Arrays.sort(r);
        return r;
    }


    private static class Metadata {
        CharSequence word;
        CharSequence alternative;
    }

    static class Result implements Comparable<Result> {

        Result(CharSequence word, int index) {
            this.word = word;
            this.index = index;
        }

        CharSequence word;
        int index;

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
