package com.zjutkz.dexerlib.dex;

import java.util.List;

/**
 * Created by kangzhe on 16/10/21.
 */

public class Class {

    public int class_idx;
    public int access_flags;
    public Class super_class;
    public List<String> interfaces;
    public String annotation;

    public List<Field> fields;
    public List<Method> methods;
}


