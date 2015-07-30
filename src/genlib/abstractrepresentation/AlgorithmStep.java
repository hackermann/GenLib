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

package genlib.abstractrepresentation;

import java.util.Random;

/**
 * The AlgorithmStep defines a current position in one algorithm run. It defines
 * as example the current generation.
 *
 * @author Hilmar
 */
public abstract class AlgorithmStep extends GenObject {

    /**
     * The algorithm shall end?
     *
     * @return true, if the algorithm should still run
     */
    public abstract boolean hasNext();

    /**
     * returns the next step, as example with incremented generation
     *
     * @return the next AlgorithmStep
     */
    public abstract AlgorithmStep next();

    /**
     * returns the parent of this step
     *
     * @return the parent
     */
    public abstract AlgorithmPass getParent();

    /**
     * returns a random-object. It is usually initialized once
     * in the parent AlgorithmPass.
     *
     * @return a random-object
     */
    public abstract Random getRandom();

}
