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
package io.s4.app.words;

import java.util.Random;
import static java.lang.Math.abs;

public class WordGenerator {

    static int DEFAULT_WORD_SIZE = 10;
    Random random;
    int wordLength = 10;
    char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    int alphabetSize = alphabet.length;
    char[] word;
    
    public WordGenerator() {
        this(0, DEFAULT_WORD_SIZE);
    }
    
    public WordGenerator(long seed) {
        this(seed, DEFAULT_WORD_SIZE);
    }
    
    public WordGenerator(long seed, int wordLength) {
        this.random = new Random(seed);
        this.wordLength = wordLength;
        word = new char[wordLength];
    }
        
    String getWord() {
        
        for (int i=0; i<wordLength; i++) {
            
            int num = abs(random.nextInt()) % alphabetSize;
            word[i] = alphabet[num];
        }
        return new String(word);
    }
}
