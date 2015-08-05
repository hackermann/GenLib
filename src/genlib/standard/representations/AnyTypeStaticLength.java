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

import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.utils.Utils;
import java.util.List;
import java.util.Random;

/**
 * This abstract class describe a representation of individuum, that get
 * represented through arrays as genoType or phenoType. The length of this
 * array is static and there has to be the possibility, to represent every
 * value in the array as long- OR double-value.
 *
 * @author Hilmar
 */
public abstract class AnyTypeStaticLength extends GenRepresentation {

    /**
     * the static length of the arrays of the instances
     */
    protected final int length;

    /**
     * The constructor
     *
     * @param _length the static length of the arrays of the instances
     */
    public AnyTypeStaticLength(int _length) {
        if (_length <= 0)
            throw new IllegalArgumentException("invalid length (has to be >= 1)");
        length = _length;
    }

    /**
     * get the static length of the arrays of the instances
     *
     * @return the length
     */
    public int getLength() {
        return length;
    }

    @Override
    protected boolean isEquals(GenRepresentation other) {
        return getClass().equals(other.getClass()) && ((AnyTypeStaticLength)other).length == length;
    }

    @Override
    public List<Attribute> getAttributes() {
        return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "length", length) );
    }

    /**
     * Is the basic type of the arrays of the instances more
     * a long (short, int, boolean as example) or more a double?
     *
     * @return true, if it is more a long-type
     */
    public abstract boolean isLongType();

    /**
     * create an instance with a long-array given
     *
     * @param _array the array
     * @return the instance with the converted array
     */
    public abstract AnyTypeStaticLengthInstance instantiateFromLongs (long [] _array);

    /**
     * create an instance with a double-array given
     *
     * @param _array the array
     * @return the instance with the converted array
     */
    public abstract AnyTypeStaticLengthInstance instantiateFromDoubles (double [] _array);

    /**
     * get a random value as long, matching with the basic-type of the array
     *
     * @param random a random-object
     * @return the requested long
     */
    public abstract long getRandomLong(Random random);

    /**
     * get a random value as double, matching with the basic-type of the array
     *
     * @param random a random-object
     * @return the requested double
     */
    public abstract double getRandomDouble(Random random);

    /**
     * converts a given long value in a long value in bounds (compatible with this
     * static-length-type)
     *
     * @param value the input value
     * @return the value in bounds
     */
    public abstract long applyBounds(long value);

    /**
     * converts a given double value in a double value in bounds (compatible with this
     * static-length-type)
     *
     * @param value the input value
     * @return the value in bounds
     */
    public abstract double applyBounds(double value);

    /**
     * this class represents AnyTypeStaticLength, when the basic type of
     * the array is a long
     */
    public abstract static class AnyLongStaticLength extends AnyTypeStaticLength {

        /**
         * the constructor
         *
         * @param _length the static length of the arrays of the instances
         */
        public AnyLongStaticLength(int _length) {
            super(_length);
        }

        @Override
        public boolean isLongType() {
            return true;
        }

        @Override
        public AnyTypeStaticLengthInstance instantiateFromDoubles(double[] _array) {
            long [] convertedArray = new long [_array.length];
            for (int i=0; i<convertedArray.length; i++)
                convertedArray[i] = (long)_array[i];
            return instantiateFromLongs(convertedArray);
        }

        @Override
        public double getRandomDouble(Random random) {
            return (double)getRandomLong(random);
        }

        @Override
        public double applyBounds(double value) {
            return applyBounds((long)value);
        }

    }

    /**
     * this class represents AnyTypeStaticLength, when the basic type of
     * the array is a double
     */
    public abstract static class AnyDoubleStaticLength extends AnyTypeStaticLength {

        /**
         * the constructor
         *
         * @param _length the static length of the arrays of the instances
         */
        public AnyDoubleStaticLength(int _length) {
            super(_length);
        }

        @Override
        public boolean isLongType() {
            return false;
        }

        @Override
        public AnyTypeStaticLengthInstance instantiateFromLongs(long[] _array) {
            double [] convertedArray = new double [_array.length];
            for (int i=0; i<convertedArray.length; i++)
                convertedArray[i] = (double)_array[i];
            return instantiateFromDoubles(convertedArray);
        }

        @Override
        public long getRandomLong(Random random) {
            return (long)getRandomDouble(random);
        }

        @Override
        public long applyBounds(long value) {
            return (long)applyBounds((double)value);
        }

    }

    /**
     * this class represents an instance of AnyTypeStaticLength
     */
    public static abstract class AnyTypeStaticLengthInstance extends GenInstance {

        /**
         * the constructor
         *
         * @param _parent the parent (GenRepresentation) of this instance
         */
        public AnyTypeStaticLengthInstance (AnyTypeStaticLength _parent) {
            super(_parent);
        }

        /**
         * returns the value of one array-entry as long
         *
         * @param index the index of the requested array-value
         * @return the value as long
         */
        public abstract long getLongValue (int index);

        /**
         * returns the value of one array-entry as double
         *
         * @param index the index of the requested array-value
         * @return the value as double
         */
        public abstract double getDoubleValue (int index);

        /**
         * returns the array, represented as long-array
         *
         * @return the converted long-array
         */
        public abstract long [] getLongArray ();

        /**
         * returns the array, represented as double-array
         *
         * @return the converted double-array
         */
        public abstract double [] getDoubleArray ();

        /**
         * The subclass of AnyTypeStaticLengthInstance, if the basic-type is long
         */
        public abstract static class AnyLongStaticLengthInstance extends AnyTypeStaticLengthInstance {

            /**
             * the constructor
             *
             * @param _parent the parent (GenRepresentation) of this instance
             */
            public AnyLongStaticLengthInstance (AnyLongStaticLength _parent) {
                super(_parent);
            }

            @Override
            public double getDoubleValue(int index) {
                return (double)getLongValue(index);
            }

            @Override
            public double[] getDoubleArray() {
                long [] longArray = getLongArray();
                double [] convertedArray = new double [longArray.length];
                for (int i=0; i<convertedArray.length; i++)
                    convertedArray[i] = (double)longArray[i];
                return convertedArray;
            }

        }

        /**
         * The subclass of AnyTypeStaticLengthInstance, if the basic-type is double
         */
        public abstract static class AnyDoubleStaticLengthInstance extends AnyTypeStaticLengthInstance {

            /**
             * the constructor
             *
             * @param _parent the parent (GenRepresentation) of this instance
             */
            public AnyDoubleStaticLengthInstance (AnyDoubleStaticLength _parent) {
                super(_parent);
            }

            @Override
            public long getLongValue(int index) {
                return (long)getDoubleValue(index);
            }

            @Override
            public long[] getLongArray() {
                double [] doubleArray = getDoubleArray();
                long [] convertedArray = new long [doubleArray.length];
                for (int i=0; i<convertedArray.length; i++)
                    convertedArray[i] = (long)doubleArray[i];
                return convertedArray;
            }

        }

    }

}
