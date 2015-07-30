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

/**
 * the representation of a geno- or pheno-type. It just defines the type of
 * representation itself, NOT the content of one of its instances.
 *
 * @author Hilmar
 */
public abstract class GenRepresentation extends GenObject {

    /**
     * this method has to be implemented. It is the same as equals(Object), but it is guaranteed,
     * that the parameter is never null.
     *
     * @param other the other GenRepresentation, we will do an equals-check with, can't be null
     * @return true, if equals
     */
    protected abstract boolean isEquals (GenRepresentation other);

    /**
     * instantiate a random instance of this type
     *
     * @param step the state of the running algorithm
     * @return the random instance
     */
    public abstract GenInstance instantiateRandom(AlgorithmStep step);

}
