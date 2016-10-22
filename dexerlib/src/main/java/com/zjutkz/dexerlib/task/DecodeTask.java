package com.zjutkz.dexerlib.task;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.zjutkz.dexerlib.dex.Annotation;
import com.zjutkz.dexerlib.dex.AnnotationElement;
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

    private static final int VALUE_BYTE = 0;
    private static final int VALUE_SHORT = 2;
    private static final int VALUE_CHAR = 3;
    private static final int VALUE_INT = 4;
    private static final int VALUE_LONG = 6;
    private static final int VALUE_FLOAT = 16;
    private static final int VALUE_DOUBLE = 17;
    private static final int VALUE_STRING = 23;
    private static final int VALUE_TYPE = 24;
    private static final int VALUE_FIELD = 25;
    private static final int VALUE_METHOD = 26;
    private static final int VALUE_ENUM = 27;
    private static final int VALUE_ARRAY = 28;
    private static final int VALUE_ANNOTATION = 29;
    private static final int VALUE_NULL = 30;
    private static final int VALUE_BOOLEAN = 31;

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
    private static ByteBuffer annotationsDirectoryItem;
    private static ByteBuffer annotationSetItem;
    private static ByteBuffer annotationItem;
    private static ByteBuffer annotationSetRefList;

    private static SparseIntArray fieldAnnotationPositions = new SparseIntArray();
    private static SparseIntArray methodAnnotationPositions = new SparseIntArray();
    private static SparseIntArray paramAnnotationPositions = new SparseIntArray();
    private static SparseArray<Class> id2class = new SparseArray<>();
    private static List<Class> allClasses = new ArrayList<>();
    private static List<Method> allMethods = new ArrayList<>();
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
        annotationsDirectoryItem = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        annotationSetItem = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        annotationItem = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        annotationSetRefList = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);

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
            int annotations_off = classDef.getInt();
            int class_data_off = classDef.getInt();
            int static_values_off = classDef.getInt();
            parseAnnotation(clz,annotations_off);
            getFieldsAndMethods(clz,class_data_off,static_values_off);
            if(!TextUtils.isEmpty(clz.source_file_name)){
                replaceSourceFileName(clz);
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

    private static void parseAnnotation(Class clz, int annotations_off) {
        if (annotations_off != 0) {
            annotationsDirectoryItem.position(annotations_off);

            int class_annotations_off = annotationsDirectoryItem.getInt();
            int field_annotation_size = annotationsDirectoryItem.getInt();
            int method_annotation_size = annotationsDirectoryItem.getInt();
            int parameter_annotation_size = annotationsDirectoryItem.getInt();

            if (class_annotations_off != 0) {
                getAnnotationSetItem(clz,class_annotations_off);
            }
            for (int i = 0; i < field_annotation_size; i++) {
                int field_idx = annotationsDirectoryItem.getInt();
                int field_annotations_offset = annotationsDirectoryItem.getInt();
                fieldAnnotationPositions.put(field_idx, field_annotations_offset);
            }
            for (int i = 0; i < method_annotation_size; i++) {
                int method_idx = annotationsDirectoryItem.getInt();
                int method_annotation_offset = annotationsDirectoryItem.getInt();
                methodAnnotationPositions.put(method_idx, method_annotation_offset);
            }
            for (int i = 0; i < parameter_annotation_size; i++) {
                int method_idx = annotationsDirectoryItem.getInt();
                int parameter_annotation_offset = annotationsDirectoryItem.getInt();
                paramAnnotationPositions.put(method_idx, parameter_annotation_offset);
            }
        }
    }

    private static void getAnnotationSetItem(Object injected, int annotations_off) {
        if(annotations_off != 0){
            ByteBuffer buffer = annotationSetItem;
            buffer.position(annotations_off);
            int size = buffer.getInt();
            for (int j = 0; j < size; j++) {
                int annotation_off = buffer.getInt();
                Annotation annotation = getAnnotation(annotation_off);
                if(injected instanceof Class){
                    ((Class) injected).addAnnotation(annotation);
                }else if(injected instanceof Field){
                    ((Field) injected).addAnnotation(annotation);
                }else if(injected instanceof Method){
                    ((Method) injected).addMethodAnnotation(annotation);
                }
            }
        }
    }

    private static Annotation getAnnotation(int annotation_off) {
        ByteBuffer buffer = annotationItem;
        buffer.position(annotation_off);
        Annotation annotation = new Annotation();
        int visibility = 0xFF & buffer.get();
        int type_idx = readULeb128(buffer);
        int size = readULeb128(buffer);
        String type = getType(type_idx);
        annotation.visibility = visibility;
        annotation.name = type;
        for (int i = 0; i < size; i++) {
            AnnotationElement element = new AnnotationElement();
            int name_idx = readULeb128(buffer);
            String name = getString(name_idx);
            Object value = readEncodedValue(buffer);
            element.name = name;
            element.value = value;
            annotation.addElement(element);
        }

        return annotation;
    }

    private static Object readEncodedValue(ByteBuffer in) {
        int b = 0xFF & in.get();
        int type = b & 0x1f;
        switch (type) {
            case VALUE_BYTE:
                return new Byte((byte) readIntBits(in, b));

            case VALUE_SHORT:
                return new Short((short) readIntBits(in, b));

            case VALUE_INT:
                return new Integer((int) readIntBits(in, b));

            case VALUE_LONG:
                return new Long(readIntBits(in, b));

            case VALUE_CHAR:
                return new Character((char) readUIntBits(in, b));

            case VALUE_STRING:
                return getString((int) readUIntBits(in, b));

            case VALUE_FLOAT:
                return Float.intBitsToFloat((int) (readFloatBits(in, b) >> 32));

            case VALUE_DOUBLE:
                return Double.longBitsToDouble(readFloatBits(in, b));

            case VALUE_NULL:
                return null;

            case VALUE_BOOLEAN: {
                return new Boolean(((b >> 5) & 0x3) != 0);

            }
            default:
                return null;
        }
    }

    private static void replaceSourceFileName(Class clz) {
        if(clz.class_name.contains("$")){
            String outerClz = clz.source_file_name.split("\\.")[0];
            String[] maybeMore = clz.class_name.split("\\$");
            String innerClz = "";
            for(int i = 1;i < maybeMore.length;i++){
                if(i == 1){
                    innerClz = maybeMore[i];
                }else {
                    innerClz = innerClz + "$" + maybeMore[i];
                }
            }
            //exclude ";"
            innerClz = innerClz.substring(0,innerClz.length() -1);
            clz.source_file_name = outerClz + "$" + innerClz + ".java";
        }
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

        Method method = new Method(clzName,parameterTypes,returnType,getString(name_idx));

        Integer method_annotation_offset = methodAnnotationPositions.get(id);
        getAnnotationSetItem(method,method_annotation_offset);

        Integer parameter_annotation_offset = paramAnnotationPositions.get(id);
        getAnnotationSetRefList(method,parameter_annotation_offset);

        return method;
    }

    private static void getAnnotationSetRefList(Method method, Integer parameter_annotation_offset) {
        if(parameter_annotation_offset != 0){
            ByteBuffer buffer = annotationSetRefList;
            buffer.position(parameter_annotation_offset);

            int size = buffer.getInt();
            for (int j = 0; j < size; j++) {
                int param_annotation_offset = buffer.getInt();
                if (param_annotation_offset == 0) {
                    continue;
                }
                getAnnotationSetItem(method,param_annotation_offset);
            }
        }
    }

    private static int findFields(ByteBuffer buffer,int lastField,List<Field> fields) {
        if(fields == null){
            fields = new ArrayList<>();
        }
        int diff = readULeb128(buffer);
        int field_access_flags = readULeb128(buffer);
        int field_id = lastField + diff;

        fields.add(getField(field_id,field_access_flags));

        return field_id;
    }

    private static Field getField(int id,int field_access_flags) {
        Field field = new Field();
        fieldId.position(id * 8);
        skip(fieldId,2);
        int type_idx = 0xFFFF & fieldId.getShort();
        int name_idx = fieldId.getInt();

        String type = getType(type_idx);
        String name = getString(name_idx);

        Integer annotation_offset = fieldAnnotationPositions.get(id);
        getAnnotationSetItem(field,annotation_offset);

        field.type = type;
        field.name = name;
        field.access_flag = field_access_flags;
        return field;
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

    private static long readIntBits(ByteBuffer in, int before) {
        int length = ((before >> 5) & 0x7) + 1;
        long value = 0;
        for (int j = 0; j < length; j++) {
            value |= ((long) (0xFF & in.get())) << (j * 8);
        }
        int shift = (8 - length) * 8;
        return value << shift >> shift;
    }

    private static long readUIntBits(ByteBuffer in, int before) {
        int length = ((before >> 5) & 0x7) + 1;
        long value = 0;
        for (int j = 0; j < length; j++) {
            value |= ((long) (0xFF & in.get())) << (j * 8);
        }
        return value;
    }

    private static long readFloatBits(ByteBuffer in, int before) {
        int bytes = ((before >> 5) & 0x7) + 1;
        long result = 0L;
        for (int i = 0; i < bytes; ++i) {
            result |= ((long) (0xFF & in.get())) << (i * 8);
        }
        result <<= (8 - bytes) * 8;
        return result;
    }
}
