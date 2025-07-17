package com.myapp.parser;


import java.nio.file.Path;

public abstract class AbstractMdParser<T> {
    public abstract T parse(Path mdFile);
}