package io.s4.meter.common;

public class NetworkClassLoader extends ClassLoader {

    public NetworkClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }

    public Class<?> loadClass(String className, byte[] classBuffer, int offset,
            int length) {
        //Class<?> loadedClass = super.findLoadedClass(className);

        return super.defineClass(className, classBuffer, offset, length);
/*
        if (null == loadedClass) {
            return super.defineClass(className, classBuffer, offset, length);
        }

        return loadedClass; */
    }
}
