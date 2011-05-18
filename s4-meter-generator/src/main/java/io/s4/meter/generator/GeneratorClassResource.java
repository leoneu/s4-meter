/**
 * 
 */
package io.s4.meter.generator;

import io.s4.meter.common.BaseResource;
import io.s4.meter.common.NetworkClassLoader;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.Post;

public class GeneratorClassResource extends BaseResource {

    private static Logger logger = Logger
            .getLogger("io.s4.meter.generator.GeneratorClassResource");

    /**
     * Handle POST requests.
     * 
     * @throws IOException
     */
    @Post
    public void acceptGeneratorClass(Representation entity) throws IOException {

        byte[] classBytes;
        NetworkClassLoader cl = null;

        try {

            if (MediaType.APPLICATION_JAVA_OBJECT.equals(getRequest()
                    .getEntity().getMediaType())) {
                ObjectRepresentation<byte[]> rep = new ObjectRepresentation<byte[]>(
                        getRequest().getEntity());
                classBytes = rep.getObject();

                /* Load the event generator class. */
                if (classLoaders.isEmpty()) {
                    cl = new NetworkClassLoader(this.getClass()
                            .getClassLoader());

                    /* Add only first Class Loader to list. */
                    classLoaders.add(cl);
                } else {
                    cl = (NetworkClassLoader) classLoaders.get(0);
                }
                Class<?> acceptedClass = cl.loadClass(null, classBytes, 0,
                        classBytes.length);
                logger.trace("genclass: " + acceptedClass.toString());
                logger.trace("genclass classloader: "
                        + acceptedClass.getClassLoader().toString());

                String acceptedClassName = acceptedClass.getSuperclass()
                        .getSimpleName();
                logger.trace("class name: " + acceptedClassName);

                /* If this is the Event Generator class, set static variable. */

                /*
                 * TODO make this recursive to check all ancestor classes .
                 */
                if (acceptedClassName.contentEquals("EventGenerator")) {
                    generatorClass = acceptedClass;
                    logger.info("Loaded EventGenerator class: "
                            + acceptedClass.toString());
                }
            }

        } catch (IOException ioe) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        } catch (ClassNotFoundException cnfe) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }

        setStatus(Status.SUCCESS_OK);

    }
}
