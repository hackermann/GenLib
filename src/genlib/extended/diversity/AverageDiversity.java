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
import genlib.abstractrepresentation.GenObject;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.extended.diversity.AverageDiversity.IndividuumDistanceOp.StandardDistanceOp;
import genlib.standard.representations.AnyTypeStaticLength;
import genlib.standard.representations.AnyTypeStaticLength.AnyTypeStaticLengthInstance;
import genlib.utils.Utils;
import java.util.List;

/**
 * This diversity-operator calculates the diversity, through an average distance
 * of individuums in the population
 *
 * @author Hilmar
 */
public class AverageDiversity extends AbstractDiversity {

    /**
     * this operator calculates the distance between two individuums
     */
    protected final IndividuumDistanceOp distanceOp;

    /**
     * the constructor
     */
    public AverageDiversity() {
        this(new StandardDistanceOp());
    }

    /**
     * the constructor
     *
     * @param _distanceOp the operator, which calculates the distance between two individuums
     * @throws NullPointerException if distanceOp is null
     *
     */
    public AverageDiversity(IndividuumDistanceOp _distanceOp) {
        super();

        if (_distanceOp == null)
            throw new NullPointerException("_distanceOp cannot be null.");

        distanceOp = _distanceOp;
    }

    /**
     * the constructor
     *
     * @param _percentPopulation the percent of a population, we do the diversity measuring with. The individuums will be chosen randomly.
     * @throws IllegalArgumentException if percentPopulation is not between 0 and 1 or equal to 0
     */
    public AverageDiversity (double _percentPopulation) {
        this (new StandardDistanceOp(), _percentPopulation);
    }

    /**
     * the constructor
     *
     * @param _distanceOp the operator, which calculates the distance between two individuums
     * @param _percentPopulation the percent of a population, we do the diversity measuring with. The individuums will be chosen randomly.
     * @throws IllegalArgumentException if percentPopulation is not between 0 and 1 or equal to 0
     * @throws NullPointerException if distanceOp is null
     */
    public AverageDiversity (IndividuumDistanceOp _distanceOp, double _percentPopulation) {
        super(_percentPopulation);

        if (_distanceOp == null)
            throw new NullPointerException("_distanceOp cannot be null.");

        distanceOp = _distanceOp;
    }

    @Override
    protected double calculateDiversity(List<GenInstance> population, AlgorithmStep step) {
        if (population.size() <= 1)
            return 0;

        double sum = 0;
        for (int i=0; i<population.size(); i++)
            for (int j=i+1; j<population.size(); j++)
                sum += distanceOp.distanceOp(population.get(i), population.get(j));

        return sum / (population.size()*(population.size()-1));
    }

    @Override
    public boolean isCompatible(GenRepresentation representation) {
        return (distanceOp.isCompatibleWith(representation));
    }

    @Override
    public String getName() {
        return "average diversity";
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.extendList( super.getAttributes(), new Attribute(new AttributeType(Type.MainAttribute), "distanceOp", distanceOp));
    }

    /**
     * an operator, that calculates a distance between two individuums
     */
    public static abstract class IndividuumDistanceOp extends GenObject {

        /**
         * the actual operator
         *
         * @param i1 the left individuum
         * @param i2 the right individuum
         * @return the calculated distance
         */
        public abstract double distanceOp (GenInstance i1, GenInstance i2);

        /**
         * Is this operator compatible with this representation
         *
         * @param representation the representation
         * @return true, if compatible
         */
        public abstract boolean isCompatibleWith (GenRepresentation representation);

        @Override
        public List <Attribute> getAttributes() {
            return Utils.createList();
        }

        /**
         * a standard-implementation for the individuum-distance-operator.
         * The distance is the average of each distance between two entries.
         */
        public static class StandardDistanceOp extends IndividuumDistanceOp {

            @Override
            public double distanceOp(GenInstance i1, GenInstance i2) {
                AnyTypeStaticLengthInstance castedI1 = (AnyTypeStaticLengthInstance)i1;
                AnyTypeStaticLengthInstance castedI2 = (AnyTypeStaticLengthInstance)i2;
                double [] array1 = castedI1.getDoubleArray();
                double [] array2 = castedI2.getDoubleArray();
                double distance = 0;
                for (int i=0; i<array1.length; i++)
                    distance += Math.abs(array1[i]-array2[i]);
                return distance/array1.length;
            }

            @Override
            public boolean isCompatibleWith(GenRepresentation representation) {
                return representation instanceof AnyTypeStaticLength;
            }

        }

    }

}
