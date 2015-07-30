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
import genlib.output.TextLogger;
import genlib.standard.algorithms.StaticGeneticAlgorithm;

/**
 * A minimal example, how to do an graphical output with a plot.
 *
 * @author Hilmar
 */
public class GraphExampleMinimal {

    public static void main (String [] args) {

        //a standard genetic algorithm, the optimization-task is a standard example and not important for this example
        StaticGeneticAlgorithm algorithm = new StaticGeneticAlgorithm();

        //first we need a logger, to log our data: In this case we will log the generation (x-axis) vs the average fitness (y-axis)
        Graph2DLogger graphLogger = new Graph2DLogger(AxisType.generations(), AxisType.averageFitness());

        //run the algorithm, run with a text-logger additionally
        algorithm.run(new TextLogger(), graphLogger);

        //now we can open a window with our graph
        graphLogger.createGraph2D();

    }

}
