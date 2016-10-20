package com.zjutkz.dexerlib.task;

/**
 * Created by kangzhe on 16/10/20.
 */

public class DefaultTask implements Task{

    private static final String TAG = "Task";

    protected Object src;

    public void setSrc(Object src){
        this.src = src;
    }

    //do nothing
    public void doTask(){

    }
}
