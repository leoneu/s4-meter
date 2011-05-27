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

/**
 * 
 * Custom class loader used to load deserialize classes in remote containers.
 * 
 * @author Leo Neumeyer
 * 
 */
public class NetworkClassLoader extends ClassLoader {

    /**
     * @param parentClassLoader
     */
    public NetworkClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }

    /**
     * @param className
     *            the class to be loaded
     * @param classBuffer
     *            the byte buffer with the bytecode for the class.
     * @param offset
     *            the start index in the buffer.
     * @param length
     *            the length of the buffer.
     * @return the Class object.
     * @see java.lang.ClassLoader
     */
    public Class<?> loadClass(String className, byte[] classBuffer, int offset,
            int length) {
        // Class<?> loadedClass = super.findLoadedClass(className);

        return super.defineClass(className, classBuffer, offset, length);
        /*
         * if (null == loadedClass) { return super.defineClass(className,
         * classBuffer, offset, length); }
         * 
         * return loadedClass;
         */
    }
}
