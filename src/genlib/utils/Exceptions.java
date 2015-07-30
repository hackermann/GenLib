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

package genlib.utils;

/**
 * This class is a bundle of Exceptions for the GenLib
 *
 * @author Hilmar
 */
public class Exceptions {

    /**
     * no instantiation allowed
     */
    private Exceptions() {}

    /**
     * this Exception is thrown, if this error should be catchable
     */
    public static class GeneticException extends Exception {

        /**
         * the constructor
         *
         * @param msg the exception-message
         */
        public GeneticException(String msg) {
            super(msg);
        }

        /**
         * the constructor
         *
         * @param cause the exception, this one inherits from
         */
        public GeneticException(Throwable cause) {
            super(cause);
        }

    }

    /**
     * this Exception is thrown, if this error should not be catched in the standard case
     */
    public static class GeneticRuntimeException extends RuntimeException {

        /**
         * the constructor
         *
         * @param msg the exception-message
         */
        public GeneticRuntimeException(String msg) {
            super(msg);
        }

        /**
         * the constructor
         *
         * @param cause the exception, this one inherits from
         */
        public GeneticRuntimeException(Throwable cause) {
            super(cause);
        }

    }

    /**
     * this Exception is thrown, if this error should not be catched in the
     * standard case AND it is an internal exception. If such an exception is
     * thrown, there is a bug in the GenLib.
     */
    public static class GeneticInternalException extends RuntimeException {

        /**
         * the constructor
         *
         * @param msg the exception-message
         */
        public GeneticInternalException(String msg) {
            super(msg);
        }

        /**
         * the constructor
         *
         * @param cause the exception, this one inherits from
         */
        public GeneticInternalException(Throwable cause) {
            super(cause);
        }

    }

}
