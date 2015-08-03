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
import genlib.extended.diversity.ShannonEntropyDiversity;
import genlib.extended.diversity.SubStringDiversity;
import genlib.output.Graph2DLogger;
import genlib.output.Graph2DLogger.AxisType;
import genlib.output.TextLogger;
import genlib.output.gui.Graph2D;
import genlib.output.gui.Graph2D.PlotCollection;
import genlib.standard.algorithms.StaticAlgorithmPass;
import genlib.standard.algorithms.StaticGeneticAlgorithm;
import genlib.standard.representations.BooleanStaticLength;

/**
 * This example demonstrates the different diversity-measurements
 *
 * @author Hilmar
 */
public class DiversityExample {

    /**
     * open the example
     */
    public static void open () {

        //a standard genetic algorithm, the optimization-task is a standard example and not important for this example
        StaticGeneticAlgorithm algorithm = new StaticGeneticAlgorithm();

        //we have to use a shorter BooleanStaticLength, because the sub-string-diversity
        //is too hard too calculate with 512 (this is the standard-value)
        algorithm.setGenoType(new BooleanStaticLength(64));

        //for the first plot, log the generation-count (x-axis) vs the average fitness of the allthe individuums (y-axis)
        //for the next three plots, log the average-diversity, the shannon-entropy-diversity and the sub-string-diversity
        Graph2DLogger generationToAvg = new Graph2DLogger(AxisType.generations(), AxisType.averageFitness());
        Graph2DLogger generationToAvgDiversity = new Graph2DLogger(AxisType.generations(), AxisType.diversityGenoType(new AverageDiversity()));
        Graph2DLogger generationToShannonEntropyDiversity = new Graph2DLogger(AxisType.generations(), AxisType.diversityGenoType(new ShannonEntropyDiversity()));
        Graph2DLogger generationToSubStringDiversity = new Graph2DLogger(AxisType.generations(), AxisType.diversityGenoType(new SubStringDiversity()));

        //log the diversity again, but with 10% samples now (instead of 100%)
        final double percent = 0.1;
        Graph2DLogger generationToAvgDiversityL = new Graph2DLogger(AxisType.generations(), AxisType.diversityGenoType(new AverageDiversity(percent)));
        Graph2DLogger generationToShannonEntropyDiversityL = new Graph2DLogger(AxisType.generations(), AxisType.diversityGenoType(new ShannonEntropyDiversity(percent)));
        Graph2DLogger generationToSubStringDiversityL = new Graph2DLogger(AxisType.generations(), AxisType.diversityGenoType(new SubStringDiversity(percent)));

        //run the algorithm, run with a text-logger additionally
        algorithm.run(
                new StaticAlgorithmPass(256, 64, 32, 0.1),    //we reach the fitness 1.0 usually after ~ 16 generations, so we can stop after 32
                new TextLogger(),
                generationToAvg,
                generationToAvgDiversity, generationToShannonEntropyDiversity, generationToSubStringDiversity,
                generationToAvgDiversityL, generationToShannonEntropyDiversityL, generationToSubStringDiversityL);

        PlotCollection plot1 = PlotCollection.createContinuousPointsChart(
                "Generation vs fitness/diversity",    //title of the complete plot (all three plots)
                "generation",                         //title of the x-axis
                "fitness/diversity",                  //title of the y-axis
                generationToAvg.createContinousPlot(),
                generationToAvgDiversity.createContinousPlot(),
                generationToShannonEntropyDiversity.createContinousPlot(),
                generationToSubStringDiversity.createContinousPlot());

        PlotCollection plot2 = PlotCollection.createContinuousPointsChart(
                "Generation vs fitness/diversity (diversity just 10% samples now)",    //title of the complete plot (all three plots)
                "generation",                         //title of the x-axis
                "fitness/diversity",                  //title of the y-axis
                generationToAvg.createContinousPlot(),
                generationToAvgDiversityL.createContinousPlot(),
                generationToShannonEntropyDiversityL.createContinousPlot(),
                generationToSubStringDiversityL.createContinousPlot());

        Graph2D.open(plot1, plot2);

    }

}
