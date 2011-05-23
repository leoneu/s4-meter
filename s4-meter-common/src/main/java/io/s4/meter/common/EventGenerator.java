package io.s4.meter.common;

import io.s4.client.Driver;
import io.s4.client.Message;
import io.s4.client.ReadMode;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.json.JSONObject;

@SuppressWarnings("serial")
public abstract class EventGenerator implements Serializable {

    private static Logger logger = Logger.getLogger(EventGenerator.class);

    final private String hostName;
    final private int port;
    final protected String s4StreamName;
    final protected String s4EventClassName;
    final protected long eventPeriod;
    transient protected Driver driver;
    transient private long startTime;
    transient private long time;
    transient private long eventCount;
    transient private int modulus;

    public EventGenerator(String hostName, int port, String s4StreamName,
            String s4EventClassName, float eventRate) {
        super();
        this.port = port;
        this.hostName = hostName;
        this.s4StreamName = s4StreamName;
        this.s4EventClassName = s4EventClassName;
        this.eventPeriod = (long) (1000f / eventRate);
    }

    /*
     * This method is called when the object is deserialized and can be used to
     * initialized transient fields.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {

        in.defaultReadObject();
        init();
        logger.info("Initialized event generator.");

    }

    protected void init() {

        logger.info("Initializing S4 driver for EventGenerator.");

        eventCount = 0;
        modulus = (int) (10000f / (float) eventPeriod); // Every 10 secs.
        driver = new Driver(hostName, port);
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

    abstract public void start() throws InterruptedException;

    protected void send(JSONObject jsonDoc) throws InterruptedException {

        String avgRate;

        if (eventCount == 0) {

            /* Initialize when we send the first event. */
            time = System.currentTimeMillis();
            startTime = time;

        } else
            while ((time - startTime) < (eventPeriod * eventCount)) {

                /*
                 * Else, wait if we are transmitting faster than the target
                 * rate.
                 */
                Thread.sleep(5);
                time = System.currentTimeMillis();
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

        Message m = new Message(s4StreamName, s4EventClassName,
                jsonDoc.toString());
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
    public String getHostName() {
        return hostName;
    }

    /**
     * @return the port
     */
    public int getPort() {
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
}
