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

package genlib.standard.representations;

import genlib.abstractrepresentation.AlgorithmStep;
import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.utils.Utils;
import java.util.List;

/**
 * The representation of a binary-array with given, static length
 *
 * @author Hilmar
 */
public class BinaryStaticLength extends GenRepresentation {

    /**
     * the length of the binary-array
     */
    protected final int length;

    /**
     * the constructor
     *
     * @throws IllegalArgumentException if length is smaller than 1
     */
    public BinaryStaticLength (int _length) {
        length = _length;

        if (length <= 0)
            throw new IllegalArgumentException("invalid length: '" + length + "'.");
    }

    /**
     * the getter for the length
     *
     * @return the length
     */
    public int getLength () {
        return length;
    }

    @Override
    protected boolean isEquals(GenRepresentation other) {
        return (other instanceof BinaryStaticLength &&
                ((BinaryStaticLength)other).length == length);
    }

    @Override
    public GenInstance instantiateRandom(AlgorithmStep step) {
        boolean [] array = new boolean[length];
        for (int i=0; i<array.length; i++)
            array[i] = step.getRandom().nextBoolean();
        return new BinaryStaticLengthInstance(this, array);
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "length", length) );
    }

}
