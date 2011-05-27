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

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.resource.Get;

/**
 * Resource for processing actions.The actions are executed using a REST
 * interface. The URI is set the in the properties file read by
 * {@link GeneratorModule}.
 * 
 * <ul>
 * 
 * <li><em>start</em> - start the event generator. on a separate thread.
 * 
 * </ul>
 * 
 * @author Leo Neumeyer
 * 
 */
public class ActionResource extends BaseResource {

    private static Logger logger = Logger.getLogger(ActionResource.class);

    /**
     * 
     * Parse the action command received in an HTTP Get method and execute the
     * command.
     * 
     * @return the parameter that was received in the action URI.
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    @Get
    public String action() throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Form queryParams = getQuery();
        Parameter param;
        param = queryParams.getFirst("start");
        if (param != null) {

            /* Run!. */
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        generator.start();
                    } catch (InterruptedException e) {
                        logger.info("Process stopped. Terminating.", e);
                    } catch (Exception e) {
                        logger.error("Couldn't start thread", e);
                    } finally {
                        generator.close();
                    }
                }
            };

            generatorThread = new Thread(r);
            generatorThread.start();

            logger.info("Received action: " + param.getName() + "  "
                    + param.getValue());
            return param.getName();
        }

        String msg = "Unknown parameter: " + getReference().getRemainingPart();
        logger.error(msg);
        return msg;
    }
}
