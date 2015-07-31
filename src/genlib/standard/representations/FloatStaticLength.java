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
import genlib.standard.distributions.BasicTypeDistributions.DoubleDistribution;
import genlib.standard.distributions.BasicTypeDistributions.FloatDistribution;
import genlib.standard.distributions.LinearDistribution;
import genlib.standard.representations.AnyTypeStaticLength.AnyDoubleStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyLongStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance.AnyDoubleStaticLengthInstance;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance.AnyLongStaticLengthInstance;
import genlib.utils.Utils;
import java.util.List;
import java.util.Random;

/**
 * With this class, your GenRepresentation will be represented
 * through an float-array with static length
 *
 * @author Hilmar
 */
public class FloatStaticLength extends AnyDoubleStaticLength {

    /**
     * the random-values will be generated with this distribution
     */
    protected final FloatDistribution distribution;

    /**
     * the constructor
     *
     * @param _length the static length of the arrays of the instances
     */
    public FloatStaticLength(int _length) {
        super(_length);

        distribution = new LinearDistribution(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    /**
     * the constructor
     *
     * @param _length the static length of the arrays of the instances
     * @param _distribution a user-defined distribution for the random-values
     * @throws NullPointerException if _distribution is null
     */
    public FloatStaticLength(int _length, FloatDistribution _distribution) {
        super(_length);

        if (_distribution == null)
            throw new NullPointerException("distribution can't be null.");

        distribution = _distribution;
    }

    @Override
    public AnyTypeStaticLengthInstance instantiateFromDoubles(double[] _array) {
        float [] convertedArray = new float [_array.length];
        for (int i=0; i<convertedArray.length; i++)
            convertedArray[i] = (float)_array[i];
        return new FloatStaticLengthInstance(this, convertedArray);
    }

    @Override
    public double getRandomDouble(Random random) {
        return distribution.getRandomFloat(random);
    }

    @Override
    public GenInstance instantiateRandom(AlgorithmStep step) {
        float [] randomArray = new float[length];
        for (int i=0; i<randomArray.length; i++)
            randomArray[i] = distribution.getRandomFloat(step.getRandom());
        return new FloatStaticLengthInstance(this, randomArray);
    }

    /**
     * this class represents an FloatStaticLength - instance
     */
    public static class FloatStaticLengthInstance extends AnyDoubleStaticLengthInstance {

        /**
         * the float-array
         */
        protected final float [] array;

        /**
         * the constructor
         *
         * @param _parent the parent (GenRepresentation) of this instance
         * @param _array the float-array
         * @throws IllegalArgumentException if the length of the array is not matching with the length of the parent
         */
        public FloatStaticLengthInstance(FloatStaticLength _parent, float ... _array) {
            super(_parent);

            if (_array.length != _parent.getLength())
                throw new IllegalArgumentException("length of array not as expected, is: " + _array.length + ", expected: " + _parent.getLength());

            array = new float[_array.length];
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
            for (int i=0; i<convertedArray.length; i++)
                convertedArray[i] = (double)array[i];
            return convertedArray;
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.extendList( super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "array", array) );
        }

    }

}
