package com.zjutkz.dexerlib.dex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangzhe on 16/10/21.
 */

public class Field {

    public String type;
    public String name;
    public int access_flag;
    public List<Annotation> annotations;

    public Field(){
        this.annotations = new ArrayList<>();
    }

    public Field(String type,String name,int access_flag){
        this(type, name, access_flag, null);
    }

    public Field(String type,String name,int access_flag,List<Annotation> annotations){
        this.type = type;
        this.name = name;
        this.access_flag = access_flag;
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

    public String dump(){
        StringBuilder sb = new StringBuilder();
        sb.append("field name: " + name + "\n");
        sb.append("field type: " + type + "\n");
        sb.append("access flag: " + access_flag + " (see in the AccessFlag)" + "\n");
        sb.append("============field annotations data start============" + "\n");
        for(Annotation annotation : annotations){
            sb.append(annotation.dump());
        }
        sb.append("============field annotations data end============" + "\n");

        return sb.toString();
    }
}
