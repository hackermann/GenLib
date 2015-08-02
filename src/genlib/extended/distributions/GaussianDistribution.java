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
 * This distribution is a gaussian distribution, with a center,
 * a range to the left and right with 99% of the values and an
 * optional minimum and maximum border.
 *
 * @author Hilmar
 */
public class GaussianDistribution extends AbstractDistribution {        

    /**
     * the center of the gaussian distribution
     */
    protected double center;

    /**
     * these factors will be multiplied to the standard-gaussian-distribution
     */
    protected double factorLeft, factorRight;

    /**
     * A value will never be smaller than center-maxLeft or larger than center+maxRight.
     * These values can be NaN, then there is no minimum or maximum.
     */
    protected double maxLeft, maxRight;

    /**
     * This is useful, if factorLeft != factorRight. The number of values left
     * or right of the center will be adjusted, depending of factorLeft and factorRight.
     */
    protected boolean correctLeftRightDistribution;

    /**
     * the constructor
     */
    public GaussianDistribution () {
        this(0, 2.576, 2.576);
    }

    /**
     * the constructor
     *
     * @param _center the center of the gaussian distribution
     * @throws IllegalArgumentException if one of the values is in invalid range (as example NaN or infinite)
     */
    public GaussianDistribution (double _center) {
        this(_center, 2.576, 2.576);
    }

    /**
     * the constructor
     *
     * @param _center the center of the gaussian distribution
     * @param _range in the interval 'center-range till center+range' are more than 99% of the values and center is exactly in the middle between them
     * @throws IllegalArgumentException if one of the values is in invalid range (as example NaN or infinite)
     */
    public GaussianDistribution (double _center, double _range) {
        this(_center, _range, _range);
    }

    /**
     * the constructor, please note that left and right are not 50% of the values, respective,
     * as long rangeLeft != rangeRight. If this behavior is not wished, use the constructor with
     * more parameters and set _correctLeftRightDistribution = false
     *
     * @param _center the center of the gaussian distribution
     * @param _rangeLeft in the interval 'center-rangeLeft till center' are more than 49.5% of the values
     * @param _rangeRight in the interval 'center till rangeRight+center' are more than 49.5% of the values
     * @throws IllegalArgumentException if one of the values is in invalid range (as example NaN or infinite)
     */
    public GaussianDistribution (double _center, double _rangeLeft, double _rangeRight) {
        this(_center, _rangeLeft, _rangeRight, true, Double.NaN, Double.NaN);
    }

    /**
     * the constructor
     *
     * @param _center the center of the gaussian distribution
     * @param _rangeLeft in the interval 'center-rangeLeft till center' are more than 49.5% of the values
     * @param _rangeRight in the interval 'center till rangeRight+center' are more than 49.5% of the values
     * @param _correctLeftRightDistribution This is useful, if rangeLeft != rangeRight. The number of values left or right of the center will be adjusted, depending of rangeLeft and rangeRight.
     * @param _maxLeft CAN be NaN, than there is no border to the left / a value will be never smaller than center-maxLeft (maxLeft has to be at least as large as rangeLeft)
     * @param _maxRight CAN be NaN, than there is no border to the right / a value will be never smaller than center+maxRight (maxRight has to be at least as large as rangeRight)
     * @throws IllegalArgumentException if one of the values is in invalid range (as example NaN or infinite)
     */
    public GaussianDistribution (double _center, double _rangeLeft, double _rangeRight, boolean _correctLeftRightDistribution, double _maxLeft, double _maxRight) {
        if (!Double.isFinite(center))
            throw new IllegalArgumentException("invalid _center (has to be finite and not NaN)");
        if (!Double.isFinite(_rangeLeft) || _rangeLeft <= 0)
            throw new IllegalArgumentException("invalid _rangeLeft (has to be finite, not NaN and > 0)");
        if (!Double.isFinite(_rangeRight) || _rangeRight <= 0)
            throw new IllegalArgumentException("invalid _rangeRight (has to be finite, not NaN and > 0)");
        if (Double.isInfinite(_maxLeft) || _maxLeft <= 0 || _maxLeft < _rangeLeft)
            throw new IllegalArgumentException("invalid _maxLeft (has to be finite, not NaN, > 0 and >= _rangeLeft)");
        if (Double.isInfinite(_maxRight) || _maxRight <= 0 || _maxRight < _rangeRight)
            throw new IllegalArgumentException("invalid _maxRight (has to be finite, not NaN, > 0 and >= _rangeRight)");

        center = _center;
        //99% of the values are between -2.576 and +2.576
        factorLeft = _rangeLeft/2.576;
        factorRight = _rangeRight/2.576;
        correctLeftRightDistribution = _correctLeftRightDistribution;
        maxLeft = _maxLeft;
        maxRight = _maxRight;
    }

    @Override
    public long getRandomLong(Random random) {
        return (long)getRandomDouble(random);
    }

    @Override
    public int getRandomInt(Random random) {
        return (int)getRandomDouble(random);
    }

    @Override
    public short getRandomShort(Random random) {
        return (short)getRandomDouble(random);
    }

    @Override
    public char getRandomChar(Random random) {
        return (char)getRandomDouble(random);
    }

    @Override
    public byte getRandomByte(Random random) {
        return (byte)getRandomDouble(random);
    }

    @Override
    public double getRandomDouble(Random random) {
        while (true) {

            //create gaussian-distributed value with the Box–Muller method
            double U = random.nextDouble();
            double V = random.nextDouble();
            double X = 0;

            //if we correct, we have to have more values left or right of the center
            if (correctLeftRightDistribution) {
                if (random.nextDouble() < factorLeft/(factorLeft+factorRight))
                    X = Math.sqrt(-2*Math.log(U))*Math.cos(Math.PI/2.0*V+Math.PI/2.0);
                else
                    X = Math.sqrt(-2*Math.log(U))*Math.cos(Math.PI/2.0*V);
            } else
                X = Math.sqrt(-2*Math.log(U))*Math.cos(Math.PI*V);

            //is X left of the center or right?
            if (X < 0) {
                X *= factorLeft;
                if (!Double.isNaN(maxLeft) && X < -maxLeft)
                    continue;
            } else {
                X *= factorRight;
                if (!Double.isNaN(maxRight) && X > maxRight)
                    continue;
            }

            return X+center;
        }
    }

    @Override
    public float getRandomFloat(Random random) {
        return (float)getRandomDouble(random);
    }
    
    @Override
    public List<Attribute> getAttributes() {
        return Utils.extendList(super.getAttributes(),
                new Attribute(new AttributeType(Type.MainAttribute), "center", center),
                new Attribute(new AttributeType(Type.MainAttribute), "factorLeft", factorLeft),
                new Attribute(new AttributeType(Type.MainAttribute), "factorRight", factorRight),
                new Attribute(new AttributeType(Type.NormalAttribute), "maxLeft", maxLeft),
                new Attribute(new AttributeType(Type.NormalAttribute), "maxRight", maxRight),
                new Attribute(new AttributeType(Type.NormalAttribute), "correctLeftRightDistribution", correctLeftRightDistribution));
    }

}
