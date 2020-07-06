package com.enhance.spring.script.tool;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字节码加载为class
 *
 */
public class MemoryClassLoader extends URLClassLoader {
    private final Map<String, byte[]> classBytes;

    public MemoryClassLoader(final Map<String, byte[]> classBytes) {
        super(new URL[0], MemoryClassLoader.class.getClassLoader());
        this.classBytes=new ConcurrentHashMap<>();
        this.classBytes.putAll(classBytes);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        byte[] buf = classBytes.get(name);
        if (buf == null) {
            return super.findClass(name);
        }
        classBytes.remove(name);
        return defineClass(name, buf, 0, buf.length);
    }

}
