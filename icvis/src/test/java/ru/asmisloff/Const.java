package ru.asmisloff;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Const {

    public static final ObjectMapper om = new ObjectMapper();

    public static String fromTestRoot(String path) {
        return "./src/test/java/ru/asmisloff/" + path;
    }
}
