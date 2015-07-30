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
import genlib.abstractrepresentation.MutationOp;
import genlib.standard.representations.BinaryStaticLength;
import genlib.standard.representations.BinaryStaticLengthInstance;
import genlib.utils.Utils;
import java.util.List;

/**
 * This mutation-operator changes one entry of the sequence randomly.
 *
 * @author Hilmar
 */
public class OnePointMutation extends GenObject implements MutationOp {

    @Override
    public GenInstance mutationOp(GenInstance input, AlgorithmStep step) {
        boolean [] array = ((BinaryStaticLengthInstance)input).getArray();
        int index = step.getRandom().nextInt(array.length);
        array[index] = !array[index];
        return new BinaryStaticLengthInstance((BinaryStaticLength)input.getRepresentation(), array);
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
    public List <Attribute> getAttributes() {
        return Utils.createList( );
    }

}
