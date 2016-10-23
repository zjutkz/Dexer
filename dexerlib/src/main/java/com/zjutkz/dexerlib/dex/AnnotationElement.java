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
        if(value instanceof Field){
            sb.append("annotation element value: " + ((Field) value).name + "\n");
        }else if(value instanceof Object[]){
            for(Object obj : (Object[])value){
                if(obj instanceof Field){
                    sb.append("annotation element value: " + ((Field) value).name + "\n");
                }else{
                    sb.append("annotation element value: " + value + "\n");
                }
            }
        }else{
            sb.append("annotation element value: " + value + "\n");
        }


        return sb.toString();
    }
}
