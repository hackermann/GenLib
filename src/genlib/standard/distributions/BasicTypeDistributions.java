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

package genlib.standard.distributions;

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

    }

}
