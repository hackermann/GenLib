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

import genlib.output.Graph2DLogger;
import genlib.output.Graph2DLogger.AxisType;
import genlib.output.Graph2DLogger.Pt;
import genlib.output.TextLogger;
import genlib.output.gui.Graph2D;
import genlib.output.gui.Graph2D.PlotCollection;
import genlib.standard.algorithms.StaticGeneticAlgorithm;
import java.util.List;

/**
 * An extended example to demonstrate the ability of this library to plot graphs.
 *
 * @author Hilmar
 */
public class GraphExampleExtended {

    public static void main (String [] args) {

        //a standard genetic algorithm, the optimization-task is a standard example and not important for this example
        StaticGeneticAlgorithm algorithm = new StaticGeneticAlgorithm();

        //for the first plot, log the generation-count (x-axis) vs the average fitness of the best 3/all/the worst 3 individuums (y-axis)
        Graph2DLogger generationToBest3 = new Graph2DLogger(AxisType.generations(), AxisType.bestKFitness(3));
        Graph2DLogger generationToAvg = new Graph2DLogger(AxisType.generations(), AxisType.averageFitness());
        Graph2DLogger generationToWorst3 = new Graph2DLogger(AxisType.generations(), AxisType.worstKFitness(3));

        //for the second and third plot, log the milliseconds (x-axis) vs the generation-count, grouped to 25 (y-axis)
        Graph2DLogger milliSecondsToGeneration25 = new Graph2DLogger(AxisType.milliSeconds(), AxisType.kGenerations(25));

        //run the algorithm, run with a text-logger additionally
        algorithm.run(new TextLogger(), generationToBest3, generationToAvg, generationToWorst3, milliSecondsToGeneration25);

        //the first plot, will be a plot with unconnected points
        PlotCollection plot1 = PlotCollection.createContinuousPointsChart(
                "Generation vs fitness",    //title of the complete plot (all three plots)
                "generation",               //title of the x-axis
                "fitness",                  //title of the y-axis
                generationToBest3.createContinousPlot(), generationToAvg.createContinousPlot(), generationToWorst3.createContinousPlot());

        //the second plot should be the same, just with other data
        PlotCollection plot2 = PlotCollection.createContinuousPointsChart(
                milliSecondsToGeneration25.getSuggestedName(),          //title can be taken from the suggested name, because we have just one plot in this case, ..
                milliSecondsToGeneration25.getSuggestedXAxisName(),     //.. the same for x-axis ..
                milliSecondsToGeneration25.getSuggestedYAxisName(),     //.. and y-axis
                milliSecondsToGeneration25.createContinousPlot());

        //for the third plot, we want to have a discrete plot. We want to discretize the
        //milliseconds in 7 groups, so at first we need to get the range of the measured milli-seconds
        List <Pt> points = milliSecondsToGeneration25.getDataSortedByX();
        double minX = points.get(0).getX().doubleValue();
        double maxX = points.get(points.size()-1).getX().doubleValue();

        //now we can discretize the plot: we have to calculate the area of one discretized block
        PlotCollection plot3 = PlotCollection.createDiscretePointsChart(
                milliSecondsToGeneration25.getSuggestedName(),
                milliSecondsToGeneration25.getSuggestedXAxisName(),
                milliSecondsToGeneration25.getSuggestedYAxisName(),
                0.0,        //the standard-value, if one discretized group has no entry: this can't happen in our case, because we have just one plot
                true,       //x-axis will get ints: the double values will be round
                false,      //y-axis should be still doubles
                milliSecondsToGeneration25.createDiscretePlot( (maxX-minX) / 7));       //(max-min)/7 => we get 7 groups

        //open ONE window with the 3 plots
        Graph2D.open(plot1, plot2, plot3);
    }
}