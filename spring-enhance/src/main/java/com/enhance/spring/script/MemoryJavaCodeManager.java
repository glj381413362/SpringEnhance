package com.enhance.spring.script;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

/**
 * 内存代码管理
 *
 */
class MemoryJavaCodeManager extends ForwardingJavaFileManager<JavaFileManager> {
    private final Map<String, byte[]> classBytes;

    MemoryJavaCodeManager(final JavaFileManager fileManager) {
        super(fileManager);
        this.classBytes = new ConcurrentHashMap<>();
    }

    Map<String, byte[]> getClassBytes() {
        return new HashMap<>(this.classBytes);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
        classBytes.clear();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(final Location location,
                                               final String className,
                                               final Kind kind,
                                               final FileObject sibling) throws IOException {
        if (kind == Kind.CLASS) {
            return new MemoryOutputJavaFileObject(className);
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    JavaFileObject makeStringSource(final String name, final String code) {
        return new MemoryInputJavaFileObject(name, code);
    }

    static class MemoryInputJavaFileObject extends SimpleJavaFileObject {
        final String code;

        MemoryInputJavaFileObject(final String name, final String code) {
            super(URI.create("string:///" + name), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharBuffer getCharContent(final boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(code);
        }
    }

    class MemoryOutputJavaFileObject extends SimpleJavaFileObject {
        final String name;

        MemoryOutputJavaFileObject(final String name) {
            super(URI.create("string:///" + name), Kind.CLASS);
            this.name = name;
        }

        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {

                @Override
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    classBytes.put(name, bos.toByteArray());
                }
            };
        }

    }
}
