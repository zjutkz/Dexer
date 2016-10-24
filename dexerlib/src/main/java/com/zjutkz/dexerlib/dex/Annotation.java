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

    public String dump(){
        StringBuilder sb = new StringBuilder();
        sb.append("annotation name: " + name + "\n");
        sb.append("annotation visibility: " + visibility + "\n");
        sb.append("============annotation elements data start============" + "\n");
        for(AnnotationElement element : elements){
            sb.append(element.dump());
        }
        sb.append("============annotation elements data end============" + "\n");

        return sb.toString();
    }
}
