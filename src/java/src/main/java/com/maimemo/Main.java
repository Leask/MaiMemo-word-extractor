package com.maimemo;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by TJT on 3/21/16.
 */
public class Main {

    public static final String LIBRARY = "/Users/TJT/all_vocs.txt";

    public static final String TEXT = "/Users/TJT/groups.txt";

    public static void main(String[] args) throws IOException {
        List<String> libraryList = Files.readLines(new File(LIBRARY), Charsets.UTF_8);
        String[] library = new String[libraryList.size()];
        libraryList.toArray(library);
        String alice = Files.toString(new File(TEXT), Charsets.UTF_8);
//        String alice = "make a sound\n" +
//                "make a stab at ...\n" +
//                "make a telephone call";
        Extractor extractor = new Extractor(library);
        extractor.buildMap();
        Extractor.Result[] results = extractor.extract(alice);
        for (Extractor.Result result : results) {
            System.out.println(result.word);
        }
    }
}
