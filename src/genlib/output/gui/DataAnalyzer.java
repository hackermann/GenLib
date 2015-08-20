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
import genlib.extended.distributions.BasicTypeDistributions.MinMaxDouble;
import genlib.utils.Utils;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * This tool can help, to visualize unknown data. You can project
 * parts of the data on a 2d-picture and scroll through a 3rd dimension.
 * The remaining dimensions will be set a) to a specified default-value
 * or b) to a random value, that can be re-created through a click
 * on a random-button
 *
 * @author Hilmar
 */
public class DataAnalyzer extends ApplicationFrame {

    /**
     * in the model is all the non-gui data
     */
    protected final DataAnalyzerModel model;

    /**
     * the label of the 2d-image, which will be rendered
     */
    protected final JLabel mainLabel;

    /**
     * The label, if there are invalid input-parameters
     */
    protected final JLabel infoLabel;

    /**
     * in these boxes, you can choose the content of the 3 dimensions
     */
    protected final JComboBox [] comboBoxes = new JComboBox[3];

    /**
     * with this slider, you can scroll through 3rd dimension
     */
    protected final JSlider slider;

    /**
     * the constructor
     *
     * @param title the title of the windows
     * @param _model in the model is all the non-gui data
     * @param _mainLabel the label of the 2d-image, which will be rendered
     * @param _infoLabel The label, if there are invalid input-parameters
     * @param _slider with this slider, you can scroll through 3rd dimension
     */
    private DataAnalyzer(String title, DataAnalyzerModel _model, JLabel _mainLabel, JLabel _infoLabel, JSlider _slider) {
        super(title);

        model = _model;
        mainLabel = _mainLabel;
        infoLabel = _infoLabel;
        slider = _slider;
    }

    /**
     * open the data-analyzer
     *
     * @param _model in the model is all the non-gui data
     * @return the data-analyzer
     */
    public static DataAnalyzer open (DataAnalyzerModel _model) {
        DataAnalyzer ret = new DataAnalyzer("Data Analyzer", _model, new JLabel(""), new JLabel(" "), new JSlider(0, _model.discreteSteps-1));

        JPanel mainGrid = new JPanel();
        mainGrid.setLayout(new GridBagLayout());
        mainGrid.add(ret.mainLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainGrid.add(ret.infoLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        String [] descriptions = {"X: ", "Y: ", "Adjustable: ", "Adjust: "};
        for (int i=0; i<4; i++) {
            JPanel subGrid = new JPanel();
            subGrid.setLayout(new GridLayout(1,2,10,10));
            subGrid.add(new JLabel(descriptions[i]));
            if (i < 3) {
                ret.comboBoxes[i] = new JComboBox();
                ret.comboBoxes[i].setModel(new DefaultComboBoxModel(ret.model.dataDescriptions));
                ret.comboBoxes[i].setSelectedIndex(i);
                ret.comboBoxes[i].addActionListener((ActionEvent evt) -> { ret.redraw(evt); });
                subGrid.add(ret.comboBoxes[i]);
            } else {
                ret.slider.addChangeListener((ChangeEvent evt) -> { ret.redraw(evt); });
                subGrid.add(ret.slider);
            }
            mainGrid.add(subGrid, new GridBagConstraints(0, i+2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }
        if (ret.model.staticStandardValues == null) {
            JButton button = new JButton("Randomize");
            button.addActionListener((ActionEvent evt) -> { ret.redraw(evt); });
            mainGrid.add(button, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }

        ret.setContentPane(mainGrid);
        ret.pack();

        ret.setContentPane(mainGrid);
        ret.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        ret.setSize(new Dimension((int)(screenSize.getWidth()*0.75), (int)(screenSize.getHeight()*0.75)));
        ret.addComponentListener(new ComponentListener(){
            @Override public void componentResized(ComponentEvent e) { ret.redraw(); }
            @Override public void componentMoved(ComponentEvent e) {}
            @Override public void componentShown(ComponentEvent e) {}
            @Override public void componentHidden(ComponentEvent e) {}
        });
        RefineryUtilities.centerFrameOnScreen(ret);
        ret.setVisible(true);
        if (ret.model.completeCache)
            ret.model.createData();
        ret.redraw();

        return ret;
    }

    /**
     * redraw the picture
     *
     * @param evt the event
     */
    protected void redraw(EventObject evt) {
        if (evt instanceof ActionEvent) {

            if (evt.getSource() instanceof JButton) {
                if (model.completeCache)
                    model.createData();
                else
                    model.cachedData = new HashMap();
            }

        }

        redraw();
    }

    /**
     * redraw the picture
     */
    protected void redraw() {
        Dimension size = new Dimension (mainLabel.getSize().width, mainLabel.getSize().height);
        if (size.width == 0 || size.height == 0)
            return;

        BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, size.width, size.height, model.drawImage(size, comboBoxes[0].getSelectedIndex(), comboBoxes[1].getSelectedIndex(), comboBoxes[2].getSelectedIndex(), slider.getValue(), infoLabel), 0, size.width);
        ImageIcon icon = new ImageIcon(img);
        mainLabel.setIcon(icon);
    }

    /**
     * in the model is all the non-gui data
     */
    public static class DataAnalyzerModel extends GenObject {

        /**
         * how will the data be scaled?
         */
        public enum ScaleType {Linear, Log, Exp};

        /**
         * the discretization of the data
         */
        protected final int discreteSteps;

        /**
         * the callback-function calculates the data
         */
        protected final CreateDataCallback callback;

        /**
         * is an output desired, how much of the data is created?
         */
        protected final boolean textualOutput;

        /**
         * how will the data be scaled?
         */
        protected final ScaleType scaleType;

        /**
         * standard-values, if they are not part of one of the
         * three dimensions. Null, to use random values.
         */
        protected final double [] staticStandardValues;

        /**
         * the names of the data-parts
         */
        protected final String [] dataDescriptions;

        /**
         * should be everything in the cache, or recalculated with every change of the input parameters?
         */
        protected final boolean completeCache;

        /**
         * the minimum and maximum of every data-part
         */
        protected final MinMaxDouble [] minMax;

        /**
         * the cached data
         */
        protected Map <DataKey, double [][][]> cachedData = new HashMap();

        /**
         * the lowest and highest value in cached data
         */
        protected double cachedMin, cachedMax;

        /**
         * the constructor
         *
         * @param _discreteSteps the discretization of the data
         * @param _callback the callback-function calculates the data
         * @param numberOfData the number of data-parts
         * @throws IllegalArgumentException if discreteSteps is smaller or equal to 0 or numberOfData is smaller than 3
         * @throws NullPointerException if callback is null
         */
        public DataAnalyzerModel (int _discreteSteps, CreateDataCallback _callback, int numberOfData) {
            this(_discreteSteps, _callback, false, ScaleType.Linear, null, new String[numberOfData], false, new MinMaxDouble[numberOfData]);

            for (int i=0; i<numberOfData; i++) {
                dataDescriptions[i] = "Data " + (i+1);
                minMax[i] = new MinMaxDouble(0,1);
            }
        }

        /**
         * the constructor
         *
         * @param _discreteSteps the discretization of the data
         * @param _callback the callback-function calculates the data
         * @param _textualOutput is an output desired, how much of the data is created?
         * @param _scaleType how will the data be scaled?
         * @param _staticStandardValues standard-values, if they are not part of one of the three dimensions. Null, to use random values.
         * @param _dataDescriptions the names of the data-parts
         * @param _completeCache should be everything in the cache, or recalculated with every change of the input parameters?
         * @param _minMax the minimum and maximum of every data-part
         * @throws IllegalArgumentException if discreteSteps is smaller or equal to 0 or length of dataDescriptions, staticStandardValues and minMax is not the same or is lower than 3
         * @throws NullPointerException if callback, scaleType, dataDescriptions or minMax is null
         */
        public DataAnalyzerModel (int _discreteSteps, CreateDataCallback _callback, boolean _textualOutput, ScaleType _scaleType, double [] _staticStandardValues, String [] _dataDescriptions, boolean _completeCache, MinMaxDouble ... _minMax) {
            if (_discreteSteps <= 0)
                throw new IllegalArgumentException("discreteSteps hast to be at least 1.");
            if (_callback == null)
                throw new NullPointerException("callback can't be null.");
            if (_scaleType == null)
                throw new NullPointerException("scaleType can't be null.");
            if (_dataDescriptions == null)
                throw new NullPointerException("dataDescriptions can't be null.");
            if (_minMax == null)
                throw new NullPointerException("minMax can't be null.");
            if (_minMax.length != _dataDescriptions.length || (_staticStandardValues != null && _minMax.length != _staticStandardValues.length) || _dataDescriptions.length < 3)
                throw new IllegalArgumentException("length of dataDescriptions, staticStandardValues and minMax have to be the same and >= 3.");

            discreteSteps = _discreteSteps;
            callback = _callback;
            textualOutput = _textualOutput;
            scaleType = _scaleType;
            staticStandardValues = _staticStandardValues;
            dataDescriptions = _dataDescriptions;
            completeCache = _completeCache;
            minMax = _minMax;
        }

        /**
         * render the image and returns it
         *
         * @param size the size in pixel
         * @param x the index of the x-data-part
         * @param y the index of the y-data-part
         * @param a the index of the adjust-data-part
         * @param adjustPosition the position of the adjust-part
         * @param infoLabel the info-label
         * @return the requested image
         */
        protected int [] drawImage (Dimension size, int x, int y, int a, int adjustPosition, JLabel infoLabel) {
            int [] ret = new int [size.width*size.height];
            infoLabel.setText(" ");

            if (x == y || y == a || x == a) {
                infoLabel.setText("Invalid input parameters.");
                for (int i=0; i<ret.length; i++)
                    ret[i] = 0xFFFF0000;
            } else {
                int xWhere = (x < y && x < a ? 0 : (x > y && x > a ? 2 : 1));
                int yWhere = (y < x && y < a ? 0 : (y > x && y > a ? 2 : 1));
                DataKey dataKey = new DataKey(Math.min(x, Math.min(y, a)), (xWhere == 1 ? x : (yWhere == 1 ? y : a)), Math.max(x, Math.max(y, a)));
                if (!cachedData.containsKey(dataKey))
                    createData(dataKey);

                double[][][] data = cachedData.get(dataKey);

                for (int i=0; i<size.width; i++)
                    for (int j=0; j<size.height; j++) {
                        int xData = (int)(((double)i/(size.width-1)) * (discreteSteps-1));
                        int yData = (int)(((double)j/(size.height-1)) * (discreteSteps-1));
                        int index1 = (xWhere == 0 ? xData : (yWhere == 0 ? yData : adjustPosition));
                        int index2 = (xWhere == 1 ? xData : (yWhere == 1 ? yData : adjustPosition));
                        int index3 = (xWhere == 2 ? xData : (yWhere == 2 ? yData : adjustPosition));
                        int frag = (int)((( scaleValue(data[index1][index2][index3]) - cachedMin) / (cachedMax-cachedMin == 0 ? 1 : cachedMax-cachedMin))*255.0);
                        ret[j*size.width + i] = 0xFF000000 + frag + (frag << 8) + (frag << 16);
                    }
            }

            return ret;
        }

        /**
         * create the complete data of every possible DataKey
         */
        protected void createData () {
            createData(null);
        }

        /**
         * create the data of the specified DataKey or of all
         * possible data-keys, if it is null
         *
         * @param dataKey a data-key or null
         */
        protected void createData (DataKey dataKey) {

            double [] standardValues = new double[minMax.length];
            if (staticStandardValues == null) {
                Random rand = new Random(System.nanoTime());
                for (int i=0; i<standardValues.length; i++)
                    standardValues[i] = rand.nextDouble()*minMax[i].getDistance() + minMax[i].getMin();
            } else
                System.arraycopy(staticStandardValues, 0, standardValues, 0, standardValues.length);

            if (textualOutput)
                System.out.println("Creating data ..");

            cachedData = new HashMap();
            cachedMin = Double.POSITIVE_INFINITY;
            cachedMax = Double.NEGATIVE_INFINITY;

            if (dataKey == null) {
                int counter = 1;
                for (int i=0; i<standardValues.length; i++)
                    for (int j=i+1; j<standardValues.length; j++)
                        for (int k=j+1; k<standardValues.length; k++) {
                            if (textualOutput)
                                System.out.println("Data creating: " + (counter++) + " / " + (standardValues.length*(standardValues.length-1)*(standardValues.length-2)));
                            cachedData.put(new DataKey(i,j,k), createData(new DataKey(i,j,k), standardValues));
                        }
            } else
                cachedData.put(dataKey, createData(dataKey, standardValues));

            cachedMin = scaleValue(cachedMin);
            cachedMax = scaleValue(cachedMax);

            if (textualOutput)
                System.out.println("Finished creating data ..");
        }

        /**
         * create the data of the specified DataKey with given
         * standard-values
         *
         * @param key the data-key
         * @param standardValue the standard-values
         * @return the created data
         */
        protected double[][][] createData (DataKey key, double [] standardValue) {
            double [][][] ret = new double[discreteSteps][discreteSteps][discreteSteps];
            for (int i=0; i<discreteSteps; i++) {
                if (textualOutput)
                    System.out.println("  Sub-Data creating: " + (i+1) + " / " + discreteSteps);

                for (int j=0; j<discreteSteps; j++)
                    for (int k=0; k<discreteSteps; k++) {
                        double [] input = new double [standardValue.length];
                        System.arraycopy(standardValue, 0, input, 0, input.length);
                        input[key.index1] = minMax[key.index1].getDistance()*((double)i/(discreteSteps-1)) + minMax[key.index1].getMin();
                        input[key.index2] = minMax[key.index2].getDistance()*((double)j/(discreteSteps-1)) + minMax[key.index2].getMin();
                        input[key.index3] = minMax[key.index3].getDistance()*((double)k/(discreteSteps-1)) + minMax[key.index3].getMin();
                        ret[i][j][k] = callback.create(input);
                        if (ret[i][j][k] > cachedMax)
                            cachedMax = ret[i][j][k];
                        if (ret[i][j][k] < cachedMin)
                            cachedMin = ret[i][j][k];
                    }
            }
            return ret;
        }

        /**
         * scale the value with the given ScaleType
         *
         * @param in the input
         * @return the scaled value
         */
        protected double scaleValue (double in) {
            if (scaleType == ScaleType.Log)
                return Math.log(in);
            else if (scaleType == ScaleType.Exp)
                return Math.exp(in);

            return in;
        }

        @Override
        public List<Attribute> getAttributes() {
            return Utils.createList(new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "discreteSteps", discreteSteps),
                                    new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "callback", callback),
                                    new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "textualOutput", textualOutput),
                                    new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "scaleType", scaleType),
                                    new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "staticStandardValues", staticStandardValues),
                                    new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "dataDescriptions", dataDescriptions),
                                    new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "minMax", minMax),
                                    new Attribute(new AttributeType(AttributeType.Type.TemporaryOrUnimportant), "completeCache", completeCache),
                                    new Attribute(new AttributeType(AttributeType.Type.TemporaryOrUnimportant), "cachedData", minMax),
                                    new Attribute(new AttributeType(AttributeType.Type.TemporaryOrUnimportant), "cachedMin", cachedMin),
                                    new Attribute(new AttributeType(AttributeType.Type.TemporaryOrUnimportant), "cachedMax", cachedMax));
        }

        /**
         * this key stores the indices of the three dimensions
         */
        protected static class DataKey extends GenObject {

            /**
             * the indices of the 3 dimensions
             */
            protected final int index1, index2, index3;

            /**
             * the constructor
             *
             * @param _index1 the first dimension
             * @param _index2 the second dimension
             * @param _index3 the third dimension
             */
            public DataKey (int _index1, int _index2, int _index3) {
                index1 = _index1;
                index2 = _index2;
                index3 = _index3;
            }

            @Override
            public int hashCode () {
                return index1+index2+index3;
            }

            @Override
            public List<Attribute> getAttributes() {
                return Utils.createList(new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "index1", index1),
                                        new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "index2", index2),
                                        new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "index3", index3));
            }
        }

    }

    /**
     * this interface is used for the callback-function
     */
    public static interface CreateDataCallback {

        /**
         * create the data for the given input
         *
         * @param input the input
         * @return the created data
         */
        public double create (double [] input);

    }

}
