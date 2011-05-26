package io.s4.meter.common;

import io.s4.client.Driver;
import io.s4.client.Message;
import io.s4.client.ReadMode;

import java.io.IOException;
import java.io.Serializable;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public abstract class EventGenerator implements Serializable {

    private static Logger logger = Logger.getLogger(EventGenerator.class);

    final private String hostname;
    final private String port;
    final private String s4StreamName;
    final private String s4EventClassName;
    final private long eventPeriod;
    final private long numEvents;

    transient private Driver driver;
    transient private long startTime;
    transient private long time;
    transient private long eventCount;
    transient private int modulus;
    transient private boolean isInterrupted;

    public EventGenerator(String hostname, String port, String s4StreamName,
            String s4EventClassName, float eventRate, long numEvents) {
        super();
        this.port = port;
        this.hostname = hostname;
        this.s4StreamName = s4StreamName;
        this.s4EventClassName = s4EventClassName;
        this.eventPeriod = (long) (1000f / eventRate);
        this.numEvents = numEvents;
    }

    /*
     * This method is called when the object is deserialized and can be used to
     * initialized transient fields.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {

        in.defaultReadObject();
        initInternal();
        logger.info("Initialized event generator.");

    }

    private void initInternal() {

        logger.info("Initializing S4 driver for EventGenerator.");

        isInterrupted = false;
        eventCount = 0;
        modulus = (int) (10000f / (float) eventPeriod); // Every 10 secs.
        driver = new Driver(hostname, Integer.parseInt(port));
        driver.setReadMode(ReadMode.None);
        driver.setDebug(true); // set to true to debug.

        try {
            if (!driver.init()) {
                logger.error("Driver initialization failed.");
                System.exit(1);
            }

            if (!driver.connect()) {
                logger.error("Driver initialization failed.");
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws InterruptedException, JSONException {

        /*
         * Initialize the concrete class lazily to make sure all fields are set
         * after serialization.
         */
        init();
        
        /* We use time in milliseconds to control the event rate. */
        time = System.currentTimeMillis();
        startTime = time;

        /* Let's send the events to the adaptor. */
        for (long i = 0; i < numEvents; i++) {
            send(i);
        }

        /* Close driver connection. */
        close();
    }

    protected void send(long eventID) throws InterruptedException,
            JSONException {

        String avgRate;
        JSONObject jsonDoc = getDocument(eventID);
        time = System.currentTimeMillis();
        long delta = (time - startTime) - (eventPeriod * eventCount);
        if (delta < 0) {

            /*
             * Wait if we are transmitting faster than the target rate.
             */
            Thread.sleep(-delta);
        }

        Message m = new Message(s4StreamName, s4EventClassName,
                jsonDoc.toString());

        if (isInterrupted) {
            close();
            throw new InterruptedException();
        }

        try {
            driver.send(m);
        } catch (IOException e) {

            logger.error("Could not send message using driver.", e);

            avgRate = String.format("%8.2f",
                    ((float) (eventCount * 1000) / (float) (time - startTime)));
            logger.error("Event count: " + String.format("%10d", eventCount)
                    + " time: "
                    + String.format("%8d", (time - startTime) / 1000)
                    + " avg rate: " + avgRate + "   " + jsonDoc.toString()
                    + " " + s4StreamName + " " + s4EventClassName);
        }

        if (logger.isTraceEnabled() && (eventCount % modulus) == 0) {

            if (eventCount > 0)
                avgRate = String
                        .format("%8.2f",
                                ((float) (eventCount * 1000) / (float) (time - startTime)));
            else
                avgRate = "--------";

            logger.trace("count: " + String.format("%10d", eventCount)
                    + " time: "
                    + String.format("%8d", (time - startTime) / 1000)
                    + " avg rate: " + avgRate + "   " + jsonDoc.toString()
                    + " " + s4StreamName + " " + s4EventClassName);
        }
        eventCount++;
    }

    public void stop() {

        isInterrupted = true;

    }

    public void close() {
        try {
            if (driver != null)
                driver.disconnect();
        } catch (Exception e) {
            logger.error("Error when trying to disconnect driver.");
        }
    }

    /**
     * @return the hostName
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @return the S4 client driver
     */
    public Driver getDriver() {
        return driver;
    }

    /**
     * @param driver
     *            the S4 client driver to set
     */
    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    /**
     * @return the eventRate
     */
    public float getEventRate() {
        if (time - startTime > 0)
            return (float) eventCount / (float) ((time - startTime) * 1000);
        else
            return 0.0f;
    }

    /**
     * @return the eventCount
     */
    public float getEventCount() {
        return eventCount;
    }

    abstract protected JSONObject getDocument(long eventID)
            throws JSONException;

    abstract protected void init();
}
