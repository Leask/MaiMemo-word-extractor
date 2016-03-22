package com.maimemo;

/**
 * Created by TJT on 3/22/16.
 */
public class CharUtils {

    public static char simpleToLower(char c) {
        if (c > 64 && c <= 90) {
            return (char) (c + 32);
        }
        return c;
    }

}
