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
 * This operator transfers a genoType in a phenoType
 *
 * @author Hilmar
 */
public interface GenoToPhenoOp extends Operator {

    /**
     * the actual operator.
     *
     * @param input the instance of the genoType
     * @param step the status of the running algorithm
     * @return the converted phenoType
     */
    public GenInstance genoToPhenoOp (GenInstance input, AlgorithmStep step);

    /**
     * is this operator compatible with this type of a genoType and phenoType?
     *
     * @param genoType the type of the genoType
     * @param phenoType the type of the phenoType
     * @return true, if compatible
     */
    public boolean isGenoPhenoCompatible(GenRepresentation genoType, GenRepresentation phenoType);

}
