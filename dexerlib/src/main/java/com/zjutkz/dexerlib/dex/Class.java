package com.zjutkz.dexerlib.dex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangzhe on 16/10/21.
 */

public class Class {

    public static int count = 1;
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

    public String dump(){
        StringBuilder sb = new StringBuilder();
        sb.append("============" + count++ + "============" + "\n");
        sb.append("class name: " + class_name + "\n");
        sb.append("class access_flag: " + access_flags + " (see in the AccessFlag)" + "\n");
        sb.append("super class name: " + super_class_name + "\n");
        sb.append("implements: ");
        for(String interfaceName : interfaces){
            sb.append(" " + interfaceName + " ");
        }
        sb.append("\n");
        sb.append("====fields data====" + "\n");
        for(Field field : static_fields){
            sb.append("static field: " + field.dump());
        }
        for(Field field : instance_fields){
            sb.append("instance field: " + field.dump());
        }
        sb.append("====methods data====" + "\n");
        for(Method method : direct_methods){
            sb.append("direct method: " + method.dump());
        }
        for(Method method : virtual_methods){
            sb.append("virtual method: " + method.dump());
        }
        sb.append("====class annotations data====" + "\n");
        for(Annotation annotation : annotations){
            sb.append(annotation.dump());
        }
        return sb.toString();
    }
}


