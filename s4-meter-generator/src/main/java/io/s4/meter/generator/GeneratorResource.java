/**
 * 
 */
package io.s4.meter.generator;

import io.s4.meter.common.SerializationUtils;
import io.s4.meter.common.BaseResource;
import io.s4.meter.common.EventGenerator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.Post;


public class GeneratorResource extends BaseResource {

    private static Logger logger = Logger.getLogger("io.s4.meter.generator.GeneratorResource");

    /**
     * Handle POST requests.
     * 
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    @Post
    public void acceptGenerator(Representation entity) throws IOException,
            IllegalArgumentException, SecurityException,
            InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {

        if (MediaType.APPLICATION_JAVA_OBJECT.equals(getRequest().getEntity()
                .getMediaType())) {
            
            /* Store serialized object in a byte array so we can load it using our custom 
             * class loader. 
             */
            ObjectRepresentation<byte[]> rep = new ObjectRepresentation<byte[]>(
                    getRequest().getEntity());
            
            byte[] objectBuffer = rep.getObject();

            /* Deserialize using the custom class loader. */
            Object gen = null;
            try {
                gen = SerializationUtils.deserialize(objectBuffer, classLoaders);            
            } catch (ClassNotFoundException e) {
                logger.error("Couldn't find class loader for deserialization.");
            }
            logger.trace("gen: " + gen.toString());
            logger.trace("genclass loader: " + gen.getClass().getClassLoader().toString());
            
            generatorClass.getMethod("start").invoke(gen);

            /* Run!. */
            //gen.start();
        }

        setStatus(Status.SUCCESS_OK);

    }
}
