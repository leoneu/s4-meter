package io.s4.meter.common;

import io.s4.client.Driver;
import io.s4.client.Message;

import java.io.IOException;
import java.io.Serializable;

import org.json.JSONObject;

@SuppressWarnings("serial")
public abstract class EventGenerator implements Serializable {

    final private String hostName;
    final private int port;
    final protected String s4StreamName;
    final protected String s4EventClassName;
    transient protected Driver driver;

    public EventGenerator(String hostName, int port, String s4StreamName,
            String s4EventClassName) {
        super();
        this.port = port;
        this.hostName = hostName;
        this.s4StreamName = s4StreamName;
        this.s4EventClassName = s4EventClassName;
    }

    /*
     * This method is called when the object is deserialized and can be used to
     * initialized transient fields.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {

        in.defaultReadObject();
        init();
        System.out.println("XXXXXXXXXXXXXInit.");//DEBUG

    }

    protected void init() {
        //private void init() {

        System.out.println("Initializing S4 driver for EventGenerator.");

        driver = new Driver(hostName, port);
        driver.setDebug(true); // DEBUG

        try {
            if (!driver.init()) {
                System.err.println("Driver initialization failed.");
                System.exit(1);
            }

            if (!driver.connect()) {
                System.err.println("Driver initialization failed.");
                System.exit(1);
            }
            System.err.println("Driver state after connect: " + driver.getState() + "\n"); //DEBUG
            
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    abstract public void start();

    protected void send(JSONObject jsonDoc) {

        System.out.println("Sending: " + jsonDoc.toString() + " " + s4StreamName + " " + s4EventClassName);

        Message m = new Message(s4StreamName, s4EventClassName,
                jsonDoc.toString());
        try {
            driver.send(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract public void stop();

    public void close() {
        try {
            driver.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
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
}
