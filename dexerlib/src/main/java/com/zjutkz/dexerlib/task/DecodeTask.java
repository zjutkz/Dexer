package com.zjutkz.dexerlib.task;

import android.text.TextUtils;
import android.util.Log;

import com.zjutkz.dexerlib.util.UTF8;

import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kangzhe on 16/10/20.
 */

public class DecodeTask extends DefaultTask{

    private static final String TAG = "DecodeTask";

    private static final int MAGIC_DEX = 0x0A786564;
    private static final int MAGIC_035 = 0x00353330;
    private static final int MAGIC_036 = 0x00363330;

    private static int string_ids_size;
    private static int type_ids_size;
    private static int field_ids_size;
    private static int method_ids_size;
    private static int class_def_size;

    private static ByteBuffer encodedArrayItem;
    private static ByteBuffer stringId;
    private static ByteBuffer typeId;
    private static ByteBuffer protoId;
    private static ByteBuffer fieldId;
    private static ByteBuffer methodId;
    private static ByteBuffer classDef;
    private static ByteBuffer typeList;
    private static ByteBuffer stringData;

    private static List<String> allClasses = new ArrayList<>();
    private static List<String> allMethods = new ArrayList<>();
    private static Map<String,List<String>> clz2methods = new HashMap<>();
    private static Map<String,Integer> name2Id = new HashMap<>();

    @Override
    public void doTask() {
        byte[] dexSrc = (byte[])src;
        decodeDexFile(dexSrc);
    }

    private void decodeDexFile(byte[] src){
        decodeDexFile(ByteBuffer.wrap(src));
    }

    private static void decodeDexFile(ByteBuffer buffer){
        restore(buffer);
        buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);

        //magic & version
        int magic = buffer.getInt();
        int version = buffer.getInt();

        if(!isDexFile(magic,version)){
            Log.d(TAG, "Finish decoding,it is not a dex file!");
            return;
        }
        // skip checksum
        // skip signature
        // skip file_size
        // skip header_size 0x70
        // skip endian_tag
        // skip uint link_size
        // skip uint link_off
        // skip uint map_off
        skip(buffer, 4 + 20 + 4 + 4 + 4 + 4 + 4 + 4);
        
        string_ids_size = buffer.getInt();
        int string_ids_off = buffer.getInt();
        type_ids_size = buffer.getInt();
        int type_ids_off = buffer.getInt();
        int proto_ids_size = buffer.getInt();
        int proto_ids_off = buffer.getInt();
        field_ids_size = buffer.getInt();
        int field_ids_off = buffer.getInt();
        method_ids_size = buffer.getInt();
        int method_ids_off = buffer.getInt();
        class_def_size = buffer.getInt();
        int class_defs_off = buffer.getInt();

        stringId = slice(buffer, string_ids_off, string_ids_size * 4);
        typeId = slice(buffer, type_ids_off, type_ids_size * 4);
        protoId = slice(buffer, proto_ids_off, proto_ids_size * 12);
        fieldId = slice(buffer, field_ids_off, field_ids_size * 8);
        methodId = slice(buffer, method_ids_off, method_ids_size * 8);
        classDef = slice(buffer, class_defs_off, class_def_size * 32);

        restore(buffer);
        stringData = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        encodedArrayItem = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        typeList = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);

        preFetch();
    }

    private static void preFetch() {
        getAllClassNames();
        getAllMethodNames();

        int clzSize = allClasses.size();
        int methodSize = allMethods.size();

        Log.d(TAG, "There are " + clzSize + " classes with " + methodSize + " methods.");
    }

    private static boolean isDexFile(int magic, int version) {
        return magic == MAGIC_DEX && (version == MAGIC_035 || version == MAGIC_036);
    }

    public static List<String> getAllMethodsInClass(String clzName) {
        if(name2Id.size() == 0){
            getFileNames();
        }
        if(clz2methods.containsKey(clzName)){
            return clz2methods.get(clzName);
        }
        List<String> allMethods = new ArrayList<>();
        Integer clzId = name2Id.get(clzName);
        if(clzId != null){
            allMethods = getMethodNamesWithClzName(clzId);
        }
        return allMethods;
    }

    public static boolean hasMethod(String clzName,String methodName){
        List<String> allMethods = getAllMethodsInClass(clzName);
        return allMethods.contains(methodName);
    }

    public static boolean hasClass(String clzName){
        if(name2Id.size() != 0){
            return name2Id.containsKey(clzName);
        }

        getFileNames();
        return name2Id.containsKey(clzName);
    }

    private static ByteBuffer slice(ByteBuffer in, int offset, int length) {
        in.position(offset);
        ByteBuffer b = in.slice();
        b.limit(length);
        b.order(ByteOrder.LITTLE_ENDIAN);
        return b;
    }

    private static void restore(ByteBuffer buffer){
        buffer.position(0);
    }

    private static void skip(ByteBuffer data, int pos) {
        data.position(data.position() + pos);
    }

    private static List<String> getMethodNamesWithClzName(int clzId){
        List<String> names = new ArrayList<>(method_ids_size);
        ByteBuffer buffer = methodId;
        for (int mid = 0; mid < method_ids_size; mid++) {
            buffer.position(mid * 8);
            if(clzId == (int)buffer.getShort()){
                // skip proto_idx
                skip(buffer,2);
                String methodName = getString(buffer.getInt());
                names.add(methodName.trim());
            }

        }
        return names;
    }

    public static List<String> getAllMethodNames(){
        if(allMethods.size() != 0){
            return allMethods;
        }
        List<String> names = new ArrayList<>(method_ids_size);
        ByteBuffer buffer = methodId;
        restore(buffer);
        for (int mid = 0; mid < method_ids_size; mid++) {
            buffer.position(mid * 8);
            int clzId = buffer.getShort();
            String clzName = getType(clzId);
            // skip proto_idx
            skip(buffer,2);
            String methodName = getString(buffer.getInt());
            names.add(clzName + "=======>" + methodName);
        }
        allMethods = names;
        return names;
    }

    public static List<String> getAllClassNames() {
        if(allClasses.size() != 0){
            return allClasses;
        }
        List<String> names = new ArrayList<>(class_def_size);
        ByteBuffer buffer = classDef;
        restore(buffer);
        for (int cid = 0; cid < class_def_size; cid++) {
            buffer.position(cid * 32);
            String className = getType(buffer.getInt());
            names.add(className);
        }
        allClasses = names;
        return names;
    }

    public static void clearAllData(){
        allClasses.clear();
        allMethods.clear();
        name2Id.clear();
    }
    
    private static void getFileNames() {
        ByteBuffer buffer = classDef;
        restore(buffer);
        for (int cid = 0; cid < class_def_size; cid++) {
            int class_idx = buffer.getInt();
            // skip access_flags
            // skip superclass_idx
            // skip interfaces_off
            skip(buffer,4 + 4 + 4);
            int source_file_idx = buffer.getInt();
            // skip annotations_off
            // skip class_data_off
            // skip static_values_off
            skip(buffer,4 + 4 + 4);

            String fileName = getString(source_file_idx);
            if(!TextUtils.isEmpty(fileName)){
                String clzName = fileName.split("\\.")[0];
                name2Id.put(clzName.trim(),class_idx);
            }
        }
    }

    private static String getType(int id) {
        if (id == -1) {
            return null;
        }
        return getString(typeId.getInt(id * 4));
    }

    private static String getString(int id) {
        if (id == -1) {
            return null;
        }
        int offset = stringId.getInt(id * 4);
        stringData.position(offset);
        int length = readULeb128(stringData);
        try {
            StringBuilder buff = new StringBuilder(length);
            return UTF8.decode(stringData, buff);
        } catch (UTFDataFormatException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static int readULeb128(ByteBuffer in) {
        byte[] byteAry = in.array();

        int index = 0, cur;
        int result = byteAry[index];
        index++;

        if (byteAry.length == 1) {
            return result;
        }

        if (byteAry.length == 2) {
            cur = byteAry[index];
            index++;
            result = (result & 0x7f) | ((cur & 0x7f) << 7);
            return result;
        }

        if (byteAry.length == 3) {
            cur = byteAry[index];
            index++;
            result |= (cur & 0x7f) << 14;
            return result;
        }

        if (byteAry.length == 4) {
            cur = byteAry[index];
            index++;
            result |= (cur & 0x7f) << 21;
            return result;
        }

        if (byteAry.length == 5) {
            cur = byteAry[index];
            index++;
            result |= cur << 28;
            return result;
        }

        return result;
    }
}
