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

package genlib.standard.algorithms;

import genlib.abstractrepresentation.AlgorithmPass;
import genlib.abstractrepresentation.AlgorithmStep;
import genlib.utils.Utils;
import java.util.List;
import java.util.Random;

/**
 * The corresponding AlgorithmStep of StaticAlgorithmPass
 *
 * @author Hilmar
 */
public class StaticAlgorithmStep extends AlgorithmStep {

    /**
     * the parent
     */
    protected final StaticAlgorithmPass parent;

    /**
     * the current generation
     */
    protected final int generation;

    /**
     * the sub-step while being in one generation
     */
    protected final int operationStep;

    /**
     * the constructor
     *
     * @param _parent the parent
     */
    public StaticAlgorithmStep (StaticAlgorithmPass _parent) {
        this(_parent, 0);
    }

    /**
     * the constructor
     *
     * @param _parent the parent
     * @param _generation the generation-number
     */
    public StaticAlgorithmStep (StaticAlgorithmPass _parent, int _generation) {
        parent = _parent;
        generation = _generation;
        operationStep = -1;
    }

    /**
     * the constructor
     *
     * @param from copied from this StaticAlgorithmStep
     * @param _step sub-step while being in one generation
     */
    public StaticAlgorithmStep (StaticAlgorithmStep from, int _step) {
        parent = from.parent;
        generation = from.generation;
        operationStep = _step;
    }

    /**
     * the getter for the generation-number
     *
     * @return the generation-number
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * getter for the sub-step while being in one generation
     *
     * @return the sub-step
     */
    public int getOperationStep() {
        return operationStep;
    }

    @Override
    public boolean hasNext() {
        return generation < parent.getGenerations()-1;
    }

    @Override
    public AlgorithmStep next() {
        return new StaticAlgorithmStep(parent, generation+1);
    }

    @Override
    public AlgorithmPass getParent() {
        return parent;
    }

    @Override
    public Random getRandom() {
        return parent.random;
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.HierarchicalParent), "parent", parent),
                                new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "generation", generation),
                                new Attribute(new AttributeType(AttributeType.Type.NormalAttribute), "operationStep", operationStep));
    }

}
