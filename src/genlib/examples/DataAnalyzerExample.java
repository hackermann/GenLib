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

import genlib.output.gui.DataAnalyzer;
import genlib.output.gui.DataAnalyzer.CreateDataCallback;
import genlib.output.gui.DataAnalyzer.DataAnalyzerModel;

/**
 * With the data-analyzer-tool, you can explore unknown data
 * (as example calculated fitness-values of a n-dimensional-room)
 * In this example we have a look at a 4-dimensional sin-function
 *
 * @author Hilmar
 */
public class DataAnalyzerExample {

    /**
     * the entry-point for this example
     */
    public static void open () {
        DataAnalyzer.open(new DataAnalyzerModel(100,new ExampleCreateDataCallback(), 4));
    }

    /**
     * we need that class, to calculate the data
     */
    public static class ExampleCreateDataCallback implements CreateDataCallback {

        @Override
        public double create(double[] input) {
            return Math.sin(20*input[0]*input[0])*Math.sin(10*input[1]*input[1])+Math.sin(5*input[2]*input[2])*Math.sin(25*input[3]*input[3]);

        }

    }

}
