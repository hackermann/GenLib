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

package genlib.abstractrepresentation;

import genlib.abstractrepresentation.GenObject.AttributeType.Recommended;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.output.Logger;
import genlib.standard.operators.AverageFitness;
import genlib.standard.operators.GenoToPhenoIdentity;
import genlib.standard.operators.KPointCrossover;
import genlib.standard.operators.OnePointMutation;
import genlib.standard.representations.BooleanStaticLength;
import genlib.utils.Exceptions.GeneticRuntimeException;
import genlib.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains all operators and informations, to do a simple genetic algorithm
 *
 * @author Hilmar
 */
public abstract class GeneticAlgorithm extends GenObject {

    /**
     * the name of the algorithm. If not given, it will be generated
     * automatically (A0, A1, A2, ..)
     */
    protected final String name;

    /**
     * the counter for the automatically generated algorithm-names
     */
    protected static long algorithmNameCounter = 0;

    /**
     * represents the genoType and the phenoType. They are not instantiated yet,
     * they just represent the type of the genoType and phenoType.
     */
    protected GenRepresentation genoType, phenoType;

    /**
     * the recombination-operator.
     */
    protected RecombinationOp recombination;

    /**
     * the mutation-operator.
     */
    protected MutationOp mutation;

    /**
     * the geno-to-pheno-operator. Converts a genoType into the phenoType.
     */
    protected GenoToPhenoOp genoToPheno;

    /**
     * the fitness-operator. Calculates the fitness of a given phenoType.
     */
    protected FitnessOp fitness;

    /**
     * An algorithm-pass defines the environment of one algorithm call.
     * It will be always the same, if staticAlgorithmPass is not null.
     */
    protected AlgorithmPass staticAlgorithmPass;

    /**
     * the loggers, which will log the results. Can be empty,
     * additionally there can be more loggers defined while starting.
     */
    protected List <Logger> staticLoggers = new ArrayList();

    /**
     * is the algorithm currently running? If yes, you can't change the
     * operators and representations.
     */
    protected boolean isRunning = false;

    /**
     * the generation of the current run. If the algorithm is not running
     * anymore, it is the last generation of the last run.
     */
    protected int currentGeneration = 0;

    /**
     * the population of the current run. If the algorithm is not running
     * anymore, it is the population of the last generation of the last run.
     */
    protected List <Individuum> population = new ArrayList();

    /**
     * the constructor. The name of the algorithm is given automatically (A0, A1, A2, ..)
     */
    public GeneticAlgorithm () {
        name = "A" + (algorithmNameCounter++);
    }

    /**
     * the constructor with an individual given name
     *
     * @param _name the name for the algorithm
     * @throws NullPointerException if the name is null
     */
    public GeneticAlgorithm (String _name) {
        if (_name == null)
            throw new NullPointerException("name cannot be null.");
        name = _name;
    }

    /**
     * sets the genoType
     *
     * @param _genoType the genoType
     * @throws NullPointerException is thrown, if parameter is null
     * @throws IllegalArgumentException is thrown, if not compatible with a previous set operator
     * @throws GeneticRuntimeException is thrown, if the algorithm is currently running
     */
    public void setGenoType (GenRepresentation _genoType) {
        if (isRunning)
            throw new GeneticRuntimeException("you can't change the genoType, while the algorithm is running.");
        if (_genoType == null)
            throw new NullPointerException("genoType cannot be null.");

        genoType = _genoType;

        if (recombination != null && !recombination.isCompatible(genoType))
            throw new IllegalArgumentException("genoType is not compatible with recombinationOperator.");
        if (mutation != null && !mutation.isCompatible(genoType))
            throw new IllegalArgumentException("genoType is not compatible with mutationOperator.");
        if (genoToPheno != null && !genoToPheno.isCompatible(genoType))
            throw new IllegalArgumentException("genoType is not compatible with genoToPhenoOperator.");
        if (genoToPheno != null && phenoType != null && !genoToPheno.isGenoPhenoCompatible(genoType, phenoType))
            throw new IllegalArgumentException("genoType/phenoType-combination is not compatible with genoToPhenoOperator.");
    }

    /**
     * returns the genoType
     *
     * @return the genoType
     */
    public GenRepresentation getGenoType() {
        return genoType;
    }

    /**
     * sets the phenoType
     *
     * @param _phenoType the phenoType
     * @throws NullPointerException is thrown, if parameter is null
     * @throws IllegalArgumentException is thrown, if not compatible with a previous set operator
     * @throws GeneticRuntimeException is thrown, if the algorithm is currently running
     */
    public void setPhenoType (GenRepresentation _phenoType) {
        if (isRunning)
            throw new GeneticRuntimeException("you can't change the phenoType, while the algorithm is running.");
        if (_phenoType == null)
            throw new NullPointerException("phenoType cannot be null.");

        phenoType = _phenoType;

        if (genoToPheno != null && genoType != null && !genoToPheno.isGenoPhenoCompatible(genoType, phenoType))
            throw new IllegalArgumentException("genoType/phenoType-combination is not compatible with genoToPhenoOperator.");
        if (fitness != null && !fitness.isCompatible(phenoType))
            throw new IllegalArgumentException("phenoType is not compatible with fitnessOperator.");
    }

    /**
     * returns the phenoType
     *
     * @return the phenoType
     */
    public GenRepresentation getPhenoType() {
        return phenoType;
    }

    /**
     * sets the recombinationOperator
     *
     * @param _recombinationOp the recombinationOperator
     * @throws NullPointerException is thrown, if parameter is null
     * @throws IllegalArgumentException is thrown, if not compatible with a previous set genoType
     * @throws GeneticRuntimeException is thrown, if the algorithm is currently running
     */
    public void setRecombinationOp (RecombinationOp _recombinationOp) {
        if (isRunning)
            throw new GeneticRuntimeException("you can't change the recombinationOperator, while the algorithm is running.");
        if (_recombinationOp == null)
            throw new NullPointerException("recombinationOperator cannot be null.");

        recombination = _recombinationOp;

        if (genoType != null && !recombination.isCompatible(genoType))
            throw new IllegalArgumentException("genoType is not compatible with recombinationOperator.");
    }

    /**
     * returns the recombinationOperator
     *
     * @return the recombinationOperator
     */
    public RecombinationOp getRecombinationOp() {
        return recombination;
    }

    /**
     * sets the mutationOperator
     *
     * @param _mutationOp the mutationOperator
     * @throws NullPointerException is thrown, if parameter is null
     * @throws IllegalArgumentException is thrown, if not compatible with a previous set genoType
     * @throws GeneticRuntimeException is thrown, if the algorithm is currently running
     */
    public void setMutationOp (MutationOp _mutationOp) {
        if (isRunning)
            throw new GeneticRuntimeException("you can't change the mutationOperator, while the algorithm is running.");
        if (_mutationOp == null)
            throw new NullPointerException("mutationOperator cannot be null.");

        mutation = _mutationOp;

        if (genoType != null && !mutation.isCompatible(genoType))
            throw new IllegalArgumentException("genoType is not compatible with mutationOperator.");
    }

    /**
     * returns the mutationOperator
     *
     * @return the mutationOperator
     */
    public MutationOp getMutationOp() {
        return mutation;
    }

    /**
     * sets the genoToPhenoOperator
     *
     * @param _genoToPhenoOp the genoToPhenoOperator
     * @throws NullPointerException is thrown, if parameter is null
     * @throws IllegalArgumentException is thrown, if not compatible with a previous set genoType or phenoType
     * @throws GeneticRuntimeException is thrown, if the algorithm is currently running
     */
    public void setGenoToPhenoOp (GenoToPhenoOp _genoToPhenoOp) {
        if (isRunning)
            throw new GeneticRuntimeException("you can't change the genoToPhenoOperator, while the algorithm is running.");
        if (_genoToPhenoOp == null)
            throw new NullPointerException("genoToPhenoOperator cannot be null.");

        genoToPheno = _genoToPhenoOp;

        if (genoType != null && !genoToPheno.isCompatible(genoType))
            throw new IllegalArgumentException("genoType is not compatible with genoToPhenoOperator.");
        if (genoType != null && phenoType != null && !genoToPheno.isGenoPhenoCompatible(genoType, phenoType))
            throw new IllegalArgumentException("genotype/phenotype-combination is not compatible with genoToPhenoOperator.");
    }

    /**
     * returns the genoToPhenoOperator
     *
     * @return the genoToPhenoOperator
     */
    public GenoToPhenoOp getGenoToPhenoOp() {
        return genoToPheno;
    }

    /**
     * sets the fitnessOperator
     *
     * @param _fitnessOp the fitnessOperator
     * @throws NullPointerException is thrown, if parameter is null
     * @throws IllegalArgumentException is thrown, if not compatible with a previous set phenoType
     * @throws GeneticRuntimeException is thrown, if the algorithm is currently running
     */
    public void setFitnessOp (FitnessOp _fitnessOp) {
        if (isRunning)
            throw new GeneticRuntimeException("you can't change the fitnessOperator, while the algorithm is running.");
        if (_fitnessOp == null)
            throw new NullPointerException("fitnessOperator cannot be null.");

        fitness = _fitnessOp;

        if (phenoType != null && !fitness.isCompatible(phenoType))
            throw new IllegalArgumentException("phenoType is not compatible with fitnessOperator.");
    }

    /**
     * returns the fitnessOperator
     *
     * @return the fitnessOperator
     */
    public FitnessOp getFitnessOp() {
        return fitness;
    }

    /**
     * sets the staticAlgorithmPass (null = there is no static algorithm pass)
     *
     * @param _staticAlgorithmPass the staticAlgorithmPass
     * @throws GeneticRuntimeException is thrown, if the algorithm is currently running
     */
    public void setStaticAlgorithmPass (AlgorithmPass _staticAlgorithmPass) {
        if (isRunning)
            throw new GeneticRuntimeException("you can't change the staticAlgorithmPass, while the algorithm is running.");

        staticAlgorithmPass = _staticAlgorithmPass;
    }

    /**
     * returns the staticAlgorithmPass
     *
     * @return the staticAlgorithmPass
     */
    public AlgorithmPass getStaticAlgorithmPass() {
        return staticAlgorithmPass;
    }

    /**
     * adds the staticLogger
     *
     * @param logger the staticLogger
     * @throws NullPointerException is thrown, if the logger is null
     * @throws GeneticRuntimeException is thrown, if the algorithm is currently running
     */
    public void addStaticLogger(Logger logger) {
        if (isRunning)
            throw new GeneticRuntimeException("you can't change the staticLoggers, while the algorithm is running.");
        if (logger == null)
            throw new NullPointerException("logger can't be null");

        staticLoggers.add(logger);
    }

    /**
     * removes the staticLogger
     *
     * @param logger the staticLogger
     * @throws NullPointerException is thrown, if the logger is null
     * @throws GeneticRuntimeException is thrown, if the algorithm is currently running or the logger isn't in the staticLogger-list yet
     */
    public void removeStaticLogger(Logger logger) {
        if (isRunning)
            throw new GeneticRuntimeException("you can't change the staticLoggers, while the algorithm is running.");
        if (logger == null)
            throw new NullPointerException("logger can't be null");
        if (!staticLoggers.contains(logger))
            throw new GeneticRuntimeException("the staticLoggers don't contain this logger.");

        staticLoggers.remove(logger);
    }

    /**
     * the getter for the algorithm-name
     * @return the name of the algorithm
     */
    public String getName() {
        return name;
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.createList( new Attribute(new AttributeType(Type.Descriptor), "name", name),
                                new Attribute(new AttributeType(Type.MainAttribute), "genoType", genoType),
                                new Attribute(new AttributeType(Type.MainAttribute), "phenoType", phenoType),
                                new Attribute(new AttributeType(Type.MainAttribute), "recombination", recombination),
                                new Attribute(new AttributeType(Type.MainAttribute), "mutation", mutation),
                                new Attribute(new AttributeType(Type.MainAttribute), "genoToPheno", genoToPheno),
                                new Attribute(new AttributeType(Type.MainAttribute), "fitness", fitness),
                                new Attribute(new AttributeType(Type.NormalAttribute, Recommended.No, Recommended.NotSpecified), "staticAlgorithmPass", staticAlgorithmPass),
                                new Attribute(new AttributeType(Type.NormalAttribute, Recommended.No, Recommended.NotSpecified), "staticLoggers", staticLoggers),
                                new Attribute(new AttributeType(Type.MainAttribute), "isRunning", isRunning),
                                new Attribute(new AttributeType(Type.NormalAttribute), "currentGeneration", currentGeneration),
                                new Attribute(new AttributeType(Type.NormalAttribute), "population", population));
    }



    // ===================================
    // the runtime-code starts here
    // ===================================

    /**
     * returns the current population. If the algorithm is not running anymore,
     * it returns the population of the last run of the last generation. The
     * population is already sort at decreasing order. (by fitness, best individuum first)
     *
     * @return the population
     */
    public Individuum [] getCurrentPopulation () {
        return population.toArray(new Individuum[population.size()]);
    }

    /**
     * the number of the current generation. If the algorithm is not running
     * anymore, it returns the last generation of the last run.
     *
     * @return the generation
     */
    public int getCurrentGeneration() {
        return currentGeneration;
    }

    /**
     * initializes the algorithm. Every null-value of the parameters will be set
     * to a standard value.
     *
     * @throws IllegalArgumentException is thrown, if one of the standard-values are not compatible with another set value
     * @throws GeneticRuntimeException is thrown, if one of the standard-values are not compatible with another set value.
     */
    private void initialize () {

        //should be set normally, but in the case not set, use standard binary array
        if (genoType == null)
            setGenoType(new BooleanStaticLength(256));

        //in this case, there is no difference between geno- and pheno-type
        if (phenoType == null)
            setPhenoType(genoType);

        if (recombination == null)
            setRecombinationOp(new KPointCrossover(1));
        if (mutation == null)
            setMutationOp(new OnePointMutation());
        if (genoToPheno == null)
            setGenoToPhenoOp(new GenoToPhenoIdentity());
        if (fitness == null)
            setFitnessOp(new AverageFitness());

        //do some compatibility checks
        for (int inputSize : getRecombinationInputSize())
            if (!recombination.isInputSizeCompatible(inputSize))
                throw new GeneticRuntimeException("incompatible input-size (between algorithm and recombinationOperator): '" + inputSize + "'.");
        for (int outputSize : getRecombinationOutputSize())
            if (!recombination.isOutputSizeCompatible(outputSize))
                throw new GeneticRuntimeException("incompatible output-size (between algorithm and recombinationOperator): '" + outputSize + "'.");
    }

    protected abstract AlgorithmPass getStandardAlgorithmPass();

    /**
     * start the algorithm. If a staticAlgorithmPass is defined, use this one, otherwise
     * a standard AlgorithmPass will be used.
     *
     * @param dynamicLoggers optional loggers, that will be invoked, additionaly to the staticLoggers.
     * @throws NullPointerException if the dynamicLoggers contains a null
     */
    public void run (Logger ... dynamicLoggers) {
        if (staticAlgorithmPass == null)
            run(getStandardAlgorithmPass(), dynamicLoggers);
        else
            run(staticAlgorithmPass);
    }

    /**
     * start the algorithm with the specified AlgorithmPass.
     *
     * @param algorithmPass the algorithm-pass
     * @param dynamicLoggers optional loggers, that will be invoked, additionaly to the staticLoggers.
     * @throws NullPointerException if algorithmPass is null or the dynamicLoggers contains a null
     */
    public void run (AlgorithmPass algorithmPass, Logger ... dynamicLoggers) {

        if (algorithmPass == null)
            throw new NullPointerException("algorithmPass can't be null.");
        for (Logger logger : dynamicLoggers)
            if (logger == null)
                throw new NullPointerException("there is a null-logger in the dynamicLoggers.");

        //merge the static and the dynamic loggers
        Logger [] loggers = new Logger[staticLoggers.size()+dynamicLoggers.length];
        System.arraycopy(staticLoggers.toArray(new Logger[staticLoggers.size()]), 0, loggers, 0, staticLoggers.size());
        System.arraycopy(dynamicLoggers, 0, loggers, staticLoggers.size(), dynamicLoggers.length);

        //initializing
        initialize();
        isRunning = true;
        currentGeneration = 0;
        population = new ArrayList();

        //the algorithm step defines the state of the running algorithm
        AlgorithmStep step = algorithmPass.createInitial();

        for (Logger logger : loggers) {
            logger.compatibilityCheck(this, algorithmPass);
            logger.startAlgorithm(this, step);
        }

        //doStepAbstract is an abstract method, that defines what will be done per step
        doStepAbstract(step, loggers);
        while (step.hasNext()) {
            currentGeneration ++;
            step = step.next();
            doStepAbstract(step, loggers);
        }

        //everything is done
        isRunning = false;
        for (Logger logger : loggers)
            logger.endAlgorithm(this, step);

    }

    /**
     * this is, what an algorithm should do per step. The loggers have
     * no null-values.
     *
     * @param step the algorithm-step
     * @param loggers the loggers, we should log every step in
     */
    protected void doStepAbstract (AlgorithmStep step, Logger [] loggers) {
        doStep(step);
        Collections.sort(population);
        for (Logger logger : loggers)
            logger.logGeneration(this, step);
    }

    /**
     * this is, what an algorithm does per step.
     * @param step the status of the algorithm
     */
    protected abstract void doStep(AlgorithmStep step);

    /**
     * is this algorithm compatible with this AlgorithmPass?
     *
     * @param algorithmPass the algorithmPass
     * @return true, if compatible
     */
    protected abstract boolean isCompatible(AlgorithmPass algorithmPass);

    /**
     * returns all the possible input-lengths, this algorithm could
     * possibly feed the recombination-operator with.
     *
     * @return all possible input-lengths
     */
    protected abstract int[] getRecombinationInputSize();

    /**
     * returns all the possible output-lengths, this algorithm could
     * possibly request from the recombination-operator.
     *
     * @return all possible output-lengths
     */
    protected abstract int[] getRecombinationOutputSize();

    /**
     * one individuum in the population
     */
    public class Individuum extends GenObject implements Comparable <Individuum> {

        /**
         * the instance of the actual individuum (the genoType)
         */
        protected final GenInstance genoInstance;

        /**
         * the instance of the actual individuum (the phenoType)
         */
        protected final GenInstance phenoInstance;

        /**
         * the calculated fitness of this individuum
         */
        protected final double fitnessValue;

        /**
         * the constructor
         *
         * @param _instance the actual individuum (as genoType)
         * @param step the state of the current run of the algorithm
         */
        public Individuum (GenInstance _instance, AlgorithmStep step) {
            genoInstance = _instance;
            phenoInstance = genoToPheno.genoToPhenoOp(genoInstance, step);
            fitnessValue = fitness.fitnessOp(phenoInstance, step);
        }

        /**
         * returns the actual individuum as genoType
         *
         * @return the instance as genoType
         */
        public GenInstance getGenoType () {
            return genoInstance;
        }

        /**
         * returns the actual individuum as phenoType
         *
         * @return the instance as phenoType
         */
        public GenInstance getPhenoType () {
            return phenoInstance;
        }

        /**
         * returns the fitness of this individuum.
         *
         * @return the fitness
         */
        public double getFitness () {
            return fitnessValue;
        }

        @Override
        public int compareTo(Individuum o) {
            return new Double(o.fitnessValue).compareTo(fitnessValue);
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.createList(new Attribute(new AttributeType(Type.HierarchicalChild), "genoInstance", genoInstance),
                                    new Attribute(new AttributeType(Type.HierarchicalChild, (genoToPheno instanceof GenoToPhenoIdentity ? Recommended.No : Recommended.NotSpecified), Recommended.NotSpecified), "phenoInstance", phenoInstance),
                                    new Attribute(new AttributeType(Type.MainAttribute), "fitnessValue", fitnessValue));
        }

    }

}
