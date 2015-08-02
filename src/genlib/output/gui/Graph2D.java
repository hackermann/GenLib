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

package genlib.output.gui;

import genlib.abstractrepresentation.GenObject;
import genlib.utils.Exceptions.GeneticRuntimeException;
import genlib.utils.MergeOperator;
import genlib.utils.Utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * This is an interface to the JFreeChart-Library to display graphs within GenLib
 *
 * @author Hilmar
 */
public class Graph2D extends ApplicationFrame {

    /**
     * Open a windows with the specified graph inside
     *
     * @param title the window-title
     */
    private Graph2D(String title) {
        super(title);

        //we don't want the whole program to close
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    @Override
    public void windowClosing(java.awt.event.WindowEvent event) {}

    /**
     * open a window with one plot
     *
     * @param plot the plot
     * @param xAxisName the name of the x-axis
     * @param yAxisName the name of the y-axis
     * @return the window-object
     */
    public static Graph2D open (Plot plot, String xAxisName, String yAxisName) {
        if (plot instanceof Plot2DDiscreteX)
            return open(PlotCollection.createDiscretePointsChart(plot.title, xAxisName, yAxisName, (Plot2DDiscreteX)plot));
        else
            return open(PlotCollection.createContinuousPointsChart(plot.title, xAxisName, yAxisName, (Plot2DContinuousX)plot));
    }

    /**
     * open a window with one or more plot-collections
     *
     * @param plotCollections the plot-collections, can have null's inside,then there is empty space on the window
     * @return the window-object
     * @throws IllegalArgumentException if 0 plots are specified
     */
    public static Graph2D open (PlotCollection ... plotCollections) {

        if (plotCollections.length == 0)
            throw new IllegalArgumentException("there have to be at least 1 plot-collection.");

        //calculate the count of rows and cols
        int rows = 1;
        int cols = 1;
        while (rows*cols < plotCollections.length) {
            if (cols == rows)
                cols++;
            else
                rows++;
        }

        //we have to fill the rest with null
        PlotCollection[] completePlotCollections = new PlotCollection[rows*cols];
        System.arraycopy(plotCollections, 0, completePlotCollections, 0, plotCollections.length);
        return open(rows, cols, 10, (plotCollections.length == 1 ? 0.5 : 0.75), completePlotCollections);
    }

    /**
     * open a window with one or more plot-collections
     *
     * @param rows count of rows
     * @param cols count of columns
     * @param gap size of gaps between Plots (in pixel)
     * @param percentSizeOfScreen the size of the complete window in per-cent of the windows
     * @param plots the plot-collections, can have null's inside,then there is empty space on the window
     * @return the window-object
     * @throws IllegalArgumentException if rows or cols lower than 1, gap lower than 0, percentSizeOfScreen lower than 0.001 or higher than 1 or number of plots not the same as rows*cols
     */
    public static Graph2D open (int rows, int cols, int gap, double percentSizeOfScreen, PlotCollection ... plots) {
        if (rows <= 0)
            throw new IllegalArgumentException("invalid rows-count: '" + rows + "'.");
        if (cols <= 0)
            throw new IllegalArgumentException("invalid cols-count: '" + cols + "'.");
        if (gap < 0)
            throw new IllegalArgumentException("invalid gap: '" + gap + "'.");
        if (percentSizeOfScreen < 0.001 || percentSizeOfScreen > 1)
            throw new IllegalArgumentException("invalid percentSizeOfScreen: '" + percentSizeOfScreen + "'.");
        if (plots.length != rows*cols)
            throw new IllegalArgumentException("length of plots is not rows*cols.");

        //the window-title is a concatenation of all plots
        //but first, we have to ignore all null-plots
        String windowTitle = "";
        List <PlotCollection> plotsNotNull = new ArrayList();
        for (PlotCollection plot : plots)
            if (plot != null)
                plotsNotNull.add(plot);

        if (plotsNotNull.isEmpty())
            windowTitle = "no plots";
        else
            for (int i=0; i<plotsNotNull.size(); i++)
                windowTitle += (i > 0 ? ", " : "") + plotsNotNull.get(i).title;

        //create the window
        Graph2D ret = new Graph2D(windowTitle);

        //and create the row-col-layout
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(rows, cols, gap, gap));
        for (PlotCollection plot : plots)
            if (plot == null)
                grid.add(new JLabel(""));
            else
                grid.add(plot.getGUIElement());

        //some other attributes of the window
        grid.setBackground(Color.BLACK);
        ret.setContentPane(grid);
        ret.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        ret.setSize(new Dimension((int)(screenSize.getWidth()*percentSizeOfScreen), (int)(screenSize.getHeight()*percentSizeOfScreen)));
        RefineryUtilities.centerFrameOnScreen(ret);
        ret.setVisible(true);

        return ret;
    }

    /**
     * a PlotCollection stores one or more plots and displays
     * them in one graph with one x-axis and one y-axis
     */
    public static class PlotCollection extends GenObject {

        /**
         * the graph-type
         */
        protected enum Type {JustLines, JustPoints, LineAndPoints, BarCharts};

        /**
         * the graph-type
         */
        protected final Type type;

        /**
         * the title
         */
        protected final String title;

        /**
         * the description of the x-axis and y-axis
         */
        protected final String xAxis, yAxis;

        /**
         * just needed for discrete chart: If the plots don't
         * have the exact same discrete keys, we need a standard-
         * value for this case
         */
        protected final double valueForMissingData;

        /**
         * all the discrete plots
         */
        protected final Plot2DDiscreteX [] plots2DDiscreteX;

        /**
         * all the non-discrete plots
         */
        protected final Plot2DContinuousX [] plots2DContinuousX;

        /**
         * the x-axis and y-axis can be converted to integer-values only
         */
        protected final boolean xAxisDiscreteInts, yAxisDiscreteInts;

        /**
         * the constructor, just available for creation-methods
         *
         * @param _type the graph-type
         * @param _title the title
         * @param _xAxis description x-axis
         * @param _yAxis description y-axis
         * @param _valueForMissingData just needed for discrete chart: If the plots don't have the exact same discrete keys, we need a standard-value for this case
         * @param _xAxisDiscreteInts the x-axis can be converted to integer-values only
         * @param _yAxisDiscreteInts the y-axis can be converted to integer-values only
         * @param _plots2DDiscreteX all the discrete plots
         * @param _plots2DContinuousX all the non-discrete plots
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        private PlotCollection (Type _type, String _title, String _xAxis, String _yAxis, double _valueForMissingData, boolean _xAxisDiscreteInts, boolean _yAxisDiscreteInts, Plot2DDiscreteX [] _plots2DDiscreteX, Plot2DContinuousX [] _plots2DContinuousX) {
            type = _type;
            title = _title;
            xAxis = _xAxis;
            yAxis = _yAxis;
            valueForMissingData = _valueForMissingData;
            plots2DDiscreteX = _plots2DDiscreteX;
            plots2DContinuousX = _plots2DContinuousX;
            xAxisDiscreteInts = _xAxisDiscreteInts;
            yAxisDiscreteInts = _yAxisDiscreteInts;

            if (title == null)
                throw new NullPointerException("title cannot be null.");
            if (xAxis == null)
                throw new NullPointerException("xAxis-name cannot be null.");
            if (yAxis == null)
                throw new NullPointerException("yAxis-name cannot be null.");

            if (plots2DDiscreteX != null) {
                if (plots2DDiscreteX.length == 0)
                    throw new IllegalArgumentException("there has to be at least 1 plot.");

                //every title is just allowed one time
                Set <String> titlesInUse = new HashSet();
                for (int i=0; i<plots2DDiscreteX.length; i++) {
                    String title = plots2DDiscreteX[i].getTitle();
                    if (titlesInUse.contains(title))
                        for (int j=2; true; j++)
                            if (!titlesInUse.contains(title + " (" + j + ")")) {
                                plots2DDiscreteX[i] = new Plot2DDiscreteX(plots2DDiscreteX[i], title + " (" + j + ")");
                                break;
                            }

                    titlesInUse.add(plots2DDiscreteX[i].getTitle());
                    plots2DDiscreteX[i].finish();
                }
            } 
            if (plots2DContinuousX != null) {
                if (plots2DContinuousX.length == 0)
                    throw new IllegalArgumentException("there has to be at least 1 plot.");

                //every title is just allowed one time
                Set <String> titlesInUse = new HashSet();
                for (int i=0; i<plots2DContinuousX.length; i++) {
                    String title = plots2DContinuousX[i].getTitle();
                    if (titlesInUse.contains(title))
                        for (int j=2; true; j++)
                            if (!titlesInUse.contains(title + " (" + j + ")")) {
                                plots2DContinuousX[i] = new Plot2DContinuousX(plots2DContinuousX[i], title + " (" + j + ")");
                                break;
                            }

                    titlesInUse.add(plots2DContinuousX[i].getTitle());
                    plots2DContinuousX[i].finish();
                }
            }
        }

        /**
         * the creation-method for a non-discrete line-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createContinuousLineChart (String title, String xAxis, String yAxis, Plot2DContinuousX ... plots) {
            return new PlotCollection(Type.JustLines, title, xAxis, yAxis, -1.0, false, false, null, plots);
        }

        /**
         * the creation-method for a non-discrete points-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createContinuousPointsChart (String title, String xAxis, String yAxis, Plot2DContinuousX ... plots) {
            return new PlotCollection(Type.JustPoints, title, xAxis, yAxis, -1.0, false, false, null, plots);
        }

        /**
         * the creation-method for a non-discrete line-and-points-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createContinuousLineAndPointsChart (String title, String xAxis, String yAxis, Plot2DContinuousX ... plots) {
            return new PlotCollection(Type.LineAndPoints, title, xAxis, yAxis, -1.0, false, false, null, plots);
        }

        /**
         * the creation-method for a discrete line-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createDiscreteLineChart (String title, String xAxis, String yAxis, Plot2DDiscreteX ... plots) {
            return new PlotCollection(Type.JustLines, title, xAxis, yAxis, 0.0, false, false, plots, null);
        }

        /**
         * the creation-method for a discrete points-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createDiscretePointsChart (String title, String xAxis, String yAxis, Plot2DDiscreteX ... plots) {
            return new PlotCollection(Type.JustPoints, title, xAxis, yAxis, 0.0, false, false, plots, null);
        }

        /**
         * the creation-method for a discrete line-and-points-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createDiscreteLineAndPointsChart (String title, String xAxis, String yAxis, Plot2DDiscreteX ... plots) {
            return new PlotCollection(Type.LineAndPoints, title, xAxis, yAxis, 0.0, false, false, plots, null);
        }

        /**
         * the creation-method for a discrete bar-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createBarChart (String title, String xAxis, String yAxis, Plot2DDiscreteX ... plots) {
            return new PlotCollection(Type.BarCharts, title, xAxis, yAxis, 0.0, false, false, plots, null);
        }

        /**
         * the creation-method for a non-discrete line-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param xAxisDiscreteInts the x-axis can be converted to integer-values only
         * @param yAxisDiscreteInts the y-axis can be converted to integer-values only
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createContinuousLineChart (String title, String xAxis, String yAxis, boolean xAxisDiscreteInts, boolean yAxisDiscreteInts, Plot2DContinuousX ... plots) {
            return new PlotCollection(Type.JustLines, title, xAxis, yAxis, -1.0, xAxisDiscreteInts, yAxisDiscreteInts, null, plots);
        }

        /**
         * the creation-method for a non-discrete points-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param xAxisDiscreteInts the x-axis can be converted to integer-values only
         * @param yAxisDiscreteInts the y-axis can be converted to integer-values only
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createContinuousPointsChart (String title, String xAxis, String yAxis, boolean xAxisDiscreteInts, boolean yAxisDiscreteInts, Plot2DContinuousX ... plots) {
            return new PlotCollection(Type.JustPoints, title, xAxis, yAxis, -1.0, xAxisDiscreteInts, yAxisDiscreteInts, null, plots);
        }

        /**
         * the creation-method for a non-discrete line-and-points-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param xAxisDiscreteInts the x-axis can be converted to integer-values only
         * @param yAxisDiscreteInts the y-axis can be converted to integer-values only
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createContinuousLineAndPointsChart (String title, String xAxis, String yAxis, boolean xAxisDiscreteInts, boolean yAxisDiscreteInts, Plot2DContinuousX ... plots) {
            return new PlotCollection(Type.LineAndPoints, title, xAxis, yAxis, -1.0, xAxisDiscreteInts, yAxisDiscreteInts, null, plots);
        }

        /**
         * the creation-method for a discrete line-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param valueForMissingData If the plots don't have the exact same discrete keys, we need a standard-value for this case
         * @param xAxisDiscreteInts the x-axis can be converted to integer-values only
         * @param yAxisDiscreteInts the y-axis can be converted to integer-values only
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createDiscreteLineChart (String title, String xAxis, String yAxis, double valueForMissingData, boolean xAxisDiscreteInts, boolean yAxisDiscreteInts, Plot2DDiscreteX ... plots) {
            return new PlotCollection(Type.JustLines, title, xAxis, yAxis, valueForMissingData, xAxisDiscreteInts, yAxisDiscreteInts, plots, null);
        }

        /**
         * the creation-method for a discrete points-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param valueForMissingData If the plots don't have the exact same discrete keys, we need a standard-value for this case
         * @param xAxisDiscreteInts the x-axis can be converted to integer-values only
         * @param yAxisDiscreteInts the y-axis can be converted to integer-values only
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createDiscretePointsChart (String title, String xAxis, String yAxis, double valueForMissingData, boolean xAxisDiscreteInts, boolean yAxisDiscreteInts, Plot2DDiscreteX ... plots) {
            return new PlotCollection(Type.JustPoints, title, xAxis, yAxis, valueForMissingData, xAxisDiscreteInts, yAxisDiscreteInts, plots, null);
        }

        /**
         * the creation-method for a discrete line-and-points-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param valueForMissingData If the plots don't have the exact same discrete keys, we need a standard-value for this case
         * @param xAxisDiscreteInts the x-axis can be converted to integer-values only
         * @param yAxisDiscreteInts the y-axis can be converted to integer-values only
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createDiscreteLineAndPointsChart (String title, String xAxis, String yAxis, double valueForMissingData, boolean xAxisDiscreteInts, boolean yAxisDiscreteInts, Plot2DDiscreteX ... plots) {
            return new PlotCollection(Type.LineAndPoints, title, xAxis, yAxis, valueForMissingData, xAxisDiscreteInts, yAxisDiscreteInts, plots, null);
        }

        /**
         * the creation-method for a discrete bar-chart
         *
         * @param title the title
         * @param xAxis description x-axis
         * @param yAxis description y-axis
         * @param valueForMissingData If the plots don't have the exact same discrete keys, we need a standard-value for this case
         * @param xAxisDiscreteInts the x-axis can be converted to integer-values only
         * @param yAxisDiscreteInts the y-axis can be converted to integer-values only
         * @param plots the plots
         * @return the requested plot-collection
         * @throws IllegalArgumentException if we have 0 plots
         * @throws NullPointerException if title, x-axis-name or y-axis-name is null
         */
        public static PlotCollection createBarChart (String title, String xAxis, String yAxis, double valueForMissingData, boolean xAxisDiscreteInts, boolean yAxisDiscreteInts, Plot2DDiscreteX ... plots) {
            return new PlotCollection(Type.BarCharts, title, xAxis, yAxis, valueForMissingData, xAxisDiscreteInts, yAxisDiscreteInts, plots, null);
        }

        /**
         * returns, if this plot-collection has lines (the type of graph)
         *
         * @return true, if it has lines
         */
        protected boolean hasLines () {
            return type == Type.JustLines || type == Type.LineAndPoints;
        }

        /**
         * returns, if this plot-collection has points (the type of graph)
         *
         * @return true, if it has points
         */
        protected boolean hasPoints () {
            return type == Type.JustPoints || type == Type.LineAndPoints;
        }

        /**
         * this method constructs the GUI-element of the graph. We use
         * the JFreeChart-Library for this.
         *
         * @return the GUI-object
         */
        protected ChartPanel getGUIElement() {

            JFreeChart chart = null;

            //even if the continuous and discrete graphs have many similiarities,
            //the differences are too large - so we have two complete case-handlings
            if (plots2DContinuousX != null) {

                XYSeriesCollection seriesCollection = new XYSeriesCollection();
                for (Plot2DContinuousX plot : plots2DContinuousX)
                    seriesCollection.addSeries(plot.getGraphData());

                //the line / points / line-and-points graphs are in general the same graph with a different option set
                chart = ChartFactory.createXYLineChart(title, xAxis, yAxis, seriesCollection, PlotOrientation.VERTICAL, true, true, false);

                XYPlot plot = chart.getXYPlot();
                plot.setBackgroundPaint(new Color(230, 230, 230));
                plot.setDomainGridlinePaint(Color.white);
                plot.setRangeGridlinePaint(Color.white);

                //set the lines / points visible or not
                XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
                for (int i=0; i<plots2DContinuousX.length; i++) {
                    renderer.setSeriesLinesVisible(i, hasLines());
                    renderer.setSeriesShapesVisible(i, hasPoints());
                }
                plot.setRenderer(renderer);

                //in the non-discrete case we just set the two axis' to integer-ticks
                if (xAxisDiscreteInts)
                    ((NumberAxis)plot.getDomainAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                if (yAxisDiscreteInts)
                    ((NumberAxis)plot.getRangeAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            } else {

                Comparable [] plotTitles = new Comparable[plots2DDiscreteX.length];
                List <Comparable> keys = new ArrayList();

                //calculate the discrete keys of all plots
                for (int i=0; i<plots2DDiscreteX.length; i++ ) {
                    Plot2DDiscreteX plot = plots2DDiscreteX[i];
                    plotTitles[i] = plot.title;
                    for (Plot2DDiscreteX.Pt pt : plot.pts) {
                        Comparable key = pt.x;
                        //in the case of a x-axis with integers, we have to convert the keys too
                        if (xAxisDiscreteInts && key instanceof Number)
                            key = ((Number)key).longValue();
                        if (!keys.contains(key))
                            keys.add(key);
                    }
                }

                Comparable [] plotKeys = keys.toArray(new Comparable[keys.size()]);
                //you can imagine the start- and end-values as the start and the end of bars in a bar-chart
                Number [][] startValues = new Number[plots2DDiscreteX.length][plotKeys.length];
                Number [][] endValues = new Number[plots2DDiscreteX.length][plotKeys.length];

                for (int i=0; i<plots2DDiscreteX.length; i++ ) {
                    Arrays.fill(startValues[i], 0);
                    Arrays.fill(endValues[i], valueForMissingData);

                    for (Plot2DDiscreteX.Pt pt : plots2DDiscreteX[i].pts) {
                        Comparable key = pt.x;
                        if (xAxisDiscreteInts && key instanceof Number)
                            key = ((Number)key).longValue();
                        endValues[i][keys.indexOf(key)] = pt.y;
                    }
                }

                //the data-representation for JFreeChart-Library
                String [] formattedPlotKeys = new String [plotKeys.length];
                for (int i=0; i<plotKeys.length; i++) {
                    if (plotKeys[i] instanceof Double)
                        formattedPlotKeys[i] = Utils.doubleToCompactStr((Double)plotKeys[i]);
                    else if (plotKeys[i] instanceof Float)
                        formattedPlotKeys[i] = Utils.doubleToCompactStr((Float)plotKeys[i]);
                    else
                        formattedPlotKeys[i] = plotKeys[i].toString();
                }
                CategoryDataset dataSet = new DefaultIntervalCategoryDataset(plotTitles, formattedPlotKeys, startValues, endValues);

                //the bar-chart is a different chart-type
                if (type == Type.BarCharts)
                    chart = ChartFactory.createBarChart(title, xAxis, yAxis, dataSet, PlotOrientation.VERTICAL, true, true, false);
                else
                    chart = ChartFactory.createLineChart(title, xAxis, yAxis, dataSet, PlotOrientation.VERTICAL, true, true, false);

                CategoryPlot plot = chart.getCategoryPlot();
                plot.setBackgroundPaint(new Color(230, 230, 230));
                plot.setDomainGridlinePaint(Color.white);
                plot.setRangeGridlinePaint(Color.white);

                //set the lines / points (but just if we don't have a bar-chart)
                if (type != Type.BarCharts) {
                    LineAndShapeRenderer renderer = new LineAndShapeRenderer();
                    for (int i=0; i<plots2DDiscreteX.length; i++) {
                        renderer.setSeriesLinesVisible(i, hasLines());
                        renderer.setSeriesShapesVisible(i, hasPoints());
                    }
                    plot.setRenderer(renderer);
                }

                //the xAxisDiscreteInts did we already handle with the integer-keys
                if (yAxisDiscreteInts)
                    ((NumberAxis)plot.getRangeAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());

                //cause of a bug in the JFreeChart-Libray (at least I think it is a bug ..)
                //the range of the y-axis has to be specified manually
                double highestY = Double.NEGATIVE_INFINITY, lowestY = Double.POSITIVE_INFINITY;
                for (int i=0; i<plots2DDiscreteX.length; i++ )
                    for (Plot2DDiscreteX.Pt pt : plots2DDiscreteX[i].pts) {
                        if (pt.y > highestY)
                            highestY = pt.y;
                        if (pt.y < lowestY)
                            lowestY = pt.y;
                    }
                ((NumberAxis)plot.getRangeAxis()).setRange(new Range(lowestY, highestY));

            }

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(640, 400));
            return chartPanel;
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "type", type),
                                    new Attribute(new AttributeType(AttributeType.Type.Descriptor), "title", title),
                                    new Attribute(new AttributeType(AttributeType.Type.NormalAttribute), "xAxis", xAxis),
                                    new Attribute(new AttributeType(AttributeType.Type.NormalAttribute), "yAxis", yAxis),
                                    new Attribute(new AttributeType( (plots2DDiscreteX != null ? AttributeType.Type.NormalAttribute : AttributeType.Type.TemporaryOrUnimportant) ), "valueForMissingData", valueForMissingData),
                                    new Attribute(new AttributeType( (plots2DDiscreteX != null ? AttributeType.Type.NormalAttribute : AttributeType.Type.TemporaryOrUnimportant) ), "plots2DDiscreteX", plots2DDiscreteX),
                                    new Attribute(new AttributeType( (plots2DContinuousX != null ? AttributeType.Type.NormalAttribute : AttributeType.Type.TemporaryOrUnimportant) ), "plots2DContinuousX", plots2DContinuousX),
                                    new Attribute(new AttributeType(AttributeType.Type.NormalAttribute), "xAxisDiscreteInts", xAxisDiscreteInts),
                                    new Attribute(new AttributeType(AttributeType.Type.NormalAttribute), "yAxisDiscreteInts", yAxisDiscreteInts));
        }

    }

    /**
     * this class contains a single plot
     */
    public static abstract class Plot extends GenObject {

        /**
         * the title of the plots
         */
        protected final String title;

        /**
         * it is not allowed to add anymore values, after the
         * finish()-method is called once
         */
        protected boolean inUse = false;

        /**
         * the constructor
         *
         * @param _title the title
         */
        protected Plot (String _title) {
            title = _title;
        }

        /**
         * after finishing, the plot-values should not be changed anymore
         */
        protected void finish() {
            inUse = true;
        }

        /**
         * gets the title.
         *
         * @return the title
         */
        public String getTitle () {
            return title;
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.Descriptor), "title", title),
                                    new Attribute(new AttributeType(AttributeType.Type.TemporaryOrUnimportant), "inUse", inUse));
        }

    }

    /**
     * a discrete plot
     */
    public static class Plot2DDiscreteX extends Plot {

        /**
         * the discrete points in insert-order
         */
        protected List <Pt> pts = new ArrayList();

        /**
         * the constructor
         *
         * @param _title the title
         */
        public Plot2DDiscreteX (String _title) {
            super(_title);
        }

        /**
         * the constructor
         *
         * @param _title the title
         * @param ptMap an initializing map of points
         */
        public Plot2DDiscreteX (String _title, Map <Comparable, Double> ptMap) {
            super(_title);

            List <Comparable> xValues = new ArrayList();
            xValues.addAll(ptMap.keySet());
            //we sort the values, because there is no given order in the input-map
            Collections.sort(xValues);
            for (Comparable x : xValues)
                add(x, ptMap.get(x));
        }

        /**
         * creates of copy of the specified plot. The new plot
         * won't be in use, will have a deepcopy of the points and
         * can have a new title.
         *
         * @param from the origin plot
         * @param newTitle the new title
         */
        public Plot2DDiscreteX (Plot2DDiscreteX from, String newTitle) {
            super(newTitle);

            pts.addAll(from.pts);
        }

        /**
         * adds a new point
         *
         * @param _x x
         * @param _y y
         * @throws GeneticRuntimeException if this method is called, after the plot is finished
         */
        public void add (Comparable _x, double _y) {
            if (inUse)
                throw new GeneticRuntimeException("this plot is already in use - you can't add any points anymore.");

            pts.add(new Pt(_x, _y));
        }

        /**
         * A discretized graph can have per discretized group still many different
         * values. The standard behavior is, that a random value will represent the group.
         * With this method, you can choose, which value should represent the group
         *
         * @param mergeOp an operator, that merges a set of double-values (the values on the y-axis)
         */
        public void mergePointsInSameDiscretizedGroup(MergeOperator mergeOp) {
            List <Pt> newPts = new ArrayList();

            //in every iteration-step, find the group with same x-values as the first point in pts
            while (pts.size() > 0) {
                List <Double> valuesInSameGroup = new ArrayList(Arrays.asList(pts.get(0).y));
                for (int i=1; i<pts.size(); i++) {
                    if (pts.get(i).x.equals(pts.get(0).x)) {
                        valuesInSameGroup.add(pts.remove(i).y);
                        i--;
                    }
                }

                //do the merge
                newPts.add(new Pt(pts.remove(0).x, mergeOp.merge(valuesInSameGroup)));
            }
            pts = newPts;
        }

        /**
         * converts this plot in a continuous plot. If the
         * x-values are not numbers, they will get numerated:
         * 0, 1, 2, ...
         *
         * @return this plot in continuous form
         */
        public Plot2DContinuousX convertToContinuousPlot () {
            double [] points = new double [pts.size()*2];
            for (int i=0; i<pts.size(); i++) {
                if (pts.get(i).x instanceof Number)
                    points[i*2] = ((Number)pts.get(i).x).doubleValue();
                else
                    points[i*2] = i;
                points[i*2+1] = pts.get(i).y;
            }
            return new Plot2DContinuousX(title, points);
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.extendList(super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "pts", pts));
        }

        /**
         * a container-class for a discrete point
         */
        protected class Pt extends GenObject {

            /**
             * the x
             */
            protected final Comparable x;

            /**
             * the y
             */
            protected final double y;

            /**
             * the constructor
             *
             * @param _x the x
             * @param _y the y
             */
            public Pt (Comparable _x, double _y) {
                x = _x;
                y = _y;
            }

            @Override
            public List <Attribute> getAttributes() {
                return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "x", x),
                                        new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "y", y));
            }

        }

    }

    /**
     * a non-discrete plot
     */
    public static class Plot2DContinuousX extends Plot {

        /**
         * the points in insert-order
         */
        protected List <Pt> pts = new ArrayList();

        /**
         * the constructor
         *
         * @param _title the title
         * @param pts an initializing set of points, the first double is a x-value, the second a y-value, the third the x-value of the second point and so on..
         * @throws IllegalArgumentException if length of point-array not dividable by 2
         */
        public Plot2DContinuousX (String _title, double ... pts) {
            super(_title);

            if (pts.length%2 == 1)
                throw new IllegalArgumentException("invalid length of pts: has to be dividable by 2.");

            for (int i=0; i<pts.length; i+=2)
                add(pts[i], pts[i+1]);
        }

        /**
         * the constructor
         *
         * @param _title the title
         * @param ptMap an initializing map of points
         */
        public Plot2DContinuousX (String _title, Map <Double, Double> ptMap) {
            super(_title);

            List <Double> xValues = new ArrayList();
            xValues.addAll(ptMap.keySet());
            //we sort the values, because there is no given order in the input-map
            Collections.sort(xValues);
            for (Double x : xValues)
                add(x, ptMap.get(x));
        }

        /**
         * creates of copy of the specified plot. The new plot
         * won't be in use, will have a deepcopy of the points and
         * can have a new title.
         *
         * @param from the origin plot
         * @param newTitle the new title
         */
        public Plot2DContinuousX (Plot2DContinuousX from, String newTitle) {
            super(newTitle);

            pts.addAll(from.pts);
        }

        /**
         * adds a new point
         *
         * @param _x x
         * @param _y y
         * @throws GeneticRuntimeException if this method is called, after the plot is finished
         */
        public void add (double _x, double _y) {
            if (inUse)
                throw new GeneticRuntimeException("this plot is already in use - you can't add any points anymore.");

            pts.add(new Pt(_x, _y));
        }

        /**
         * converts this plot in a discrete plot.
         *
         * @return this plot in discrete form
         */
        public Plot2DDiscreteX convertToDiscretePlot () {
            Map <Comparable, Double> points = new HashMap();
            for (Pt pt : pts)
                points.put(pt.x, pt.y);
            return new Plot2DDiscreteX(title, points);
        }

        /**
         * convert the data in a JFreeChart-Data-Format
         *
         * @return the data as XYSeries
         */
        protected XYSeries getGraphData () {
            XYSeries series = new XYSeries(title);
            for (Pt pt : pts)
                series.add(pt.x, pt.y);
            return series;
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.extendList(super.getAttributes(), new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "pts", pts));
        }

        /**
         * a container-class for a non-discrete point
         */
        protected class Pt extends GenObject {

            /**
             * the x
             */
            protected final double x;

            /**
             * the y
             */
            protected final double y;

            /**
             * the constructor
             *
             * @param _x the x
             * @param _y the y
             */
            public Pt (double _x, double _y) {
                x = _x;
                y = _y;
            }

            @Override
            public List <Attribute> getAttributes() {
                return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "x", x),
                                        new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "y", y));
            }

        }

    }
}
