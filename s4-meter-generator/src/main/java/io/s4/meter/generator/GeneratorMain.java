package io.s4.meter.generator;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;


public class GeneratorMain {

    private static Logger logger = Logger.getLogger("io.s4.meter");

    public static void main(String[] args) throws Exception {
        
        /* Set up logger basic configuration. */
        BasicConfigurator.configure();
        logger.setLevel(Level.TRACE);

        // Create a new Component.
        Component component = new Component();

        // Add a new HTTP server listening on port 8182.
        component.getServers().add(Protocol.HTTP, 8182);

		// Increase the number of connections. 
		component.getServers().getContext().getParameters().add("maxTotalConnections", "50");

        component.getDefaultHost().attach("/s4meter",
                new RestletApp());

        // Start the component.
        component.start();
    }

}
