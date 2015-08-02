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

import genlib.output.gui.Graph2D;
import genlib.output.gui.Graph2D.Plot2DContinuousX;
import genlib.output.gui.Graph2D.PlotCollection;
import genlib.extended.distributions.AbstractDistribution;
import genlib.extended.distributions.GaussianDistribution;
import genlib.extended.distributions.LinearDistribution;

/**
 * An extended example, that demonstrates the distributions.
 *
 * @author Hilmar
 */
public class DistributionExampleExtended {

    /**
     * the entry-point for this example
     *
     * @param args arguments (unused)
     */
    public static void main (String [] args) {

        //create some distributions first, they all start with -256 and end with 256 (the gaussian-distributions just approximately)
        LinearDistribution linearDistribution = new LinearDistribution(-256, 256);
        GaussianDistribution gaussDistribution1 = new GaussianDistribution(0, 256);
        GaussianDistribution gaussDistribution2 = new GaussianDistribution(-128, 128, 256+128);

        //modify the plots, because a) we want continuous plots and b) we want another name
        Plot2DContinuousX plot1 = new Plot2DContinuousX (
                linearDistribution.plot(AbstractDistribution.NumberType.Int).convertToContinuousPlot(),
                "a linear distribution");

        Plot2DContinuousX plot2 = new Plot2DContinuousX (
                gaussDistribution1.plot(AbstractDistribution.NumberType.Int).convertToContinuousPlot(),
                "a gaussian distribution");

        Plot2DContinuousX plot3 = new Plot2DContinuousX (
                gaussDistribution2.plot(AbstractDistribution.NumberType.Int).convertToContinuousPlot(),
                "a gaussian distribution with the focus more left");

        //create a plot-collection with all distributions
        PlotCollection plotCollection = Graph2D.PlotCollection.createContinuousLineChart(
                "different distribution-examples",
                "random-value",
                "count",
                true,           //x-axis will get ints
                true,           //y-axis will get ints
                plot1,
                plot2,
                plot3);

        Graph2D.open(plotCollection);

    }

}
