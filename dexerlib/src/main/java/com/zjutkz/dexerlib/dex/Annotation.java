package com.zjutkz.dexerlib.dex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangzhe on 16/10/22.
 */

public class Annotation {

    public int visibility;
    public String name;
    public List<AnnotationElement> elements;

    public Annotation(){
        elements = new ArrayList<>();
    }

    public void addElement(AnnotationElement element){
        if(elements != null){
            elements.add(element);
        }
    }
}
