package com.zjutkz.dexerlib.dex;

/**
 * Created by kangzhe on 16/10/23.
 */

public class AccessFlag {

    public static final int PUBLIC = 1;
    public static final int PRIVATE = 2;
    public static final int PROTECTED = 4;
    public static final int STATIC = 8;
    public static final int FINAL = 16;
    public static final int SYNCHRONIZED = 32;
    public static final int VOLATILE = 64;
    public static final int BRIDGE = 64;
    public static final int TRANSIENT = 128;
    public static final int VARARGS = 128;
    public static final int NATIVE = 256;
    public static final int INTERFACE = 512;
    public static final int ABSTRACT = 1024;
    public static final int STRICT = 2048;
    public static final int SYNTHETIC = 4096;
    public static final int ANNOTATION = 8192;
    public static final int ENUM = 16384;
    public static final int CONSTRUCTOR = 65536;
    public static final int DECLARED_SYNCHRONIZED = 131072;

    public static String dump(){
        StringBuilder sb = new StringBuilder();
        sb.append("PUBLIC: " + PUBLIC + "\n");
        sb.append("PRIVATE: " + PRIVATE + "\n");
        sb.append("PROTECTED: " + PROTECTED + "\n");
        sb.append("STATIC: " + STATIC + "\n");
        sb.append("FINAL: " + FINAL + "\n");
        sb.append("SYNCHRONIZED: " + SYNCHRONIZED + "\n");
        sb.append("VOLATILE: " + VOLATILE + "\n");
        sb.append("BRIDGE: " + BRIDGE + "\n");
        sb.append("TRANSIENT: " + TRANSIENT + "\n");
        sb.append("VARARGS: " + VARARGS + "\n");
        sb.append("NATIVE: " + NATIVE + "\n");
        sb.append("INTERFACE: " + INTERFACE + "\n");
        sb.append("ABSTRACT: " + ABSTRACT + "\n");
        sb.append("STRICT: " + STRICT + "\n");
        sb.append("SYNTHETIC: " + SYNTHETIC + "\n");
        sb.append("ANNOTATION: " + ANNOTATION + "\n");
        sb.append("ENUM: " + ENUM + "\n");
        sb.append("CONSTRUCTOR: " + CONSTRUCTOR + "\n");
        sb.append("DECLARED_SYNCHRONIZED: " + DECLARED_SYNCHRONIZED + "\n");

        return sb.toString();
    }
}
