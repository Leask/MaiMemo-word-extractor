package com.maimemo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TJT on 3/27/16.
 */
class PhraseSearchTree {

    private final Pattern REPLACEMENT_REGEX = Pattern.compile(" *(\\boneself\\b|\\bsomebody's\\b|\\bsomebody\\b|\\bsomething\\b|" +
            "\\bone's\\b|\\bdo sth\\.|\\bsb's\\b|\\bdo sth\\b|" +
            "\\bsb\\.'s\\b|\\bsb\\.|\\bsb\\b|\\bsth\\.|\\bsth\\b|\\.\\.\\.) *", Pattern.CASE_INSENSITIVE);

    private final Pattern SPLIT_SPACE_REGEX = Pattern.compile(" ");

    private final Matcher ANY_MATCHER = REPLACEMENT_REGEX.matcher("");

    private static final int BASE_SIZE = 800;

    private final CharSequenceWrapper ANY = new CharSequenceWrapper("");

    private CharSequence[] library;

    private Node root;

    private volatile boolean builded = false;

    PhraseSearchTree(CharSequence[] library) {
        this.library = library;
    }

    private boolean isAny(CharSequence word) {
        ANY_MATCHER.reset(word);
        return ANY_MATCHER.find();
    }

    private class BuilderNode {
        private int hash = 0;
        public List<CharSequence> words = new LinkedList<>();
        public List<CharSequence> array;

        @Override
        public int hashCode() {
            int h = hash;
            if (h != 0 && array.size() > 0) {
                for (CharSequence s : array) {
                    for (int i = 0; i < s.length(); i++) {
                        h = 31 * h + s.charAt(i);
                    }
                }
            }
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BuilderNode) {
                BuilderNode builderNode = (BuilderNode) obj;
                if (builderNode.array.size() != array.size()) {
                    return false;
                }
                for (int i = 0; i < array.size(); i++) {
                    if (!builderNode.array.get(i).equals(array.get(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    public void build() {
        Map<BuilderNode, BuilderNode> builderNodeSet = new HashMap<>(BASE_SIZE);

        root = new Node();
        root.children = new HashMap<>(BASE_SIZE / 4);

        for (CharSequence s : library) {
            if (TextUtils.indexOf(s, ' ', 0) == -1) {
                continue;
            }
            if (isAny(s)) {
                CharSequence[] _split = SPLIT_SPACE_REGEX.split(s);
                List<CharSequence> _split1 = new LinkedList<>();
                for (int i = 0; i < _split.length; i++) {
                    if (isAny(_split[i])) {
                        if (i != _split.length - 1) {
                            _split1.add(ANY);
                        }
                    } else {
                        _split1.add(new CharSequenceWrapper(_split[i]));
                    }
                }

                BuilderNode node = new BuilderNode();
                node.words.add(s);
                node.array = _split1;

                BuilderNode n = builderNodeSet.get(node);
                if (n != null) {
                    n.words.add(s);
                } else {
                    builderNodeSet.put(node, node);
                }
            }
        }

        for (BuilderNode n : builderNodeSet.values()) {
            addWordToTree(n);
        }

        // move any nodes of root node to children of root
        if (root.any != null) {
            for (Node node : root.any) {
                if (node.children != null) {
                    root.children.putAll(node.children);
                }
            }
        }
        root.any = null;

        builded = true;
    }

    private void addWordToTree(BuilderNode node) {
        List<CharSequence> splittedWords = node.array;
        int size = splittedWords.size();
        Node parent = root;
        Node child;
        for (int i = 0; i < size; i++) {
            child = null;
            if (parent.children != null) {
                child = parent.children.get(splittedWords.get(i));
            }
            if (child == null) {
                child = new Node();
                if (i == size - 1) {
                    child.word = node.words;
                }
                // is any
                if (splittedWords.get(i) == ANY) {
                    if (parent.any == null) {
                        parent.any = new LinkedList<>();
                    }
                    parent.any.add(child);
                } else {
                    if (parent.children == null) {
                        parent.children = new TreeMap<>();
                    }
                    parent.children.put(splittedWords.get(i), child);
                }
            }
            parent = child;
        }
    }

    public boolean isBuilded() {
        return builded;
    }

    private final SubCharSequence subCharSequence = new SubCharSequence();
    private final FastIntPairArray intPairArray = new FastIntPairArray(20);
    private final WordIterator wordIterator = new WordIterator();

    public List<CharSequence> search(CharSequence sentence) {
        wordIterator.update(sentence);
        wordIterator.setEnableSplitHyphen(false);
        wordIterator.justSplit(intPairArray);
        int size = intPairArray.size();
        if (size < 2) {
            return null;
        }
        List<CharSequence> word = null;

        for (int i = 0; i < size - 2; i++) {
            List<CharSequence> _word = _search(sentence, intPairArray, i);
            if (_word != null) {
                if (word == null) {
                    word = new LinkedList<>();
                }
                word.addAll(_word);
            }
        }

        return word;
    }

    private List<CharSequence> _search(CharSequence sentence, FastIntPairArray intPairArray, int start) {
        int size = intPairArray.size();
        List<CharSequence> word = null;
        Node node = root;
        for (int i = start; i < size; i++) {
            subCharSequence.update(sentence, intPairArray.getStart(i), intPairArray.getEnd(i));

            if (node.any != null) {
                Node nn;
                for (Node n : node.any) {
                    if (n.children != null) {
                        nn = n.children.get(subCharSequence);
                        if (nn != null) {
                            node = nn;
                            break;
                        }
                    }
                }
            }

            if (node.word != null) {
                word = node.word;
                break;
            }
            Node n = null;
            if (node.children != null) {
                n = node.children.get(subCharSequence);
            }
            if (n != null) {
                node = n;
            }
        }
        return word;
    }

    private class Node {
        public List<CharSequence> word;
        public CharSequence[] splittedWords;
        public List<Node> any;
        public Map<CharSequence, Node> children;
    }
}
