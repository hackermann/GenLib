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
import genlib.extended.distributions.BasicTypeDistributions.DoubleDistribution;
import genlib.extended.distributions.LinearDistribution;
import genlib.standard.representations.AnyTypeStaticLength.AnyDoubleStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyLongStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance.AnyDoubleStaticLengthInstance;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance.AnyLongStaticLengthInstance;
import genlib.utils.Utils;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * With this class, your GenRepresentation will be represented
 * through an double-array with static length
 *
 * @author Hilmar
 */
public class DoubleStaticLength extends AnyDoubleStaticLength {

    /**
     * the random-values will be generated with this distribution
     */
    protected final DoubleDistribution distribution;

    /**
     * the constructor
     *
     * @param _length the static length of the arrays of the instances
     */
    public DoubleStaticLength(int _length) {
        super(_length);

        distribution = new LinearDistribution(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * the constructor
     *
     * @param _length the static length of the arrays of the instances
     * @param _distribution a user-defined distribution for the random-values
     * @throws NullPointerException if _distribution is null
     */
    public DoubleStaticLength(int _length, DoubleDistribution _distribution) {
        super(_length);

        if (_distribution == null)
            throw new NullPointerException("distribution can't be null.");

        distribution = _distribution;
    }

    @Override
    public AnyTypeStaticLengthInstance instantiateFromDoubles(double[] _array) {
        return new DoubleStaticLengthInstance(this, _array);
    }

    @Override
    public double getRandomDouble(Random random) {
        return distribution.getRandomDouble(random);
    }

    @Override
    public GenInstance instantiateRandom(AlgorithmStep step) {
        double [] randomArray = new double[length];
        for (int i=0; i<randomArray.length; i++)
            randomArray[i] = getRandomDouble(step.getRandom());
        return new DoubleStaticLengthInstance(this, randomArray);
    }

    @Override
    public List<Attribute> getAttributes() {
        return Utils.extendList( super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "distribution", distribution) );
    }

    /**
     * this class represents an DoubleStaticLength - instance
     */
    public static class DoubleStaticLengthInstance extends AnyDoubleStaticLengthInstance {

        /**
         * the double-array
         */
        protected final double [] array;

        /**
         * the constructor
         *
         * @param _parent the parent (GenRepresentation) of this instance
         * @param _array the double-array
         * @throws IllegalArgumentException if the length of the array is not matching with the length of the parent
         */
        public DoubleStaticLengthInstance(DoubleStaticLength _parent, double ... _array) {
            super(_parent);

            if (_array.length != _parent.getLength())
                throw new IllegalArgumentException("length of array not as expected, is: " + _array.length + ", expected: " + _parent.getLength());

            array = new double[_array.length];
            System.arraycopy(_array, 0, array, 0, array.length);
        }

        @Override
        public double getDoubleValue(int index) {
            if (index < 0 || index >= array.length)
                throw new IllegalArgumentException("index out of bounds");

            return array[index];
        }

        @Override
        public double[] getDoubleArray() {
            double [] convertedArray = new double [array.length];
            System.arraycopy(array, 0, convertedArray, 0, array.length);
            return convertedArray;
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.extendList( super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "array", array) );
        }

        @Override
        public int hashCode () {
            return Arrays.hashCode(array);
        }

    }

}
