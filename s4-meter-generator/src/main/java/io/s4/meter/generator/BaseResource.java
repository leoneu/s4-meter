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

import io.s4.meter.common.EventGenerator;

import org.restlet.resource.ServerResource;

/**
 * Base resource class that supports common behaviors or attributes shared by
 * all resources.
 * 
 * Based on Restlet sample code.
 * 
 */
public abstract class BaseResource extends ServerResource {

    static Class<?> generatorClass = null;
    static ClassLoader classLoader = null;
    static EventGenerator generator = null;
    static Thread generatorThread = null;
}
