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

package genlib.extended.distributions;

import genlib.abstractrepresentation.GenObject;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.utils.Utils;
import java.util.List;
import java.util.Random;

/**
 * This class contains interfaces for basic-type-distributions
 *
 * @author Hilmar
 */
public class BasicTypeDistributions {

    /**
     * instantiation not allowed
     */
    private BasicTypeDistributions () {}

    /**
     * a distribution with the ability, to return random-long numbers
     */
    public interface LongDistribution {

        /**
         * get a random-number of the distribution
         *
         * @param random a random-object
         * @return the requested random-number
         */
        public long getRandomLong(Random random);

        /**
         * returns the minimum possible value and the maximum possible
         * value of this distribution
         *
         * @return the minimum and maximum values
         */
        public MinMaxLong getMinMaxLong();

    }

    /**
     * a distribution with the ability, to return random-int numbers
     */
    public interface IntDistribution {

        /**
         * get a random-number of the distribution
         *
         * @param random a random-object
         * @return the requested random-number
         */
        public int getRandomInt(Random random);

        /**
         * returns the minimum possible value and the maximum possible
         * value of this distribution
         *
         * @return the minimum and maximum values
         */
        public MinMaxLong getMinMaxLong();

    }

    /**
     * a distribution with the ability, to return random-short numbers
     */
    public interface ShortDistribution {

        /**
         * get a random-number of the distribution
         *
         * @param random a random-object
         * @return the requested random-number
         */
        public short getRandomShort(Random random);

        /**
         * returns the minimum possible value and the maximum possible
         * value of this distribution
         *
         * @return the minimum and maximum values
         */
        public MinMaxLong getMinMaxLong();

    }

    /**
     * a distribution with the ability, to return random-char numbers
     */
    public interface CharDistribution {

        /**
         * get a random-number of the distribution
         *
         * @param random a random-object
         * @return the requested random-number
         */
        public char getRandomChar(Random random);

        /**
         * returns the minimum possible value and the maximum possible
         * value of this distribution
         *
         * @return the minimum and maximum values
         */
        public MinMaxLong getMinMaxLong();

    }

    /**
     * a distribution with the ability, to return random-byte numbers
     */
    public interface ByteDistribution {

        /**
         * get a random-number of the distribution
         *
         * @param random a random-object
         * @return the requested random-number
         */
        public byte getRandomByte(Random random);

        /**
         * returns the minimum possible value and the maximum possible
         * value of this distribution
         *
         * @return the minimum and maximum values
         */
        public MinMaxLong getMinMaxLong();

    }

    /**
     * a distribution with the ability, to return random-double numbers
     */
    public interface DoubleDistribution {

        /**
         * get a random-number of the distribution
         *
         * @param random a random-object
         * @return the requested random-number
         */
        public double getRandomDouble(Random random);

        /**
         * returns the minimum possible value and the maximum possible
         * value of this distribution
         *
         * @return the minimum and maximum values
         */
        public MinMaxDouble getMinMaxDouble();

    }

    /**
     * a distribution with the ability, to return random-float numbers
     */
    public interface FloatDistribution {

        /**
         * get a random-number of the distribution
         *
         * @param random a random-object
         * @return the requested random-number
         */
        public float getRandomFloat(Random random);

        /**
         * returns the minimum possible value and the maximum possible
         * value of this distribution
         *
         * @return the minimum and maximum values
         */
        public MinMaxDouble getMinMaxDouble();

    }

    /**
     * a representation for a long minimum and a long maximum value
     */
    public static class MinMaxLong extends GenObject {

        /**
         * the minimum and maximum values
         */
        protected final long min, max;

        /**
         * the constructor
         *
         * @param _min the minimum
         * @param _max the maximum
         * @throws IllegalArgumentException if max is smaller than min
         */
        public MinMaxLong (long _min, long _max) {
            if (_max < _min)
                throw new IllegalArgumentException("max has to be >= min.");

            min = _min;
            max = _max;
        }

        /**
         * return the minimum
         *
         * @return the minimum
         */
        public long getMin () {
            return min;
        }

        /**
         * return the maximum
         *
         * @return the maximum
         */
        public long getMax () {
            return max;
        }

        /**
         * is the given value in the bounds?
         *
         * @param value the input value
         * @return true, if it is in bounds
         */
        public boolean inBounds(long value) {
            return (value >= min && value <= max);
        }

        /**
         * apply the bounds to the given value
         *
         * @param value the input value
         * @return the value, in bounds
         */
        public long applyBounds(long value) {
            if (value < min)
                return min;
            if (value > max)
                return max;
            return value;
        }

        /**
         * returns the distance between min and max
         *
         * @return the distance
         */
        public long getDistance () {
            return max-min;
        }

        /**
         * converts this MinMaxLong to a MinMaxDouble
         *
         * @return the requested MinMaxDouble
         */
        public MinMaxDouble toMinMaxDouble () {
            return new MinMaxDouble((double)min, (double)max);
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.createList(
                    new Attribute(new AttributeType(Type.MainAttribute), "min", min),
                    new Attribute(new AttributeType(Type.MainAttribute), "max", max));
        }

    }

    /**
     * a representation for a double minimum and a double maximum value
     */
    public static class MinMaxDouble extends GenObject {

        /**
         * the minimum and maximum values
         */
        protected final double min, max;

        /**
         * the constructor
         *
         * @param _min the minimum
         * @param _max the maximum
         * @throws IllegalArgumentException if max is smaller than min
         */
        public MinMaxDouble (double _min, double _max) {
            if (_max < _min)
                throw new IllegalArgumentException("max has to be >= min.");

            min = _min;
            max = _max;
        }

        /**
         * return the minimum
         *
         * @return the minimum
         */
        public double getMin () {
            return min;
        }

        /**
         * return the maximum
         *
         * @return the maximum
         */
        public double getMax () {
            return max;
        }

        /**
         * is the given value in the bounds?
         *
         * @param value the input value
         * @return true, if it is in bounds
         */
        public boolean inBounds(double value) {
            return (value >= min && value <= max);
        }

        /**
         * apply the bounds to the given value
         *
         * @param value the input value
         * @return the value, in bounds
         */
        public double applyBounds(double value) {
            if (value < min)
                return min;
            if (value > max)
                return max;
            return value;
        }

        /**
         * returns the distance between min and max
         *
         * @return the distance
         */
        public double getDistance () {
            return max-min;
        }

        /**
         * converts this MinMaxDouble to a MinMaxLong
         *
         * @return the requested MinMaxLong
         */
        public MinMaxLong toMinMaxLong () {
            return new MinMaxLong((long)Math.floor(min), Math.round(max));
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.createList(
                    new Attribute(new AttributeType(Type.MainAttribute), "min", min),
                    new Attribute(new AttributeType(Type.MainAttribute), "max", max));
        }

    }

}
