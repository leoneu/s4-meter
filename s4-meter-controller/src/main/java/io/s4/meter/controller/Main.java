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

import java.io.IOException;
import org.restlet.resource.ResourceException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

    private static Logger logger = Logger.getLogger("io.s4.meter");

    public static void main(String[] args) throws IOException,
            ResourceException, InstantiationException, IllegalAccessException,
            ClassNotFoundException {

        /* Set up logger basic configuration. */
        BasicConfigurator.configure();
        logger.setLevel(Level.TRACE);
        
        /* Initialize Guice module for the controller. */
        Injector injector = Guice.createInjector(new ControllerModule());
        
        /* Build an Event Generator object using Injection. This instance will be 
         * serialized and sent to remote hosts. 
         */
        EventGenerator gen = injector.getInstance(EventGenerator.class);
        
        /* The communicator interface hides the implementation details of how the 
         * EventGenerator instance is sent to remote hosts. 
         */
        Communicator comm = injector.getInstance(Communicator.class);

        /* Send generator classes. TODO list of classes needs to be passed to 
         * the communicator. 
         */
        comm.sendClasses();
        
        /* Instance comm will take care of serializing this configured object to the 
         * remote hosts. 
         */
        comm.sendGenerator(gen);

    }
}
