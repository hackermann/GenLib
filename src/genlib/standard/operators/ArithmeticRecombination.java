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

package genlib.standard.operators;

import genlib.abstractrepresentation.AlgorithmPass;
import genlib.abstractrepresentation.AlgorithmStep;
import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GenObject;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.abstractrepresentation.RecombinationOp;
import genlib.standard.representations.AnyTypeStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance;
import genlib.utils.Utils;
import java.util.List;
import java.util.Random;

/**
 * With this recombination-operator, you can interpolate and
 * extrapolate single values in a representation
 *
 * @author Hilmar
 */
public class ArithmeticRecombination extends GenObject implements RecombinationOp {

    /**
     * the maximum-factor, we can reach. If it is smaller than 1.0,
     * we speak about an interpolation, otherwise about an extrapolation.
     */
    protected final double maxFactor;

    /**
     * how large is the area of possible values, we can reach till the average (0.5).
     * If we want a extrapolation only, we can choose as example maxFactor 2 and
     * percentReachable 0.66. The reachable area for the factor is then between
     * 1.0 and 2.0 (and -1.0 till -2.0 on the other side)
     */
    protected final double percentReachable;

    /**
     * how much differs the factor for each entry? If we choose 0.0, every
     * entry will have the same factor in ONE call of this operator
     */
    protected final double differenceFactor;

    /**
     * the constructor, with _percentReachable 1.0 and _differenceFactor 0.5
     *
     * @param _maxFactor the maximum-factor, we can reach. If it is smaller than 1.0, we speak from an interpolation, otherwise from an extrapolation.
     */
    public ArithmeticRecombination (double _maxFactor) {
        this(_maxFactor, 1.0, 0.5);
    }

    /**
     * the constructor
     *
     * @param _maxFactor the maximum-factor, we can reach. If it is smaller than 1.0, we speak from an interpolation, otherwise from an extrapolation.
     * @param _percentReachable how large is the area of possible values, we can reach till the average (0.5). If we want a extrapolation only, we can choose as example maxFactor 2 and percentReachable 0.66. The reachable area for the factor is then between 1.0 and 2.0 (and 0.0 till -1.0 on the other side)
     * @param _differenceFactor how much differs the factor for each entry? If we choose 0.0, every entry will have the same factor in ONE call of this operator
     * @throws IllegalArgumentException if maxFactor is not larger or equal to 0.5 or percentReachable / differenceFactor is not between 0.0 and 1.0
     */
    public ArithmeticRecombination (double _maxFactor, double _percentReachable, double _differenceFactor) {

        if (!Double.isFinite(_maxFactor) || _maxFactor < 0.5)
            throw new IllegalArgumentException ("maxFactor has to be >= 0.5");
        if (!Double.isFinite(_percentReachable) || _percentReachable < 0.0 || _percentReachable > 1.0)
            throw new IllegalArgumentException ("percentReachable has to be >= 0 and <= 1");
        if (!Double.isFinite(_differenceFactor) || _differenceFactor < 0.0 || _differenceFactor > 1.0)
            throw new IllegalArgumentException ("differenceFactor has to be >= 0 and <= 1");

        maxFactor = _maxFactor;
        percentReachable = _percentReachable;
        differenceFactor = _differenceFactor;
    }

    /**
     * get a random-value out of the factor-area, we have defined
     * with maxFactor and percentReachable
     *
     * @param rand the random-object
     * @return the random-factor
     */
    protected double getRandomValue (Random rand) {
        double value = rand.nextDouble()*(maxFactor-0.5)*percentReachable+0.5+(maxFactor-0.5)*(1.0-percentReachable);
        return value;
    }

    @Override
    public GenInstance[] recombinationOp(GenInstance[] input, AlgorithmStep step, int outputSize) {
        AnyTypeStaticLengthInstance instL = (AnyTypeStaticLengthInstance)input[0];
        AnyTypeStaticLengthInstance instR = (AnyTypeStaticLengthInstance)input[1];
        AnyTypeStaticLength representation = (AnyTypeStaticLength)instL.getRepresentation();

        //we will later combine this value with a localValue, depending of the differenceFactor
        double globalValue = getRandomValue(step.getRandom());

        double [] resultArray = new double[representation.getLength()];
        for (int i=0; i<representation.getLength(); i++) {
            double localValue = getRandomValue(step.getRandom());
            double actualValue = localValue*differenceFactor + globalValue*(1.0-differenceFactor);

            //let's do a random-invert here, so there is definitive no difference, if we define the order of the input
            if (step.getRandom().nextBoolean())
                actualValue = 1.0 - actualValue;
            resultArray[i] = representation.applyBounds(instL.getDoubleValue(i) * actualValue + instR.getDoubleValue(i) * (1.0-actualValue));
        }

        return new GenInstance[] { representation.instantiateFromDoubles(resultArray) };
    }

    @Override
    public boolean isInputSizeCompatible(int size) {
        return size == 2;
    }

    @Override
    public boolean isOutputSizeCompatible(int size) {
        return size == 1;
    }

    @Override
    public boolean isCompatible(GenRepresentation representation) {
        return representation instanceof AnyTypeStaticLength;
    }

    @Override
    public boolean isCompatible(AlgorithmPass algorithmPass) {
        return true;
    }

    @Override
    public List<Attribute> getAttributes() {
        return Utils.createList(
                new Attribute(new AttributeType(Type.MainAttribute), "maxFactor", maxFactor),
                new Attribute(new AttributeType(Type.MainAttribute), "percentReachable", percentReachable),
                new Attribute(new AttributeType(Type.MainAttribute), "differenceFactor", differenceFactor));
    }

}
