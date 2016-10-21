package com.zjutkz.dexerlib.dex;

import java.util.List;

/**
 * Created by kangzhe on 16/10/21.
 */

public class Method {

    public String className;
    public List<String> paramTypes;
    public String returnType;
    public String name;

    public Method(){

    }

    public Method(String className,List<String> paramTypes,String returnType,String name){
        this.className = className;
        this.paramTypes = paramTypes;
        this.returnType = returnType;
        this.name = name;
    }
}
