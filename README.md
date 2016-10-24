#Dexer

A useful library provides some operations doing on .dex file



#Download

Please use JitPack.

```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

```groovy
compile 'com.github.zjutkz:Dexer:1.2'
```



#Usage

#####You should decode the .dex file first.Just construct the Dexer class

```java
public void decode_dex(View view){
    try {
        byte[] src = stream2byte(getAssets().open("classes.dex"));
        dexer = new Dexer(src);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

```java
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
```



#####There are 6 functions support here:

1.Getting all classes in .dex file:

```java
public void get_all_classes(View view){
    dexer.getAllClasses(new Dexer.OnFetchDataListener() {
        @Override
        public void onFetchDataSuccess(Object data) {
            List<Class> allClasses = (List<Class>)data;
            for(Class clz : allClasses){
                Log.d(TAG, clz.class_name);
            }
        }
    });
}
```

2.Getting all methods in .dex file:

```java
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
```

3.Getting all methods in a specific class:

```java
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
```

4.Judge if this .dex file has a particular class or method:

```java
public void has_method_and_class(View view){
    dexer.hasClass("MainActivity", new Dexer.OnFetchDataListener() {
        @Override
        public void onFetchDataSuccess(Object data) {
            Log.d(TAG, "dex file has MainActivity: " + (boolean)data);
        }
    });

    dexer.hasClass("MainActivity2", new Dexer.OnFetchDataListener() {
        @Override
        public void onFetchDataSuccess(Object data) {
            Log.d(TAG, "dex file has MainActivity2: " + (boolean)data);
        }
    });

    dexer.hasMethods("MainActivity", "get_all_methods_in_class", new Dexer.OnFetchDataListener() {
        @Override
        public void onFetchDataSuccess(Object data) {
            Log.d(TAG, "MainActivity has method get_all_methods_in_class: " + (boolean)data);
        }
    });

    dexer.hasMethods("MainActivity", "get_all_methods_in_class2", new Dexer.OnFetchDataListener() {
        @Override
        public void onFetchDataSuccess(Object data) {
            Log.d(TAG, "MainActivity has method get_all_methods_in_class2: " + (boolean)data);
        }
    });
}
```

5.Getting a specific class(support inner class):

```java
public void get_class(View view){
        dexer.getClass("TestClass", new Dexer.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(Object data) {
                Class clz = (Class)data;
                Log.d(TAG, clz.class_name + " " + clz.super_class_name);
                for(Field field : clz.instance_fields){
                    Log.d(TAG, "instance field: " + field.name);
                }
                for(Field field : clz.static_fields){
                    Log.d(TAG, "static field: " + field.name);
                }
                Log.d(TAG, "" + clz.access_flags);
                Log.d(TAG, "=========================================");
            }
        });

        dexer.getClass("TestClass$InnerClass", new Dexer.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(Object data) {
                Class clz = (Class)data;
                for(Field field : clz.instance_fields){
                    Log.d(TAG, "instance field: " + field.name);
                }
                for(Field field : clz.static_fields){
                    Log.d(TAG, "static field: " + field.name);
                }
                Log.d(TAG, "" + clz.access_flags);
                Log.d(TAG, "=========================================");
            }
        });

        dexer.getClass("TestClass$InnerClass$DoubleInnerClass", new Dexer.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(Object data) {
                Class clz = (Class)data;
                for(Field field : clz.instance_fields){
                    Log.d(TAG, "instance field: " + field.name);
                }
                for(Field field : clz.static_fields){
                    Log.d(TAG, "static field: " + field.name);
                }
                Log.d(TAG, "" + clz.access_flags);
                Log.d(TAG, "=========================================");
            }
        });
}
```

6.Getting a specific method:

```java
public void get_method(View view){
        dexer.getMethod("MainActivity", "onCreate", new Dexer.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(Object data) {
                Method method = (Method)data;
                Log.d(TAG, method.name);
                Log.d(TAG, method.returnType);
                for(String type : method.paramTypes){
                    Log.d(TAG, type);
                }
                for(Annotation annotation : method.methodAnnotations){
                    Log.d(TAG, annotation.name);
                }
            }
        });
}
```

7.Dumpping the dex:

   7.1 In logcat

```java
public void dump(View view){
    dexer.dumpDex();
}
```

   7.2 In file

```java
public void dump(View view){
    dexer.dumpDex("storage/sdcard0/dump.txt");
}
```

8.Getting methods & classes count:

```java
public void get_count(View view){
    dexer.getClassCount(new Dexer.OnFetchDataListener() {
        @Override
        public void onFetchDataSuccess(Object data) {
            Log.d(TAG, "classes count: " + data);
        }
    });

    dexer.getMethodCount(new Dexer.OnFetchDataListener() {
        @Override
        public void onFetchDataSuccess(Object data) {
            Log.d(TAG, "methods count: " + data);
        }
    });
}
```



#Todo

######1.Support the code item

######2.Support array-type annotation value



#Thanks

[dex2jar](https://github.com/pxb1988/dex2jar)



#License

```
Copyright 2016 zjutkz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```