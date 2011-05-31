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
package io.s4.meter.controller.plugin.randomdoc;

import java.util.Random;
import static java.lang.Math.abs;

/**
 * Utility class to generate random words.
 * 
 * The sequence of words is a deterministic function of the seed value.
 * 
 * @author Leo Neumeyer
 * 
 */
public class RandomWord {

	private static int DEFAULT_WORD_SIZE = 10;
	private Random random;
	private int wordLength = 10;
	private char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	private int alphabetSize = alphabet.length;
	private char[] word;

	/**
	 * Creates a random word generator.Uses a seed value of zero and a default
	 * word length.
	 * 
	 * @see #RandomWord(long seed, int wordLength)
	 */
	public RandomWord() {
		this(0, DEFAULT_WORD_SIZE);
	}

	/**
	 * Creates a random word generator.Uses a default word length.
	 * 
	 * @param seed
	 *            the seed for the random number generator.
	 * @see #RandomWord(long seed, int wordLength)
	 */
	public RandomWord(long seed) {
		this(seed, DEFAULT_WORD_SIZE);
	}

	/**
	 * Creates a random word generator.
	 * 
	 * @param seed
	 *            the seed for the random number generator.
	 * @param wordLength
	 *            the desired word length.
	 */
	public RandomWord(long seed, int wordLength) {
		this.random = new Random(seed);
		this.wordLength = wordLength;
		word = new char[wordLength];
	}

	/**
	 * * Creates a random word generator.
	 * 
	 * @param seed
	 *            the seed for the random number generator.
	 * @param wordLength
	 *            the desired word length.
	 * @param alphabet
	 *            a string with the characters to be used in the words.
	 */
	public RandomWord(long seed, int wordLength, String alphabet) {
		
		this(seed, wordLength);
		this.alphabet = alphabet.toCharArray();
		this.alphabetSize = this.alphabet.length;
	}

	/**
	 * @return a random word.
	 */
	public String getWord() {

		for (int i = 0; i < wordLength; i++) {

			int num = abs(random.nextInt()) % alphabetSize;
			word[i] = alphabet[num];
		}
		return new String(word);
	}
}
