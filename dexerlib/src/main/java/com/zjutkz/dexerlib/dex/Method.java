package com.zjutkz.dexerlib.dex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangzhe on 16/10/21.
 */

public class Method {

    public String className;
    public List<String> paramTypes;
    public String returnType;
    public String name;

    public List<Annotation> methodAnnotations;
    public List<Annotation> paramAnnotations;

    public Method(){
        this.methodAnnotations = new ArrayList<>();
        this.paramAnnotations = new ArrayList<>();
    }

    public Method(String className,List<String> paramTypes,String returnType,String name){
        this(className, paramTypes, returnType, name,null,null);
    }

    public Method(String className,List<String> paramTypes,String returnType,String name,List<Annotation> annotations,List<Annotation> paramAnnotations){
        this.className = className;
        this.paramTypes = paramTypes;
        this.returnType = returnType;
        this.name = name;
        if(annotations == null){
            this.methodAnnotations = new ArrayList<>();
            this.paramAnnotations = new ArrayList<>();
        }else{
            this.methodAnnotations = annotations;
            this.paramAnnotations = paramAnnotations;
        }
    }

    public void addMethodAnnotation(Annotation annotation){
        if(methodAnnotations != null){
            methodAnnotations.add(annotation);
        }
    }

    public void addParamAnnotation(Annotation annotation){
        if(paramAnnotations != null){
            paramAnnotations.add(annotation);
        }
    }
}
