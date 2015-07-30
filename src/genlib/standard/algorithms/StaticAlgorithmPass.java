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
 * the algorithmPass with static generation and population size
 *
 * @author Hilmar
 */
public class StaticAlgorithmPass extends AlgorithmPass {

    /**
     * the population size
     */
    protected final int population;

    /**
     * this is the size of the population, that will be the
     * same. The worst individuums outside this size will be
     * replaced through new individuums.
     */
    protected final int retainedPopulation;

    /**
     * the number of generations
     */
    protected final int generations;

    /**
     * the probability of a mutation. A mutation replaces a
     * recombination.
     */
    protected final double mutationProbability;

    /**
     * the random-object
     */
    protected final Random random;

    /**
     * the constructor
     *
     * @param _population the population size
     * @param _retainedPopulation this is the size of the population, that will be the same. The worst individuums outside this size will be replaced through new individuums.
     * @param _generations the number of generations
     * @param _mutationProbability the probability of a mutation. A mutation replaces a recombination
     */
    public StaticAlgorithmPass (int _population, int _retainedPopulation, int _generations, double _mutationProbability) {
        population = _population;
        retainedPopulation = _retainedPopulation;
        generations = _generations;
        mutationProbability = _mutationProbability;
        random = new Random(System.nanoTime());

        if (population <= 0)
            throw new IllegalArgumentException("invalid population: '" + population + "'.");
        if (retainedPopulation <= 0)
            throw new IllegalArgumentException("invalid retainedPopulation: '" + retainedPopulation + "'.");
        if (retainedPopulation > population)
            throw new IllegalArgumentException("retainedPopulation have to be smaller or equals to population: '" + retainedPopulation + " / " + population + "'.");
        if (generations <= 0)
            throw new IllegalArgumentException("invalid generations: '" + generations + "'.");
        if (mutationProbability < 0 || mutationProbability > 1)
            throw new IllegalArgumentException("invalid mutationProbability: '" + mutationProbability + "'.");
    }

    /**
     * getter for the population size
     *
     * @return the population size
     */
    public int getPopulation () {
        return population;
    }

    /**
     * getter for the retained population. This is the size of the population,
     * that will be the same. The worst individuums outside this size will be
     * replaced through new individuums.
     *
     * @return the retained population size
     */
    public int getRetainedPopulation () {
        return retainedPopulation;
    }

    /**
     * getter for the number of generations
     *
     * @return the number of generations
     */
    public int getGenerations () {
        return generations;
    }

    /**
     * getter for the mutation probability
     *
     * @return the mutation probability
     */
    public double getMutationProbability () {
        return mutationProbability;
    }

    @Override
    public AlgorithmStep createInitial() {
        return new StaticAlgorithmStep(this);
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "population", population),
                                new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "retainedPopulation", retainedPopulation),
                                new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "generations", generations),
                                new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "mutationProbability", mutationProbability),
                                new Attribute(new AttributeType(AttributeType.Type.TemporaryOrUnimportant), "random", random));
    }

}
