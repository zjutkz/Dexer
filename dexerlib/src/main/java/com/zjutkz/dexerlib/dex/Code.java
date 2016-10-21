package com.zjutkz.dexerlib.dex;

/**
 * Created by kangzhe on 16/10/21.
 */

public class Code {

    public int register_size;
    public int ins_size;
    public int outs_size;
    public int tries_size;
    public int debug_info_off;
    public int insns_size;
    public int[] insns_array;
    public int padding;
    public TryItem[] tries;
    public EncodedCatchHandlerList handlers;
}
