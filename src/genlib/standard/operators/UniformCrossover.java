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
import java.util.List;

/**
 * This operator recombinates 1-n input-representations. Every entry of the child
 * is randomly selected of one of the entries of the parents.
 *
 * @author Hilmar
 */
public class UniformCrossover extends GenObject implements RecombinationOp {

    @Override
    public GenInstance[] recombinationOp(GenInstance[] input, AlgorithmStep step, int outputSize) {
        AnyTypeStaticLength representation = (AnyTypeStaticLength)input[0].getRepresentation();

        if (representation.isLongType()) {
            long [] outputValues = new long [representation.getLength()];
            for (int i=0; i<outputValues.length; i++)
                outputValues[i] = ((AnyTypeStaticLengthInstance)input[step.getRandom().nextInt(representation.getLength())]).getLongValue(i);
            return new GenInstance[] {representation.instantiateFromLongs(outputValues)};
            
        } else {
            double [] outputValues = new double [representation.getLength()];
            for (int i=0; i<outputValues.length; i++)
                outputValues[i] = ((AnyTypeStaticLengthInstance)input[step.getRandom().nextInt(representation.getLength())]).getDoubleValue(i);
            return new GenInstance[] {representation.instantiateFromDoubles(outputValues)};
        }
    }

    @Override
    public boolean isInputSizeCompatible(int size) {
        return size >= 1;
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
        return Utils.createList();
    }

}
