package com.maimemo;

/**
 * 遍历所有词组
 * <p/>
 * Created by TJT on 3/21/16.
 */
public class WordGroupIterator {

    private static final int MINIMUM_WORD_GROUP_SIZE = 1;

    private CharSequence text;
    private int currentPos = -1;
    private int currentSize = MINIMUM_WORD_GROUP_SIZE;
    private FastIntPairArray splitResult = new FastIntPairArray(3);
    private int size;

    public WordGroupIterator() {
    }

    public void update(CharSequence newText) {
        text = newText;
        currentPos = -1;
        TextUtils.fastSplit(newText, ' ', splitResult);
        size = splitResult.size();
    }

    /**
     * @param stopExpand 是否停止增加词组长度，比如 alice in -> alice in wonderland
     */
    public boolean nextWordGroup(boolean stopExpand, SubCharSequence outValue) {
        if (size < 2) {
            return false;
        }
        if (currentPos + currentSize >= size || stopExpand) {
            currentSize = MINIMUM_WORD_GROUP_SIZE;
            currentPos++;
        }
        if (currentPos == size - 1) {
            return false;
        }
        if (currentPos == -1) {
            currentPos = 0;
        }
        outValue.update(text, splitResult.getStart(currentPos), splitResult.getEnd(currentPos + currentSize));
        currentSize++;
        return true;
    }

}
