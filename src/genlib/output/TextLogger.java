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
import genlib.abstractrepresentation.GeneticAlgorithm;
import genlib.abstractrepresentation.GeneticAlgorithm.Individuum;
import genlib.output.TextLogger.PopulationLogging.Type;
import genlib.utils.Exceptions.GeneticRuntimeException;
import genlib.utils.Utils;
import genlib.utils.Utils.FillDirection;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * this logger saves the information as text and can print them.
 *
 * @author Hilmar
 */
public class TextLogger extends Logger {

    /**
     * the stream, the output will be written in
     */
    protected final OutputStream stream;

    /**
     * shall the output be written, while the algorithm runs?
     */
    protected final boolean logJustInTime;

    /**
     * what should be logged of one population?
     */
    protected final PopulationLogging populationLogging;

    /**
     * The type of logging the time? The fixed length will
     * reserve for every time-stamp the length of the longest possible time-stamp
     */
    public enum TimeLogging {None, MicroSeconds, MilliSeconds, Seconds, MicroSecondsFixedLength, MilliSecondsFixedLength, SecondsFixedLength};

    /**
     * the time-logging
     */
    protected final TimeLogging timeLogging;

    /**
     * the entries, will just be filled, if we do NOT a justInTime-logging
     */
    protected List <String> logEntries;

    /**
     * the timestamp of the first activity (in microseconds)
     */
    protected long startMicroTime;

    /**
     * the constructor, it will log with standard-values, just-in-time and on System.out
     */
    public TextLogger() {
        this(System.out, true, PopulationLogging.populationLogAtMostK(3), TimeLogging.MilliSeconds);
    }

    /**
     * the constructor
     *
     * @param _logJustInTime shall the output be written, while the algorithm runs?
     * @param _populationLogging what should be logged of one population?
     * @param _timeLogging the time-logging
     */
    public TextLogger(boolean _logJustInTime, PopulationLogging _populationLogging, TimeLogging _timeLogging) {
        this(System.out, _logJustInTime, _populationLogging, _timeLogging);
    }

    /**
     * the constructor
     *
     * @param _stream the stream, the output will be written in (as example System.out)
     * @param _logJustInTime shall the output be written, while the algorithm runs?
     * @param _populationLogging what should be logged of one population?
     * @param _timeLogging the time-logging
     */
    public TextLogger(OutputStream _stream, boolean _logJustInTime, PopulationLogging _populationLogging, TimeLogging _timeLogging) {
        stream = _stream;
        logJustInTime = _logJustInTime;
        populationLogging = _populationLogging;
        timeLogging = _timeLogging;
    }
    
    @Override
    public void compatibilityCheck(GeneticAlgorithm algorithm) { }

    @Override
    protected void log (LogType logType, GeneticAlgorithm algorithm) {
        switch (logType) {
            case StartAlgorithm:
                addLogEntry("Started algorithm '" + algorithm.getName() + "': " + algorithm);
                break;
            case EndAlgorithm:
                addLogEntry("Finished algorithm '" + algorithm.getName() + "'");
                break;
            case Generation:
                StringBuilder entry = new StringBuilder();
                entry.append("Generation '").append(algorithm.getCurrentGeneration()).append("' of algorithm '").append(algorithm.getName()).append("': ");
                Individuum [] entries = algorithm.getCurrentPopulation();

                //log the k best individuums in the population (the array is already sorted)
                for (int i=0; (i < entries.length && (populationLogging.type == Type.PopulationAtMostK ? i < populationLogging.k : true)); i++)
                    entry.append("\n               Individuum 'I").append(i).append("': ").append(entries[i]);

                addLogEntry(entry.toString());
                break;
            default:
                throw new AssertionError(logType.name());
        }
    }

    @Override
    protected void starting () {
        startMicroTime = System.nanoTime()/1000;
        logEntries = new ArrayList();
    }

    @Override
    protected void ending() {
        try {
            if (!logJustInTime)
                for (String logEntry : logEntries)
                    stream.write(logEntry.getBytes());

        } catch (IOException e) {
            throw new GeneticRuntimeException(e);
        }
    }

    /**
     * adds a log-entry. In the case of just-in-time-logging, it will be written directly
     *
     * @param entry the log-message
     */
    protected void addLogEntry (String entry) {

        try {
            String complete = getFormattedTime() + entry + "\n";

            if (logJustInTime)
                stream.write(complete.getBytes());
            else
                logEntries.add(complete);

        } catch (IOException e) {
            throw new GeneticRuntimeException(e);
        }
    }

    /**
     * returns the formatted time, depending on the chosen time-logging.
     *
     * @return the formatted time as String
     */
    protected String getFormattedTime() {
        long current = System.nanoTime()/1000 - startMicroTime;
        switch (timeLogging) {
            case None:
                return "";
            case MicroSeconds:
                return "[" + current + " µs]  ";
            case MilliSeconds:
                return "[" + current/1000 + " ms]  ";
            case Seconds:
                return "[" + current/1000000 + " s]  ";
            case MicroSecondsFixedLength:
                return "[" + Utils.lengthAtLeast("" + current, new String("" + Long.MAX_VALUE).length(), ' ', FillDirection.Left) + " µs]  ";
            case MilliSecondsFixedLength:
                return "[" + Utils.lengthAtLeast("" + current/1000, new String("" + Long.MAX_VALUE/1000).length(), ' ', FillDirection.Left) + " ms]  ";
            case SecondsFixedLength:
                return "[" + Utils.lengthAtLeast("" + current/1000000, new String("" + Long.MAX_VALUE/1000000).length(), ' ', FillDirection.Left) + " s]  ";
            default:
                throw new AssertionError(timeLogging.name());
        }
    }

    @Override
    public List <Attribute> getAttributes() {
        return Utils.extendList( super.getAttributes(),  new Attribute(new AttributeType(AttributeType.Type.TemporaryOrUnimportant), "stream", stream),
                                                        new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "logJustInTime", logJustInTime),
                                                        new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "populationLogging", populationLogging),
                                                        new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "timeLogging", timeLogging),
                                                        new Attribute(new AttributeType(AttributeType.Type.TemporaryOrUnimportant), "logEntries", logEntries),
                                                        new Attribute(new AttributeType(AttributeType.Type.TemporaryOrUnimportant), "startMicroTime", startMicroTime));
    }

    /**
     * the type of the population-logging
     */
    public static class PopulationLogging extends GenObject {

        /**
         * the basic-type: What shall be logged of the population?
         */
        protected enum Type {PopulationAll, PopulationAtMostK};

        /**
         * the basic-type: What shall be logged of the population?
         */
        protected final Type type;

        /**
         * the k, just needed for some of the enum-entries
         */
        protected final int k;

        /**
         * the constructor, just available for the creation-methods
         *
         * @param _type the basic-type
         * @param _k the optional k
         */
        private PopulationLogging (Type _type, int _k) {
            type = _type;
            k = _k;
        }

        /**
         * a creation-method: log the k best individuums per population
         *
         * @param _k the k
         * @return the requested population-logging
         * @throws IllegalArgumentException if k is smaller than 0
         */
        public static PopulationLogging populationLogAtMostK (int _k) {
            if (_k < 0)
                throw new IllegalArgumentException("illegal k: '" + _k + "'.");

            return new PopulationLogging(Type.PopulationAtMostK, _k);
        }

        /**
         * a creation-method: log the complete population
         *
         * @return the requested population-logging
         */
        public static PopulationLogging populationLogAll () {
            return new PopulationLogging(Type.PopulationAll, -1);
        }

        /**
         * a creation-method: log nothing of the population
         *
         * @return the requested population-logging
         */
        public static PopulationLogging populationLogNothing () {
            return new PopulationLogging(Type.PopulationAtMostK, 0);
        }

        @Override
        public List <Attribute> getAttributes() {
            return Utils.createList( new Attribute(new AttributeType(AttributeType.Type.MainAttribute), "type", type),
                                    new Attribute(new AttributeType( (type == Type.PopulationAll ? AttributeType.Type.TemporaryOrUnimportant : AttributeType.Type.NormalAttribute) ), "k", k));
        }

    }


}
