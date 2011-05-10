package io.s4.meter.common;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.ServerResource;

/**
 * Base resource class that supports common behaviors or attributes shared by
 * all resources.
 * 
 */
public abstract class BaseResource extends ServerResource {
    
    static protected Class<?> generatorClass;
    static protected List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();

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

}
