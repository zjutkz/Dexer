package com.zjutkz.dexerlib.dex;

/**
 * Created by kangzhe on 16/10/22.
 */

public class AnnotationElement {

    public String name;
    public Object value;

    public String dump(){
        StringBuilder sb = new StringBuilder();
        sb.append("annotation element name: " + name + "\n");
        sb.append("annotation element value: " + value + "\n");

        return sb.toString();
    }
}
