package com.maimemo.text;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by TJT on 11/4/16.
 */
public class FastIntPairArrayTest {
    @Test
    public void add() throws Exception {
        FastIntPairArray array = new FastIntPairArray(4);
        assertEquals(0, array.size());
        array.add(1, 2);
        assertEquals(1, array.size());
        array.add(2, 3);
        array.add(2, 3);
        array.add(2, 3);
        array.add(2, 3);
        array.add(2, 3);
        assertEquals(6, array.size());
    }

    @Test
    public void setStart() throws Exception {
        FastIntPairArray array = new FastIntPairArray(4);
        array.add(1, 2);
        array.setStart(0, 0);
        assertEquals(0, array.getStart(0));
    }

    @Test
    public void getEnd() throws Exception {
        FastIntPairArray array = new FastIntPairArray(4);
        assertEquals(0, array.size());
    }

    @Test
    public void setEnd() throws Exception {

    }

    @Test
    public void clear() throws Exception {

    }

    @Test
    public void remove() throws Exception {

    }

    @Test
    public void pop() throws Exception {

    }

    @Test
    public void size() throws Exception {

    }

}