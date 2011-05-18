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
package io.s4.meter.controller;

import io.s4.meter.common.EventGenerator;
import io.s4.meter.common.SerializationUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class RestletCommunicator implements Communicator {

    private static Logger logger = Logger.getLogger(RestletCommunicator.class);

    final private ClientResource genClassResource;
    final private ClientResource genResource;
    private Class<?>[] classes;

    public RestletCommunicator(String[] hosts, String port, String classURI,
            String instanceURI, String eventGeneratorClassname,
            String[] dependentClassnames) {

        logger.trace("http://" + hosts[0] + ":" + port + classURI);
        logger.trace("http://" + hosts[0] + ":" + port + instanceURI);
        /* Create REST resources. */
        genClassResource = new ClientResource("http://" + hosts[0] + ":" + port
                + classURI);

        genResource = new ClientResource("http://" + hosts[0] + ":" + port
                + instanceURI);

        /*
         * We need to send the event generator class and dependent classes to
         * the remote hosts. These classes are not available in the remote host
         * and need to all be loaded with the same ClassLoader.
         */
        int i = 0;
        try {
            int numClasses = dependentClassnames.length + 1;
            classes = new Class[numClasses];
            classes[i++] = Class.forName(eventGeneratorClassname);
            for (; i < numClasses; i++) {
                classes[i] = Class.forName(dependentClassnames[i - 1]);
            }

        } catch (ClassNotFoundException e) {
            String className = (i == 0) ? eventGeneratorClassname
                    : dependentClassnames[i - 1];
            logger.error("Couldn't find class for name: " + className);
            e.printStackTrace();
        }
    }

    
    @Override
    public void sendClasses() throws IOException {
        
        int numClasses = classes.length;
        for(int i=0; i<numClasses; i++){
            sendClass(classes[i]);
        }
    }
    
    private void sendClass(Class<?> clazz) throws IOException {

        sendClassInternal(clazz, genClassResource);
    }

    @Override
    public void sendGenerator(EventGenerator gen) {

        byte[] byteArray = SerializationUtils.serialize(gen);

        logger.trace("post gen: " + byteArray.toString());
        Representation rep = genResource.post(byteArray);

    }

    private void sendClassInternal(Class<?> clazz, ClientResource resource)
            throws IOException {

        String filename = clazz.getName().replace('.', File.separatorChar)
                + ".class";
        InputStream in = null;
        in = clazz.getClassLoader().getResourceAsStream(filename);

        if (in == null) {
            // System.out.println("Could not find resource: " + filename);
            logger.error("Could not find resource: " + filename);
        }
        byte[] classBytes = IOUtils.toByteArray(in);

        Representation rep = resource.post((Object) classBytes);
        // TODO use rep
        logger.trace("post genclass: " + classBytes.toString());
    }
}
