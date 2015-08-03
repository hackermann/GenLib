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

package genlib.extended.diversity;

import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.standard.representations.AnyTypeStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance;
import genlib.standard.representations.DoubleStaticLength;
import genlib.standard.representations.DoubleStaticLength.DoubleStaticLengthInstance;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * this diversity-operator counts all substrings occurences in
 * complete population and compares it to the substrings in
 * the single individuums. This operator is a bit bruteforce-like,
 * it could have very bad performance.
 *
 * @author Hilmar
 */
public class SubStringDiversity extends AbstractDiversity {

    /**
     * the constructor
     */
    public SubStringDiversity() {
        super();
    }

    /**
     * the constructor
     *
     * @param _percentPopulation the percent of a population, we do the diversity measuring with. The individuums will be chosen randomly.
     * @throws IllegalArgumentException if percentPopulation is not between 0 and 1 or equal to 0
     */
    public SubStringDiversity (double _percentPopulation) {
        super(_percentPopulation);
    }

    /**
     * this method splits the instance in all substrings
     *
     * @param instance the GenInstance
     * @return a set of all the substrings
     */
    protected Set <DoubleStaticLengthInstance> splitInSubStrings (AnyTypeStaticLengthInstance instance) {

        Set <DoubleStaticLengthInstance> ret = new HashSet();
        double [] array = instance.getDoubleArray();
        for (int length=1; length<=array.length; length++)
            for (int i=0; i<=array.length-length; i++) {
                double [] subArray = new double[length];
                System.arraycopy(array, i, subArray, 0, length);
                ret.add(new DoubleStaticLengthInstance(new DoubleStaticLength( length ), subArray));
            }

        return ret;
    }

    @Override
    protected double calculateDiversity(List<GenInstance> population) {
        Set <DoubleStaticLengthInstance> allSubStrings = new HashSet();
        long sum = 0;
        for (GenInstance instance : population) {
            Set <DoubleStaticLengthInstance> subStrings = splitInSubStrings((AnyTypeStaticLengthInstance)instance);
            allSubStrings.addAll(subStrings);
            sum += subStrings.size();
        }
        return (double)((long)allSubStrings.size()) / sum;
    }

    @Override
    public boolean isCompatibleWith(GenRepresentation representation) {
        return representation instanceof AnyTypeStaticLength;
    }

    @Override
    public String getName() {
        return "sub-string-diversity";
    }

}
