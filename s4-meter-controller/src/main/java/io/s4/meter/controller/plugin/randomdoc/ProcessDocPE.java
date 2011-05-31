/*
 * Copyright (c) 2011 Yahoo! Inc. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *            http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License. See accompanying LICENSE file. 
 */
package io.s4.meter.controller.plugin.randomdoc;

import io.s4.dispatcher.EventDispatcher;
import io.s4.processor.AbstractPE;

/**
 * PE that parses incoming documents.
 * 
 * @author Leo Neumeyer
 * 
 */
public class ProcessDocPE extends AbstractPE {

	private String id;
	private EventDispatcher dispatcher;
	private String outputStreamName;

	public EventDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String getOutputStreamName() {
		return outputStreamName;
	}

	public void setOutputStreamName(String outputStreamName) {
		this.outputStreamName = outputStreamName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void processEvent(Document doc) {

		if (doc.getText() == null)
			return;

		/*
		 * Parse document and dispatch words. For efficiency we may want to add
		 * a Map to count repeated words in document before dispatching.
		 */
		for (String token : doc.getText().split("\\s")) {

			Word word = new Word(token, doc.getId(), 1);
			dispatcher.dispatchEvent(outputStreamName, word);
		}
		System.out.printf("Doc ID: %20s, Text: %s\n", doc.getId(),
				doc.getText());
	}

	@Override
	public void output() {
		// not called in this example
	}

    @Override
    public String getId() {
        return this.id;
    }

}
