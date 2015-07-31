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
import genlib.standard.distributions.AbstractDistribution.NumberType;
import genlib.standard.distributions.GaussianDistribution;
import genlib.standard.distributions.LinearDistribution;

/**
 * A minimal example, that demonstrates the distributions.
 *
 * @author Hilmar
 */
public class DistributionExampleMinimal {

    /**
     * the entry-point for this example
     *
     * @param args arguments (unused)
     */
    public static void main (String [] args) {
        GaussianDistribution distribution = new GaussianDistribution();
        Graph2D.open(distribution.plotCollection(NumberType.Double));

    }

}
