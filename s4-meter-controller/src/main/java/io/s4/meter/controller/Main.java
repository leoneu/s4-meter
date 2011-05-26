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

        /*
         * Need to get name of plugin module. Load ControllerModule to get
         * configuration.
         */
        Injector injector = Guice.createInjector(new ControllerModule());

        Controller controller = injector.getInstance(Controller.class);
        try {
            controller.start();
        } catch (Exception e) {
            logger.error("Failed to start the controller.", e);
        }

    }
}
