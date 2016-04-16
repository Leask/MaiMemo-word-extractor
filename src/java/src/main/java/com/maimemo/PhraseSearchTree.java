package com.maimemo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TJT on 3/27/16.
 */
class PhraseSearchTree {

    private static final String[] PRONOUNS = new String[]{
            "do sth.", "do sth",
            "sb.'s", "sth.", "sb.",
            "sth", "sb", "...",
            "one's", "somebody's",
            "somebody", "something"
    };

    private final Pattern REPLACEMENT_REGEX = Pattern.compile(" *(\\bsomebody's\\b|\\bsomebody\\b|\\bsomething\\b|" +
            "\\bone's\\b|\\bdo sth\\.|\\bsb's\\b|\\bdo sth\\b|" +
            "\\bsb\\.'s\\b|\\bsb\\.|\\bsb\\b|\\bsth\\.|\\bsth\\b|\\.\\.\\.) *", Pattern.CASE_INSENSITIVE);

    private final Matcher matcher = REPLACEMENT_REGEX.matcher("");

    private static final int BASE_SIZE = 800;

    private final Node ANY = new Node();

    private CharSequence[] library;

    private Node root;

    private volatile boolean builded = false;

    private List<CharSequence> tempResults = new ArrayList<>(4);

    PhraseSearchTree(CharSequence[] library) {
        this.library = library;
    }

    public void build() {
        Pattern splitSpace = Pattern.compile(" ");
        List<Node> splits = new ArrayList<>(BASE_SIZE);
        for (CharSequence s : library) {
            if (TextUtils.indexOf(s, ' ', 0) == -1) {
                continue;
            }
            for (String pronoun : PRONOUNS) {
                if (TextUtils.standaloneIndexOf(s, pronoun, 0) != -1) {
                    System.out.println(s);
                    Node note = new Node();
                    note.word = s;
                    String[] words = splitSpace.split(s);
                    CharSequence[] wordsWrapper = new CharSequence[words.length];
                    for (int i = 0; i < words.length; i++) {
                        wordsWrapper[i] = new CharSequenceWrapper(words[i]);
                    }
                    note.splittedWords = wordsWrapper;
                    splits.add(note);
                    break;
                }
            }
        }

        root = new Node();
        root.children = new HashMap<>(BASE_SIZE / 2);

        for (Node node : splits) {
            addNodeToTree(node);
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

    private void addNodeToTree(Node node) {
        Node parent = root;
        Node child;
        for (int i = 0; i < node.splittedWords.length; i++) {
            child = null;
            if (parent.children != null) {
                child = parent.children.get(node.splittedWords[i]);
            }
            if (child == null) {
                if (parent.children == null) {
                    parent.children = new TreeMap<>();
                }
                child = new Node();
                if (i == node.splittedWords.length - 1) {
                    matcher.reset(node.splittedWords[i]);
                    if (matcher.find()) {
                        // if the last word is any, just ignore it
                        parent.word = node.word;
                        break;
                    }
                    child.word = node.word;
                }
                matcher.reset(node.splittedWords[i]);
                // is any
                if (matcher.find()) {
                    if (parent.any == null) {
                        parent.any = new ArrayList<>(2);
                    }
                    parent.any.add(child);
                } else {
                    parent.children.put(node.splittedWords[i], child);
                }
            }
            parent = child;
        }
    }

    public boolean isBuilded() {
        return builded;
    }

    private final SubCharSequence subCharSequence = new SubCharSequence();
    private final WordIterator wordIterator = new WordIterator();

    public CharSequence[] search(CharSequence sentence) {
        wordIterator.update(sentence);
        Node node = root;
        while (wordIterator.nextWord(subCharSequence)) {
        }
        return null;
    }

    private class Node {
        public CharSequence word;
        public CharSequence[] splittedWords;
        public List<Node> any;
        public Map<CharSequence, Node> children;
    }
}
