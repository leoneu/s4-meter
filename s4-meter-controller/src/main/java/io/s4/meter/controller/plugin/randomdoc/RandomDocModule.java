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

import io.s4.meter.common.EventGenerator;
import io.s4.meter.controller.ControllerModule;

import com.google.inject.Provides;
import com.google.inject.Singleton;

public class RandomDocModule extends ControllerModule {


    protected void configure() {
        super.configure();
    }
    
    @Provides @Singleton
    EventGenerator provideEventGenerator() {
      EventGenerator gen = new RandomDocGenerator(
              config.getString("s4Adaptor.hostname"),
              config.getInt("s4Adaptor.port"),
              config.getString("s4App.streamName"),
              config.getString("s4App.eventClassName"),
              config.getFloat("generator.eventRate"),
              config.getInt("randomDocGenerator.wordSize"),
              config.getInt("randomDocGenerator.numWordsPerDoc"),
              config.getInt("randomDocGenerator.numEvents"));
      return gen;
    }

}
