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

package genlib.output;

import genlib.abstractrepresentation.GenObject;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.abstractrepresentation.GeneticAlgorithm;
import genlib.abstractrepresentation.GeneticAlgorithm.Individuum;
import genlib.output.Graph2DLogger.AxisType.BasicType;
import genlib.output.gui.Graph2D;
import genlib.output.gui.Graph2D.Plot;
import genlib.output.gui.Graph2D.Plot2DContinuousX;
import genlib.output.gui.Graph2D.Plot2DDiscreteX;
import genlib.utils.Exceptions.GeneticInternalException;
import genlib.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This logger logs specified informations, so a 2d-graph can be drawn afterwards.
 *
 * @author Hilmar
 */
public class Graph2DLogger extends Logger {

    /**
     * xAxis and yAxis specified, what has to be logged for the x- and y-axis
     */
    protected final AxisType xAxis, yAxis;

    /**
     * the time-stamp of the first activity (in micro-seconds)
     */
    protected long startMicroTime;

    /**
     * the values of the x-axis, in the order they arrived
     */
    protected List <Number> xValues = new ArrayList();

    /**
     * the values of the y-axis, in the order they arrived
     */
    protected List <Number> yValues = new ArrayList();

    /**
     * the constructor
     *
     * @param _xAxis what to log for the x-axis
     * @param _yAxis what to log for the y-axis
     */
    public Graph2DLogger (AxisType _xAxis, AxisType _yAxis) {
        xAxis = _xAxis;
        yAxis = _yAxis;
    }

    /**
     * this method will open one window with this logged graph inside.
     *
     * @return the object of the window.
     */
    public Graph2D createGraph2D () {
        return Graph2D.open(createPlot(), xAxis.getSuggestedName(), yAxis.getSuggestedName());
    }

    /**
     * constructs a plot of the measured data. Use Graph2D.open() to display this plot.
     *
     * @return the plot-object
     */
    public Plot createPlot() {
        return createPlot(getSuggestedName());
    }

    /**
     * constructs a plot of the measured data. Use Graph2D.open() to display this plot.
     *
     * @param name the title of the plot
     * @return the plot-object
     */
    public Plot createPlot(String name) {
        return createContinousPlot(name);
    }

    /**
     * constructs a non-discretized plot of the measured data. Use Graph2D.open() to
     * display this plot or construct a PlotCollection with multiple plot-objects.
     *
     * @return the plot-object
     */
    public Plot2DContinuousX createContinousPlot() {
        return createContinousPlot(getSuggestedName());
    }

    /**
     * constructs a non-discretized plot of the measured data. Use Graph2D.open() to
     * display this plot or construct a PlotCollection with multiple plot-objects.
     *
     * @param name the title of the plot
     * @return the plot-object
     */
    public Plot2DContinuousX createContinousPlot(String name) {
        Plot2DContinuousX plot = new Plot2DContinuousX(name);
        for (int i=0; i<xValues.size(); i++)
            plot.add(xValues.get(i).doubleValue(), yValues.get(i).doubleValue());
        return plot;
    }

    /**
     * constructs a discretized plot of the measured data. Use Graph2D.open() to
     * display this plot or construct a PlotCollection with multiple plot-objects.
     *
     * @return the plot-object
     */
    public Plot2DDiscreteX createDiscretePlot() {
        return createDiscretePlot(getSuggestedName(), 1);
    }

    /**
     * constructs a discretized plot of the measured data. Use Graph2D.open() to
     * display this plot or construct a PlotCollection with multiple plot-objects.
     * Consider, if there is not enough data, there can be created empty discretized
     * blocks, so there can be less than discretizedBlockCount blocks!
     *
     * @param discretizedBlockCount the measured data will be grouped, the number of groups desired are in this parameter
     * @return the plot-object
     * @throws IllegalArgumentException if we have less than 1 discretizedBlockCount
     */
    public Plot2DDiscreteX createDiscretePlot(int discretizedBlockCount) {
        return createDiscretePlot(getSuggestedName(), discretizedBlockCount);
    }

    /**
     * constructs a discretized plot of the measured data. Use Graph2D.open() to
     * display this plot or construct a PlotCollection with multiple plot-objects.
     *
     * @param discretizeBlocks the measured data will be grouped, the groups are of the size of this parameter
     * @return the plot-object
     */
    public Plot2DDiscreteX createDiscretePlot(double discretizeBlocks) {
        return createDiscretePlot(getSuggestedName(), discretizeBlocks);
    }

    /**
     * constructs a discretized plot of the measured data. Use Graph2D.open() to
     * display this plot or construct a PlotCollection with multiple plot-objects.
     *
     * @param name the title of the plot
     * @return the plot-object
     */
    public Plot2DDiscreteX createDiscretePlot(String name) {
        return createDiscretePlot(name, 1);
    }

    /**
     * constructs a discretized plot of the measured data. Use Graph2D.open() to
     * display this plot or construct a PlotCollection with multiple plot-objects.
     * Consider, if there is not enough data, there can be created empty discretized
     * blocks, so there can be less than discretizedBlockCount blocks!
     *
     * @param name the title of the plot
     * @param discretizedBlockCount the measured data will be grouped, the number of groups desired are in this parameter
     * @return the plot-object
     * @throws IllegalArgumentException if we have less than 1 discretizedBlockCount
     */
    public Plot2DDiscreteX createDiscretePlot(String name, int discretizedBlockCount) {

        if (discretizedBlockCount < 1)
            throw new IllegalArgumentException("discretizedBlockCount has to be >= 1.");

        //calculate the block size, so we get discretizedBlockCount blocks
        List <Pt> points = getDataSortedByX();
        double minX = points.get(0).getX().doubleValue();
        double maxX = points.get(points.size()-1).getX().doubleValue();

        return createDiscretePlot (name, (discretizedBlockCount == 1 ? Double.POSITIVE_INFINITY : (maxX-minX)/(discretizedBlockCount-1)) );
    }

    /**
     * constructs a discretized plot of the measured data. Use Graph2D.open() to
     * display this plot or construct a PlotCollection with multiple plot-objects.
     *
     * @param name the title of the plot
     * @param discretizeBlocks the measured data will be grouped, the groups are of the size of this parameter
     * @return the plot-object
     */
    public Plot2DDiscreteX createDiscretePlot(String name, double discretizeBlocks) {
        Plot2DDiscreteX plot = new Plot2DDiscreteX(name);
        List <Pt> pts = getDataSortedByX();
        for (Pt pt : pts) {
            Number xValue = pt.getX();
            if (xValue instanceof Double)
                plot.add( ((long) Math.floor( (Double)xValue/discretizeBlocks))*discretizeBlocks, pt.getY().doubleValue());
            else if (xValue instanceof Float)
                plot.add( ((long) Math.floor( (Float)xValue/discretizeBlocks))*discretizeBlocks, pt.getY().doubleValue());
            else if (xValue instanceof Integer)
                plot.add( ((int) Math.floor( (Integer)xValue/discretizeBlocks))*discretizeBlocks, pt.getY().doubleValue());
            else if (xValue instanceof Long)
                plot.add( ((long) Math.floor( (Long)xValue/discretizeBlocks))*discretizeBlocks,  pt.getY().doubleValue());
            else if (xValue instanceof Short)
                plot.add( ((long) Math.floor( (Short)xValue/discretizeBlocks))*discretizeBlocks,  pt.getY().doubleValue());
            else if (xValue instanceof Byte)
                plot.add( ((long) Math.floor( (Byte)xValue/discretizeBlocks))*discretizeBlocks,  pt.getY().doubleValue());
            else
                throw new GeneticInternalException("unexpected instance of Number: '" + xValue.getClass().getSimpleName() + "' (internal error)");
        }
        return plot;
    }

    /**
     * the suggested name for this graph
     *
     * @return the suggested name
     */
    public String getSuggestedName() {
        return xAxis.getSuggestedName() + " (x-axis) vs " + yAxis.getSuggestedName() + " (y-axis)";
    }

    /**
     * the suggested name for the x-axis
     *
     * @return the suggested name
     */
    public String getSuggestedXAxisName() {
        return xAxis.getSuggestedName();
    }

    /**
     * the suggested name for the y-axis
     *
     * @return the suggested name
     */
    public String getSuggestedYAxisName() {
        return yAxis.getSuggestedName();
    }

    /**
     * returns the measured data, in the order it was inserted.
     * It will return a deep-copy of the data.
     *
     * @return a deep-copy of the measured data
     */
    public List <Pt> getDataInsertOrder () {
        List <Pt> ret = new ArrayList();
        for (int i=0; i<xValues.size(); i++)
            ret.add(new Pt(xValues.get(i), yValues.get(i)));
        return ret;
    }

    /**
     * returns the measured data, sorted by x-axis (increasing).
     * It will return a deep-copy of the data.
     *
     * @return a deep-copy of the measured data (sorted)
     */
    public List <Pt> getDataSortedByX () {
        List <Pt> ret = getDataInsertOrder();
        Collections.sort(ret);
        return ret;
    }

    /**
     * adds a point to the data-set. x or y can be null,
     * but the whole point will be ignored in this case.
     *
     * @param x the x-value, can be null
     * @param y the y-value, can be null
     */
    public void addPoint (Number x, Number y) {

        //if one of the values if null, this point is ignored
        if (x != null && y != null) {
            xValues.add(x);
            yValues.add(y);
        }
    }

    @Override
    protected void log(LogType logType, GeneticAlgorithm algorithm) {
        if (logType == LogType.Generation) {
            //first calculate the specified value for the x-axis, then for the y-axis
            Number xValue = calculate(xAxis, algorithm);
            Number yValue = calculate(yAxis, algorithm);

            //if one of the values if null, this point will not be saved
            addPoint(xValue, yValue);
        }
    }

    @Override
    protected void starting() {
        startMicroTime = System.nanoTime()/1000;
        xValues = new ArrayList();
        yValues = new ArrayList();
    }

    @Override
    protected void ending() { }

    /**
     * this method calculates the requested value for the given axis. It CAN return
     * null, if the point should not be saved.
     *
     * @param axisType the requested value
     * @param algorithm the algorithm currently running
     * @return the calculated value or null
     */
    protected Number calculate (AxisType axisType, GeneticAlgorithm algorithm) {
        switch (axisType.basicType) {

            case Time:
                long timeOffset = System.nanoTime()/1000 - startMicroTime;
                return (double)(timeOffset * axisType.timeFactor);

            case KGeneration:
                if (algorithm.getCurrentGeneration() % axisType.kGenerations == axisType.kGenerations-1)
                    return algorithm.getCurrentGeneration();
                else
                    return null;

            case AverageFitness:
            {
                double avgFitness = 0;
                Individuum [] population = algorithm.getCurrentPopulation();
                for (Individuum individuum : population)
                    avgFitness += individuum.getFitness();
                return avgFitness / (population.length == 0 ? 1 : population.length);
            }

            case BestKFitnessAverage:
            case WorstKFitnessAverage:
            {
                double avgFitness = 0;
                int avgCount = 0;
                Individuum [] population = algorithm.getCurrentPopulation();

                //get the best or worst k individuums and calculate the average fitness of them
                for (int i=0; i<axisType.kFitness; i++) {
                    int index = (axisType.basicType == BasicType.BestKFitnessAverage ? i : population.length-1-i);
                    if (index < 0 || index >= population.length)
                        break;
                    avgCount++;
                    avgFitness += population [index].getFitness();
                }
                return avgFitness / (avgCount == 0 ? 1 : avgCount);
            }

            case JustName:
                return null;

            default:
                throw new AssertionError(axisType.basicType.name());
        }
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.extendList( super.getAttributes(),  new Attribute(new AttributeType(Type.MainAttribute), "xAxis", xAxis),
                                                        new Attribute(new AttributeType(Type.MainAttribute), "yAxis", yAxis),
                                                        new Attribute(new AttributeType(Type.TemporaryOrUnimportant), "startMicroTime", startMicroTime),
                                                        new Attribute(new AttributeType(Type.TemporaryOrUnimportant), "xValues", xValues),
                                                        new Attribute(new AttributeType(Type.TemporaryOrUnimportant), "yValues", yValues));
    }

    /**
     * This class defines the type of the requested value for one axis
     */
    public static class AxisType extends GenObject {

        /**
         * The basic-type: Measure the Time, Generation (or just every k'th generation), the average fitness
         * or the average fitness of the k best or worst individuums or measure nothing but specify a name
         */
        protected enum BasicType {Time, KGeneration, AverageFitness, BestKFitnessAverage, WorstKFitnessAverage, JustName};

        /**
         * the basicType, see the javadoc of the BasicType for more information
         */
        protected final BasicType basicType;

        /**
         * the time-factor is just needed, if we measure the time.
         * Milliseconds will have the time-factor 0.001 as example,
         * microseconds are factor 1. (they are the basic unit)
         */
        protected final double timeFactor;

        /**
         * just needed if the basicType is KGeneration. Every k'th
         * generation will just be measured.
         */
        protected final int kGenerations;

        /**
         * just needed for BestKFitnessAverage and WorstKFitnessAverage.
         * Just the k'th best or worst individuums will count.
         */
        protected final int kFitness;

        /**
         * if it is not null, this will be the suggested name
         */
        protected final String individualName;

        /**
         * the constructor, should just be invoked of the static creation-methods.
         *
         * @param _basicType the basicType
         * @param _timeFactor the timeFactor
         * @param _kGenerations Just the k'th best or worst individuums will count.
         * @param _kFitness Just the k'th best or worst individuums will count.
         * @param _individualName if it is not null, this will be the suggested name
         */
        private AxisType (BasicType _basicType, double _timeFactor, int _kGenerations, int _kFitness, String _individualName) {
            basicType = _basicType;
            timeFactor = _timeFactor;
            kGenerations = _kGenerations;
            kFitness = _kFitness;
            individualName = _individualName;
        }

        /**
         * the suggested name of the axis
         *
         * @return the suggested name
         */
        protected String getSuggestedName () {
            switch (basicType) {

                case Time:
                    //for better understanding, we calculate the factor from unit microseconds
                    //to factor seconds (so seconds will be 1 now and microseconds 0.000001)
                    double resolvedFactor = 0.000001/timeFactor;

                    //in the case we are near seconds / milliseconds / microseconds, we want to replace
                    //the factor in the string with this name
                    if (Utils.doubleEquality(resolvedFactor, 1, 0.001))
                        return "elapsed time (in seconds)";
                    else if (Utils.doubleEquality(resolvedFactor, 0.001, 0.000001))
                        return "elapsed time (in milli-seconds)";
                    else if (Utils.doubleEquality(resolvedFactor, 0.000001, 0.000000001))
                        return "elapsed time (in micro-seconds)";
                    else
                        return "elapsed time (in " + resolvedFactor + " x seconds)";

                case KGeneration:
                    return "generation count" + (kGenerations == 1 ? "" : " (every " + kGenerations + " generations)");

                case AverageFitness:
                    return "average fitness";

                case BestKFitnessAverage:
                    return "best fitness" + (kFitness == 1 ? "" : " (average of the " + kFitness + " best values)");

                case WorstKFitnessAverage:
                    return "worst fitness" + (kFitness == 1 ? "" : " (average of the " + kFitness + " worst values)");

                case JustName:
                    return individualName;

                default:
                    throw new AssertionError(basicType.name());
            }
        }

        /**
         * the creation-method for a second-axis
         *
         * @return the requested axis-type
         */
        public static AxisType seconds () {
            return new AxisType(BasicType.Time, 0.000001, -1, -1, null);
        }

        /**
         * the creation-method for a milli-second-axis
         *
         * @return the requested axis-type
         */
        public static AxisType milliSeconds () {
            return new AxisType(BasicType.Time, 0.001, -1, -1, null);
        }

        /**
         * the creation-method for a micro-second-axis
         *
         * @return the requested axis-type
         */
        public static AxisType microSeconds () {
            return new AxisType(BasicType.Time, 1, -1, -1, null);
        }

        /**
         * the creation-method for an individual time-axis.
         * The factor is relative to seconds, so seconds would be
         * a factor of 1 and milli-seconds 0.001 as example.
         *
         * @param factorToSeconds the factor
         * @return the requested axis-type
         * @throws IllegalArgumentException if the factor is lower than 1/10 of microseconds: microseconds are the minimum precision, that can be measured.
         */
        public static AxisType individualTime (double factorToSeconds) {
            if (factorToSeconds < 0.0000001)
                throw new IllegalArgumentException("microseconds is the minimum precision of the measuring. (factor should be >= 0.000001)");

            return new AxisType(BasicType.Time, (0.000001/factorToSeconds), -1, -1, null);
        }

        /**
         * the creation-method for a generation-axis
         *
         * @return the requested axis-type
         */
        public static AxisType generations () {
            return new AxisType(BasicType.KGeneration, Double.NaN, 1, -1, null);
        }

        /**
         * the creation-method for a k-generation-axis:
         * Just every k'th generation will be saved.
         *
         * @param kGenerations the k
         * @return the requested axis-type
         * @throws IllegalArgumentException if the parameter is lower than 1
         */
        public static AxisType kGenerations (int kGenerations) {
            if (kGenerations < 1)
                throw new IllegalArgumentException("k has to be at least 1.");

            return new AxisType(BasicType.KGeneration, Double.NaN, kGenerations, -1, null);
        }

        /**
         * the creation-method for an average-fitness-axis
         *
         * @return the requested axis-type
         */
        public static AxisType averageFitness () {
            return new AxisType(BasicType.AverageFitness, Double.NaN, -1, -1, null);
        }

        /**
         * the creation-method for a best-fitness-axis
         *
         * @return the requested axis-type
         */
        public static AxisType bestFitness () {
            return new AxisType(BasicType.BestKFitnessAverage, Double.NaN, -1, 1, null);
        }

        /**
         * the creation-method for a k-best-fitness-axis. This will
         * calculate the average of the best k individuums.
         *
         * @param bestKFitness the k
         * @return the requested axis-type
         * @throws IllegalArgumentException if the parameter is lower than 1
         */
        public static AxisType bestKFitness (int bestKFitness) {
            if (bestKFitness < 1)
                throw new IllegalArgumentException("the best average fitness has to be calculated at least from 1 fitness.");

            return new AxisType(BasicType.BestKFitnessAverage, Double.NaN, -1, bestKFitness, null);
        }

        /**
         * the creation-method for a worst-fitness-axis
         *
         * @return the requested axis-type
         */
        public static AxisType worstFitness () {
            return new AxisType(BasicType.WorstKFitnessAverage, Double.NaN, -1, 1, null);
        }

        /**
         * the creation-method for a k-worst-fitness-axis. This will
         * calculate the average of the worst k individuums.
         *
         * @param worstKFitness the k
         * @return the requested axis-type
         * @throws IllegalArgumentException if the parameter is lower than 1
         */
        public static AxisType worstKFitness (int worstKFitness) {
            if (worstKFitness < 1)
                throw new IllegalArgumentException("the worst average fitness has to be calculated at least from 1 fitness.");

            return new AxisType(BasicType.WorstKFitnessAverage, Double.NaN, -1, worstKFitness, null);
        }

        /**
         * the creation-method for an axis, that will measure nothing, but have
         * an individual name specified.
         *
         * @param name the individual name
         * @return the requested axis-type
         * @throws NullPointerException if the parameter is null
         */
        public static AxisType justName (String name) {
            if (name == null)
                throw new NullPointerException("the name can't be null.");

            return new AxisType(BasicType.JustName, Double.NaN, -1, -1, name);
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.createList( new Attribute(new AttributeType(Type.MainAttribute), "basicType", basicType),
                                    new Attribute(new AttributeType( (basicType == BasicType.Time ? Type.NormalAttribute : Type.TemporaryOrUnimportant) ), "timeFactor", timeFactor),
                                    new Attribute(new AttributeType( (basicType == BasicType.KGeneration ? Type.NormalAttribute : Type.TemporaryOrUnimportant) ), "kGenerations", kGenerations),
                                    new Attribute(new AttributeType( (basicType == BasicType.BestKFitnessAverage || basicType == BasicType.WorstKFitnessAverage ? Type.NormalAttribute : Type.TemporaryOrUnimportant) ), "kFitness", kFitness));
        }

    }

    /**
     * This class saves a point, the unit is Number (java.lang.Number).
     * The comparison is done with the double-values of this numbers.
     */
    public static class Pt extends GenObject implements Comparable <Pt> {

        /**
         * the x- and y-values of this point
         */
        protected final Number x, y;

        /**
         * the constructor
         *
         * @param _x the x-value
         * @param _y the y-value
         */
        public Pt (Number _x, Number _y) {
            x = _x;
            y = _y;
        }

        /**
         * the getter for x
         *
         * @return x
         */
        public Number getX () {
            return x;
        }

        /**
         * the getter for y
         *
         * @return y
         */
        public Number getY () {
            return y;
        }

        @Override
        public int compareTo(Pt o) {
            return new Double(x.doubleValue()).compareTo(o.x.doubleValue());
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.createList( new Attribute(new AttributeType(Type.MainAttribute), "x", x),
                                    new Attribute(new AttributeType(Type.MainAttribute), "y", y));
        }
    }

}
