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

package genlib.examples;

import genlib.extended.diversity.AverageDiversity;
import genlib.extended.diversity.HierarchicalDiversity;
import genlib.output.Graph2DLogger;
import genlib.output.Graph2DLogger.AxisType;
import genlib.output.TextLogger;
import genlib.output.gui.Graph2D;
import genlib.output.gui.Graph2D.Plot2DContinuousX;
import genlib.output.gui.Graph2D.PlotCollection;
import genlib.standard.algorithms.StaticAlgorithmPass;
import genlib.standard.algorithms.StaticGeneticAlgorithm;
import genlib.standard.operators.AverageFitness;
import genlib.standard.operators.HierarchicalOp.HierarchicalAverageFitness;
import genlib.standard.operators.HierarchicalOp.HierarchicalRandomMutationOp;
import genlib.standard.operators.HierarchicalOp.HierarchicalRecombinationOp;
import genlib.standard.operators.HierarchicalOp.HierarchicalType;
import genlib.standard.operators.HierarchicalOp.ReductionType;
import genlib.standard.operators.KPointCrossover;
import genlib.standard.operators.OnePointMutation;
import genlib.standard.operators.UniformCrossover;
import genlib.standard.representations.BooleanStaticLength;
import genlib.standard.representations.Hierarchical.And;

/**
 * This example demonstrates the use of Hierarchical-Representations (And, Or , ..)
 * and its operators
 *
 * @author Hilmar
 */
public class HierarchicalExample {

    /**
     * open the example
     */
    public static void open () {

        //we want to demonstrate the hierarchical-representation through one slow algorithm (with hierarchical) and one faster algorithm
        StaticGeneticAlgorithm algorithmSlow = new StaticGeneticAlgorithm();
        StaticGeneticAlgorithm algorithmFast = new StaticGeneticAlgorithm();

        //the Uniform-crossover is a bit better recombination in our example
        //the representation is an And with 3 Boolean-arrays (this has nothing to do with the speed..)
        algorithmSlow.setGenoType(new And(new BooleanStaticLength(256), new BooleanStaticLength(256), new BooleanStaticLength(256)));

        //set 4 recombination-operators with random match, so with 1/4 probability the
        //'good' operator will be chosen
        algorithmSlow.setRecombinationOp(new HierarchicalRecombinationOp(HierarchicalType.UseRandomMatch, new KPointCrossover(1), new KPointCrossover(1), new KPointCrossover(1), new UniformCrossover()));
        algorithmSlow.setMutationOp(new HierarchicalRandomMutationOp(HierarchicalType.UseFirstMatch, new OnePointMutation()));

        //the fitness will be the average of the 3 average-fitnesses
        algorithmSlow.setFitnessOp(new HierarchicalAverageFitness(HierarchicalType.UseFirstMatch, ReductionType.average(), new AverageFitness()));

        //for the slow algorithm, just use a single representation and just the uniform-crossover
        algorithmFast.setGenoType(new BooleanStaticLength(256));
        algorithmFast.setRecombinationOp(new UniformCrossover());
        algorithmFast.setMutationOp(new OnePointMutation());
        algorithmFast.setFitnessOp(new AverageFitness());

        //measure the average-fitness and the average-diversity
        Graph2DLogger generationToAvgSlow = new Graph2DLogger(AxisType.generations(), AxisType.averageFitness());
        Graph2DLogger generationToAvgFast = new Graph2DLogger(AxisType.generations(), AxisType.averageFitness());

        //for performance reasons, just measure the diversity from the first entry of the And,
        //the 2nd and 3rd AverageDiversity's are just pseudo-operators, because otherwise we
        //could not refer to the first child only, because justOne() refers to operators, not to childs
        Graph2DLogger generationToAvgDiversitySlow = new Graph2DLogger(AxisType.generations(), AxisType.diversityGenoType(new HierarchicalDiversity(HierarchicalType.ExactMatch, ReductionType.justOne(0), new AverageDiversity(), new AverageDiversity(), new AverageDiversity())));
        Graph2DLogger generationToAvgDiversityFast = new Graph2DLogger(AxisType.generations(), AxisType.diversityGenoType(new AverageDiversity()));

        //run the slow and then the fast algorithm, run with a text-logger additionally
        algorithmSlow.run(
                new StaticAlgorithmPass(256, 64, 32, 0.1),
                new TextLogger(),
                generationToAvgSlow,
                generationToAvgDiversitySlow);

        algorithmFast.run(
                new StaticAlgorithmPass(256, 64, 32, 0.1),
                new TextLogger(),
                generationToAvgFast,
                generationToAvgDiversityFast);

        //the fitness plot
        PlotCollection plot1 = PlotCollection.createContinuousPointsChart(
                "Generation vs fitness",    //title of the complete plot (all three plots)
                "generation",               //title of the x-axis
                "fitness",                  //title of the y-axis
                new Plot2DContinuousX(generationToAvgSlow.createContinousPlot(), "slow algorithm"),
                new Plot2DContinuousX(generationToAvgFast.createContinousPlot(), "fast algorithm"));

        //the diversity plot
        PlotCollection plot2 = PlotCollection.createContinuousPointsChart(
                "Generation vs diversity",  //title of the complete plot (all three plots)
                "generation",               //title of the x-axis
                "diversity",                //title of the y-axis
                new Plot2DContinuousX(generationToAvgDiversitySlow.createContinousPlot(), "slow algorithm"),
                new Plot2DContinuousX(generationToAvgDiversityFast.createContinousPlot(), "fast algorithm"));

        Graph2D.open(plot1, plot2);

    }

}
