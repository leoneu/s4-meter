S4 Meter - A Performance Evaluation Framework for S4
====================================================

The goal of this project is to provide an end to end framework for running 
performance tests on S4 clusters.

The framework should be able to manage multiple event generators. A single 
master app should be able to configure, start, stop, and query all event 
generators. The master should be capable of to carry out test plans 
automatically, detect failures, and create comprehensive reports.

Task: The initial task is as follows:

* Each event contains a document and a unique ID.
* The document is a sequence of random words.
* Each word is a sequence of random characters generated on the fly.
* Num words, word length, and alphabet are configurable.
* When initiated with the same seed, event generators generate exactly the
  same sequence of events. 
* The stream application parses the document, and emits Word Events.
* Each unique word is counted.


Description
-----------

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



