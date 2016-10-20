package com.zjutkz.dexerlib.thread;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by kangzhe on 16/10/20.
 */

public class DecodeThread extends HandlerThread{

    private Handler mHandler;

    public DecodeThread(String name) {
        super(name);
        start();
        mHandler = new Handler(getLooper());
    }

    public DecodeThread(String name, int priority) {
        super(name, priority);
        start();
        mHandler = new Handler(getLooper());
    }

    public Handler getHandler(){
        return mHandler;
    }

    public boolean isDecodeThreadAlive() {
        return (mHandler != null && getLooper() != null && isAlive());
    }

    @Override
    public boolean quit() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        return super.quit();
    }
}
