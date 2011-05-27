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
package io.s4.meter.controller.plugin.randomdoc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import io.s4.meter.common.EventGenerator;
import io.s4.meter.controller.ControllerModule;

import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * This module is used to configure a reference implementation of {@link EventGenerator}.
 * 
 * @author Leo Neumeyer
 *
 */
public class RandomDocModule extends ControllerModule {

    private static Logger logger = Logger.getLogger(RandomDocModule.class);

    /* (non-Javadoc)
     * @see io.s4.meter.controller.ControllerModule#configure()
     */
    protected void configure() {
        super.configure();
    }

    /*
     * Provide a list with the generators that must be sent to remote hosts. The
     * number of generators and the configuration is determined from the
     * properties file. All generators are configured identically except for the
     * hostname and port of the S4 adaptor.
     * 
     * @return list of configured event generators.
     * 
     * @throws Exception
     */
    @Provides
    @Singleton
    List<EventGenerator> provideEventGenerators() throws Exception {

        String[] hostnames = config.getStringArray("s4Adaptor.hostnames");
        String[] ports = config.getStringArray("s4Adaptor.ports");
        List<EventGenerator> eventGenerators = new ArrayList<EventGenerator>();
        EventGenerator gen;

        if (hostnames.length != ports.length) {
            logger.error("Mismatched length: s4Adaptor.hostnames and s4Adaptor.ports");
            throw new Exception();
        }

        int numAdaptors = hostnames.length;
        for (int i = 0; i < numAdaptors; i++) {

            gen = new RandomDocGenerator(hostnames[i], ports[i],
                    config.getString("s4App.streamName"),
                    config.getString("s4App.eventClassName"),
                    config.getFloat("generator.eventRate"),
                    config.getLong("generator.numEvents"),
                    config.getInt("randomDocGenerator.wordSize"),
                    config.getInt("randomDocGenerator.numWordsPerDoc"));

            eventGenerators.add(gen);
        }
        return eventGenerators;
    }
}
