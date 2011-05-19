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

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public class RestletApp extends Application implements Service {

    private static Logger logger = Logger
    .getLogger("io.s4.meter.generator.RestletApp");
    
    final private String generatorInstanceURI;
    final private String generatorClassURI;
    
    RestletApp(
            int port,
            String generatorClassURI,
            String generatorInstanceURI) {
        super();
        
        this.generatorInstanceURI = generatorInstanceURI;
        this.generatorClassURI = generatorClassURI;
        
        // Create a new Component.
        Component component = new Component();

        // Add a new HTTP server listening on port.
        component.getServers().add(Protocol.HTTP, port);

        // Increase the number of connections. 
        component.getServers().getContext().getParameters().add("maxTotalConnections", "50");
        
        component.getDefaultHost().attach(this);

        // Start the component.
        try {
            component.start();
        } catch (Exception e) {
            logger.error("Couldn't start server.");
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that defines routes.
        Router router = new Router(getContext());

        // Defines a route for the resource "event generator"
        router.attach(generatorInstanceURI, GeneratorResource.class);
        // Defines a route for the resource "event generator class"
        router.attach(generatorClassURI, GeneratorClassResource.class);

        return router;
    }
}
