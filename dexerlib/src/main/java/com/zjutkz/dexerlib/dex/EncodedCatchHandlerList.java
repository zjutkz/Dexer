package com.zjutkz.dexerlib.dex;

/**
 * Created by kangzhe on 16/10/21.
 */

public class EncodedCatchHandlerList {

    public int size;

    public static class EncodedCatchHandle{
        public int size;
        public EncodedTypeAddPair[] handlers;
        public int catch_all_addr;
    }
}
