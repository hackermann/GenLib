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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Some Utility-functions
 *
 * @author Hilmar
 */
public class Utils {

    /**
     * instantiation is not allowed
     */
    private Utils() { }

    /**
     * Where to fill the string with empty characters?
     */
    public enum FillDirection {Left, Center, Right};

    /**
     * the string should have at least the length minLength
     *
     * @param input the origin string
     * @param minLength the minimum length, it should have afterwards
     * @param fillWith the character to fill with (as example the space ' ')
     * @param fillDirection where should the fill-characters inserted?
     * @return the new string
     */
    public static String lengthAtLeast (String input, int minLength, char fillWith, FillDirection fillDirection) {
        boolean fillLeft = (fillDirection == FillDirection.Left);

        while (input.length() < minLength) {
            input = (fillLeft ? fillWith + input : input + fillWith);
            if (fillDirection == FillDirection.Center)
                fillLeft = !fillLeft;
        }
        return input;
    }

    /**
     * the standard-epsilon for the double-equality check
     */
    private static double epsilonDoubleEq = 0.00001;

    /**
     * a pseudo-equality check for doubles
     *
     * @param v1 double no. 1
     * @param v2 double no. 2
     * @return true, if they are pseudo-equal
     */
    public static boolean doubleEquality (double v1, double v2) {
        return Math.abs(v1-v2) < epsilonDoubleEq;
    }

    /**
     * a pseudo-equality check for doubles
     *
     * @param v1 double no. 1
     * @param v2 double no. 2
     * @param epsilon an user-defined epsilon
     * @return true, if they are pseudo-equal
     */
    public static boolean doubleEquality (double v1, double v2, double epsilon) {
        return Math.abs(v1-v2) < epsilon;
    }

    /**
     * create a list and fill it with this entries
     * @param entries the entries
     * @return the list
     */
    public static List createList (Object ... entries) {
        List list = new ArrayList();
        for (Object entry : entries)
            list.add(entry);
        return list;
    }

    /**
     * create a list, insert an old list and fill it with this entries
     * @param extendFromList the old list
     * @param entries the entries
     * @return the list
     */
    public static List extendList (List extendFromList, Object ... entries) {
        List list = new ArrayList();
        list.addAll(extendFromList);
        for (Object entry : entries)
            list.add(entry);
        return list;
    }

}
