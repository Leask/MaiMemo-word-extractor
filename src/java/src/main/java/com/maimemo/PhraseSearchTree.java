package com.maimemo;

import java.util.ArrayList;
import java.util.List;
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

    private static final int BASE_SIZE = 800;

    private CharSequence[] library;

    private List<Node> splits = new ArrayList<>(BASE_SIZE);

    private volatile boolean builded = false;

    private List<CharSequence> tempResults = new ArrayList<>(4);

    PhraseSearchTree(CharSequence[] library) {
        this.library = library;
    }

    public void build() {
        for (CharSequence s : library) {
            if (TextUtils.indexOf(s, ' ', 0) == -1) {
                continue;
            }
            for (String pronoun : PRONOUNS) {
                if (TextUtils.standaloneIndexOf(s, pronoun, 0) != -1) {
                    Node note = new Node();
                    note.word = s;
                    note.splits = REPLACEMENT_REGEX.split(s);
                    splits.add(note);
                    break;
                }
            }
        }
        builded = true;
    }

    public boolean isBuilded() {
        return builded;
    }

    public CharSequence[] search(CharSequence sentence) {
        tempResults.clear();
        int start = 0;
        boolean searched;
        for (Node n : splits) {
            searched = true;
            start = 0;
            for (int i = 0; i < n.splits.length; i++) {
                start = TextUtils.standaloneIndexOf(sentence, n.splits[i], start + 1);
                if (start == -1) {
                    searched = false;
                    break;
                }
            }
            if (searched) {
                tempResults.add(n.word);
            }
        }
        if (tempResults.size() == 0) {
            return null;
        }
        CharSequence[] array = new CharSequence[tempResults.size()];
        tempResults.toArray(array);
        return array;
    }

    private class Node {
        public CharSequence word;
        public String[] splits;
    }
}
