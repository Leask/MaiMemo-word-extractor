package com.maimemo.text.extractor;

/**
 * Implement to put vocabulary in custom class
 * Created by TJT on 10/23/16.
 */
public abstract class IWord {
    public String tag;
    public int order;
    public abstract CharSequence getWord();
    public abstract IWord copy();
}
