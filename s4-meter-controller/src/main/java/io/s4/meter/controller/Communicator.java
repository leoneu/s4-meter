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

import io.s4.meter.common.EventGenerator;

/**
 * 
 * A communication interface to communicate with remote generators.
 * 
 * @author Leo Neumeyer
 */
public interface Communicator {

    /**
     * Send custom generator classes to the remote generator containers.
     * 
     * @throws IOException
     */
    void sendClasses() throws IOException;

    /**
     * Tells remote generators to start generating events.
     * 
     * @throws IOException
     */
    void start() throws IOException;

    /**
     * @param generators
     *            the list of configured generators that will be sent to all the
     *            remote generator containers.
     * 
     * @throws Exception
     */
    void sendGenerator(List<EventGenerator> generators) throws Exception;
}
