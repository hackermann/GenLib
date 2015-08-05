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
import genlib.standard.representations.AnyTypeStaticLength.AnyLongStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance.AnyLongStaticLengthInstance;
import genlib.utils.Utils;
import java.util.List;
import java.util.Random;

/**
 * With this class, your GenRepresentation will be represented
 * through a boolean-array with static length
 *
 * @author Hilmar
 */
public class BooleanStaticLength extends AnyLongStaticLength {

    /**
     * the constructor
     *
     * @param _length the static length of the arrays of the instances
     */
    public BooleanStaticLength(int _length) {
        super(_length);
    }

    @Override
    public AnyTypeStaticLengthInstance instantiateFromLongs(long[] _array) {
        boolean [] convertedArray = new boolean [_array.length];
        for (int i=0; i<convertedArray.length; i++)
            convertedArray[i] = _array[i] != 0;
        return new BooleanStaticLengthInstance(this, convertedArray);
    }

    @Override
    public long getRandomLong(Random random) {
        return random.nextBoolean() ? 1 : 0;
    }

    @Override
    public GenInstance instantiateRandom(AlgorithmStep step) {
        boolean [] randomArray = new boolean[length];
        for (int i=0; i<randomArray.length; i++)
            randomArray[i] = step.getRandom().nextBoolean();
        return new BooleanStaticLengthInstance(this, randomArray);
    }

    @Override
    public long applyBounds(long value) {
        return (value <= 0 ? 0 : 1);
    }

    /**
     * this class represents an BooleanStaticLength - instance
     */
    public static class BooleanStaticLengthInstance extends AnyLongStaticLengthInstance {

        /**
         * the boolean-array
         */
        protected final boolean [] array;

        /**
         * the constructor
         *
         * @param _parent the parent (GenRepresentation) of this instance
         * @param _array the boolean-array
         * @throws IllegalArgumentException if the length of the array is not matching with the length of the parent
         */
        public BooleanStaticLengthInstance(BooleanStaticLength _parent, boolean ... _array) {
            super(_parent);

            if (_array.length != _parent.getLength())
                throw new IllegalArgumentException("length of array not as expected, is: " + _array.length + ", expected: " + _parent.getLength());

            array = new boolean[_array.length];
            System.arraycopy(_array, 0, array, 0, array.length);
        }

        @Override
        public long getLongValue(int index) {
            if (index < 0 || index >= array.length)
                throw new IllegalArgumentException("index out of bounds");

            return (array[index] ? 1 : 0);
        }

        @Override
        public long[] getLongArray() {
            long [] convertedArray = new long [array.length];
            for (int i=0; i<convertedArray.length; i++)
                convertedArray[i] = array[i] ? 1 : 0;
            return convertedArray;
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.extendList( super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "array", array) );
        }

    }

}
