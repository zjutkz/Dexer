package com.zjutkz.sample;

import android.util.Log;

/**
 * Created by kangzhe on 16/10/21.
 */

public class TestClass {

    private static final String TAG = "TestClass";
    
    public String str1;
    public String str2;
    public String str3;
    public int int1;
    public short short1;
    
    private static String private_static_str1;
    public static int public_static_int1;
    public static final String public_static_final_str2 = "DEF";
    
    public static void public_static_void_method(){
        Log.d(TAG, "public_static_void_method");
    }
    
    private String private_string_method(){
        return "private_string_method";
    }
    
    public final void public_final_void_method(){
        Log.d(TAG, "public_final");
    }

    private void private_void_param_method(String param1,int param2){
        Log.d(TAG, "private_void_param_method");
    }

    static class InnerClass {
        private static final String TAG = "InnerClass";

        public static String inner_public_static_str1;
        private int inner_private_int1;

        public static final String inner_public_static_final = "INNER_DEF";

        private static void inner_private_static_method(){
            Log.d(TAG, "inner_private_static_method: ");
        }

        public static class DoubleInnerClass {
            public static String double_inner_public_static_str1;
        }
    }
}
