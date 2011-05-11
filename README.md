S4 Meter - A Performance Evaluation Framework for S4
==================================================== 

DRAFT DRAFT

The goal of this project is to provide an end to end framework for running 
performance tests on S4 clusters.

The framework can manage multiple remote event generators. A single 
master app deploys pluggable event generators to the remote hosts. The 
remote generators can run as a service which is controlled by the main
app. The controller has the ability to configure, start, and stop, the
remote generators. It can also query the generator states and produce
test reports. The logic for the event generators and the S4 application
are bundled together as plugins.  

Implementation
--------------

The code is organized in three modules:

* common: code common to both the controller and the remote generators.
* controller: code that runs locally in the controller.
* generator: code that is deployed to remote hosts to generate events 
without including the concrete implementation of the event generators.

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

# Extract s4 image from tgz file.
tar xvzf  s4-0.3-SNAPSHOT-bin.tgz
rm -f  s4-0.3-SNAPSHOT-bin.tgz
cd s4-0.3-SNAPSHOT
export S4_IMAGE=`pwd`

Build
-----

<pre>
cd myapps
extract words.tar.gz
or git

cd words
gradle clean build
</pre>

Deploy
------

<pre>
gradle deploy
</pre>

Run
---

<pre>
# Start S4 server.
$S4_IMAGE/scripts/s4-start.sh -r client-adapter &

# Start S4 client adapter server.
$S4_IMAGE/scripts/run-client-adapter.sh -s client-adapter \
-g s4 -x -d $S4_IMAGE/s4-core/conf/default/client-stub-conf.xml &

# Inject events.
java -classpath "$S4_IMAGE/s4-apps/words/lib/*" \
io.s4.app.words.Injector localhost 2334 RawDoc \
io.s4.app.words.Document
</pre>



