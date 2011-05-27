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

import java.io.InputStream;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Module to configure the generator container.
 * 
 * @author Leo Neumeyer
 * 
 */
class GeneratorModule extends AbstractModule {

    private static Logger logger = Logger.getLogger(GeneratorModule.class);

    protected PropertiesConfiguration config = null;

    /**
     * Loads properties.
     * 
     * @param binder
     *            the Guice binder.
     */
    private void loadProperties(Binder binder) {

        try {
            InputStream is = this.getClass().getResourceAsStream(
                    "/generator.properties");
            config = new PropertiesConfiguration();
            config.load(is);

            // System.out.println(ConfigurationUtils.toString(config));
            logger.info(ConfigurationUtils.toString(config));

            /* Make all properties injectable. Do we need this? */
            Names.bindProperties(binder,
                    ConfigurationConverter.getProperties(config));
        } catch (ConfigurationException e) {
            binder.addError(e);
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see AbstractModule#configure()
     */
    @Override
    protected void configure() {
        if (config == null)
            loadProperties(binder());

        String port = System.getProperty("generator.port");
        if (port != null) {
            logger.info("Using port " + port + " read from command line.");
            config.setProperty("generator.port", Integer.parseInt(port));
        }
    }

    /*
     * Provide a Service singleton configured with the URIs and the port number.
     * 
     * @return list of configured event generators.
     * 
     */
    @Provides
    @Singleton
    Service provideCommunicator() {
        Service comm = new RestletApp(config.getInt("generator.port"), config
                .getString("generator.classURI").trim(), config.getString(
                "generator.instanceURI").trim(),
                config.getString("generator.actionURI"));

        return comm;
    }
}
