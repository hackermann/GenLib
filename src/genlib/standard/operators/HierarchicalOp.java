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
import genlib.abstractrepresentation.FitnessOp;
import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GenObject;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.abstractrepresentation.GenoToPhenoOp;
import genlib.abstractrepresentation.MutationOp;
import genlib.abstractrepresentation.Operator;
import genlib.abstractrepresentation.RecombinationOp;
import genlib.standard.representations.Hierarchical;
import genlib.standard.representations.Hierarchical.HierarchicalInstance;
import genlib.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This abstract Hierachical-Operator operates on GenRepresentation of
 * the type Hierarchical (And, Or, ..)
 *
 * @author Hilmar
 */
public abstract class HierarchicalOp extends GenObject implements Operator {

    /**
     * the basic-type of the operator
     *    * ExactMatch: for every static child of the input-GenInstance, we define exactly one sub-operator
     *    * UseFirstMatch: for every static child of the input-GenInstance, we search for the first matching operator in the sub-operator list
     *    * UseRandomMatch: for every static child of the input-GenInstance, we search for a random matching operator in the sub-operator list
     */
    public enum HierarchicalType {ExactMatch, UseFirstMatch, UseRandomMatch};

    /**
     * the basic-type of the operator. More details in the java-doc of the enum HierarchicalType
     */
    protected final HierarchicalType type;

    /**
     * the sub-operators of this operator. They behave like specified in type
     */
    protected final Operator [] subOperators;

    /**
     * the constructor, the sub-operators have to match the childs of the instances exactly.
     *
     * @param _subOperators the sub-operators of this operator. They behave like specified in type
     * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators
     */
    public HierarchicalOp (Operator ... _subOperators) {
        this(HierarchicalType.ExactMatch, _subOperators);
    }

    /**
     * the constructor
     *
     * @param _type the basic-type of the operator. More details in the java-doc of the enum HierarchicalType
     * @param _subOperators the sub-operators of this operator. They behave like specified in type
     * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators
     * @throws NullPointerException if type is null
     */
    public HierarchicalOp (HierarchicalType _type, Operator ... _subOperators) {
        if (_type == null)
            throw new NullPointerException("type can't be null.");
        if (_subOperators == null || _subOperators.length == 0)
            throw new IllegalArgumentException("there has to be at least one subOperator.");
        for (Operator child: _subOperators)
            if (child == null)
                throw new IllegalArgumentException("a 'null'-subOperator is not allowed.");

        type = _type;
        subOperators = new Operator[_subOperators.length];
        System.arraycopy(_subOperators, 0, subOperators, 0, _subOperators.length);
    }

    @Override
    public boolean isCompatible(GenRepresentation representation) {
        if (!(representation instanceof Hierarchical))
            return false;

        return !(hierarchicalIndexToOperators((Hierarchical)representation) == null);
    }

    @Override
    public boolean isCompatible(AlgorithmPass algorithmPass) {
        for (Operator subOperator : subOperators)
            if (!subOperator.isCompatible(algorithmPass))
                return false;

        return true;
    }

    @Override
    public List<Attribute> getAttributes() {
        return Utils.createList(new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "type", type),
                                new Attribute(new AttributeType(AttributeType.Type.NormalAttribute), "subOperators", subOperators));
    }

    /**
     * returns a copy of the sub-operators
     *
     * @return a copy of the sub-operators
     */
    public Operator [] getSubOperators () {
        Operator [] ret = new Operator[subOperators.length];
        System.arraycopy(subOperators, 0, ret, 0, subOperators.length);
        return ret;
    }

    /**
     * returns the hierarchical-type
     *
     * @return the hierarchical-type
     */
    public HierarchicalType getHierarchicalType () {
        return type;
    }

    /**
     * returns a map, that matches to every hierarchical-instance-child a list
     * of possible operators (as indices to the subOperators-array). If this
     * list contains more than one entry, you have to choose the operator
     * randomly. If the matching can't be done completely (= the specified
     * representation is not compatible) it returns null.
     *
     * @param representation the (hierarchical-)representation we want to match with
     * @return the requested map or null, if the representation is not compatible
     */
    protected Map <Integer, List <Integer> > hierarchicalIndexToOperators (Hierarchical representation) {
        //we want to ignore none of the operators
        boolean [] _ignoreChilds = new boolean [subOperators.length];

        return hierarchicalIndexToOperators(representation, _ignoreChilds);
    }

    /**
     * returns a map, that matches to every hierarchical-instance-child a list
     * of possible operators (as indices to the subOperators-array). If this
     * list contains more than one entry, you have to choose the operator
     * randomly. If the matching can't be done completely (= the specified
     * representation is not compatible) it returns null.
     *
     * @param representation the (hierarchical-)representation we want to match with
     * @param ignoreChilds an array of this size of the representation-childs, if an entry is true, this child of the representation will be ignored
     * @return the requested map or null, if the representation is not compatible
     */
    protected Map <Integer, List <Integer> > hierarchicalIndexToOperators (Hierarchical representation, boolean [] ignoreChilds) {

        Map <Integer, List <Integer> > ret = new HashMap();
        GenRepresentation [] childs = representation.getChilds();

        switch (type) {

            case ExactMatch:
                if (subOperators.length != childs.length )
                    return null;

                for (int i=0; i<subOperators.length; i++) {
                    if (ignoreChilds[i])
                        continue;

                    if (!operatorCompatible(subOperators[i], childs[i] ) )
                        return null;
                    else
                        ret.put(i, new ArrayList(Arrays.asList(i)) );
                }

                return ret;

            case UseFirstMatch:
                for (int i=0; i<childs.length; i++) {

                    for (int j=0; j<subOperators.length; j++) {
                        if (ignoreChilds[j])
                            continue;

                        if (operatorCompatible(subOperators[j], childs[i])) {
                            ret.put(i, new ArrayList(Arrays.asList(j)) );
                            break;
                        }
                    }

                    if (!ret.containsKey(i))
                        return null;
                }

                return ret;

            case UseRandomMatch:

                for (int i=0; i<childs.length; i++) {
                    ret.put(i, new ArrayList() );
                    for (int j=0; j<subOperators.length; j++) {
                        if (ignoreChilds[j])
                            continue;

                        if (operatorCompatible(subOperators[j], childs[i]))
                            ret.get(i).add(j);
                    }

                    if (ret.get(i).isEmpty())
                        return null;
                }

                return ret;

            default:
                throw new AssertionError(type.name());

        }
    }

    /**
     * Is the given (sub-)Operator compatible with the given representation (which
     * will be a child of a hierarchical representation)?
     *
     * @param op the given (sub-)operator
     * @param representation the representation
     * @return true, if compatible
     */
    protected abstract boolean operatorCompatible (Operator op, GenRepresentation representation);

    /**
     * this class represents a Hierarchical-Operators, that transforms
     * a Hierarchical-input in one double-value
     */
    public abstract static class HierarchicalReductionOp extends HierarchicalOp {

        /**
         * the type of reduction (how will the different sub-results be merged)
         */
        protected final ReductionType reductionType;

        /**
         * the constructor, the sub-operators have to match the childs of the instances exactly.
         *
         * @param _reductionType the type of reduction (how will the different sub-results be merged)
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators or the reduction-type is not compatible with the number of operators
         */
        public HierarchicalReductionOp (ReductionType _reductionType, Operator ... _subOperators) {
            super(_subOperators);

            reductionType = _reductionType;
            if (!reductionType.isCompatible(subOperators.length))
                throw new IllegalArgumentException("reduction type is not compatible with the number of operators");
        }

        /**
         * the constructor
         *
         * @param _type the basic-type of the operator. More details in the java-doc of the enum HierarchicalType
         * @param _reductionType the type of reduction (how will the different sub-results be merged)
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators or the reduction-type is not compatible with the number of operators
         * @throws NullPointerException if type is null
         */
        public HierarchicalReductionOp (HierarchicalType _type, ReductionType _reductionType, Operator ... _subOperators) {
            super(_type, _subOperators);

            reductionType = _reductionType;
            if (!reductionType.isCompatible(subOperators.length))
                throw new IllegalArgumentException("reduction type is not compatible with the number of operators");
        }

        @Override
        public boolean isCompatible(GenRepresentation representation) {
            if (!(representation instanceof Hierarchical))
                return false;

            return !(hierarchicalIndexToOperators((Hierarchical)representation, reductionType.buildIgnoreArray(subOperators.length)) == null);
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.extendList(super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "reductionType", reductionType));
        }

        /**
         * do the actual reduction (the actual operator of the inheriting class
         * should invoke this method)
         *
         * @param input the hierachical-instance
         * @param step the algorithm-step
         * @return the double-result
         */
        public double reduce (HierarchicalInstance input, AlgorithmStep step) {
            List <Integer> matchedOperators = input.getMatchedOperators(hierarchicalIndexToOperators((Hierarchical)input.getRepresentation(), reductionType.buildIgnoreArray(subOperators.length)), step);
            GenInstance [] subInput = input.getChilds();
            double [] values = new double[subOperators.length];
            for (int i=0; i<subInput.length; i++)
                if (matchedOperators.get(i) != null)
                    values[matchedOperators.get(i)] = calculateSubResult(subOperators[matchedOperators.get(i)], new GenInstance[] {subInput[i]}, step);
            return reductionType.reduce(values);
        }

        /**
         * calculate the double-values of the specified child of the hierarchical-instance
         *
         * @param op the operator to use
         * @param input the child(s) of the hierarchical-instance (can be multiple, as example for a diversity-operator)
         * @param step the algorithm-step
         * @return the double-result of this calculation
         */
        protected abstract double calculateSubResult (Operator op, GenInstance [] input, AlgorithmStep step);

    }

    /**
     * This class represents a reduction of multiple double values
     */
    public static class ReductionType extends GenObject {

        /**
         * the type of reduction
         *   - Average: return the average of every value
         *   - IndividualWeights: return a weighted result with given weights
         *   - JustOne: returns the value just of this ONE specified input
         */
        protected enum Type {Average, IndividualWeights, JustOne};

        /**
         * the type of this reduction (see java-doc of the enum for more details)
         */
        protected final Type type;

        /**
         * the weights for the case 'IndividualWeights'
         */
        protected final double [] weights;

        /**
         * the index for the case 'JustOne'
         */
        protected final int justOneIndex;

        /**
         * the constructor
         *
         * @param _type the type of this reduction (see java-doc of the enum for more details)
         * @param _weights the weights for the case 'IndividualWeights'
         * @param _justOneIndex the index for the case 'JustOne'
         * @throws IllegalArgumentException if the sum of all weights is smaller or equal to 0
         */
        private ReductionType (Type _type, double [] _weights, int _justOneIndex) {
            type = _type;
            if (_weights != null) {
                double sum = 0;
                for (int i=0; i<_weights.length; i++)
                    sum += _weights[i];

                if (sum <= 0)
                    throw new IllegalArgumentException("the sum of all probabilities have to be > 0");

                weights = new double[_weights.length];
                for (int i=0; i<_weights.length; i++)
                    weights[i] = _weights[i]/sum;
            } else
                weights = null;
            justOneIndex = _justOneIndex;
        }

        /**
         * creates a reduction, which returns the average value over all operator-values
         *
         * @return the requested reduction
         */
        public static ReductionType average () {
            return new ReductionType(Type.Average, null, -1);
        }

        /**
         * creates a reduction, which returns a weighted result over the operators
         *
         * @param _weights the individual weights
         * @return the requested reduction
         * @throws IllegalArgumentException if there is not at least one weight, one of the weights is NaN, infinte or smaller than 0 or the sum of the weights is 0
         */
        public static ReductionType individualProbabilities (double ... _weights) {

            if (_weights == null || _weights.length == 0)
                throw new IllegalArgumentException("there need to be at least 1 weight");
            for (double prob : _weights)
                if (!Double.isFinite(prob) || prob < 0.0)
                    throw new IllegalArgumentException("every weight can't be NaN or infinite and has to be >= 0");

            return new ReductionType(Type.IndividualWeights, _weights, -1);
        }

        /**
         * creates a reduction, which returns always the value of one specified sub-operator
         *
         * @param index the index of the specified operator
         * @return the requested reduction
         * @throws IllegalArgumentException if the index is smaller than 0
         */
        public static ReductionType justOne (int index) {
            if (index < 0 )
                throw new IllegalArgumentException("index has to be >= 0");

            return new ReductionType(Type.JustOne, null, index);
        }

        /**
         * is this reduction compatible with a number of operators
         *
         * @param numberOfChilds the number of operators
         * @return true, if compatible
         */
        public boolean isCompatible (int numberOfChilds) {
            if (type == Type.IndividualWeights && weights.length != numberOfChilds)
                return false;
            if (type == Type.JustOne && justOneIndex >= numberOfChilds)
                return false;

            return true;
        }

        /**
         * builds an array, that have a true at every position, where the
         * value is not needed. At this positions will be a 0.0 in the
         * input-array later
         *
         * @param numberOfChilds the size of the array
         * @return the requested array
         */
        public boolean [] buildIgnoreArray (int numberOfChilds) {
            boolean [] ret = new boolean[numberOfChilds];
            if (type == Type.IndividualWeights)
                for (int i=0; i<ret.length; i++)
                    ret[i] = (weights[i] == 0);
            else if (type == Type.JustOne)
                for (int i=0; i<ret.length; i++)
                    ret[i] = (i != justOneIndex);
            return ret;
        }

        /**
         * does the actual reduction
         *
         * @param input the input-values
         * @return the result of the reduction
         */
        public double reduce (double [] input) {

            double sum = 0;

            switch (type) {
                case Average:
                    for (int i=0; i<input.length; i++)
                        sum += input[i];
                    return sum / input.length;

                case IndividualWeights:
                    for (int i=0; i<input.length; i++)
                        sum += input[i] * weights[i];
                    return sum;

                case JustOne:
                    return input[justOneIndex];

                default:
                    throw new AssertionError(type.name());
            }
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.createList(new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "type", type),
                                    new Attribute(new AttributeType(type == Type.IndividualWeights ? AttributeType.Type.NormalAttribute : AttributeType.Type.TemporaryOrUnimportant), "weights", weights),
                                    new Attribute(new AttributeType(type == Type.JustOne ? AttributeType.Type.NormalAttribute : AttributeType.Type.TemporaryOrUnimportant), "justOneIndex", justOneIndex));
        }

    }

    /**
     * a hierarchical fitness-operator. The fitness will be the
     * average of all child-fitnesses
     */
    public static class HierarchicalAverageFitness extends HierarchicalReductionOp implements FitnessOp {

        /**
         * the constructor, the sub-operators have to match the childs of the instances exactly.
         *
         * @param _reductionType the type of reduction (how will the different sub-results be merged)
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators or the reduction-type is not compatible with the number of operators
         * @throws NullPointerException if type is null
         */
        public HierarchicalAverageFitness(ReductionType _reductionType, FitnessOp ... _subOperators) {
            super(_reductionType, _subOperators);
        }

        /**
         * the constructor
         *
         * @param _type the basic-type of the operator. More details in the java-doc of the enum HierarchicalType
         * @param _reductionType the type of reduction (how will the different sub-results be merged)
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators or the reduction-type is not compatible with the number of operators
         * @throws NullPointerException if type is null
         */
        public HierarchicalAverageFitness(HierarchicalType _type, ReductionType _reductionType, FitnessOp ... _subOperators) {
            super(_type, _reductionType, _subOperators);
        }

        @Override
        protected boolean operatorCompatible(Operator op, GenRepresentation representation) {
            return ((FitnessOp)op).isCompatible(representation);
        }

        @Override
        public double fitnessOp(GenInstance input, AlgorithmStep step) {
            return reduce((HierarchicalInstance)input, step);
        }

        @Override
        protected double calculateSubResult (Operator op, GenInstance [] input, AlgorithmStep step) {
            //the standard-HierarchicalReductionOp just has one GenInstance as input
            return ((FitnessOp)op).fitnessOp(input[0], step);
        }

    }

    /**
     * a hierarchical genoToPheno-operator. It will apply the given sub-Operators to
     * each of the childs of the instance. It can transform two non-identically hierarchical-
     * instances (as example AND to OR), but the amount of children have to be the same
     */
    public static class HierarchicalGenoToPhenoOp extends HierarchicalOp implements GenoToPhenoOp {

        /**
         * the constructor, the sub-operators have to match the childs of the instances exactly.
         *
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators
         */
        public HierarchicalGenoToPhenoOp(GenoToPhenoOp ... _subOperators) {
            super(_subOperators);
        }

        /**
         * the constructor
         *
         * @param _type the basic-type of the operator. More details in the java-doc of the enum HierarchicalType
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators
         * @throws NullPointerException if type is null
         */
        public HierarchicalGenoToPhenoOp(HierarchicalType _type, GenoToPhenoOp ... _subOperators) {
            super(_type, _subOperators);
        }

        @Override
        protected boolean operatorCompatible(Operator op, GenRepresentation representation) {
            return ((GenoToPhenoOp)op).isGenoPhenoCompatible(representation, representation);
        }

        @Override
        public GenInstance genoToPhenoOp(GenInstance input, AlgorithmStep step) {
            return input;
        }

        @Override
        public boolean isGenoPhenoCompatible(GenRepresentation genoType, GenRepresentation phenoType) {

            //both representations have to be hierarchical and the same number of childs
            if (    !(genoType instanceof Hierarchical && phenoType instanceof Hierarchical) ||
                    ((Hierarchical)genoType).getChilds().length != ((Hierarchical)phenoType).getChilds().length)
                return false;

            //the standard-check, that is already done in isCompatible()
            Map <Integer, List <Integer> > indexToOp = hierarchicalIndexToOperators((Hierarchical)genoType);
            if (indexToOp == null)
                return false;

            //check all the subOperators, if they can use the geno/pheno-type
            for (int index : indexToOp.keySet())
                if (! ((GenoToPhenoOp)subOperators[index]).isGenoPhenoCompatible(((Hierarchical)genoType).getChilds()[index], ((Hierarchical)phenoType).getChilds()[index]))
                    return false;

            return true;
        }

    }

    /**
     * a hierarchical recombination-operator. It will return childs,
     * where every child is recombinated with the childs of the other
     * individuums.
     */
    public static class HierarchicalRecombinationOp extends HierarchicalOp implements RecombinationOp {

        /**
         * the constructor, the sub-operators have to match the childs of the instances exactly.
         *
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators
         */
        public HierarchicalRecombinationOp(RecombinationOp ... _subOperators) {
            super(_subOperators);
        }

        /**
         * the constructor
         *
         * @param _type the basic-type of the operator. More details in the java-doc of the enum HierarchicalType
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators
         * @throws NullPointerException if type is null
         */
        public HierarchicalRecombinationOp(HierarchicalType _type, RecombinationOp ... _subOperators) {
            super(_type, _subOperators);
        }

        @Override
        protected boolean operatorCompatible(Operator op, GenRepresentation representation) {
            return ((RecombinationOp)op).isCompatible(representation);
        }

        @Override
        public GenInstance[] recombinationOp(GenInstance[] input, AlgorithmStep step, int outputSize) {
            Hierarchical representation = (Hierarchical)input[0].getRepresentation();
            List <Integer> matchedOperators = ((HierarchicalInstance)input[0]).getMatchedOperators(hierarchicalIndexToOperators(representation), step);

            //split the input in 2d-array [childs of every input-instance][number of individuums (input)]
            GenInstance [][] subInput = new GenInstance[((HierarchicalInstance)input[0]).getChilds().length][input.length];
            for (int i=0; i<input.length; i++) {
                GenInstance [] childs = ((HierarchicalInstance)input[i]).getChilds();
                for (int j=0; j<childs.length; j++)
                    subInput[j][i] = childs[j];
            }

            //apply the child-recombination-operators and split the output in a
            //2d-array [number of individuums (output)][childs of every output-instance]
            GenInstance [][] newChilds = new GenInstance[outputSize][subInput.length];
            for (int i=0; i<subInput.length; i++) {
                GenInstance [] recombination = ((RecombinationOp)subOperators[matchedOperators.get(i)]).recombinationOp(subInput[i], step, outputSize);
                for (int j=0; j<recombination.length; j++)
                    newChilds[j][i] = recombination[j];
            }

            //calculate the return-array (we have to instantiate 'outputSize' instances)
            GenInstance [] ret = new GenInstance[outputSize];
            for (int i=0; i<ret.length; i++)
                ret[i] = representation.instantiateFromChilds(newChilds[i]);
            return ret;
        }

        @Override
        public boolean isInputSizeCompatible(int size) {
            for (Operator op : subOperators)
                if (!(((RecombinationOp)op).isInputSizeCompatible(size)))
                    return false;
            return true;
        }

        @Override
        public boolean isOutputSizeCompatible(int size) {
            for (Operator op : subOperators)
                if (!(((RecombinationOp)op).isOutputSizeCompatible(size)))
                    return false;
            return true;
        }

    }

    /**
     * a hierarchical mutation-operator. It will mutate one random-childs with
     * the given subOperator for this child
     */
    public static class HierarchicalRandomMutationOp extends HierarchicalOp implements MutationOp {

        /**
         * the constructor, the sub-operators have to match the childs of the instances exactly.
         *
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators
         */
        public HierarchicalRandomMutationOp(MutationOp ... _subOperators) {
            super(_subOperators);
        }

        /**
         * the constructor
         *
         * @param _type the basic-type of the operator. More details in the java-doc of the enum HierarchicalType
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators
         * @throws NullPointerException if type is null
         */
        public HierarchicalRandomMutationOp(HierarchicalType _type, MutationOp ... _subOperators) {
            super(_type, _subOperators);
        }

        @Override
        protected boolean operatorCompatible(Operator op, GenRepresentation representation) {
            return ((MutationOp)op).isCompatible(representation);
        }

        @Override
        public GenInstance mutationOp(GenInstance input, AlgorithmStep step) {
            List <Integer> matchedOperators = ((HierarchicalInstance)input).getMatchedOperators(hierarchicalIndexToOperators((Hierarchical)input.getRepresentation()), step);
            GenInstance [] subInput = ((HierarchicalInstance)input).getChilds();
            int randomIndex = step.getRandom().nextInt(subInput.length);
            subInput[randomIndex] = ((MutationOp)subOperators[matchedOperators.get(randomIndex)]).mutationOp(subInput[randomIndex], step);
            return ((Hierarchical)input.getRepresentation()).instantiateFromChilds(subInput);
        }

    }

}
