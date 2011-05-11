package io.s4.meter.generator;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class RestletApp extends Application {

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {
		// Create a router Restlet that defines routes.
		Router router = new Router(getContext());

        // Defines a route for the resource "event generator"
        router.attach("/generator", GeneratorResource.class);
     // Defines a route for the resource "event generator class"
        router.attach("/generator_class", GeneratorClassResource.class);

		return router;
	}
}
