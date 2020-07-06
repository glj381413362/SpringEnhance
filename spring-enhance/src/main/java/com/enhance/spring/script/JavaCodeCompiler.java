package com.enhance.spring.script;

import com.enhance.spring.script.tool.ClassResolver;
import com.enhance.spring.script.tool.MemoryClassLoader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * java代码编译
 *
 */
public class JavaCodeCompiler {
    private final JavaCompiler javaCompiler;
    private final StandardJavaFileManager standardJavaFileManager;

    public JavaCodeCompiler() {
        this.javaCompiler = ToolProvider.getSystemJavaCompiler();
        this.standardJavaFileManager = this.javaCompiler.getStandardFileManager
                (null, Locale.getDefault(), Charset.defaultCharset());
    }

    public Class<?> loadClass(final String sourceCode) throws IOException, ClassNotFoundException {
        final ClassResolver.Result result = ClassResolver.resolver(sourceCode);

        // 一旦类被加载不允许再修改
        synchronized (this) {
            try {
                return Class.forName(result.getClassFullName());
            } catch (ClassNotFoundException ex) {
                Map<String, byte[]> clazzBytes = this.compile(result.getClassFileName(), sourceCode);
                return this.loadClass(result.getClassFullName(), clazzBytes);
            }
        }
    }

    /**
     * 编译java代码到内存
     *
     * @param fileName java文件名称 xxx.java
     * @param source   代码内容
     * @return 字节码
     * @throws IOException 异常信息
     */
    private Map<String, byte[]> compile(final String fileName, final String source) throws IOException {
        try (MemoryJavaCodeManager manager = new MemoryJavaCodeManager(this.standardJavaFileManager)) {
            final JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
            final CompilationTask task = this.javaCompiler.getTask(
                    null,
                    manager,
                    null,
                    null,
                    null,
                    Collections.singletonList(javaFileObject)
            );
            final Boolean result = task.call();
            if (result == null || !result) {
                throw new RuntimeException("Compilation failed.");
            }
            return manager.getClassBytes();
        }
    }

    /**
     * 加载字节码文件到jvm中
     *
     * @param name       类名称
     * @param classBytes 字节码
     * @return 类
     * @throws ClassNotFoundException 类不存在
     * @throws IOException            io异常
     */
    private Class<?> loadClass(final String name,
                               final Map<String, byte[]> classBytes) throws ClassNotFoundException, IOException {
        try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
            return classLoader.loadClass(name);
        }
    }
}
