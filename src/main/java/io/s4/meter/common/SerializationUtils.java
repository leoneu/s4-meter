package io.s4.meter.common;

// source: http://www.java2s.com/Tutorial/Java/0180__File/SerializationUtilities.htm

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.List;

public class SerializationUtils {

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(buffer);
            oos.writeObject(obj);
            oos.close();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error writing to byte-array!");
        }
    }

    // Used snippet from here:
    // http://blogs.sun.com/adventures/entry/desrializing_objects_custom_class_loaders
    public static Object deserialize(byte[] bytes,
            List<ClassLoader> customClassLoaders) throws ClassNotFoundException {
        try {
            final List<ClassLoader> classLoaderList = customClassLoaders;
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(input) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc)
                        throws IOException, ClassNotFoundException {
                    String className = desc.getName();
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException exc) {
                        for (ClassLoader cl : classLoaderList)
                            try {
                                return cl.loadClass(className);
                            } catch (ClassNotFoundException e) {
                            }
                        throw new ClassNotFoundException(className);
                    }
                }
            };
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error reading from byte-array!");
        }
    }
}
