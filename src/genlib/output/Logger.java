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

import genlib.abstractrepresentation.AlgorithmPass;
import genlib.abstractrepresentation.AlgorithmStep;
import genlib.abstractrepresentation.GenObject;
import genlib.abstractrepresentation.GenObject.Attribute;
import genlib.abstractrepresentation.GenObject.AttributeType.Type;
import genlib.abstractrepresentation.GeneticAlgorithm;
import genlib.utils.NamingConvention;
import genlib.utils.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A logger logs information of an algorithm run, that can be accessed later or
 * can be displayed in any way. You can use one logger multiple times, but
 * it will always just store the information of the last algorithm run.
 *
 * @author Hilmar
 */
public abstract class Logger extends GenObject {

    /**
     * All the algorithm-points, the logger can be invoked
     */
    public enum LogType {StartAlgorithm, EndAlgorithm, Generation};

    /**
     * The algorithms, that are currently running and are logged. This is just
     * important, if one algorithm invokes more algorithms with the same logger.
     */
    protected Set <GeneticAlgorithm> startedAlgorithms = new HashSet();

    /**
     * this method will be invoked by the algorithm, if it is started
     *
     * @param algorithm the invoking algorithm
     * @param step the algorithm-step
     */
    public void startAlgorithm(GeneticAlgorithm algorithm, AlgorithmStep step) {
        //the first invoke of this method? => logging will reset
        if (startedAlgorithms.isEmpty())
           starting();
        startedAlgorithms.add(algorithm);

        log(LogType.StartAlgorithm, algorithm, step);
    }

    /**
     * this method will be invoked by the algorithm, if it is completed
     *
     * @param algorithm the invoking algorithm
     * @param step the algorithm-step
     */
    public void endAlgorithm(GeneticAlgorithm algorithm, AlgorithmStep step) {
        log(LogType.EndAlgorithm, algorithm, step);

        startedAlgorithms.remove(algorithm);
        //the first added algorithm is ready? => logging is completed
        if (startedAlgorithms.isEmpty())
            ending();
    }

    /**
     * this method will be invoked by the algorithm, if a new generation starts
     *
     * @param algorithm the invoking algorithm
     * @param step the algorithm-step
     */
    public void logGeneration(GeneticAlgorithm algorithm, AlgorithmStep step) {
        log(LogType.Generation, algorithm, step);
    }

    /**
     * this method does a compatibility check with the geneticAlgorithm.
     * The operators and representations of the algorithm should be set.
     *
     * @param algorithm the algorithm
     * @param pass the algorithm-pass this algorithm is executed with
     */
    public abstract void compatibilityCheck(GeneticAlgorithm algorithm, AlgorithmPass pass);

    /**
     * this method processes in the subclass the actual logging
     *
     * @param logType what is logged?
     * @param algorithm the invoking algorithm
     * @param step the algorithm-step
     */
    protected abstract void log (LogType logType, GeneticAlgorithm algorithm, AlgorithmStep step);

    /**
     * this is a reset of the logging
     */
    protected abstract void starting ();

    /**
     * the logging of one run is completed
     */
    protected abstract void ending ();

    @Override
    public List <Attribute> getAttributes() {
        return Utils.createList( new Attribute(new AttributeType(Type.TemporaryOrUnimportant), "startedAlgorithms", startedAlgorithms));
    }

}
