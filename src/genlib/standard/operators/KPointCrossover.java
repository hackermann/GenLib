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
import genlib.standard.representations.BinaryStaticLength;
import genlib.standard.representations.BinaryStaticLengthInstance;
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
            BinaryStaticLength representation = ((BinaryStaticLength)((BinaryStaticLengthInstance)input[0]).getRepresentation());
            int length = representation.getLength();

            List <Integer> crossoverPoints = new ArrayList(k);
            for (int i=0; i<k; i++)
                crossoverPoints.add(step.getRandom().nextInt( length ));
            Collections.sort(crossoverPoints);

            boolean [] result = new boolean[length];
            boolean odd = false;
            crossoverPoints.add(-1);
            for (int i=0; i<length; i++) {
                while (crossoverPoints.get(0) == i) {
                    odd = !odd;
                    crossoverPoints.remove(0);
                }
                result[i] = ((BinaryStaticLengthInstance)input[(odd ? 1 : 0)]).isSet(i);
            }
            return new GenInstance[] {new BinaryStaticLengthInstance(representation, result)};
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
        return representation instanceof BinaryStaticLength;
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
