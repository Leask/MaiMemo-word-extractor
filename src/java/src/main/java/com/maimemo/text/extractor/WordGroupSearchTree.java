package com.maimemo.text.extractor;

import com.maimemo.text.FastIntPairArray;
import com.maimemo.text.SubCharSequence;
import com.maimemo.text.TextUtils;
import com.maimemo.text.stemmer.Stemmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word group search tree
 * Created by TJT on 9/3/16.
 */
public class WordGroupSearchTree {

    private final Pattern REPLACEMENT_REGEX = Pattern.compile(" *(\\boneself\\b|\\bsomebody's\\b|\\bsomebody\\b|\\bsomething\\b|" +
            "\\bone's\\b|\\bdo sth\\.|\\bsb's\\b|\\bdo sth\\b|" +
            "\\bsb\\.'s\\b|\\bsb\\.|\\bsb\\b|\\bsth\\.|\\bsth\\b|\\.\\.\\.) *", Pattern.CASE_INSENSITIVE);

    private final Matcher ANY_MATCHER = REPLACEMENT_REGEX.matcher("");

    private List<? extends IWord> library;

    private Node root;

    private volatile boolean builded = false;

    private boolean stemEnabled = false;

    public WordGroupSearchTree(List<? extends IWord> library) {
        this.library = library;
    }

    private boolean isAny(CharSequence word) {
        ANY_MATCHER.reset(word);
        return ANY_MATCHER.find();
    }

    public void setStemEnabled(boolean stemEnabled) {
        this.stemEnabled = stemEnabled;
    }

    public boolean isStemEnabled() {
        return stemEnabled;
    }

    public void build() {
        root = new Node();
        root.children = new HashMap<>(3200);

        FastIntPairArray pairArray = new FastIntPairArray(8);
        for (IWord voc : library) {
            TextUtils.fastSplit(voc.getWord(), ' ', pairArray);
            int size = pairArray.size();
            if (size == 0) {
                continue;
            }
            Node node = root;
            boolean prevNodeIsAny = false;
            for (int i = 0; i < size; i++) {
                SubCharSequence word = new SubCharSequence();
                word.update(voc.getWord(), pairArray.getStart(i), pairArray.getEnd(i));
                boolean isAny = isAny(word);
                // ignore starts with ANY
                if (i == 0 && isAny) {
                    continue;
                }
                if (prevNodeIsAny) {
                    Node n = null;
                    if (node.any == null) {
                        node.any = new TreeMap<>();
                    } else {
                        n = node.any.get(word);
                    }
                    if (n == null) {
                        n = new Node();
                        node.any.put(word, n);
                    }
                    node = n;
                    if (i == size - 1) {
                        if (n.word == null) {
                            n.word = new ArrayList<>();
                        }
                        n.word.add(voc);
                    }
                    prevNodeIsAny = false;
                } else if (isAny) {
                    // if last word is ANY, add word to parent node
                    if (i == size - 1) {
                        if (node.word == null) {
                            node.word = new ArrayList<>();
                        }
                        node.word.add(voc);
                    } else {
                        prevNodeIsAny = true;
                    }
                } else {
                    Node n = null;
                    if (node.children == null) {
                        node.children = new TreeMap<>();
                    } else {
                        n = node.children.get(word);
                    }
                    if (n == null) {
                        n = new Node();
                        node.children.put(word, n);
                    }
                    node = n;
                    if (i == size - 1) {
                        if (node.word == null) {
                            node.word = new ArrayList<>();
                        }
                        node.word.add(voc);
                    }
                }
            }
        }

        builded = true;
    }

    public boolean isBuilded() {
        return builded;
    }

    private List<IWord> result = new ArrayList<>(10);
    private FastIntPairArray pairArray = new FastIntPairArray(10);
    private WordIterator wordIterator = new WordIterator();

    {
        wordIterator.setEnableSplitHyphen(false);
    }

    private SubCharSequence subCharSequence = new SubCharSequence();
    private SubCharSequence stemWrapper = new SubCharSequence();

    public List<IWord> search(CharSequence sentence) {
        result.clear();
        wordIterator.update(sentence);
        wordIterator.justSplit(pairArray);
        if (pairArray.size() == 0) {
            return result;
        }
        for (int i = 0; i < pairArray.size() - 1; i++) {
            searchToResult(sentence, pairArray, i, root);
        }
        return result;
    }

    private SubCharSequence stem(SubCharSequence wordToStem) {
        if (wordToStem.length() <= 3) {
            return wordToStem;
        }
        stemWrapper.update(Stemmer.stem(wordToStem));
        return stemWrapper;
    }

    private void searchToResult(CharSequence sentence, FastIntPairArray intPairArray, int start, Node startNode) {
        subCharSequence.update(sentence, 0, 1);
        Node node = startNode;
        boolean passingAny = false;
        for (int i = start; i < intPairArray.size(); i++) {
            if (i - start == 7) {
                return;
            }
            Node n = null;
            subCharSequence.update(intPairArray.getStart(i), intPairArray.getEnd(i));

            if (passingAny) {
                n = node.any.get(subCharSequence);
                if (stemEnabled && n == null) {
                    n = node.any.get(stem(subCharSequence));
                }
                if (n != null) {
                    node = n;
                    passingAny = false;
                    if (node.word != null) {
                        result.addAll(node.word);
                    }
                }
                continue;
            }

            if (node.children != null) {
                n = node.children.get(subCharSequence);
                if (stemEnabled && n == null) {
                    n = node.children.get(stem(subCharSequence));
                }
            }
            if (n == null) {
                if (node.any == null) {
                    if (node.word != null) {
                        result.addAll(node.word);
                    }
                    return;
                } else {
                    passingAny = true;
                }
            } else {
                // resolve conflicts, i.e. 'let ... alone' and 'let me along'.
                // the algorithm will match 'let me alone' first, then detect
                // current node has same child node with next word, here it is 'alone'.
                // use this node to re-search.
                if (node.any != null && i < pairArray.size() - 1) {
                    subCharSequence.update(pairArray.getStart(i + 1), pairArray.getEnd(i + 1));
                    Node nn = node.any.get(subCharSequence);
                    if (stemEnabled && nn == null) {
                        nn = node.any.get(stem(subCharSequence));
                    }
                    if (nn != null) {
                        searchToResult(sentence, intPairArray, i + 1, nn);
                    }
                }
                node = n;
                if (node.word != null) {
                    result.addAll(node.word);
                }
            }
        }
    }

    public WordGroupSearchTree copy() {
        WordGroupSearchTree searchTree = new WordGroupSearchTree(library);
        searchTree.root = root;
        searchTree.builded = builded;
        searchTree.stemEnabled = stemEnabled;
        return searchTree;
    }

    private static class Node {
        List<IWord> word;
        Map<CharSequence, Node> any;
        Map<CharSequence, Node> children;

        @Override
        public String toString() {
            if (word != null) {
                return "FINAL: " + word.toString();
            }
            if (any != null && children == null) {
                return "ANY: " + any.keySet().toString();
            }
            if (children != null && any == null) {
                return "CHILD: " + children.keySet().toString();
            }
            return "ANY and CHILD";
        }
    }
}
