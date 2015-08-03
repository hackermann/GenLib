/*
 * The MIT License
 *
 * Copyright 2015 Hilmar.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package genlib.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * With every insert of a value, this map counts the occurence
 * of this value.
 *
 * @author Hilmar
 */
public class CountingMap <E> {

    /**
     * the internal representation of the map
     */
    protected final Map <E, Integer> countMap = new HashMap();

    /**
     * insert a value
     *
     * @param entry the value
     */
    public void add (E entry) {
        if (countMap.containsKey(entry))
            countMap.put(entry, countMap.get(entry)+1);
        else
            countMap.put(entry, 1);
    }

    /**
     * how often was this value inserted?
     *
     * @param entry the value
     * @return the number of inserts of this value, 0 if it never got added
     */
    public int getCount (E entry) {
        if (countMap.containsKey(entry))
            return countMap.get(entry);
        else
            return 0;
    }

    /**
     * the key-set of all values, that got added at least 1 time
     *
     * @return the key-set
     */
    public Set <E> keySet () {
        return countMap.keySet();
    }

}
