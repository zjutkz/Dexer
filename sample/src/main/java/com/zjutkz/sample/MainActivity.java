package com.zjutkz.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zjutkz.dexerlib.Dexer;
import com.zjutkz.dexerlib.dex.Annotation;
import com.zjutkz.dexerlib.dex.AnnotationElement;
import com.zjutkz.dexerlib.dex.Class;
import com.zjutkz.dexerlib.dex.Field;
import com.zjutkz.dexerlib.dex.Method;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Sample";

    private Dexer dexer;

    @TestAnnotation(2)
    @NonNull
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void decode_dex(View view){
        try {
            byte[] src = stream2byte(getAssets().open("classes.dex"));
            dexer = new Dexer(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void get_all_classes(View view){
        dexer.getAllClasses(new Dexer.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(Object data) {
                List<Class> allClasses = (List<Class>)data;
                for(Class clz : allClasses){
                    Log.d(TAG, clz.source_file_name);
                }
            }
        });
    }

    public void get_all_methods(View view){
        dexer.getAllMethods(new Dexer.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(Object data) {
                List<Method> allMethods = (List<Method>)data;
                for(Method method : allMethods){
                    Log.d(TAG, method.name);
                }
            }
        });
    }

    public void get_all_methods_in_class(View view){
        dexer.getAllMethodsInClass("MainActivity",new Dexer.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(Object data) {
                List<Method> allMethods = (List<Method>)data;
                for(Method method : allMethods){
                    Log.d(TAG, method.name);
                }
            }
        });
    }

    public void has_method_and_class(View view){
        dexer.hasClass("MainActivity", new Dexer.OnFetchDataListener<Boolean>() {
            @Override
            public void onFetchDataSuccess(Boolean data) {
                Log.d(TAG, "dex file has MainActivity: " + (boolean)data);
            }
        });

        dexer.hasClass("MainActivity2", new Dexer.OnFetchDataListener<Boolean>() {
            @Override
            public void onFetchDataSuccess(Boolean data) {
                Log.d(TAG, "dex file has MainActivity2: " + data);
            }
        });

        dexer.hasMethods("MainActivity", "get_all_methods_in_class", new Dexer.OnFetchDataListener<Boolean>() {
            @Override
            public void onFetchDataSuccess(Boolean data) {
                Log.d(TAG, "MainActivity has method get_all_methods_in_class: " + data);
            }
        });

        dexer.hasMethods("MainActivity", "get_all_methods_in_class2", new Dexer.OnFetchDataListener<Boolean>() {
            @Override
            public void onFetchDataSuccess(Boolean data) {
                Log.d(TAG, "MainActivity has method get_all_methods_in_class2: " + data);
            }
        });
    }

    public void get_class(View view){
        dexer.getClass("TestClass", new Dexer.OnFetchDataListener<Class>() {
            @Override
            public void onFetchDataSuccess(Class data) {
                Log.d(TAG, data.class_name + " " + data.super_class_name);
                for(Field field : data.instance_fields){
                    Log.d(TAG, "instance field: " + field.name + " " + field.access_flag);
                }
                for(Field field : data.static_fields){
                    Log.d(TAG, "static field: " + field.name + " " + field.access_flag);
                }
                Log.d(TAG, "" + data.access_flags);
                Log.d(TAG, "=========================================");
            }
        });

        dexer.getClass("TestClass$InnerClass", new Dexer.OnFetchDataListener<Class>() {
            @Override
            public void onFetchDataSuccess(Class data) {
                for(Field field : data.instance_fields){
                    Log.d(TAG, "instance field: " + field.name);
                }
                for(Field field : data.static_fields){
                    Log.d(TAG, "static field: " + field.name);
                }
                Log.d(TAG, "" + data.access_flags);
                Log.d(TAG, "=========================================");
            }
        });

        dexer.getClass("TestClass$InnerClass$DoubleInnerClass", new Dexer.OnFetchDataListener<Class>() {
            @Override
            public void onFetchDataSuccess(Class data) {
                for(Field field : data.instance_fields){
                    Log.d(TAG, "instance field: " + field.name);
                }
                for(Field field : data.static_fields){
                    Log.d(TAG, "static field: " + field.name);
                }
                Log.d(TAG, "" + data.access_flags);
                Log.d(TAG, "=========================================");
            }
        });
    }

    public void get_method(View view){
        dexer.getMethod("MainActivity", "onCreate", new Dexer.OnFetchDataListener<Method>() {
            @Override
            public void onFetchDataSuccess(Method data) {
                Log.d(TAG, data.name);
                Log.d(TAG, "" + data.access_flag);
                Log.d(TAG, data.returnType);
                for(String type : data.paramTypes){
                    Log.d(TAG, type);
                }
                for(Annotation annotation : data.methodAnnotations){
                    Log.d(TAG, annotation.name);
                    for(AnnotationElement element : annotation.elements){
                        Log.d(TAG, element.name + ": " + element.value);
                    }
                }
            }
        });
    }

    public void get_count(View view){
        dexer.getClassCount(new Dexer.OnFetchDataListener<Integer>() {
            @Override
            public void onFetchDataSuccess(Integer data) {
                Log.d(TAG, "classes count: " + data);
            }
        });

        dexer.getMethodCount(new Dexer.OnFetchDataListener<Integer>() {
            @Override
            public void onFetchDataSuccess(Integer data) {
                Log.d(TAG, "methods count: " + data);
            }
        });
    }

    public void dump(View view){
        dexer.dumpDex("storage/sdcard0/dump.txt");
    }
    
    private byte[] stream2byte(InputStream src) throws IOException {
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = src.read(buffer))) {
            dest.write(buffer, 0, n);
        }
        return dest.toByteArray();
    }
}
