/*
 * Copyright (c) 2011 Yahoo! Inc. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License. See accompanying LICENSE file. 
 */
package io.s4.meter.common;

import io.s4.client.Driver;
import io.s4.client.Message;
import io.s4.client.ReadMode;

import java.io.IOException;
import java.io.Serializable;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Generates events in JSON format using pluggable event generation logic
 * provided by the application developer.
 * 
 * This class was designed primarily to implement distributed performance
 * testing of the S4 cluster. Instances can be created by a central process and
 * sent to remote generator containers where they are de-serialized and started.
 * 
 * By design, event generators are disposable objects. That is, event generators
 * are created with an immutable configuration used once to generate events, and
 * discarded. Create a new instance of <code>EventGenerator</code> every time a
 * new event stream is needed.
 * 
 * This is a base class that needs to be extended by the application developer.
 * The concrete class is required to implement the <code>init</code> and
 * <code>getDocument</code> methods.
 * 
 * @author Leo Neumeyer
 */
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
	transient private boolean isStarted;

	/**
	 * Instances can only be created using this constructor. No setter methods
	 * are provided.
	 * 
	 * @param hostname
	 *            the hostname of the S4 client adaptor server.
	 * @param port
	 *            the port of the S4 client adaptor server.
	 * @param s4StreamName
	 *            the stream name used in the S4 application that will process
	 *            the incoming events.
	 * @param s4EventClassName
	 *            the name of the event class to which the JSON events must be
	 *            converted.
	 * @param eventRate
	 *            the target event rate. May not be achieved if sufficient
	 *            resources are not available.
	 * @param numEvents
	 *            the total number of events that will be generated.
	 * 
	 * 
	 */
	protected EventGenerator(String hostname, String port, String s4StreamName,
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
		isStarted = true;
		isStarted = false;
		isInterrupted = false;
		eventCount = 0;
		modulus = (int) (10000f / (float) eventPeriod); // Every 10 secs.
		setDriver(new Driver(hostname, Integer.parseInt(port)));
		driver.setReadMode(ReadMode.None);

		if (logger.isDebugEnabled())
			driver.setDebug(false);
		else
			driver.setDebug(true);

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

	/**
	 * Starts the event generator in the remote host. Calls the
	 * <code>init</code> method in the concrete class, sends
	 * <code>numEvents</code> events using the S4 driver, and closes the
	 * connection to the S4 driver.
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {

		if (isStarted) {
			logger.error("Event generator can only be started once. Create a new instance to start a new stream.");
			throw new Exception();
		}
		isStarted = true;

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

	/**
	 * @param docId
	 *            unique ID for this document
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	private void send(long eventID) throws InterruptedException, JSONException {

		String avgRate;
		StringBuilder docId = new StringBuilder();
		docId.append(hostname).append("-").append(driver.hashCode()).append("-")
				.append(eventID);
		JSONObject jsonDoc = getDocument(docId.toString());
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

			logger.error("Unable not send a message using the S4 driver.", e);

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

	/**
	 * Stops the event generation process when a process is active and safely
	 * closes the connection to the S4 client adaptor. Does nothing otherwise.
	 */
	public void stop() {

		isInterrupted = true;

	}

	/**
	 * Closes the connection to the S4 client adaptor.
	 */
	public void close() {
		try {
			if (driver != null)
				driver.disconnect();
		} catch (Exception e) {
			logger.error("Error when trying to disconnect driver.");
		}
	}

	/**
	 * @return the hostname of the S4 client adaptor.
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @return the port of the S4 client adaptor.
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @return the S4 client driver.
	 */
	public Driver getDriver() {
		return driver;
	}

	/**
	 * @param driver
	 *            the S4 client driver.
	 */
	final private void setDriver(Driver driver) {
		this.driver = driver;
	}

	/**
	 * @return the eventRate.
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

	/**
	 * Implements the event generation logic.
	 * 
	 * @param eventID
	 *            the eventID is a positive number between <code>0</code> and
	 *            <code>numEvents</code>.
	 * @return JSONObject the document to be sent to the S4 client adaptor.
	 * @throws JSONException
	 */
	abstract protected JSONObject getDocument(String docId)
			throws JSONException;

	/**
	 * Implements initialization logic for the <code>getDocument</code> method.
	 */
	abstract protected void init();
}
