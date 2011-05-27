/*
 * Copyright (c) 2011 Yahoo! Inc. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License. See accompanying LICENSE file. 
 */
package io.s4.meter.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for serializing and deserializing objects.
 * 
 * @see <a
 *      href="http://www.java2s.com/Tutorial/Java/0180__File/SerializationUtilities.htm">Serialization
 *      utilities article.</a>
 * 
 */
public class SerializationUtils {

    /**
     * Serializes objects using standard Java serialization.
     * 
     * This is a helper method.
     * 
     * @param obj
     *            the object to be serialized.
     * @return a byte array for the object graph.
     */
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

    /**
     * @param bytes the array with the bytes to be deserialized.
     * @param customClassLoaders the class loader to be used to load all the classes.
     * @return the deserialized object.
     * @throws ClassNotFoundException
     * @see <a
     *      href="http://blogs.sun.com/adventures/entry/desrializing_objects_custom_class_loaders">Desrializing objects with custom class loaders.</a>
     * 
     * @see #deserialize(byte[], ClassLoader)
     */
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

    /**
     * Same as {@link #deserialize(byte[], List)}
     * @param bytes
     * @param customClassLoader
     * @return the deserialized object.
     * @throws ClassNotFoundException
     * @see #deserialize(byte[], List)
     */
    public static Object deserialize(byte[] bytes, ClassLoader customClassLoader)
            throws ClassNotFoundException {

        List<ClassLoader> cl = new ArrayList<ClassLoader>();
        cl.add(customClassLoader);
        return deserialize(bytes, cl);
    }
}
