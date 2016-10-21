package com.zjutkz.dexerlib.task;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.zjutkz.dexerlib.dex.Class;
import com.zjutkz.dexerlib.dex.Field;
import com.zjutkz.dexerlib.dex.Method;
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

    private static ByteBuffer stringId;
    private static ByteBuffer typeId;
    private static ByteBuffer protoId;
    private static ByteBuffer fieldId;
    private static ByteBuffer methodId;
    private static ByteBuffer classDef;
    private static ByteBuffer typeList;
    private static ByteBuffer stringData;
    private static ByteBuffer classData;
    private static ByteBuffer codeData;

    private static SparseArray<Class> id2class = new SparseArray<>();
    private static List<Class> allClasses = new ArrayList<>();
    private static List<Method> allMethods = new ArrayList<>();
    private static Map<String,Method> clz2methods = new HashMap<>();
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
        int class_def_off = buffer.getInt();

        stringId = slice(buffer, string_ids_off, string_ids_size * 4);
        typeId = slice(buffer, type_ids_off, type_ids_size * 4);
        protoId = slice(buffer, proto_ids_off, proto_ids_size * 12);
        fieldId = slice(buffer, field_ids_off, field_ids_size * 8);
        methodId = slice(buffer, method_ids_off, method_ids_size * 8);
        classDef = slice(buffer, class_def_off, class_def_size * 32);

        restore(buffer);
        stringData = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        typeList = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        classData = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        codeData = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);

        preFetch();
    }

    private static void preFetch() {
        getAllClasses();

        int clzSize = allClasses.size();
        int methodSize = allMethods.size();

        Log.d(TAG, "There are " + clzSize + " classes with " + methodSize + " methods.");
    }

    private static boolean isDexFile(int magic, int version) {
        return magic == MAGIC_DEX && (version == MAGIC_035 || version == MAGIC_036);
    }

    public static List<Method> getAllMethodsInClass(String clzName) {
        List<Method> allMethods = new ArrayList<>();
        Integer clzId = name2Id.get(clzName);
        if(clzId != null){
            allMethods = getMethodNamesWithClzId(clzId);
        }
        return allMethods;
    }

    public static boolean hasMethod(String clzName,String methodName){
        List<Method> allMethods = getAllMethodsInClass(clzName);
        for(Method method : allMethods){
            if(method.name.equals(methodName)){
                return true;
            }
        }
        return false;
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

    private static List<Method> getMethodNamesWithClzId(int clzId){
        Class dest = id2class.get(clzId,null);
        List<Method> allMethods = new ArrayList<>();
        if(dest != null){
            allMethods.addAll(dest.virtual_methods);
            allMethods.addAll(dest.direct_methods);
        }
        return allMethods;
    }

    public static Method getMethod(String clzName,String methodName){
        Class destClz;
        Integer clzId = name2Id.get(clzName);
        if(clzId != null){
            destClz = id2class.get(clzId,null);
            for(Method method : destClz.direct_methods){
                if(method.name.equals(methodName)){
                    return method;
                }
            }
            for(Method method : destClz.virtual_methods){
                if(method.name.equals(methodName)){
                    return method;
                }
            }
        }

        return null;
    }

    public static Class getClass(String clzName){
        Integer clzId = name2Id.get(clzName);
        if(clzId != null){
            return id2class.get(clzId,null);
        }

        return null;
    }

    public static List<Method> getAllMethods(){
        if(allMethods.size() != 0){
            return allMethods;
        }

        getAllClasses();
        return allMethods;
    }


    public static void clearAllData(){
        allClasses.clear();
        allMethods.clear();
        name2Id.clear();
    }

    public static List<Class> getAllClasses() {
        if(allClasses.size() != 0){
            return allClasses;
        }
        List<Class> clzs = new ArrayList<>(class_def_size);
        for (int cid = 0; cid < class_def_size; cid++) {
            Class clz = new Class();
            classDef.position(cid * 32);
            int class_idx = classDef.getInt();
            clz.class_name = getType(class_idx);
            clz.access_flags = classDef.getInt();
            clz.super_class_name = getType(classDef.getInt());
            clz.interfaces = getTypeList(classDef.getInt());
            clz.source_file_name = getString(classDef.getInt());
            // TODO: 16/10/21 support annotation
            int annotations_off = classDef.getInt();
            int class_data_off = classDef.getInt();
            int static_values_off = classDef.getInt();
            getFieldsAndMethods(clz,class_data_off,static_values_off);
            if(!TextUtils.isEmpty(clz.source_file_name)){
                name2Id.put(clz.source_file_name.split("\\.")[0],class_idx);
            }
            id2class.append(class_idx,clz);
            clzs.add(clz);
        }
        for(Class clz : allClasses){
            if(!TextUtils.isEmpty(clz.super_class_name)){
                clz.super_class = getClass(clz.super_class_name);
            }
        }
        allClasses = clzs;
        return clzs;
    }

    private static void getFieldsAndMethods(Class clz, int class_data_off,int static_values_off) {
        if(class_data_off != 0){
            ByteBuffer buffer = classData;
            buffer.position(class_data_off);
            int static_fields = readULeb128(buffer);
            int instance_fields = readULeb128(buffer);
            int direct_methods = readULeb128(buffer);
            int virtual_methods = readULeb128(buffer);

            List<Field> staticFields = new ArrayList<>();
            List<Field> instanceFields = new ArrayList<>();
            List<Method> directMethods = new ArrayList<>();
            List<Method> virtualMethods = new ArrayList<>();

            int lastField = 0;
            for (int i = 0; i < static_fields; i++) {
                lastField = findFields(buffer,lastField,staticFields);
            }

            lastField = 0;
            for (int i = 0; i < instance_fields; i++) {
                lastField = findFields(buffer,lastField,instanceFields);
            }

            int lastMethod = 0;
            for(int i = 0;i < direct_methods;i++){
                lastMethod = findMethods(buffer,lastMethod,directMethods);
            }

            lastMethod = 0;
            for(int i = 0;i < virtual_methods;i++){
                lastMethod = findMethods(buffer,lastMethod,virtualMethods);
            }

            clz.static_fields = staticFields;
            clz.instance_fields = instanceFields;
            clz.direct_methods = directMethods;
            clz.virtual_methods = virtualMethods;

            allMethods.addAll(directMethods);
            allMethods.addAll(virtualMethods);
        }
    }

    private static int findMethods(ByteBuffer buffer, int lastMethod, List<Method> methods) {
        if(methods == null){
            methods = new ArrayList<>();
        }

        int diff = readULeb128(buffer);
        // TODO: 16/10/21 support code item
        int method_access_flags = readULeb128(buffer);
        int code_off = readULeb128(buffer);
        int method_id = lastMethod + diff;
        methods.add(getMethod(method_id,code_off));

        return method_id;
    }

    private static Method getMethod(int id,int offset) {
        methodId.position(id * 8);
        int class_idx = methodId.getShort();
        int proto_idx = 0xFFFF & methodId.getShort();
        int name_idx = methodId.getInt();

        String clzName = getType(class_idx);
        List<String> parameterTypes;
        String returnType;

        protoId.position(proto_idx * 12 + 4);

        int return_type_idx = protoId.getInt();
        int parameters_off = protoId.getInt();

        returnType = getType(return_type_idx);

        parameterTypes = getTypeList(parameters_off);

        return new Method(clzName,parameterTypes,returnType,getString(name_idx));
    }

    private static int findFields(ByteBuffer buffer,int lastField,List<Field> fields) {
        if(fields == null){
            fields = new ArrayList<>();
        }
        int diff = readULeb128(buffer);
        // TODO: 16/10/21 support field access flag
        int field_access_flags = readULeb128(buffer);
        int field_id = lastField + diff;

        fields.add(getField(field_id));

        return field_id;
    }

    private static Field getField(int id) {
        fieldId.position(id * 8);
        skip(fieldId,2);
        int type_idx = 0xFFFF & fieldId.getShort();
        int name_idx = fieldId.getInt();

        String type = getType(type_idx);
        String name = getString(name_idx);

        return new Field(type,name);
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

    private static List<String> getTypeList(int offset) {
        if (offset == 0) {
            return new ArrayList<>();
        }
        typeList.position(offset);
        int size = typeList.getInt();
        List<String> types = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            types.add(getType(0xFFFF & typeList.getShort()));
        }
        return types;
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
            return UTF8.decode(stringData, buff).trim();
        } catch (UTFDataFormatException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static int readULeb128(ByteBuffer in) {
        int value = 0;
        int count = 0;
        int b = in.get();
        while ((b & 0x80) != 0) {
            value |= (b & 0x7f) << count;
            count += 7;
            b = in.get();
        }
        value |= (b & 0x7f) << count;
        return value;
    }
}
