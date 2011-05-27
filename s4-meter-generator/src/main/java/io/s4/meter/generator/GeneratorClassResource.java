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

import io.s4.meter.common.NetworkClassLoader;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;

/**
 * This class implements the following methods:
 * <ul>
 * <li><em>POST:</em> - Accepts classes as byte arrays and loads them using a
 * custom class loader. The implementation of {@link EventGenerator} is found
 * and assigned to {@link generatorClass}. The class loader is assigned to
 * {@link classLoader}.
 * <li><em>DELETE:</em> - Completely removes the event generator and leaves the
 * container ready to accept a new event generator.
 * </ul>
 * 
 * @author Leo Neumeyer
 * 
 */
public class GeneratorClassResource extends BaseResource {

    private static Logger logger = Logger
            .getLogger(GeneratorClassResource.class);

    /**
     * Handles POST requests.
     * 
     * @throws IOException
     */
    @Post
    public void acceptGeneratorClass(Representation entity) throws IOException {

        byte[] classBytes;

        try {

            if (MediaType.APPLICATION_JAVA_OBJECT.equals(getRequest()
                    .getEntity().getMediaType())) {
                ObjectRepresentation<byte[]> rep = new ObjectRepresentation<byte[]>(
                        getRequest().getEntity());
                classBytes = rep.getObject();

                /* Load the event generator class. */
                if (classLoader == null) {
                    classLoader = new NetworkClassLoader(this.getClass()
                            .getClassLoader());
                }
                Class<?> acceptedClass = ((NetworkClassLoader) classLoader)
                        .loadClass(null, classBytes, 0, classBytes.length);
                logger.trace("genclass: " + acceptedClass.toString());
                logger.trace("genclass classloader: "
                        + acceptedClass.getClassLoader().toString());

                String acceptedClassName = acceptedClass.getSuperclass()
                        .getSimpleName();
                logger.trace("class name: " + acceptedClassName);

                /* If this is the Event Generator class, set static variable. */

                /*
                 * TODO make this recursive to check all ancestor classes .
                 */
                if (acceptedClassName.contentEquals("EventGenerator")) {
                    generatorClass = acceptedClass;
                    logger.info("Loaded EventGenerator class: "
                            + acceptedClass.toString());
                }
            }

        } catch (IOException ioe) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        } catch (ClassNotFoundException cnfe) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }

        setStatus(Status.SUCCESS_OK);
    }

    /*
     * Resets the service. All previously uploaded event generator classes and
     * objects and dependent classes and objects are removed.
     * 
     * @return confirmation message.
     */
    @Delete
    public String deleteGenerator() {
        logger.info("Deleting event generator.");
        if (generator != null)
            generator.stop(); // Stop thread before removing reference.
        classLoader = null;
        generator = null;
        generatorClass = null;
        return "Deleted event generator.";
    }
}
