package io.s4.meter.common;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class EventGenerator implements Serializable {

    abstract public void start();

    abstract public void stop();
}
