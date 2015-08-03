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
 * this operator does a recombination. It gets 1-n genoType-instances as
 * input and recombinates them to 1-m genoType-instances as output.
 *
 * @author Hilmar
 */
public interface RecombinationOp {

    /**
     * the actual operator.
     *
     * @param input 1-n instances as genoType
     * @param step the status of the running algorithm
     * @param outputSize the requested output-size. The returned array has to have this length.
     * @return the array of recombinated genoTypes. The array has to have the length 'outputSize'.
     */
    public GenInstance [] recombinationOp (GenInstance [] input, AlgorithmStep step, int outputSize);

    /**
     * is this operator compatible with this length of an input?
     * (so the operator has the ability to recombinate this
     * input length?)
     *
     * @param size the input-length
     * @return true, if compatible
     */
    public boolean isInputSizeCompatible (int size);

    /**
     * is this operator compatible with this length of an output?
     * (so the operator has the ability to give this number of
     * output instances, if requested)
     *
     * @param size the output-length
     * @return true, if compatible
     */
    public boolean isOutputSizeCompatible (int size);

    /**
     * is this operator compatible with this type of a genoType?
     *
     * @param representation the type of the genoType
     * @return true, if compatible
     */
    public boolean isCompatible(GenRepresentation representation);

    /**
     * is this operator compatible with this AlgorithmPass?
     *
     * @param algorithmPass the algorithmPass
     * @return true, if compatible
     */
    public boolean isCompatible(AlgorithmPass algorithmPass);

}
