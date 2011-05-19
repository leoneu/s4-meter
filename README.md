S4 Meter - A Distributed Performance Evaluation Framework for S4
================================================================ 

The goal of this project is to provide an end to end framework for running 
performance tests on S4 clusters.

The framework can manage multiple remote event generators. A single 
master app deploys pluggable event generators to the remote hosts. The 
remote generators can run as a service which is controlled by a single 
controller. The controller has the ability to configure and start
remote generators. It can also query the generator states and produce
test reports. The logic for the event generators and the S4 application
are bundled together as plugins.  

Implementation
--------------

The code is organized in three modules:

* common: code common to both the controller and the remote generators.
* controller: code that runs locally to control the tests.
* generator: code that is installed in remote hosts as a service.

Initial Test Case
-----------------

* Each event contains a document and a unique ID.
* The document is a sequence of random words.
* Each word is a sequence of random characters generated on the fly.
* Num words, word length, and alphabet are configurable.
* When initiated with the same seed, event generators generate exactly the
  same sequence of events. 
* The stream application parses the document, and emits Word Events.
* Each unique word is counted.


Installation and Run
--------------------

Set up S4 Image
---------------

<pre>
# Extract s4 image from tgz file.
tar xvzf  s4-0.3-SNAPSHOT-bin.tgz
rm -f  s4-0.3-SNAPSHOT-bin.tgz
cd s4-0.3-SNAPSHOT
export S4_IMAGE=`pwd`
</pre>

Build
-----

<pre>
cd myapps
git clone git://github.com/leoneu/s4-meter.git
cd s4-meter
gradlew install
</pre>

Deploy
------

This will install the S4 application in the $S4_IMAGE.
<pre>
gradle deploy
</pre>

Run
---

Start each command in a different window to see the updates on the standard output.

<pre>
# Start S4 server.
$S4_IMAGE/scripts/s4-start.sh -r client-adapter &

# Start S4 client adapter server.
$S4_IMAGE/scripts/run-client-adapter.sh -s client-adapter \
-g s4 -x -d $S4_IMAGE/s4-core/conf/default/client-stub-conf.xml &

# Start a generator listening on port 8182.
./s4-meter-generator/build/install/s4-meter-generator/bin/s4-meter-generator 8182

# Start a generator listening on port 8183.
./s4-meter-generator/build/install/s4-meter-generator/bin/s4-meter-generator 8183

# Start the controller.
./s4-meter-controller/build/install/s4-meter-controller/bin/s4-meter-controller
</pre>

What happened? The controller uploaded an event generator to the two generators we 
started and instructed the event generators to start. As a result the event generators
sent ten events. Each event contains six strings with random characters. The events are 
sent using the S4 Java client to an adaptor service. If you look at the terminal where 
you started the S4 server, you will see two sets of ten lines numbered 0-9.

To change the configuration, change the properties file in.

<pre>
cat s4-meter-controller/src/main/resources/s4-meter.properties
</pre>

To change the S4 application, edit the S4 configuration file.

<pre>
cat s4-meter-controller/src/main/resources/s4-meter-controller-conf.xml
</pre>


Implementation
--------------

Generators are controlled using a REST API. To implement the interface we used
the Restlet framework (http://www.restlet.org) because it is lightweight and easy to use. The concrete
classes used to generate events are loaded into the generators by the controller 
every time a test starts. To write a new event generator follow the pattern in:

<pre>
ls -l s4-meter-controller/src/main/java/io/s4/meter/controller/plugin/randomdoc
</pre>

In this project we used Guice (http://code.google.com/p/google-guice) for dependency injection. All the configuration 
logic is implemented in modules. 
