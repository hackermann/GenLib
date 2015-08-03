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

package genlib.extended.distributions;

import genlib.abstractrepresentation.GenObject;
import genlib.extended.distributions.BasicTypeDistributions.ByteDistribution;
import genlib.extended.distributions.BasicTypeDistributions.CharDistribution;
import genlib.extended.distributions.BasicTypeDistributions.DoubleDistribution;
import genlib.extended.distributions.BasicTypeDistributions.FloatDistribution;
import genlib.extended.distributions.BasicTypeDistributions.IntDistribution;
import genlib.extended.distributions.BasicTypeDistributions.LongDistribution;
import genlib.extended.distributions.BasicTypeDistributions.ShortDistribution;
import genlib.output.Graph2DLogger;
import genlib.output.Graph2DLogger.AxisType;
import genlib.output.gui.Graph2D.Plot2DDiscreteX;
import genlib.output.gui.Graph2D.PlotCollection;
import genlib.utils.MergeOperator.AddMerge;
import genlib.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An abstract distribution, that implements all basic-types (int, long, double, etc..)
 *
 * @author Hilmar
 */
public abstract class AbstractDistribution extends GenObject implements LongDistribution, IntDistribution, ShortDistribution, CharDistribution, ByteDistribution, DoubleDistribution, FloatDistribution {

    /**
     * all supported basic-types
     */
    public enum NumberType {Long, Int, Short, Char, Byte, Double, Float};

    /**
     * create a plot-collection of this distribution
     *
     * @param numberType the basic-type, the measurement will done with
     * @return the requested PlotCollection
     */
    public PlotCollection plotCollection (NumberType numberType) {
        return plotCollection(numberType, 65536, 16);
    }

    /**
     * create a plot-collection of this distribution
     *
     * @param numberType the basic-type, the measurement will done with
     * @param measureQuantity this is the number of measurements, that will be done to create the plot
     * @param discreteBlocks the values of the x-axis will be grouped in this count of blocks
     * @return the requested PlotCollection
     * @throws IllegalArgumentException if measurementQuantity or discreteBlocks is smaller than 1
     */
    public PlotCollection plotCollection (NumberType numberType, int measureQuantity, int discreteBlocks) {
        return PlotCollection.createBarChart(
                "distribution of '" + measureQuantity + "' random-values",
                "random-value",
                "count",
                0.0,            //the standard-value, if one discretized group has no entry: this can't happen in our case, because we have just one plot
                (numberType != NumberType.Double && numberType != NumberType.Float),        //x-axis will get ints, if we have no doubles or floats
                true,           //y-axis will get ints
                plot(numberType, measureQuantity, discreteBlocks) );
    }

    /**
     * create a plot of this distribution
     *
     * @param numberType the basic-type, the measurement will done with
     * @return the requested Plot
     */
    public Plot2DDiscreteX plot (NumberType numberType) {
        return plot(numberType, 65536, 16);
    }

    /**
     * create a plot of this distribution
     *
     * @param numberType the basic-type, the measurement will done with
     * @param measureQuantity this is the number of measurements, that will be done to create the plot
     * @param discreteBlocks the values of the x-axis will be grouped in this count of blocks
     * @return the requested Plot
     * @throws IllegalArgumentException if measurementQuantity or discreteBlocks is smaller than 1
     */
    public Plot2DDiscreteX plot (NumberType numberType, int measureQuantity, int discreteBlocks) {
        if (measureQuantity < 1)
            throw new IllegalArgumentException("there has to be at least 1 measurement.");
        if (discreteBlocks < 1)
            throw new IllegalArgumentException("there has to be at least 1 discreteBlock.");

        Random random = new Random(System.nanoTime());
        List <Number> values = new ArrayList();
        for (int i=0; i<measureQuantity; i++)
            values.add(getRandom(numberType, random));

        Graph2DLogger logger = new Graph2DLogger(AxisType.justName("random-value"), AxisType.justName("count"));
        for (Number value : values)
            logger.addPoint(value, 1.0);

        Plot2DDiscreteX plot = logger.createDiscretePlot(discreteBlocks);
        plot.mergePointsInSameDiscretizedGroup(new AddMerge());
        return plot;
    }

    /**
     * get a random-number of the distribution
     *
     * @param numberType the basic-type of the random-number
     * @param random a random-object
     * @return the requested random-number
     */
    public Number getRandom (NumberType numberType, Random random) {
        switch (numberType) {
            case Long:
                return getRandomLong(random);
            case Int:
                return getRandomInt(random);
            case Short:
                return getRandomShort(random);
            case Char:
                return (int)getRandomChar(random);
            case Byte:
                return getRandomByte(random);
            case Double:
                return getRandomDouble(random);
            case Float:
                return getRandomFloat(random);
            default:
                throw new AssertionError(numberType.name());
        }
    }

    @Override
    public List<Attribute> getAttributes() {
        return Utils.createList();
    }

}
