/*
 * Copyright (c) 2010 Yahoo! Inc. All rights reserved.
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

import org.junit.Test;

public class TestWordGenerator {

    @Test
    public void test() {

        for (int wordSize = 10; wordSize <= 10000; wordSize *= 10) {

            WordGenerator wg = new WordGenerator(0, wordSize);
            long start = System.currentTimeMillis();
            
            for (int i = 0; i < 1000; i++)
                wg.getWord();
            
            long end = System.currentTimeMillis();

            System.out.println("Average Time for Word Size: " + wordSize
                    + " is " + (end - start) / 1000.0 + " ms.");
        }
    }
}
