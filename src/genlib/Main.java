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

package genlib;

import genlib.examples.ExampleViewer;
import genlib.utils.LibCompletenessTest;
import java.util.Arrays;

/**
 * The Main-class with the main-method
 *
 * @author Hilmar
 */
public class Main {

    /**
     * the main-method, arguments:
     * examples: open the example viewer
     * lib-completeness-test [PATH] execute the lib-completeness-test
     * standard-behavior: open the examples
     *
     * @param args the arguments
     */
    public static void main (String [] args) {

        if (args.length == 1 && args[0].equals("examples"))
            ExampleViewer.open();
        else if (args.length == 2 && args[0].equals("lib-completeness-test"))
            LibCompletenessTest.doTest(args[1]);
        else {

            System.out.println(Arrays.toString(args));
            System.out.println("GenLib, usage:");
            System.out.println("argument 'examples': open the example-viewer");
            System.out.println("argument 'lib-completeness-test' [PATH]: execute the lib-completeness-test");
            System.out.println("No valid arguments chosen, start example-viewer as standard-behavior ..");

            ExampleViewer.open();
        }

    }

}
