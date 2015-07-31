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

import java.util.Collections;
import java.util.List;

/**
 * The MergeOperator is an abstract class, for merging a list
 * of doubles.
 *
 * @author Hilmar
 */
public abstract class MergeOperator {

    /**
     * the actual operator
     *
     * @param values a set of doubles, should wether be null nor be empty
     * @return the requested merged value
     */
    public abstract double merge (List <Double> values);

    /**
     * this class merges a list of doubles, returning the minimum-value
     */
    public static class MinMerge extends MergeOperator {

        @Override
        public double merge(List <Double> values) {
            return Collections.min(values);
        }

    }

    /**
     * this class merges a list of doubles, returning the maximum-value
     */
    public static class MaxMerge extends MergeOperator {

        @Override
        public double merge(List <Double> values) {
            return Collections.max(values);
        }

    }

    /**
     * this class merges a list of doubles, returning the average-value
     */
    public static class AverageMerge extends MergeOperator {

        @Override
        public double merge(List <Double> values) {
            double avg = 0;
            for (Double value : values)
                avg += value/values.size();
            return avg;
        }

    }

    /**
     * this class merges a list of doubles, returning the sum of the values
     */
    public static class AddMerge extends MergeOperator {

        @Override
        public double merge(List <Double> values) {
            double sum = 0;
            for (Double value : values)
                sum += value;
            return sum;
        }

    }

}
