/**
 * 
 */
package io.s4.meter.controller.plugin.words;

import io.s4.meter.common.EventGenerator;

/**
 * @author neumeyer
 *
 */
@SuppressWarnings("serial")
public class RandomDocGenerator extends EventGenerator {
    
    /* (non-Javadoc)
     * @see firstResource.EventGenerator#start()
     */
    @Override
    public void start() {
        System.out.println("Started event generator.");

    }

    /* (non-Javadoc)
     * @see firstResource.EventGenerator#stop()
     */
    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

}
