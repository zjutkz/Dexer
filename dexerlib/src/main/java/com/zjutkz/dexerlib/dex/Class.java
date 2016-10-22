package com.zjutkz.dexerlib.dex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangzhe on 16/10/21.
 */

public class Class {

    public String class_name;
    public int access_flags;
    public String super_class_name;
    public Class super_class;
    public List<String> interfaces;
    public String source_file_name;

    public List<Field> static_fields;
    public List<Field> instance_fields;
    public List<Method> direct_methods;
    public List<Method> virtual_methods;

    public List<Annotation> annotations;

    public Class(){
        this.static_fields = new ArrayList<>();
        this.instance_fields = new ArrayList<>();
        this.direct_methods = new ArrayList<>();
        this.virtual_methods = new ArrayList<>();
        this.annotations = new ArrayList<>();
    }

    public void addAnnotation(Annotation annotation){
        if(annotations != null){
            annotations.add(annotation);
        }
    }
}


