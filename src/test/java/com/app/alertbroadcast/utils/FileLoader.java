package com.app.alertbroadcast.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileLoader {

    @SneakyThrows
    public static String load(String path) {
        InputStream inputStream = Objects.requireNonNull(FileLoader.class.getClassLoader().getResourceAsStream(path),
                () -> "resource not found under path: " + path);
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}
