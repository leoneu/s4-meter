/**
 * 
 */
package io.s4.meter.controller.plugin.words;

import org.json.JSONException;
import org.json.JSONObject;

import io.s4.meter.common.EventGenerator;

/**
 * @author neumeyer
 * 
 */
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

        WordGenerator wg = new WordGenerator(0, wordSize);
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
