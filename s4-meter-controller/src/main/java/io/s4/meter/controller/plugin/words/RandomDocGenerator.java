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
package io.s4.meter.controller.plugin.words;

import org.json.JSONException;
import org.json.JSONObject;

import io.s4.meter.common.EventGenerator;

@SuppressWarnings("serial")
public class RandomDocGenerator extends EventGenerator {

    final int wordSize;
    final int numWordsPerDoc;
    final int numEvents;

    public RandomDocGenerator(
            String hostName, 
            int port, 
            String s4StreamName,
            String s4EventClassName,
            int wordSize,
            int numWordsPerDoc, 
            int numEvents) {
        super(hostName, port, s4StreamName, s4EventClassName);

        this.wordSize = wordSize;
        this.numWordsPerDoc = numWordsPerDoc;
        this.numEvents = numEvents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see firstResource.EventGenerator#start()
     */
    @Override
    public void start() {

        JSONObject jsonDoc;
        System.out.println("Started event generator.\n");

        RandomWord wg = new RandomWord(0, wordSize);
        StringBuilder words;

        /* Generate events. */
        for (int i = 0; i < numEvents; i++) {

            /* Generate document content. */
            words = new StringBuilder();
            for (int j = 0; j < numWordsPerDoc; j++) {
                words.append(wg.getWord());
                words.append(" ");
            }

            try {
                /* Create JSON doc. */
                jsonDoc = new JSONObject();
                jsonDoc.put("id", new Integer(i));
                jsonDoc.put("text", words.toString());

                /* Send event. */
                send(jsonDoc);

            } catch (JSONException ex) {
                System.err.println("Couldn't create document.");
                ex.printStackTrace(System.err);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see firstResource.EventGenerator#stop()
     */
    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

}
