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

package genlib.abstractrepresentation;

/**
 * This interface defines an interface for a fitness-operator. A fitness-operator
 * calculates the fitness for a phenoType.
 *
 * @author Hilmar
 */
public interface FitnessOp {

    /**
     * the actual operator
     *
     * @param input This instance is the phenoType of an individuum
     * @param step This step defines the current state of the running algorithm
     * @return the calculated fitness-values
     */
    public double fitnessOp (GenInstance input, AlgorithmStep step);

    /**
     * is this operator compatible with the type of this phenoType
     *
     * @param representation the type of a phenoType
     * @return true, if it is compatible
     */
    public boolean isCompatible(GenRepresentation representation);

    /**
     * is this operator compatible with this environment (the algorithmPass)
     *
     * @param algorithmPass the environment
     * @return true, if it is compatible
     */
    public boolean isCompatible(AlgorithmPass algorithmPass);

}
