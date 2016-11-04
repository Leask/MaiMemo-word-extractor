package com.maimemo.text.extractor;

import com.maimemo.text.FastIntPairArray;
import com.maimemo.text.SubCharSequence;
import com.maimemo.text.TextUtils;
import com.maimemo.text.stemmer.IrregularWordTable;
import com.maimemo.text.stemmer.Stemmer;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * main implement
 * <p>
 * Created by TJT on 3/22/16.
 */
public class WordExtractor<T extends IWord> {

    public interface TagFormatter {
        String format(int tagIndex);
    }

    private final List<? extends IWord> library;
    private static SoftReference<HashMap<CharSequence, Item>> sMap;
    private HashMap<CharSequence, Item> map;
    private static SoftReference<WordGroupSearchTree> sSearchTree;
    private WordGroupSearchTree searchTree;
    private boolean stemEnabled = false;
    private TagFormatter tagFormatter = new TagFormatter() {
        @Override
        public String format(int tagIndex) {
            return "章节" + tagIndex;
        }
    };

    public String tagWord = "单词列表";
    public String tagWordGroup = "短语列表";

    public WordExtractor(List<? extends IWord> library) {
        this.library = library;
        Map<CharSequence, CharSequence> cache = new HashMap<>();
        Stemmer.installStemCache(cache);
    }

    public void setStemEnabled(boolean stemEnabled) {
        this.stemEnabled = stemEnabled;
        if (searchTree != null) {
            searchTree.setStemEnabled(stemEnabled);
        }
    }

    public boolean isStemEnabled() {
        return stemEnabled;
    }

    public void setTagFormatter(TagFormatter tagFormatter) {
        this.tagFormatter = tagFormatter;
    }

    public void buildMap() {
        if (sMap != null) {
            map = sMap.get();
        }
        if (map == null) {
            map = new HashMap<>(library.size());
            for (IWord word : library) {
                SubCharSequence wrapper = new SubCharSequence(word.getWord());
                Item item = map.get(wrapper);
                if (item != null) {
                    item.alternative = word;
                    continue;
                } else {
                    item = new Item();
                }
                item.word = word;
                map.put(wrapper, item);
            }
            sMap = new SoftReference<>(map);
        }
        if (sSearchTree != null) {
            searchTree = sSearchTree.get();
        }
        if (searchTree == null) {
            searchTree = new WordGroupSearchTree(library);
            searchTree.build();
            sSearchTree = new SoftReference<>(searchTree);
        }
    }

    public List<T> extractByLine(String inputText) {
        SubCharSequence substr = new SubCharSequence();
        FastIntPairArray split = TextUtils.fastSplit(inputText, '\n');
        String lastSection = null;
        List<IWord> result = new ArrayList<>();
        int chapterIndex = 1;
        for (int i = 0; i < split.size(); i++) {
            substr.update(inputText, split.getStart(i), split.getEnd(i));
            if (substr.length() == 0) {
                continue;
            }
            if (substr.charAt(0) == '#') {
                String chapter;
                if (substr.length() == 1) {
                    chapter = tagFormatter.format(chapterIndex++);
                } else {
                    chapter = substr.subSequence(1, substr.length()).toString();
                }
                lastSection = chapter;
            } else {
                Item item = map.get(substr);
                if (item != null) {
                    IWord copy = item.word.copy();
                    copy.tag = lastSection;
                    result.add(copy);
                    if (item.alternative != null) {
                        copy = item.alternative.copy();
                        copy.tag = lastSection;
                        result.add(copy);
                    }
                }
            }
        }
        //noinspection unchecked
        return (List<T>) result;
    }

    private void addResultToList(Set<Result> set, Item m, int position, String tag) {
        if (m != null) {
            IWord copy = m.word.copy();
            copy.tag = tag;
            copy.order = position;
            set.add(new Result(copy, position));
            if (m.alternative != null) {
                copy = m.alternative.copy();
                copy.tag = tag;
                copy.order = position;
                Result r = new Result(copy, position);
                set.add(r);
            }
        }
    }

    public List<T> extract(String inputText) {
        if (inputText.startsWith("#")) {
            return extractByLine(inputText);
        }
        setStemEnabled(inputText.startsWith("//"));

        int maxStart = 0;
        WordIterator wordIterator = new WordIterator(inputText);
        Set<Result> result = new HashSet<>();
        SubCharSequence stemWrapper = new SubCharSequence();
        for (SubCharSequence word : wordIterator) {
            Item metadata;
            if (stemEnabled) {
                CharSequence s = IrregularWordTable.get(word);
                if (s != null) {
                    stemWrapper.update(s);
                    metadata = map.get(stemWrapper);
                } else {
                    if (!Stemmer.isOriginTense(word)) {
                        stemWrapper.update(Stemmer.stem(word));
                        metadata = map.get(stemWrapper);
                        if (metadata == null) {
                            metadata = map.get(word);
                        }
                    } else {
                        metadata = map.get(word);
                    }
                }
            } else {
                metadata = map.get(word);
            }
            maxStart = word.getStart();
            addResultToList(result, metadata, maxStart, tagWord);
        }

        // waiting building phrase search tree
        while (!searchTree.isBuilded()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SentenceIterator sentenceIterator = new SentenceIterator(inputText);
        for (SubCharSequence sentence : sentenceIterator) {
            List<IWord> charSequences = searchTree.search(sentence);
            if (charSequences != null) {
                for (IWord s : charSequences) {
                    IWord copy = s.copy();
                    copy.order = maxStart + sentence.getStart();
                    copy.tag = tagWordGroup;
                    result.add(new Result(copy, copy.order));
                }
            }
        }
        Result[] r = new Result[result.size()];
        result.toArray(r);
        Arrays.sort(r);
        List<IWord> finalResult = new ArrayList<>(r.length);
        for (int i = 0; i < r.length; i++) {
            finalResult.add(r[i].word);
        }
        //noinspection unchecked
        return (List<T>) finalResult;
    }

    public WordExtractor<T> copy() {
        WordExtractor<T> copy = new WordExtractor<>(library);
        if (map == null || searchTree == null) {
            throw new IllegalStateException("only allow copy from a builded extractor");
        }
        copy.map = map;
        copy.searchTree = searchTree.copy();
        return copy;
    }

    private static class Result implements Comparable<Result> {

        Result(IWord word, int index) {
            this.word = word;
            this.index = index;
        }

        public IWord word;
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
            return word.getWord().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Result) {
                Result result = (Result) obj;
                return word.getWord().equals(result.word.getWord());
            }
            return false;
        }
    }

    static class Item {
        public CharSequence vocabulary;
        public IWord word;
        public IWord alternative;
    }
}
