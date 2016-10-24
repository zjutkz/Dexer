package com.zjutkz.dexerlib;

import android.content.Context;
import android.os.Handler;

import com.zjutkz.dexerlib.dex.Class;
import com.zjutkz.dexerlib.dex.Method;
import com.zjutkz.dexerlib.task.DecodeTask;
import com.zjutkz.dexerlib.task.Task;
import com.zjutkz.dexerlib.thread.DecodeThread;
import com.zjutkz.dexerlib.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by kangzhe on 16/10/20.
 */

public class Dexer {

    private static final String TAG = "Dexer";

    private static final String DECODE_THREAD = "decode_thread";

    private DecodeThread mDecodeThread;
    private Handler mDecodeHandler;

    private Context context;

    public interface OnFetchDataListener<T>{
        void onFetchDataSuccess(T data);
    }

    public Dexer(File file){
        this(file.getAbsolutePath());
    }

    public Dexer(String dexPath){
        this(FileUtil.getBytes(dexPath));
    }

    public Dexer(byte[] src){
        initThread();
        decodeDex(src);
    }

    private void decodeDex(byte[] src) {
        DecodeTask task = new DecodeTask();
        task.setSrc(src);
        postTask(task);
    }

    private void initThread() {
        if(mDecodeThread == null){
            mDecodeThread = new DecodeThread(DECODE_THREAD);
            mDecodeHandler = getDecodeHandler();
        }
    }

    private Handler getDecodeHandler(){
        if(mDecodeThread != null){
            return mDecodeThread.getHandler();
        }

        return null;
    }

    private void postTask(final Task task) {
        if (mDecodeThread.isDecodeThreadAlive() && mDecodeHandler != null) {
            mDecodeHandler.post(new Runnable() {
                @Override
                public void run() {
                    task.doTask();
                }
            });
        } else {
            task.doTask();
        }
    }

    public void getAllMethods(final OnFetchDataListener<List<Method>> listener){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                if(listener != null){
                    listener.onFetchDataSuccess(DecodeTask.getAllMethods());
                }
            }
        });
    }

    public void getAllClasses(final OnFetchDataListener<List<Class>> listener){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                if(listener != null){
                    listener.onFetchDataSuccess(DecodeTask.getAllClasses());
                }
            }
        });
    }

    public void getAllMethodsInClass(final String clzName,final OnFetchDataListener<List<Method>> listener){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                if(listener != null){
                    listener.onFetchDataSuccess(DecodeTask.getAllMethodsInClass(clzName));
                }
            }
        });
    }

    public void hasClass(final String clzName,final OnFetchDataListener<Boolean> listener){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                if(listener != null){
                    listener.onFetchDataSuccess(DecodeTask.hasClass(clzName));
                }
            }
        });
    }

    public void hasMethods(final String clzName,final String methodName,final OnFetchDataListener<Boolean> listener){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                if(listener != null){
                    listener.onFetchDataSuccess(DecodeTask.hasMethod(clzName,methodName));
                }
            }
        });
    }

    public void getClass(final String clzName,final OnFetchDataListener<Class> listener){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                if(listener != null){
                    listener.onFetchDataSuccess(DecodeTask.getClass(clzName));
                }
            }
        });
    }

    public void getMethod(final String clzName,final String methodName,final OnFetchDataListener<Method> listener){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                if(listener != null){
                    listener.onFetchDataSuccess(DecodeTask.getMethod(clzName,methodName));
                }
            }
        });
    }

    public void getMethodCount(final OnFetchDataListener<Integer> listener){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                if(listener != null){
                    listener.onFetchDataSuccess(DecodeTask.getMethodCount());
                }
            }
        });
    }

    public void getClassCount(final OnFetchDataListener<Integer> listener){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                if(listener != null){
                    listener.onFetchDataSuccess(DecodeTask.getClassCount());
                }
            }
        });
    }

    public void dumpDex(){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                DecodeTask.dumpDex();
            }
        });
    }

    public void dumpDex(final String filePath){
        getDecodeHandler().post(new Runnable() {
            @Override
            public void run() {
                DecodeTask.dumpDex(filePath);
            }
        });
    }

    public void clearCache(){
        DecodeTask.clearAllData();
    }
}
