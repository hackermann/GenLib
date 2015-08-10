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

import genlib.abstractrepresentation.AlgorithmPass;
import genlib.abstractrepresentation.AlgorithmStep;
import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.abstractrepresentation.Operator;
import genlib.standard.operators.HierarchicalOp;
import genlib.standard.operators.HierarchicalOp.HierarchicalReductionOp;
import genlib.standard.operators.HierarchicalOp.HierarchicalType;
import genlib.standard.operators.HierarchicalOp.ReductionType;
import genlib.standard.representations.Hierarchical;
import genlib.standard.representations.Hierarchical.HierarchicalInstance;
import genlib.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is possible to calculate the diversity for
 * hierarchical-instances
 *
 * @author Hilmar
 */
public class HierarchicalDiversity extends AbstractDiversity {

    /**
     * the reduction, this part is contained in this object,
     * because java has no multiple inheritance. Actual, this
     * class should be a subclass of AbstractDiversity and
     * HierarchicalReductionOp
     */
    protected final DiversityReduction reduction;

    /**
     * the constructor, the sub-operators have to match the childs of the instances exactly.
     *
     * @param _reductionType the type of reduction (how will the different sub-results be merged)
     * @param _subOperators the sub-operators of this operator. They behave like specified in type
     * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators or the reduction-type is not compatible with the number of operators
     */
    public HierarchicalDiversity(ReductionType _reductionType, AbstractDiversity ... _subOperators) {
        super();

        reduction = new DiversityReduction(_reductionType, _subOperators);
    }

    /**
     * the constructor, the sub-operators have to match the childs of the instances exactly.
     *
     * @param _percentPopulation the percent of a population, we do the diversity measuring with. The individuums will be chosen randomly.
     * @param _reductionType the type of reduction (how will the different sub-results be merged)
     * @param _subOperators the sub-operators of this operator. They behave like specified in type
     * @throws IllegalArgumentException if percentPopulation is not between 0 and 1 or equal to 0 or if the subOperators are empty or a null is in the subOperators or the reduction-type is not compatible with the number of operators
     */
    public HierarchicalDiversity (double _percentPopulation, ReductionType _reductionType, AbstractDiversity ... _subOperators) {
        super(_percentPopulation);

        reduction = new DiversityReduction(_reductionType, _subOperators);
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
    public HierarchicalDiversity(HierarchicalType _type, ReductionType _reductionType, AbstractDiversity ... _subOperators) {
        super();

        reduction = new DiversityReduction(_type, _reductionType, _subOperators);
    }

    /**
     * the constructor
     *
     * @param _percentPopulation the percent of a population, we do the diversity measuring with. The individuums will be chosen randomly.
     * @param _type the basic-type of the operator. More details in the java-doc of the enum HierarchicalType
     * @param _reductionType the type of reduction (how will the different sub-results be merged)
     * @param _subOperators the sub-operators of this operator. They behave like specified in type
     * @throws IllegalArgumentException if percentPopulation is not between 0 and 1 or equal to 0 or if the subOperators are empty or a null is in the subOperators or the reduction-type is not compatible with the number of operators
     * @throws NullPointerException if type is null
     */
    public HierarchicalDiversity (double _percentPopulation, HierarchicalType _type, ReductionType _reductionType, AbstractDiversity ... _subOperators) {
        super(_percentPopulation);

        reduction = new DiversityReduction(_type, _reductionType, _subOperators);
    }

    @Override
    protected double calculateDiversity(List<GenInstance> population, AlgorithmStep step) {
        HierarchicalInstance [] input = new HierarchicalInstance[population.size()];
        for (int i=0; i<input.length; i++)
            input[i] = (HierarchicalInstance)population.get(i);
        return reduction.reduce(input, step);
    }

    @Override
    public String getName() {
        Operator [] operators = reduction.getSubOperators();

        String operatorsNames = "";
        for (int i=0; i<operators.length; i++)
            operatorsNames += (i == 0 ? "" : ", ") + "'" + ((AbstractDiversity)operators[i]).getName() + "'";

        return "hierarchical diversity (" + operatorsNames + ")";
    }

    @Override
    public boolean isCompatible(AlgorithmPass algorithmPass) {
        return reduction.isCompatible(algorithmPass);
    }

    @Override
    public boolean isCompatible(GenRepresentation representation) {
        return reduction.isCompatible(representation);
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.extendList(super.getAttributes(), new Attribute(new AttributeType(Type.NormalAttribute), "reduction", reduction));
    }

    /**
     * this class is a hierarchical-reduction-operator for diversities
     */
    protected static class DiversityReduction extends HierarchicalReductionOp {

        /**
         * the constructor, the sub-operators have to match the childs of the instances exactly.
         *
         * @param _reductionType the type of reduction (how will the different sub-results be merged)
         * @param _subOperators the sub-operators of this operator. They behave like specified in type
         * @throws IllegalArgumentException if the subOperators are empty or a null is in the subOperators or the reduction-type is not compatible with the number of operators
         */
        public DiversityReduction (ReductionType _reductionType, Operator ... _subOperators) {
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
        public DiversityReduction (HierarchicalType _type, ReductionType _reductionType, Operator ... _subOperators) {
            super(_type, _reductionType, _subOperators);
        }

        /**
         * do the actual reduction (the hierarchical-diversity will invoke
         * this method). It is the same as defined in the super-class, but
         * it is for multiple instances, because in the diversity-operator
         * we work on a population and not on individuums
         *
         * @param input the hierachical-instances
         * @param step the algorithm-step
         * @return the double-result
         */
        public double reduce (HierarchicalInstance [] input, AlgorithmStep step) {

            //for every subOperator-index (1st dimension) we match a list of all GenInstances, he has to work on
            List <List <GenInstance> > operatorToInstances = new ArrayList();
            for (int i=0; i<subOperators.length; i++)
                operatorToInstances.add(new ArrayList());

            for (int i=0; i<input.length; i++) {
                List <Integer> opIndices = input[i].getMatchedOperators(hierarchicalIndexToOperators((Hierarchical)input[i].getRepresentation(), reductionType.buildIgnoreArray(subOperators.length)), step);
                GenInstance [] subInput = input[i].getChilds();

                //collect all operator-matchings for the current individuum (with index i)
                for (int j=0; j<opIndices.size(); j++)
                    if (opIndices.get(j) != null)
                        operatorToInstances.get(opIndices.get(j)).add(subInput[j]);
            }

            //create a double-array for the sub-results per operator
            double [] values = new double[subOperators.length];
            for (int i=0; i<subOperators.length; i++)
                if (operatorToInstances.get(i).size() != 0)
                    values[i] = calculateSubResult(subOperators[i], operatorToInstances.get(i).toArray(new GenInstance[operatorToInstances.get(i).size()]), step);

            return reductionType.reduce(values);
        }

        @Override
        protected double calculateSubResult(Operator op, GenInstance [] input, AlgorithmStep step) {
            return ((AbstractDiversity)op).diversityOp(Arrays.asList(input), step);
        }

        @Override
        protected boolean operatorCompatible(Operator op, GenRepresentation representation) {
            return ((AbstractDiversity)op).isCompatible(representation);
        }

    }


}
