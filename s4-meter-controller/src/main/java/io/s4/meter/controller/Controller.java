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

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import io.s4.meter.common.EventGenerator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

/**
 * 
 * Orchestrates workflow among the various components and provides a centralized
 * API for the distributed system.
 * 
 * Typically a controller singleton would configure generators, send them to
 * remote hosts, start the event generation process, collect results, and
 * produce reports.
 * 
 * @author Leo Neumeyer
 */
class Controller {

    final private String moduleName;

    /**
     * The generator module used to inject dependencies.
     * 
     * Because this module is provided by the application developer as a plugin,
     * we don't know the name of the module class. Instead we use a Guice named
     * annotation to retrieve the name of the module.
     */
    @Inject
    public Controller(@Named("generator.module") String moduleName) {
        this.moduleName = moduleName;
    }

    private static Logger logger = Logger.getLogger(Controller.class);

    public void start() throws Exception {

        Injector injector;
        AbstractModule module = null;

        /* Initialize Guice module for plugin. */
        try {
            module = (AbstractModule) Class.forName(moduleName).newInstance();
        } catch (Exception e) {
            logger.error("Unable to instantiate module.", e);
        }

        /* After some indirection we get the injector. */
        injector = Guice.createInjector(module);

        /*
         * Create the Event Generator objects using injection. The generators
         * will be serialized and sent to remote hosts.
         */
        List<EventGenerator> generators = injector.getInstance(Key
                .get(new TypeLiteral<List<EventGenerator>>() {
                }));

        /*
         * The communicator interface hides the implementation details of how
         * the EventGenerator instance is sent to remote hosts.
         */
        Communicator comm = injector.getInstance(Communicator.class);

        try {
            /* Send generator classes. */
            comm.sendClasses();

            /*
             * comm will serialize the generators and send them to the remote
             * hosts.
             */
            comm.sendGenerator(generators);

            /* Tell remote hosts to start generating events! */
            comm.start();

        } catch (IOException e) {
            logger.error("Unable to communicate with remote generators.", e);
        }
    }
}
