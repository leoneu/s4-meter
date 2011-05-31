/*
 * Copyright (c) 2011 Yahoo! Inc. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 	        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License. See accompanying LICENSE file. 
 */

package io.s4.meter.controller.plugin.randomdoc;

/**
 * @author Leo Neumeyer
 * 
 */
public class Word {

	private String word;
	private String docId;
	private int count;

	public Word() {
	
	}
	
	public Word(String word, String docId, int count) {
		super();
		this.word = word;
		this.docId = docId;
		this.count = count;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the id
	 */
	public String getDocId() {
		return this.docId;
	}

	/**
	 * @param docID
	 *            the docID to set
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{docId:").append(docId).append(",word:").append(word)
				.append(",count:").append(count).append("}");
		return sb.toString();
	}
}