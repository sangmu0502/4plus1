package com._plus1.domain.seed.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Csvs{
    private Csvs(){}

    // 1. Open
    public static CSVParser open(Path path) throws Exception {
        // 1). input path
        Reader reader = bomAwareReader(path);

        // 2). header 인식, header record 스킵.
        return CSVFormat.DEFAULT.builder()
                .setHeader() // 헤더 인식
                .setSkipHeaderRecord(true) // HeaderRecord skip
                .build()
                .parse(reader); // 경로.
    }

    // 2. Reader
    private static Reader bomAwareReader(Path path) throws Exception{
        // 1). IO
        InputStream input = Files.newInputStream(path);
        PushbackInputStream pushBack = new PushbackInputStream(input, 3);

        // 2). Array
        byte[] bom = new byte[3];
        int n = pushBack.read(bom, 0, 3);

        // 3). n이 3, bom[0] == 239 ?? bom[1] == 187 ?? bom[2] == 191
        if(!(n == 3 && (bom[0] == (byte)0xEF && bom[1] == (byte)0xBB && bom[2] == (byte)0xBF))) {
            // ??
            if(n>0) pushBack.unread(bom, 0, n);
        }

        // 4). return
        return new BufferedReader(new InputStreamReader(pushBack, StandardCharsets.UTF_8));
    }

    // 3. Limit
    public static boolean overLimit(int limit, int count){
        return limit > 0 && count >= limit;
    }
}