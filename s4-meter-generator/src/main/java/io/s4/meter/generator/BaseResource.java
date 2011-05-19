package io.s4.meter.generator;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.ServerResource;

/**
 * Base resource class that supports common behaviors or attributes shared by
 * all resources.
 * 
 */
abstract class BaseResource extends ServerResource {
    
    static Class<?> generatorClass = null;
    static List<ClassLoader> classLoaders = null;

    /**
     * @return the generatorClass
     */
    public Class<?> getGeneratorClass() {
        return generatorClass;
    }

    /**
     * @param generatorClass the generatorClass to set
     */
    public void setGeneratorClass(Class<?> generatorClass) {
        BaseResource.generatorClass = generatorClass;
    }
    
    /**
     * @param generatorClass the generatorClass to set
     */
    public void resetGenerator() {
        classLoaders = null;
    }

}
