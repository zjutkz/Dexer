package com.zjutkz.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zjutkz.dexerlib.Dexer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Sample";

    private Dexer dexer;

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
                List<String> allClasses = (List<String>)data;
                for(String className : allClasses){
                    Log.d(TAG, className);
                }
            }
        });
    }

    public void get_all_methods(View view){
        dexer.getAllMethods(new Dexer.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(Object data) {
                List<String> allMethods = (List<String>)data;
                for(String methodName : allMethods){
                    Log.d(TAG, methodName);
                }
            }
        });
    }

    public void get_all_methods_in_class(View view){
        dexer.getAllMethodsInClass("MainActivity",new Dexer.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(Object data) {
                List<String> allMethods = (List<String>)data;
                for(String methodName : allMethods){
                    Log.d(TAG, methodName);
                }
            }
        });
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
