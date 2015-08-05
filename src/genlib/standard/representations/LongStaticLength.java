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
import genlib.extended.distributions.BasicTypeDistributions.LongDistribution;
import genlib.extended.distributions.LinearDistribution;
import genlib.standard.representations.AnyTypeStaticLength.AnyLongStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance.AnyLongStaticLengthInstance;
import genlib.utils.Utils;
import java.util.List;
import java.util.Random;

/**
 * With this class, your GenRepresentation will be represented
 * through a long-array with static length
 *
 * @author Hilmar
 */
public class LongStaticLength extends AnyLongStaticLength {

    /**
     * the random-values will be generated with this distribution
     */
    protected final LongDistribution distribution;

    /**
     * the constructor
     *
     * @param _length the static length of the arrays of the instances
     */
    public LongStaticLength(int _length) {
        super(_length);

        distribution = new LinearDistribution(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * the constructor
     *
     * @param _length the static length of the arrays of the instances
     * @param _distribution a user-defined distribution for the random-values
     * @throws NullPointerException if _distribution is null
     */
    public LongStaticLength(int _length, LongDistribution _distribution) {
        super(_length);

        if (_distribution == null)
            throw new NullPointerException("distribution can't be null.");

        distribution = _distribution;
    }

    @Override
    public AnyTypeStaticLengthInstance instantiateFromLongs(long[] _array) {
        return new LongStaticLengthInstance(this, _array);
    }

    @Override
    public long getRandomLong(Random random) {
        return distribution.getRandomLong(random);
    }

    @Override
    public GenInstance instantiateRandom(AlgorithmStep step) {
        long [] randomArray = new long[length];
        for (int i=0; i<randomArray.length; i++)
            randomArray[i] = getRandomLong(step.getRandom());
        return new LongStaticLengthInstance(this, randomArray);
    }

    @Override
    public long applyBounds(long value) {
        return distribution.getMinMaxLong().applyBounds(value);
    }

    @Override
    public List<Attribute> getAttributes() {
        return Utils.extendList( super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "distribution", distribution) );
    }


    /**
     * this class represents an LongStaticLength - instance
     */
    public static class LongStaticLengthInstance extends AnyLongStaticLengthInstance {

        /**
         * the long-array
         */
        protected final long [] array;

        /**
         * the constructor
         *
         * @param _parent the parent (GenRepresentation) of this instance
         * @param _array the long-array
         * @throws IllegalArgumentException if the length of the array is not matching with the length of the parent
         */
        public LongStaticLengthInstance(LongStaticLength _parent, long ... _array) {
            super(_parent);

            if (_array.length != _parent.getLength())
                throw new IllegalArgumentException("length of array not as expected, is: " + _array.length + ", expected: " + _parent.getLength());

            array = new long[_array.length];
            System.arraycopy(_array, 0, array, 0, array.length);
        }

        @Override
        public long getLongValue(int index) {
            if (index < 0 || index >= array.length)
                throw new IllegalArgumentException("index out of bounds");

            return array[index];
        }

        @Override
        public long[] getLongArray() {
            long [] convertedArray = new long [array.length];
            System.arraycopy(array, 0, convertedArray, 0, array.length);
            return convertedArray;
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.extendList( super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "array", array) );
        }

    }

}
