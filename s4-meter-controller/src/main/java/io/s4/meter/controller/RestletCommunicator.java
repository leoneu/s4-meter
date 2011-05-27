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
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.restlet.resource.ClientResource;

/**
 * Implements {@link Communicator} interface using the Restlet toolkit.
 * 
 * @author Leo Neumeyer
 *
 */
class RestletCommunicator implements Communicator {

    private static Logger logger = Logger.getLogger(RestletCommunicator.class);

    final private ClientResource[] genClassResource;
    final private ClientResource[] genResource;
    final private ClientResource[] actionResource;
    final private String[] hosts;
    final private String[] ports;
    private Class<?>[] classes;

    /**
     * Creates an implementation of the {@link Communicator}. This is the only
     * way to create an instance.
     * 
     * @param hosts
     *            the remote generators hostnames.
     * @param ports
     *            the remote generators ports.
     * @param classURI
     *            the URI used to upload the custom generator class and
     *            dependent classes.
     * @param instanceURI
     *            the URI used to upload the generator object.
     * @param actionURI
     *            the URI for sending commands to the remote generators.
     * @param eventGeneratorClassname
     *            the class name of the custom event generator.
     * @param dependentClassnames
     *            the class names of the classes used by the event generator
     *            that are not available in the classpath of the remote event
     *            generators.
     */
    public RestletCommunicator(String[] hosts, String[] ports, String classURI,
            String instanceURI, String actionURI, String eventGeneratorClassname,
            String[] dependentClassnames) {

        /* Validate parameters. */
        if (hosts == null) {
            throw new NullPointerException("hosts.");
        }
        if (ports == null) {
            throw new NullPointerException("ports.");
        }
        if (hosts.length != ports.length) {
            throw new IllegalArgumentException("Number of hosts ("
                    + hosts.length + ") and number of ports (" + ports.length
                    + ") don't match.");
        }
        if (eventGeneratorClassname == null) {
            throw new NullPointerException("eventGeneratorClassname.");
        }
        if (classURI == null) {
            throw new NullPointerException("classURI.");
        }
        if (instanceURI == null) {
            throw new NullPointerException("instanceURI.");
        }
        if (actionURI == null) {
            throw new NullPointerException("actionURI.");
        }

        this.hosts = hosts;
        int numHosts = hosts.length;
        this.ports = ports;

        /* Create REST resources. */
        genClassResource = new ClientResource[numHosts];
        genResource = new ClientResource[numHosts];
        actionResource = new ClientResource[numHosts];


        for (int i = 0; i < numHosts; i++) {
            genClassResource[i] = new ClientResource("http://" + hosts[i] + ":"
                    + ports[i] + classURI);
            genResource[i] = new ClientResource("http://" + hosts[i] + ":"
                    + ports[i] + instanceURI);
            actionResource[i] = new ClientResource("http://" + hosts[i] + ":"
                    + ports[i] + actionURI);
            
            logger.trace(genClassResource.toString());
            logger.trace(genResource.toString());
            logger.trace(actionResource.toString());
        }

        /*
         * We need to send the event generator class and dependent classes to
         * the remote hosts. These classes are not available in the remote host
         * and need to all be loaded with the same ClassLoader.
         */
        int i = 0;
        try {
            int numClasses = 1;
            if (dependentClassnames != null)
                numClasses = dependentClassnames.length + 1;
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
    public void start() throws IOException {
        for (int i = 0; i < hosts.length; i++) {
            String now = String.valueOf(Calendar.getInstance().getTimeInMillis());
            actionResource[i].getReference().addQueryParameter("start", now);
            actionResource[i].get();
            logger.trace("GET: " + actionResource[i].getReference() + " by resource " + actionResource[i].toString());
        }
    }
    
    @Override
    public void sendClasses() throws IOException {

        int numClasses = classes.length;
        for (int i = 0; i < hosts.length; i++) {
            logger.info("Reset service - host: " + hosts[i] + ", port: " + ports[i]);
            reset(genClassResource[i]);
            for (int j = 0; j < numClasses; j++) {
                sendClassInternal(classes[j], genClassResource[i]);
            }
        }
    }

    @Override
    public void sendGenerator(List<EventGenerator> generators) throws Exception {

        if(generators.size() != hosts.length) {
            logger.error("Mismath in number of remote generators: " + generators.size() + " vs. " + hosts.length);
            throw new Exception();
        }
        
        for (int i = 0; i < hosts.length; i++) {
            byte[] byteArray = SerializationUtils.serialize(generators.get(i));
            genResource[i].post(byteArray);
            logger.trace("POST: " + genResource[i].getReference() + " by resource " + genResource[i].toString());
        }

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

        resource.post((Object) classBytes);
        logger.trace("POST: " + resource.getReference() + " by resource " + resource.toString());
    }

    /* Reset generator. */
    private void reset(ClientResource resource) {
        resource.delete();
        logger.trace("DELETE: " + resource.getReference() + " by resource " + resource.toString());
    }
}
