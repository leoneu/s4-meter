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
package io.s4.meter.generator;

import io.s4.meter.common.SerializationUtils;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.Post;

public class GeneratorResource extends BaseResource {

    private static Logger logger = Logger
            .getLogger(GeneratorResource.class);

    /**
     * Handle POST requests.
     * 
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @Post
    public void acceptGenerator(Representation entity) throws IOException,
            IllegalArgumentException, SecurityException,
            InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException,
            IllegalAccessException {

        if (MediaType.APPLICATION_JAVA_OBJECT.equals(getRequest().getEntity()
                .getMediaType())) {

            /*
             * Store serialized object in a byte array so we can load it using
             * our custom class loader.
             */
            ObjectRepresentation<byte[]> rep = new ObjectRepresentation<byte[]>(
                    getRequest().getEntity());

            byte[] objectBuffer = rep.getObject();

            if (logger.getLevel() == Level.TRACE) {
                for (ClassLoader cl : classLoaders) {
                    logger.trace("classloader in list: " + cl.toString());
                }
            }

            /* Deserialize using the custom class loader. */
            try {
                generator = SerializationUtils
                        .deserialize(objectBuffer, classLoaders);
            } catch (ClassNotFoundException e) {
                logger.error("Couldn't find class loader for deserialization.");
            }
            logger.trace("gen: " + generator.toString());
            logger.trace("genclass loader: "
                    + generator.getClass().getClassLoader().toString());
        }

        setStatus(Status.SUCCESS_OK);

    }
}
