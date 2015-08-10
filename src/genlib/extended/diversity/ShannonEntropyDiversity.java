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

import genlib.abstractrepresentation.AlgorithmStep;
import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.standard.representations.AnyTypeStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance;
import genlib.utils.CountingMap;
import java.util.List;

/**
 * This class constructs the average of the shannon-entropys of
 * every position in a static GenInstance. Please note, that every
 * non-equal value counts as a different entry, there is no difference
 * between 0.01 / 0.02 and 0.01 / 1000.0
 *
 * @author Hilmar
 */
public class ShannonEntropyDiversity extends AbstractDiversity {

    /**
     * the constructor
     */
    public ShannonEntropyDiversity() {
        super();
    }

    /**
     * the constructor
     *
     * @param _percentPopulation the percent of a population, we do the diversity measuring with. The individuums will be chosen randomly.
     * @throws IllegalArgumentException if percentPopulation is not between 0 and 1 or equal to 0
     */
    public ShannonEntropyDiversity (double _percentPopulation) {
        super(_percentPopulation);
    }

    @Override
    protected double calculateDiversity(List<GenInstance> population, AlgorithmStep step) {

        //the length of our static instances
        int staticLength = ((AnyTypeStaticLength)((AnyTypeStaticLengthInstance)population.get(0)).getRepresentation()).getLength();
        double sum = 0;

        for (int i=0; i<staticLength; i++) {
            CountingMap <Double> countMap = new CountingMap();

            //add all different values of all individuums at this position i
            for (GenInstance instance : population)
                countMap.add( ((AnyTypeStaticLengthInstance)instance).getDoubleValue(i) );

            //calculate the entropy, we subtract the values, because the log() will always have a negative value
            for (Double entry : countMap.keySet()) {
                double percentOccurence = (double)countMap.getCount(entry) / population.size();
                sum -= percentOccurence * Math.log(percentOccurence);
            }
        }
        return sum / staticLength;
    }

    @Override
    public boolean isCompatible(GenRepresentation representation) {
        return representation instanceof AnyTypeStaticLength;
    }

    @Override
    public String getName() {
        return "shannon-entropy-diversity";
    }

}
