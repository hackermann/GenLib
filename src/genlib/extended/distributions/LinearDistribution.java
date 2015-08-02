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

import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.utils.Utils;
import java.util.List;
import java.util.Random;

/**
 * A linear distribution, with a specified min and max value.
 * You can specify these values as long or double, so there is
 * guaranteed no precision loss.
 *
 * @author Hilmar
 */
public class LinearDistribution extends AbstractDistribution {       

    /**
     * the minimum and maximum value, saved as long
     */
    protected final long minL, maxL;

    /**
     * the minimum and maximum value, saved as double
     */
    protected final double minD, maxD;

    /**
     * the constructor with long min and max
     *
     * @param _min the minimum value
     * @param _max the maximum value
     * @throws IllegalArgumentException if min larger than max
     */
    public LinearDistribution (long _min, long _max) {
        if (_min > _max)
            throw new IllegalArgumentException("min has to be smaller or equal than max");

        minL = _min;
        maxL = _max;
        minD = (double)minL;
        maxD = (double)maxL;
    }

    /**
     * the constructor with long bound, the interval of
     * the expected values is between 0 and bound.
     *
     * @param bound the bound
     */
    public LinearDistribution (long bound) {
        if (bound >= 0) {
            minL = 0;
            maxL = bound;
        } else {
            maxL = 0;
            minL = bound;
        }
        minD = (double)minL;
        maxD = (double)maxL;
    }

    /**
     * the constructor with double min and max
     *
     * @param _min the minimum value
     * @param _max the maximum value
     * @throws IllegalArgumentException if min larger than max
     */
    public LinearDistribution (double _min, double _max) {
        if (_min > _max)
            throw new IllegalArgumentException("min has to be smaller or equal than max");

        minD = _min;
        maxD = _max;
        minL = (long)minD;
        maxL = (long)maxD;
    }

    /**
     * the constructor with double bound, the interval of
     * the expected values is between 0 and bound.
     *
     * @param bound the bound
     */
    public LinearDistribution (double bound) {
        if (bound >= 0) {
            minD = 0;
            maxD = bound;
        } else {
            maxD = 0;
            minD = bound;
        }
        minL = (long)minD;
        maxL = (long)maxD;
    }

    @Override
    public long getRandomLong(Random random) {
        if (minL > Long.MIN_VALUE/2+128 && maxL < Long.MAX_VALUE/2-128)
            return Math.abs(random.nextLong())%(maxL-minL)+minL;
        else {
            long nextLong = random.nextLong();
            while (nextLong > maxL || nextLong < minL)
                nextLong = random.nextLong();
            return nextLong;
        }
    }

    @Override
    public int getRandomInt(Random random) {
        int max = Utils.longToIntBounds(maxL);
        int min = Utils.longToIntBounds(minL);

        if (min > Integer.MIN_VALUE/2+128 && max < Integer.MAX_VALUE/2-128)
            return random.nextInt(max-min)+min;
        else {
            int nextInt = random.nextInt();
            while (nextInt > max || nextInt < min)
                nextInt = random.nextInt();
            return nextInt;
        }
    }

    @Override
    public short getRandomShort(Random random) {
        short max = Utils.longToShortBounds(maxL);
        short min = Utils.longToShortBounds(minL);

        return (short)(random.nextInt((int)max-(int)min)+(int)min);
    }

    @Override
    public char getRandomChar(Random random) {
        char max = Utils.longToCharBounds(maxL);
        char min = Utils.longToCharBounds(minL);

        return (char)(random.nextInt((int)max-(int)min)+(int)min);
    }

    @Override
    public byte getRandomByte(Random random) {
        byte max = Utils.longToByteBounds(maxL);
        byte min = Utils.longToByteBounds(minL);

        return (byte)(random.nextInt((int)max-(int)min)+(int)min);
    }

    @Override
    public double getRandomDouble(Random random) {
        return random.nextDouble()*(maxD-minD)+minD;
    }

    @Override
    public float getRandomFloat(Random random) {
        return (float)(random.nextFloat()*(maxD-minD)+minD);
    }

    @Override
    public List<Attribute> getAttributes() {
        return Utils.extendList(super.getAttributes(),
                new Attribute(new AttributeType(Type.MainAttribute), "minL", minL),
                new Attribute(new AttributeType(Type.MainAttribute), "maxL", maxL),
                new Attribute(new AttributeType(Type.MainAttribute), "minD", minD),
                new Attribute(new AttributeType(Type.MainAttribute), "maxD", maxD));
    }
}
