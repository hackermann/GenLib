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
import genlib.abstractrepresentation.GenRepresentation;
import genlib.abstractrepresentation.RecombinationOp;
import genlib.standard.representations.AnyTypeStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance;
import genlib.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * the k-point-crossover is a recombination-operator, that gets two input-
 * individuums and defines k points randomly, where it changes to receive
 * the input from. As example input AAA and BBB with k=2 can produce:
 * AAA, BBB, ABB, ABA, BAB, etc..
 *
 * @author Hilmar
 */
public class KPointCrossover extends GenObject implements RecombinationOp {

    /**
     * the k
     */
    protected final int k;

    /**
     * the constructor
     *
     * @param _k k
     * @throws IllegalArgumentException if k is smaller than 1
     */
    public KPointCrossover (int _k) {
        if (_k <= 0)
            throw new IllegalArgumentException("invalid k: '" + _k + "'.");
        k = _k;
    }

    @Override
    public GenInstance[] recombinationOp(GenInstance[] input, AlgorithmStep step, int outputSize) {

        if (input.length == 1)
            return input;
        else {
            AnyTypeStaticLength representation = ((AnyTypeStaticLength)((AnyTypeStaticLengthInstance)input[0]).getRepresentation());
            int length = representation.getLength();

            //get random crossover-points and sort them
            List <Integer> crossoverPoints = new ArrayList(k);
            for (int i=0; i<k; i++)
                crossoverPoints.add(step.getRandom().nextInt( length ));
            Collections.sort(crossoverPoints);

            long [] resultLong = null;
            double [] resultDouble = null;
            if (representation.isLongType())
                resultLong = new long[length];
            else
                resultDouble = new double[length];

            //odd defines, if we currently read in the first or in the second instance
            boolean odd = false;
            //let's add a pseudo-point, so the list will be never empty and .get(0) won't throw an exception
            crossoverPoints.add(-1);
            for (int i=0; i<length; i++) {
                while (crossoverPoints.get(0) == i) {
                    odd = !odd;
                    crossoverPoints.remove(0);
                }
                if (representation.isLongType())
                    resultLong[i] = ((AnyTypeStaticLengthInstance)input[(odd ? 1 : 0)]).getLongValue(i);
                else
                    resultDouble[i] = ((AnyTypeStaticLengthInstance)input[(odd ? 1 : 0)]).getDoubleValue(i);
            }
            return new GenInstance[] { representation.isLongType() ? representation.instantiateFromLongs(resultLong) : representation.instantiateFromDoubles(resultDouble)};
        }

    }

    @Override
    public boolean isInputSizeCompatible(int size) {
        return (size == 1 || size == 2);
    }

    @Override
    public boolean isOutputSizeCompatible(int size) {
        return (size == 1);
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
    public List <GenObject.Attribute> getAttributes() {
        return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "k", k) );
    }

}
