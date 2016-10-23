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
    public int access_flag;

    public List<Annotation> methodAnnotations;
    public List<Annotation> paramAnnotations;

    public Method(){
        this.methodAnnotations = new ArrayList<>();
        this.paramAnnotations = new ArrayList<>();
    }

    public Method(String className,List<String> paramTypes,String returnType,String name,int access_flag){
        this(className, paramTypes, returnType, name,access_flag,null,null);
    }

    public Method(String className,List<String> paramTypes,String returnType,String name,int access_flag,List<Annotation> annotations,List<Annotation> paramAnnotations){
        this.className = className;
        this.paramTypes = paramTypes;
        this.returnType = returnType;
        this.name = name;
        this.access_flag = access_flag;
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

    public String dump(){
        StringBuilder sb = new StringBuilder();
        sb.append("method name: " + name + "\n");
        sb.append("param type: ");
        for(String paramType : paramTypes){
            sb.append(" " + paramType + " ");
        }
        sb.append("\n");
        sb.append("return type: " + returnType + "\n");
        sb.append("access flag: " + access_flag + " (see in the AccessFlag)" + "\n");
        sb.append("========method annotations data========" + "\n");
        for(Annotation annotation : methodAnnotations){
            sb.append(annotation.dump());
        }
        sb.append("========param annotations data========" + "\n");
        for(Annotation annotation : paramAnnotations){
            sb.append(annotation.dump());
        }

        return sb.toString();
    }
}
