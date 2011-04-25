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
package io.s4.app.words;

import io.s4.client.Driver;
import io.s4.client.Message;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONException;

public class Injector {
    
    static int NUM_EVENTS = 1000;
    static int NUM_WORDS_PER_DOC = 6;
    static int WORD_SIZE = 10;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("No host name specified.");
            System.exit(1);
        }
        String hostName = args[0];

        if (args.length < 2) {
            System.err.println("No port specified.");
            System.exit(1);
        }

        int port = -1;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.err.println("Bad port number specified: " + args[1] + ".");
            System.exit(1);
        }
        
        if (args.length < 3) {
            System.err.println("No stream name specified.");
            System.exit(1);
        }        String streamName = args[2];

        if (args.length < 4) {
            System.err.println("No class name specified.");
            System.exit(1);
        }
        String clazz = args[3];

        Driver d = new Driver(hostName, port);   

        try {
            if (!d.init()) {
                System.err.println("Driver initialization failed.");
                System.exit(1);
            }

            if (!d.connect()) {
                System.err.println("Driver initialization failed.");
                System.exit(1);
            }

            WordGenerator wg = new WordGenerator(0, WORD_SIZE);
            StringBuilder words;
            JSONObject doc;
            
            /* Generate events. */
            for  (int i = 0; i < NUM_EVENTS; i++) {
                
                /* Generate document content. */
                words = new StringBuilder();
                for (int j = 0; j < NUM_WORDS_PER_DOC; j++) {
                    words.append(wg.getWord());
                    words.append(" ");                    
                }
                
                try {
                    /* Create JSON doc. */
                    doc = new JSONObject();
                    doc.put("id", new Integer(i));
                    doc.put("text", words.toString());
                    
                    /* Send event. */
                    Message m = new Message(streamName, clazz, doc.toString());
                    d.send(m);
                    
                } catch (JSONException ex) {
                    System.err.println("Couldn't create document.");
                    ex.printStackTrace(System.err);
                }
                
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try { d.disconnect(); } catch (Exception e) {}
        }
    }
}

            
