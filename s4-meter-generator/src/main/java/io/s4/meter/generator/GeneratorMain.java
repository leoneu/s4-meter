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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.inject.Guice;

/**
 * The generator container.
 * 
 * @author Leo Neumeyer
 * 
 */
public class GeneratorMain {

    private static Logger logger = Logger.getLogger("io.s4.meter");

    /**
     * Starts the generator container service.
     * 
     * @param args
     *            accepts a port number as a command line parameter.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        /* Set up logger basic configuration. */
        BasicConfigurator.configure();
        logger.setLevel(Level.TRACE);

        if (args.length > 0)
            System.setProperty("generator.port", args[0]);

        /* Start service. */
        logger.info("Starting service.");
        Guice.createInjector(new GeneratorModule()).getInstance(Service.class);
    }

}
