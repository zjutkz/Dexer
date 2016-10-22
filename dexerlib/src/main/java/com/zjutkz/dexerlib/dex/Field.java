package com.zjutkz.dexerlib.dex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangzhe on 16/10/21.
 */

public class Field {

    public String type;
    public String name;
    public List<Annotation> annotations;

    public Field(){
        this.annotations = new ArrayList<>();
    }

    public Field(String type,String name){
        this(type, name,null);
    }

    public Field(String type,String name,List<Annotation> annotations){
        this.type = type;
        this.name = name;
        if(annotations == null){
            annotations = new ArrayList<>();
        }else{
            this.annotations = annotations;
        }
    }

    public void addAnnotation(Annotation annotation){
        if(annotations != null){
            annotations.add(annotation);
        }
    }
}
