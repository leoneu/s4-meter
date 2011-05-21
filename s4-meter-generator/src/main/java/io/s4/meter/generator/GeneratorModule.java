package io.s4.meter.generator;

import java.io.InputStream;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class GeneratorModule extends AbstractModule {

    private static Logger logger = Logger.getLogger(GeneratorModule.class);

    protected PropertiesConfiguration config = null;
    private void loadProperties(Binder binder) {
        
        try {
            InputStream is = this.getClass().getResourceAsStream("/generator.properties");
            config = new PropertiesConfiguration();
            config.load(is);

            //System.out.println(ConfigurationUtils.toString(config));
            logger.info(ConfigurationUtils.toString(config));
            
            /* Make all properties injectable. Do we need this?*/
            Names.bindProperties(binder, ConfigurationConverter.getProperties(config));
        } catch (ConfigurationException e) {
            binder.addError(e);
            e.printStackTrace();
        }
    }

    @Override
    protected void configure() {
        if(config == null)
            loadProperties(binder());
        
        String port = System.getProperty("generator.port");
        if(port != null) {
            logger.info("Using port " + port + " read from command line.");
            config.setProperty("generator.port", Integer.parseInt(port));
        }
    }
    
    @Provides @Singleton
    Service provideCommunicator() {
        Service comm = new RestletApp(
                config.getInt("generator.port"),
                config.getString("generator.classURI").trim(),
                config.getString("generator.instanceURI").trim(),
                config.getString("generator.actionURI"));

        return comm;
    }
}
