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

package genlib.standard.representations;

import genlib.abstractrepresentation.AlgorithmStep;
import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.abstractrepresentation.GenRepresentation;
import genlib.abstractrepresentation.Operator;
import genlib.utils.Exceptions.GeneticInternalException;
import genlib.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This GenRepresentation represents a hierarchical structure
 * with a static number of childs.
 *
 * @author Hilmar
 */
public abstract class Hierarchical extends GenRepresentation {

    /**
     * the childs of this GenRepresentation, they are all non-null
     */
    protected final GenRepresentation [] childs;

    /**
     * the constructor
     *
     * @param _childs the childs
     * @throws IllegalArgumentException if there are 0 childs or one of the childs is null
     */
    public Hierarchical (GenRepresentation ... _childs) {
        if (_childs == null || _childs.length == 0)
            throw new IllegalArgumentException("there has to be at least one child.");
        for (GenRepresentation child: _childs)
            if (child == null)
                throw new IllegalArgumentException("a 'null'-child is not allowed.");

        childs = new GenRepresentation[_childs.length];
        System.arraycopy(_childs, 0, childs, 0, _childs.length);
    }

    /**
     * get the childs, this is a copy of the childs
     *
     * @return a copy of the childs
     */
    public GenRepresentation [] getChilds () {
        GenRepresentation [] copyChilds = new GenRepresentation[childs.length];
        System.arraycopy(childs, 0, copyChilds, 0, childs.length);
        return copyChilds;
    }

    /**
     * instantiate a child-instance with the given childs as parameter
     *
     * @param _childs the parameter for the new instance
     * @return the requested instance
     * @throws IllegalArgumentException if the childs-parameter is invalid (for more details look at the java-doc of the instance-constructors, respectively)
     */
    public abstract HierarchicalInstance instantiateFromChilds (GenInstance ... _childs);

    @Override
    protected boolean isEquals(GenRepresentation other) {
        return other.getClass().equals(getClass()) && Arrays.equals(childs, ((Hierarchical)other).childs);
    }

    @Override
    public List<Attribute> getAttributes() {
        return Utils.createList(new Attribute(new AttributeType(Type.MainAttribute), "childs", childs));
    }

    /**
     * this class represents a hierarchical GenInstance with a static
     * number of childs
     */
    public static abstract class HierarchicalInstance extends GenInstance {

        /**
         * the childs, they are all non-null
         */
        protected final GenInstance [] childs;

        /**
         * the constructor
         *
         * @param _parent the parent representation
         * @param _childs the childs
         * @throws IllegalArgumentException if there are 0 childs or one of the childs is null
         */
        public HierarchicalInstance(Hierarchical _parent, GenInstance ... _childs) {
            super(_parent);

            if (_childs == null || _childs.length == 0)
                throw new IllegalArgumentException("there has to be at least one child.");
            for (GenInstance child: _childs)
                if (child == null)
                    throw new IllegalArgumentException("a 'null'-child is not allowed.");

            childs = new GenInstance[_childs.length];
            System.arraycopy(_childs, 0, childs, 0, _childs.length);
        }

        /**
         * get the childs, this is a copy of the childs
         *
         * @return a copy of the childs
         */
        public GenInstance [] getChilds () {
            GenInstance [] copyChilds = new GenInstance[childs.length];
            System.arraycopy(childs, 0, copyChilds, 0, childs.length);
            return copyChilds;
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.extendList(super.getAttributes(), new Attribute(new AttributeType(Type.MainAttribute), "childs", childs));
        }

        /**
         * We get a map, that maps every child (as index) of the representation to
         * a list of one or more possible operators (also as index). In the case
         * of multiple operators, the operator will be chosen randomly. If
         * the input-map does not contain a key, that is needed, return null for
         * this operator-index in the return-list
         *
         * @param indexToOp the map, that maps child-indices to possible operators-indices
         * @param step the algorithm-step
         * @return a list of operator-indices, that should be executed per child of the instance (child 0 of instance has the first operator in this list, child 1 of the instance the second and so on ..)
         */
        public abstract List <Integer> getMatchedOperators(Map <Integer, List <Integer> > indexToOp, AlgorithmStep step);

        /**
         * returns an int-array (with the size of the instance-childs), where every
         * integer returns the index of the corresponding parent of the representation-
         * parent.
         *
         * @return the index-array
         */
        public abstract int[] getIndicesOfChilds ();

    }

    /**
     * the representation of an And. Has a static number of
     * childs, and an operator operates on all childs.
     */
    public static class And extends Hierarchical {

        /**
         * the constructor
         *
         * @param _childs the childs
         * @throws IllegalArgumentException if there are 0 childs or one of the childs is null
         */
        public And (GenRepresentation ... _childs) {
            super(_childs);
        }

        @Override
        public GenInstance instantiateRandom(AlgorithmStep step) {
            GenInstance[] randomInstances = new GenInstance[childs.length];
            for (int i=0; i<childs.length; i++)
                randomInstances[i] = childs[i].instantiateRandom(step);
            return new AndInstance(this, randomInstances);
        }

        @Override
        public HierarchicalInstance instantiateFromChilds (GenInstance ... _childs) {
            return new AndInstance(this, _childs);
        }

        /**
         * this GenInstance represents an And-Connection between GenInstances
         */
        public static class AndInstance extends HierarchicalInstance {

            /**
             * the constructor
             *
             * @param _parent the parent-representation
             * @param _childs the childs
             * @throws IllegalArgumentException if there are 0 childs or one of the childs is null or one of the childs is not compatible with the parents-childs
             */
            public AndInstance(And _parent, GenInstance ... _childs) {
                super(_parent, _childs);

                for (int i=0; i<childs.length; i++)
                    if (!childs[i].getRepresentation().equals(_parent.childs[i]))
                        throw new IllegalArgumentException("child No. '" + i + "' is not compatible with parent.");
            }

            @Override
            public List <Integer> getMatchedOperators(Map <Integer, List <Integer> > indexToOp, AlgorithmStep step) {
                List <Integer> ret = new ArrayList();

                for (int i=0; i<childs.length; i++) {
                    if (!indexToOp.containsKey(i))
                        ret.add(null);
                    else {
                        List <Integer> opList = indexToOp.get( i );
                        ret.add(opList.get(step.getRandom().nextInt(opList.size())));
                    }
                }

                return ret;
            }

            @Override
            public int[] getIndicesOfChilds () {
                int [] ret = new int [childs.length];
                for (int i=0; i<ret.length; i++)
                    ret[i] = i;
                return ret;
            }

        }

    }

    /**
     * the representation of an Or. Has a static number of
     * childs, and an operator operates on one chosen child.
     */
    public static class Or extends Hierarchical {

        /**
         * the probabilities, that one of the childs will be chosen,
         * respectively. The chance of child i to be chosen (between
         * 0.0 and 1.0) is (probabilities[i] - probabilities[i-1]) or
         * probabilities[0], if i is 0
         */
        protected final double [] probabilities;

        /**
         * the constructor, the probability to choose one child is
         * equal, respectively
         *
         * @param _childs the childs
         * @throws IllegalArgumentException if there are 0 childs or one of the childs is null
         */
        public Or (GenRepresentation ... _childs) {
            super(_childs);

            probabilities = new double[childs.length];
            for (int i=0; i<probabilities.length; i++)
                probabilities[i] = (double)(i+1) / probabilities.length;
        }

        /**
         * the constructor, the probability to choose one child is
         * equal, respectively
         *
         * @param _probabilities the probabilities of each child, to be chosen. The probabilities will be normalized to the sum of all probabilities
         * @param _childs the childs
         * @throws IllegalArgumentException if there are 0 childs or one of the childs is null, the number of childs is not the same as the number of probabilities, one of the probabilities is NaN, infinite or smaller or equal to 0 or there are two identically childs
         */
        public Or (double [] _probabilities, GenRepresentation ... _childs) {
            super(_childs);

            if (_probabilities == null || _childs.length != _probabilities.length)
                throw new IllegalArgumentException("childs and probabilities have to have the same length.");
            for (double probability : _probabilities)
                if (!Double.isFinite(probability) || probability <= 0)
                    throw new IllegalArgumentException("a probability is NaN, infinite or <= 0.");
            for (int i=0; i<_childs.length; i++)
                for (int j=i+1; j<_childs.length; j++)
                    throw new IllegalArgumentException("two childs with the same GenRepresentation (equals() == true) are not allowed");

            //calculate the sum of all probabilites, so we can norm the probabilities
            probabilities = new double[_probabilities.length];
            System.arraycopy(_probabilities, 0, probabilities, 0, _probabilities.length);
            double sum = 0;
            for (double probability : _probabilities)
                sum += probability;

            for (int i=0; i<probabilities.length; i++)
                probabilities[i] = probabilities[i]/sum + (i == 0 ? 0 : probabilities[i-1]);
        }

        @Override
        public GenInstance instantiateRandom(AlgorithmStep step) {
            double randValue = step.getRandom().nextDouble();
            for (int i=0; i<probabilities.length; i++)
                if (randValue <= probabilities[i] || i == probabilities.length-1)
                    return new OrInstance(this, childs[i].instantiateRandom(step));

            //because the length of probabilities is > 0, this exception should be unreachable
            throw new GeneticInternalException("unreachable exception (internal erro)");
        }

        @Override
        public HierarchicalInstance instantiateFromChilds (GenInstance ... _childs) {
            return new OrInstance(this, _childs[0]);
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.extendList(super.getAttributes(), new Attribute(new AttributeType(Type.NormalAttribute), "probabilities", probabilities));
        }

        /**
         * this GenInstance represents an Or with one chosen GenInstances
         */
        public static class OrInstance extends HierarchicalInstance {

            /**
             * the index in the parents childs, where this instance is
             * instantiated from (cached value, calculated in the constructor)
             */
            protected final int representationIndex;

            /**
             * the constructor
             *
             * @param _parent the parent-representation
             * @param _chosen the child
             * @throws IllegalArgumentException if _chosen is null or not compatible with the given child of the parent
             */
            public OrInstance(Or _parent, GenInstance _chosen) {
                super(_parent, new GenInstance[] {_chosen});

                for (int i=0; i<_parent.childs.length; i++)
                    if (_chosen.getRepresentation().equals(_parent.childs[i])) {
                        representationIndex = i;
                        return;
                    }

                throw new IllegalArgumentException("chosen is not compatible with parent.");
            }

            @Override
            public List <Integer> getMatchedOperators(Map <Integer, List <Integer> > indexToOp, AlgorithmStep step) {
                List <Integer> ret = new ArrayList();

                if (!indexToOp.containsKey(representationIndex))
                    ret.add(null);

                else {
                    List <Integer> opList = indexToOp.get( representationIndex );
                    ret.add(opList.get(step.getRandom().nextInt(opList.size())));
                }

                return ret;
            }

            @Override
            public int[] getIndicesOfChilds () {
                return new int [] {representationIndex};
            }

            @Override
            public List<Attribute> getAttributes() {
                return Utils.extendList(super.getAttributes(), new Attribute(new AttributeType(Type.TemporaryOrUnimportant), "representationIndex", representationIndex));
            }

        }

    }

}
