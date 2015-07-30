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
import genlib.abstractrepresentation.GenInstance;
import genlib.abstractrepresentation.GeneticAlgorithm;

/**
 * this algorithm has a static generation and population size. In every
 * generation the individuums outside the best 'retainedPopulation'-individuums
 * will be replaced through recombinated or mutated ones.
 *
 * @author Hilmar
 */
public class StaticGeneticAlgorithm extends GeneticAlgorithm {

    /**
     * the constructor. The name of the algorithm is given automatically (A0, A1, A2, ..)
     */
    public StaticGeneticAlgorithm () {
        super();
    }

    /**
     * the constructor with an individual given name
     * @param _name the name for the algorithm
     */
    public StaticGeneticAlgorithm (String _name) {
        super(_name);
    }

    @Override
    protected void doStep(AlgorithmStep step) {

        StaticAlgorithmPass pass = (StaticAlgorithmPass)step.getParent();

        //the first step?
        if (population.isEmpty()) {
            for (int i=0; i<pass.getPopulation(); i++) {
                AlgorithmStep subStep = new StaticAlgorithmStep((StaticAlgorithmStep)step, i);
                GenInstance instance = this.genoType.instantiateRandom(subStep);
                population.add(new Individuum(instance, subStep));
            }

        //the second, third, .. step (recombination & mutation)
        } else {
            for (int i=0; i<pass.getPopulation()-pass.retainedPopulation; i++) {
                AlgorithmStep subStep = new StaticAlgorithmStep((StaticAlgorithmStep)step, i);

                //the mutation-case
                if (step.getRandom().nextDouble() < pass.getMutationProbability() || pass.getMutationProbability() == 1.0) {
                    GenInstance input = population.get(step.getRandom().nextInt(pass.getRetainedPopulation())).getGenoType();
                    GenInstance result = mutation.mutationOp(input, subStep);
                    population.set(i+pass.getRetainedPopulation(), new Individuum(result, subStep));
                //the recombination-case
                } else {
                    GenInstance left = population.get(step.getRandom().nextInt(pass.getRetainedPopulation())).getGenoType();
                    GenInstance right = population.get(step.getRandom().nextInt(pass.getRetainedPopulation())).getGenoType();
                    GenInstance result = recombination.recombinationOp(new GenInstance[] {left, right}, subStep, 1)[0];
                    population.set(i+pass.getRetainedPopulation(), new Individuum(result, subStep));
                }
            }
        }
    }

    @Override
    protected AlgorithmPass getStandardAlgorithmPass() {
        return new StaticAlgorithmPass(256, 64, 512, 0.1);
    }

    @Override
    protected boolean isCompatible(AlgorithmPass algorithmPass) {
        return algorithmPass instanceof StaticAlgorithmPass;
    }

    @Override
    protected int[] getRecombinationInputSize() {
        return new int[] {2};
    }

    @Override
    protected int[] getRecombinationOutputSize() {
        return new int[] {1};
    }

}
