package com.zjutkz.dexerlib.dex;

import java.util.List;

/**
 * Created by kangzhe on 16/10/21.
 */

public class Method {

    public List<String> paramTypes;
    public String returnType;
    public String name;

    public Method(List<String> paramTypes,String returnType,String name){
        this.paramTypes = paramTypes;
        this.returnType = returnType;
        this.name = name;
    }
}
